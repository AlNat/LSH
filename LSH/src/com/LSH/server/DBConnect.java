package com.LSH.server;

import com.LSH.client.DataType.ReturnLinkData;
import com.LSH.server.Config.Config;
import com.LSH.client.DataType.GetLinkData;
import com.LSH.client.DataType.PutLinkData;
import com.LSH.server.Log.Log;
import com.LSH.server.Log.LogEvent;

import static com.LSH.server.LSHService.errorCode;

import java.sql.*;
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

    private DBConnect () { // Конструктор

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
            l.setClassName("LSH.DBConnect");
            l.setType("Connection");
            l.setMessage("Cannot connect to DB");
            Log.instance.WriteEvent(l);
        } catch (ClassNotFoundException e) {

            System.out.println("Please, check Tomcat lib folder for JDBC jar archive");

            e.printStackTrace();

            // Пишем лог
            LogEvent l = new LogEvent();
            l.setClassName("LSH.DBConnect");
            l.setType("Connection");
            l.setMessage("Cannot find jdbc driver");
            Log.instance.WriteEvent(l);
        }
    }

    /**
     * Функция проверки - занят ли этот код
     * @param code мнемноничесая ссылки
     * @return -2 если занят. -1 при ошибке кода. id от кода в другом случае
     */
    private Integer CheckAvailability (String code) {

        Integer id = Shortner.GetID(code); // Получили id по коду

        if (id == -1) { // Если код ошибочный то вернули ошибку -1
            return -1;
        }

        try {
            // Создаем запрос и выполняем его
            PreparedStatement st = connection.prepareStatement("SELECT valid FROM status WHERE user_id = ?", ResultSet.TYPE_SCROLL_INSENSITIVE);
            st.setInt(1, id);
            ResultSet rs = st.executeQuery();

            // Получаем ответ
            rs.next();
            boolean answer = rs.getBoolean("valid");

            // Закрываем
            rs.close();
            st.close();

            // Парсим ответ
            if (answer) { // Если занят то возращаем код занятости
                return -2;
            } else { // Иначе сам id
                return id;
            }

        } catch (SQLException e) { // Ловим ошибки
            if (e.getSQLState().equals("24000")) { // Пустой ответ - нет такого кода - еще не использовался
                return id;
            }
            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();

            // Пишем лог
            LogEvent l = new LogEvent();
            l.setClassName("LSH.DBConnect.CheckAvailability");
            l.setType("SQLException");
            l.setMessage(e.getMessage());
            Log.instance.WriteEvent(l);

            return -1; // И говорим про ошибку
        }

    }

    /**
     * Метод, который кладет в БД новую ссылки и
     * @param in сообщение с данными ссылки
     * @return Код ошибки или короткую ссылку
     */
    String Put(PutLinkData in) {

        int id; // id Ссылки
        String code; // Короткий код

        Statement statement;
        PreparedStatement preparedStatement;
        ResultSet resultSet;

        if (!in.getShortLink().equals("NULL")) { // Если есть желаемый короткий код

            code = in.getShortLink(); // Получаем его
            Integer answer = CheckAvailability(code); // И проверяем его наличие в БД

            if (answer == -1) { // Если код невалидный то отвечаем ошибкой
                // Пишем лог
                LogEvent l = new LogEvent(in);
                l.setClassName("LSH.DBConnect.Put");
                l.setType("InvalidCode");
                l.setMessage("answer = -1");
                Log.instance.WriteEvent(l);

                return errorCode + "<br>Invalid code!";
            } else if (answer == -2) { // Если занят тоже
                // Пишем лог
                LogEvent l = new LogEvent(in);
                l.setClassName("LSH.DBConnect.Put");
                l.setType("CodeError");
                l.setMessage("MemoUnavailable");
                Log.instance.WriteEvent(l);

                return errorCode + "<br>Unfortunately, your memo is not available";
            } else { // Иначе берем его как новый id
                id = answer;
            }

        } else {

            try { // Получаем новый id из базы
                // Создали соедниение и вызвали функцию генерации нового id
                statement = connection.createStatement();
                resultSet = statement.executeQuery("SELECT get_next_id()");

                // Получили его
                resultSet.next();
                id = resultSet.getInt(1);

                // И закрыли соединение
                resultSet.close();
                statement.close();
            } catch (SQLException e) { // Отловили ошибки
                e.printStackTrace();

                WritePutLog(in, e);// Пишем лог

                return errorCode + "<br>SQL Error!";
            }

        }


        // Логгирование пользователей
        String userLogin = in.getUserLogin(); // Логин
        Integer userID = 0; // id этого логина


        if (userLogin != null) { // если есть логин

            try { // Пошли в БД за id
                PreparedStatement st = connection.prepareStatement("SELECT id FROM users WHERE login = ?", ResultSet.TYPE_SCROLL_INSENSITIVE);
                st.setString(1, userLogin);
                ResultSet rs = st.executeQuery();

                // / Получаем ответ
                rs.next();
                userID = rs.getInt("id");

            } catch (SQLException e) {
                if (e.getSQLState().equals("24000")) { // Пустой ответ - нет такого логина - вернули ошибку

                    LogEvent l = new LogEvent(in);
                    l.setClassName("LSH.DBConnect.Put");
                    l.setType("Login not found");
                    l.setMessage("Login = " + userLogin);
                    Log.instance.WriteEvent(l);

                    return errorCode + "<br>Incorrect user!";
                }

                // Иначе это просто ошибка sql
                System.out.println("Connection Failed! Check output console");
                e.printStackTrace();

                // Пишем лог
                LogEvent l = new LogEvent(in);
                l.setClassName("LSH.DBConnect.Put");
                l.setType("SQLException");
                l.setMessage(e.getMessage());
                Log.instance.WriteEvent(l);

                return errorCode + "<br>SQL Error!";

            }
        }


        code = Shortner.GetShort(id); // Сокращаем id в код

        // Приводим дату к виду Timestamp
        String t = in.getTtl();
        Timestamp date;
        switch (t) {
            case "1 hour":
                date = new Timestamp(System.currentTimeMillis() + 3600000L ); // ТК 1 час - это 3600000  миллисекунд
                break;
            case "12 hour":
                date = new Timestamp(System.currentTimeMillis() + 43200000L ); // Аналогично
                break;
            case "1 day":
                date = new Timestamp(System.currentTimeMillis() + 86400000L );
                break;
            case "1 week":
                date = new Timestamp(System.currentTimeMillis() + 604800000L );
                break;
            case "1 month":
                date = new Timestamp(System.currentTimeMillis() + 2678400000L ); // Это, конечно, не месяц, а 31 день. Но нам огромная точность не нужна
                break;
            case "Unlimited":
                date = new Timestamp(System.currentTimeMillis() + 3155760000000L); // Это 100 лет. Куда уж больше?
                // LONG_MAX не влезает
                //date = new Timestamp(Timestamp.parse("infinity")); // Есть такой вариант, но какая разница?
                break;
            default:
                date = new Timestamp(System.currentTimeMillis()); // По дефолту будем делать ссылку не валидной
        }


        try { // Пишем в базу

            // Создали соединение
            preparedStatement = connection.prepareStatement(
                    "INSERT INTO short(user_id, link, expired_date, max_count, current_count, ip, user_agent, password, owner) VALUES (?, ?, ?, ?, ?, ?::cidr, ?, ?, ?)"
            );
            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, in.getOriginalLink());
            preparedStatement.setTimestamp(3, date);
            preparedStatement.setInt(4, in.getMaxVisits());
            preparedStatement.setInt(5, 0);
            preparedStatement.setString(6, in.getIp());
            preparedStatement.setString(7, in.getBrowser());
            preparedStatement.setString(8, in.getPassword());
            preparedStatement.setInt(9, userID);

            // Выолнили вставку и закрыли соединение
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException e) { // Отловили ошибки
            e.printStackTrace();

            WritePutLog(in, e);// Пишем лог

            return errorCode + "<br>SQL Error!";
        }



        // Пишем лог
        LogEvent l = new LogEvent(in);
        l.setClassName("LSH.DBConnect.Put");
        l.setType("Success");
        l.setMessage("Return code:" + code);
        Log.instance.WriteEvent(l);

        return code; // И возращаем саму ссылки
    }

    /**
     * Метод, который возращает оригинальную ссылку по коду
     * @param in данные об переходе
     * @return оригинальная ссылка или сообщение об ошибке
     */
    ReturnLinkData Get (GetLinkData in) {

        String code = in.getCode(); // Получили код

        int id = Shortner.GetID(code); // Попытались код преобразовать к id

        if (id == -1 || code.equals("ERROR")) { // Если ошибка то вернули

            WriteGetLog(in); // Пишем лог

            return new ReturnLinkData("<br>Error code!");
        }

        ResultSet resultSet;
        PreparedStatement preparedStatement;

        try { // Проверили, что этот id вообще есть

            // Инвалидируем ссылки
            preparedStatement = connection.prepareStatement("SELECT invalidate()");
            resultSet = preparedStatement.executeQuery();

            resultSet.close();


            // Создали и выполнили запрос
            preparedStatement = connection.prepareStatement("SELECT valid FROM status WHERE user_id = ?");
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();

            // Получили ответ
            resultSet.next();
            boolean t = resultSet.getBoolean(1);

            if (!t) { // Если этот id false = свободен, то выдаем ошибку
                // Пишем в лог
                WriteGetLog(in);

                return new ReturnLinkData("<br>Invalid code!");
            }

            // Закрыли соединение
            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) { // Вывели ошибки
            if (e.getSQLState().equals("24000")) { // Пустой ответ - нет такого кода - еще не использовался

                // Пишем в лог
                WriteGetLog(in);

                return new ReturnLinkData("<br>Invalid code!");
            }
            e.printStackTrace();

            // Пишем в лог
            LogEvent l = new LogEvent(in);
            l.setClassName("LSH.DBConnect.Get");
            l.setType("SQLException");
            l.setMessage(e.getMessage());
            Log.instance.WriteEvent(l);

            return new ReturnLinkData("<br>SQL Error!");
        }


        Integer tableID; // id в таблице для foreign key в аналитике
        Integer curCount; // Текущее кол-во переходов
        String link; // Сам линк
        String password; // Хэш от пароля
        try { // Получили оригинальную ссылку

            //connection.setAutoCommit(false); // Запретили автоматический коммит

            preparedStatement = connection.prepareStatement("SELECT id, link, password, current_count FROM short WHERE user_id = ? ORDER BY user_id DESC LIMIT 1");
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();

            resultSet.next();
            link = resultSet.getString("link");
            tableID = resultSet.getInt("id");
            curCount = resultSet.getInt("current_count");
            password = resultSet.getString("password");

            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();

            WriteGetELog(in, e);// Пишем в лог

            return new ReturnLinkData("<br>SQL Error!");
        }

        try { // Обновили ко-во переходов
            preparedStatement = connection.prepareStatement("UPDATE short SET current_count = ? WHERE id = ?");
            preparedStatement.setInt(1, curCount + 1);
            preparedStatement.setInt(2, tableID);
            preparedStatement.execute();
            preparedStatement.close();

            //connection.commit(); // Закомитили изменений

        } catch (SQLException e) {
            e.printStackTrace();

            WriteGetELog(in, e);// Пишем в лог

            return new ReturnLinkData("<br>SQL Error!");
        }

        try { // Запись аналитики

            // Вставили данные в таблицу аналитики
            preparedStatement = connection.prepareStatement("INSERT INTO analitics (short_id, visit_time, ip, user_agent) VALUES (?, ?, ?::cidr, ?)");
            preparedStatement.setInt(1, tableID);
            preparedStatement.setTimestamp(2, new Timestamp( System.currentTimeMillis() ) );
            preparedStatement.setString(3, in.getIp());
            preparedStatement.setString(4, in.getBrowser());
            preparedStatement.execute();

            preparedStatement.close(); // Закрыли соединение
        } catch (SQLException e) { // Вывели ошибки
            e.printStackTrace();

            WriteGetELog(in, e);// Пишем в лог

            return new ReturnLinkData("<br>SQL Error!");
        }

        // Пишем лог
        LogEvent l = new LogEvent(in);
        l.setClassName("LSH.DBConnect.Get");
        l.setType("Success");
        l.setMessage("Return link");
        Log.instance.WriteEvent(l);

        return new ReturnLinkData(link, password); // Вернули оригинальную ссылку для редиректа
    }

    /**
     * Функция входа пользователя
     * @param userLogin логин
     * @param userPassword пароль
     * @return true если вошел, false если ошибка
     */
    String Login(String userLogin, String userPassword) {

        // Идем в БД и проверяем на соответствие пользователя
        try {
            // Создаем запрос и выполняем его
            PreparedStatement st = connection.prepareStatement("SELECT id, password FROM users WHERE login = ?", ResultSet.TYPE_SCROLL_INSENSITIVE);
            st.setString(1, userLogin);
            ResultSet rs = st.executeQuery();

            // Получаем ответ
            rs.next();
            String pass = rs.getString("password");

            // Закрываем
            rs.close();
            st.close();

            if (pass.equals(userPassword)) { // Если пользователь ввел верный логин и пароль, то запоминаем его id
                return "OK";
            } else { // Если пароль не подошел, то возращаем ошибку
                // Пишем лог
                LogEvent l = new LogEvent();
                l.setClassName("LSH.DBConnect.Login");
                l.setType("Login");
                l.setMessage("Login = " + userLogin + "; Password = " + userPassword);
                Log.instance.WriteEvent(l);

                return errorCode + "<br>Incorrect user or password!";
            }

        } catch (SQLException e) { // Ловим ошибки

            if (e.getSQLState().equals("24000")) { // Пустой ответ - нет такого логина - создаем нового пользователя

                try {
                    // Создаем пользователя
                    PreparedStatement st = connection.prepareStatement("INSERT INTO users(login, password) VALUES (?, ?)", ResultSet.TYPE_SCROLL_INSENSITIVE);
                    st.setString(1, userLogin);
                    st.setString(2, userPassword);
                    st.execute();
                    st.close();

                    return "OK";

                } catch (SQLException ee) { // Ловим ошибки

                    System.out.println("Connection Failed! Check output console");
                    ee.printStackTrace();

                    // Пишем лог
                    LogEvent l = new LogEvent();
                    l.setClassName("LSH.DBConnect.Login.CreateNew");
                    l.setType("SQLException");
                    l.setMessage(ee.getMessage());
                    Log.instance.WriteEvent(l);
                    return errorCode + "<br>SQL Error!";

                }


            } else { // Иначе это обычная ошибка

                System.out.println("Connection Failed! Check output console");
                e.printStackTrace();

                // Пишем лог
                LogEvent l = new LogEvent();
                l.setClassName("LSH.DBConnect.Login");
                l.setType("SQLException");
                l.setMessage(e.getMessage());
                Log.instance.WriteEvent(l);

                return errorCode + "<br>SQL Error!";
            }
        }

    }


    // Функции для записи в лог - вынес, тк надоела подсветка в IDEA о дублировании кода

    /**
     * Функция, пишушья код ошибки в лог.
     * Вынес, тк используеться больше 2 раз
     * @param in данные
     * @param e exception
     */
    private void WritePutLog (PutLinkData in, SQLException e) {
        LogEvent l = new LogEvent(in);
        l.setClassName("LSH.DBConnect.Put");
        l.setType("SQLException");
        l.setMessage(e.getMessage());
        Log.instance.WriteEvent(l);
    }

    /**
     * Функция, пишушья код ошибки в лог.
     * Вынес, тк используеться больше 2 раз
     * @param in данные
     */
    private void WriteGetLog (GetLinkData in) {
        LogEvent l = new LogEvent(in);
        l.setClassName("LSH.DBConnect.Get");
        l.setType("CodeError");
        l.setMessage("Invalid code!");
        Log.instance.WriteEvent(l);
    }

    /**
     * Функция, пишушая код ошибки в лог.
     * Вынес, тк используеться больше 3 раз
     * @param in данные
     * @param e exception
     */
    private void WriteGetELog (GetLinkData in, SQLException e) {
        LogEvent l = new LogEvent(in);
        l.setClassName("LSH.DBConnect.Get");
        l.setType("SQLException");
        l.setMessage(e.getMessage());
        Log.instance.WriteEvent(l);
    }

}
