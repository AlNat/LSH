package com.Administration.server;

import com.Administration.client.LinkData;
import com.LSH.server.Config.Config;
import com.LSH.server.Log.Log;
import com.LSH.server.Log.LogEvent;
import com.LSH.server.Normalizer;
import com.LSH.server.Shortner;

import java.sql.*;
import java.sql.Date;
import java.util.*;

/**
 * Created by @author AlNat on 16.09.2016.
 * Licensed by Apache License, Version 2.0
 *
 * Класс соединения приложения с БД
 */
class DBConnect {

    static final DBConnect instance = new DBConnect(); // Реализация паттерна Singleton

    private Connection connection; // Соединение

    private DBConnect() { // Конструктор

        String url = "jdbc:postgresql:" + Config.instance.getURL();
        String login = Config.instance.getLogin();
        String password = Config.instance.getPassword();
        Properties props;

        try { // Попытались установить соединение с БД

            Class.forName("org.postgresql.Driver");

            props = new Properties();
            props.setProperty("user", login);
            props.setProperty("password", password);
            props.setProperty("ssl", "false");

            connection = DriverManager.getConnection(url, props);
        } catch (SQLException e) { // Ловим ошибку
            e.printStackTrace();

            // Пишем лог
            LogEvent l = new LogEvent();
            l.setClassName("DBConnect");
            l.setType("Connection");
            l.setMessage("Cannot connect to DB");
            Log.instance.WriteEvent(l);
        } catch (ClassNotFoundException e) {

            System.out.println("Please, check Tomcat lib folder for JDBC jar archive");

            e.printStackTrace();

            // Пишем лог
            LogEvent l = new LogEvent();
            l.setClassName("DBConnect");
            l.setType("Connection");
            l.setMessage("Cannot find jdbc driver");
            Log.instance.WriteEvent(l);
        }
    }

     /**
     * Функция, аутентифицирующая пользоватедя
     * @param login логин
     * @param password пароль
     * @return true если есть такой пользователь с таким паролем, иначе false
     */
    Boolean isUser (String login, String password) {

        try {
            // Создаем запрос и выполняем его
            PreparedStatement st = connection.prepareStatement("SELECT password FROM users WHERE login = ?", ResultSet.TYPE_SCROLL_INSENSITIVE);
            st.setString(1, login);
            ResultSet rs = st.executeQuery();

            // Получаем ответ
            rs.next();
            String pass = rs.getString("password");

            // Закрываем
            rs.close();
            st.close();

            // Пишем лог
            LogEvent l = new LogEvent();
            l.setClassName("DBConnect.isUser");
            l.setType("Login");
            l.setMessage("Login = " + login + "; Password = " + password);
            Log.instance.WriteEvent(l);

            return pass.equals(password);

        } catch (SQLException e) { // Ловим ошибки
            if (e.getSQLState().equals("24000")) { // Пустой ответ - нет такого логина
                return false;
            }
            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();

            // Пишем лог
            LogEvent l = new LogEvent();
            l.setClassName("DBConnect.isUser");
            l.setType("SQLException");
            l.setMessage(e.getMessage());
            Log.instance.WriteEvent(l);

            return false; // И говорим про ошибку
        }
    }


    /**
     * Функция, возращающая набор данных об ссылках пользователя
     * @param login логин пользователя
     * @return данные
     */
    LinkData[] getData (String login) {

        LinkedList<LinkData> list = new LinkedList<>(); // Лист с данными
        Integer id;

        // Получаем id пользователя с таким логином

        try {
            // Создаем запрос и выполняем его
            PreparedStatement st = connection.prepareStatement("SELECT id FROM users WHERE login = ?", ResultSet.TYPE_SCROLL_INSENSITIVE);
            st.setString(1, login);
            ResultSet rs = st.executeQuery();

            rs.next();
            id = rs.getInt("id"); // Получаем id

            // Закрываем
            rs.close();
            st.close();

        } catch (SQLException e) { // Ловим ошибки
            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();

            // Пишем лог
            LogEvent l = new LogEvent();
            l.setClassName("DBConnect.getData");
            l.setType("SQLException");
            l.setMessage(e.getMessage());
            Log.instance.WriteEvent(l);

            return new LinkData[0]; // И говорим про ошибку
        }

        // Получаем данные ссылок, где владелец с таким id

        try {
            // Создаем запрос и выполняем его
            PreparedStatement st = connection.prepareStatement("SELECT * FROM short WHERE owner = ?", ResultSet.TYPE_SCROLL_INSENSITIVE);
            st.setInt(1, id);
            ResultSet rs = st.executeQuery();

            // Получаем данные
            while (rs.next()) { // Заполняем лист данными
                LinkData t = new LinkData();
                t.setId(rs.getInt("id"));
                t.setCode( Shortner.GetShort(rs.getInt("user_id")) ); // На лету преобразовали id в код
                t.setExpiredDate(rs.getDate("expired_date"));
                t.setCreateDate(rs.getDate("create_time"));
                t.setLink(rs.getString("link"));
                t.setCurrentCount(rs.getInt("current_count"));
                t.setMaxCount(rs.getInt("max_count"));

                String a = rs.getString("password");
                if (a == null) {
                    t.setPassword("");
                } else {
                    t.setPassword(a);
                }

                list.add(t);
            }

            // Закрываем
            rs.close();
            st.close();

        } catch (SQLException e) { // Ловим ошибки
            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();

            // Пишем лог
            LogEvent l = new LogEvent();
            l.setClassName("DBConnect.getData");
            l.setType("SQLException");
            l.setMessage(e.getMessage());
            Log.instance.WriteEvent(l);

            return new LinkData[1];
        }


        LinkData[] a = new LinkData[list.size()];
        for (int t = 0; t < list.size(); t++) {
            a[t] = list.get(t);
        }

        return a; // Вернули данные
    }


    // Функции обновления данных

    /**
     * Функция, устанавливающая оригинальный линк
     * @param id записи
     * @param link новый линк
     * @return true если запись успешна, иначе false
     */
    Boolean setOriginalLink (int id, String link) {

        String norm = Normalizer.Normalize(link); // Нормализауем линк

        if ( norm.equals("Error!") ) { // Если нормализация не удалась, то возращаем ошибку

            // Пишем в лог
            LogEvent l = new LogEvent();
            l.setClassName("DBConnect.setOriginalLink");
            l.setType("DataError");
            l.setMessage("Illegal link");
            Log.instance.WriteEvent(l);

            return false;
        }

        try {
            // Создаем запрос и выполняем его
            PreparedStatement st = connection.prepareStatement("UPDATE short SET link = ? WHERE id = ?", ResultSet.TYPE_SCROLL_INSENSITIVE);
            st.setString(1, link);
            st.setInt(2, id);
            st.execute();
            st.close();

            // Пишем лог
            LogEvent l = new LogEvent();
            l.setClassName("DBConnect.setOriginalLink");
            l.setType("Update data");
            l.setMessage("id = " + id + "link = " + link);
            Log.instance.WriteEvent(l);

            return true;

        } catch (SQLException e) { // Ловим ошибки

            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();

            // Пишем лог
            LogEvent l = new LogEvent();
            l.setClassName("DBConnect.setOriginalLink");
            l.setType("SQLException");
            l.setMessage(e.getMessage());
            Log.instance.WriteEvent(l);

            return false; // И говорим про ошибку
        }
    }

    /**
     * Функция, устанавливающая срок действия
     * @param id записи
     * @param date новая дата
     * @return true если запись успешна, иначе false
     */
    Boolean setExpiredDate (int id, java.util.Date date) {

        try {
            // Создаем запрос и выполняем его
            PreparedStatement st = connection.prepareStatement("UPDATE short SET expired_date = ? WHERE id = ?", ResultSet.TYPE_SCROLL_INSENSITIVE);
            Date t = new Date(date.getTime());
            st.setDate(1, t);
            st.setInt(2, id);
            st.execute();
            st.close();

            // Пишем лог
            LogEvent l = new LogEvent();
            l.setClassName("DBConnect.setExpiredDate");
            l.setType("Update data");
            l.setMessage("id = " + id + "date = " + date);
            Log.instance.WriteEvent(l);

            return true;

        } catch (SQLException e) { // Ловим ошибки

            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();

            // Пишем лог
            LogEvent l = new LogEvent();
            l.setClassName("DBConnect.setExpiredDate");
            l.setType("SQLException");
            l.setMessage(e.getMessage());
            Log.instance.WriteEvent(l);

            return false; // И говорим про ошибку
        }
    }


    /**
     * Функция, устанавливающая максимальное кол-во переходов
     * @param id записи
     * @param count новое кол-во
     * @return true если запись успешна, иначе false
     */
    Boolean setMaxCount (int id, int count) {

        try {
            // Создаем запрос и выполняем его
            PreparedStatement st = connection.prepareStatement("UPDATE short SET max_count = ? WHERE id = ?", ResultSet.TYPE_SCROLL_INSENSITIVE);
            st.setInt(1, count);
            st.setInt(2, id);
            st.execute();
            st.close();

            // Пишем лог
            LogEvent l = new LogEvent();
            l.setClassName("DBConnect.setMaxCount");
            l.setType("Update data");
            l.setMessage("id = " + id + "count = " + count);
            Log.instance.WriteEvent(l);

            return true;

        } catch (SQLException e) { // Ловим ошибки

            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();

            // Пишем лог
            LogEvent l = new LogEvent();
            l.setClassName("DBConnect.setMaxCount");
            l.setType("SQLException");
            l.setMessage(e.getMessage());
            Log.instance.WriteEvent(l);

            return false; // И говорим про ошибку
        }
    }

    /**
     * Функция, устанавливающая пароль на ссылку
     * @param id записи
     * @param password новый пароль
     * @return true если запись успешна, иначе false
     */
    Boolean setPassword (int id, String password) {

        try {
            // Создаем запрос и выполняем его
            PreparedStatement st = connection.prepareStatement("UPDATE short SET password = ? WHERE id = ?", ResultSet.TYPE_SCROLL_INSENSITIVE);
            st.setString(1, password);
            st.setInt(2, id);
            st.execute();

            st.close();

            // Пишем лог
            LogEvent l = new LogEvent();
            l.setClassName("DBConnect.setPassword");
            l.setType("Update data");
            l.setMessage("id = " + id + "password = " + password);
            Log.instance.WriteEvent(l);

            return true;

        } catch (SQLException e) { // Ловим ошибки

            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();

            // Пишем лог
            LogEvent l = new LogEvent();
            l.setClassName("DBConnect.setPassword");
            l.setType("SQLException");
            l.setMessage(e.getMessage());
            Log.instance.WriteEvent(l);

            return false; // И говорим про ошибку
        }
    }


    /**
     * Функция, удаляющая запись
     * @param id записи
     * @return true если удаление успешна, иначе false
     */
    Boolean deleteLink (int id) {

        try {
            // Создаем запрос и выполняем его
            PreparedStatement st = connection.prepareStatement("DELETE FROM short WHERE id = ?", ResultSet.TYPE_SCROLL_INSENSITIVE);
            st.setInt(1, id);
            st.execute();
            st.close();

            // Пишем лог
            LogEvent l = new LogEvent();
            l.setClassName("DBConnect.deleteLink");
            l.setType("Update data");
            l.setMessage("id = " + id);
            Log.instance.WriteEvent(l);

            return true;

        } catch (SQLException e) { // Ловим ошибки

            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();

            // Пишем лог
            LogEvent l = new LogEvent();
            l.setClassName("DBConnect.deleteLink");
            l.setType("SQLException");
            l.setMessage(e.getMessage());
            Log.instance.WriteEvent(l);

            return false; // И говорим про ошибку
        }

    }
}
