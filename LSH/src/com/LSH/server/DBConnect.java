package com.LSH.server;

import com.LSH.client.Data;
import com.LSH.client.Message;
import static com.LSH.server.LSHService.errorCode;

import java.sql.*;
import java.util.Properties;

// TODO Протестировать все

/**
 * Created by @author AlNat on 16.09.2016.
 * Licensed by Apache License, Version 2.0
 *
 * Класс соединения приложения с БД
 */
class DBConnect {

    static final DBConnect instance = new DBConnect(); // Реализация паттерна Singleton

    // TODO Подумать, как брать эти данные из окружения
    private String url = "jdbc:postgresql://localhost/LSH";
    private String login = "LSH";
    private String password = "LSH";
    private Connection connection;
    private Properties props;


    private DBConnect () { // Конструктор
        try { // Попытались установить соединение с БД
            props = new Properties();
            props.setProperty("user", login);
            props.setProperty("password", password);
            props.setProperty("ssl", "false");

            connection = DriverManager.getConnection(url, props);
        } catch (SQLException e) { // Ловим ошибку
            e.printStackTrace();
        }
    }

    /**
     * Функция проверки - занят ли этот код
     * @param code мнемноничесая ссылки
     * @return -2 если занят. -1 при ошибке кода. id от кода в другом случае
     */
    private Integer CheckAvailability (String code) {

        Integer id = Shortner.GetID(code); // Получили id по коду

        if (id == -1) { // Если код ошибочный то вернули его
            return id;
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
            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
            return -1; // И говорим про ошибку
        }

    }

    /**
     * Метод, который кладет в БД новую ссылки и
     * @param in сообщение с данными ссылки
     * @return Код ошибки или короткую ссылку
     */
    String Put(Message in) {

        int id; // id Ссылки
        String code; // Короткий код

        Statement statement;
        PreparedStatement preparedStatement;
        ResultSet rs;

        if (!in.getShortLink().equals("NULL")) { // Если есть желаемый короткий код

            code = in.getShortLink(); // Получаем его
            Integer answer = CheckAvailability(code); // И проверяем его наличие в БД

            if (answer == -1) { // Если код невалидный то отвечаем
                return errorCode + "<br>Invalid code!";
            } else if (answer == -2) { // Если занят тоже
                return errorCode + "<br>Unfortunately, your memo is not available";
            } else { // Иначе берем его как новый id
                id = answer;
            }

        } else {

            try { // Получаем новый id из базы
                // Создали соедниение и вызвали функцию генерации нового id
                statement = connection.createStatement();
                rs = statement.executeQuery("SELECT get_next_id()");

                // Получили его
                rs.next();
                id = rs.getInt(1);

                // И закрыли соединение
                rs.close();
                statement.close();
            } catch (SQLException e) { // Отловили ошибки
                e.printStackTrace();
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
            case "12 hour":
                date = new Timestamp(System.currentTimeMillis() + 43200000L ); // Аналогично
            case "1 day":
                date = new Timestamp(System.currentTimeMillis() + 86400000L );
            case "1 week":
                date = new Timestamp(System.currentTimeMillis() + 604800000L );
            case "1 month":
                date = new Timestamp(System.currentTimeMillis() + 2678400000L ); // Это, конечно, не месяц, а 31 день. Но нам огромная точность не нужна
            case "Unlimited":
                date = new Timestamp(Long.MAX_VALUE); // ТК это максимально возможное время в Java
                // TODO проверить, можно ли в PostgreSQL установить время INFINITY
            default:
                date = new Timestamp(System.currentTimeMillis()); // По дефолту будем делать ссылку не валидной
        }


        try { // Пишем в базу
            // Создали соединение
            preparedStatement = connection.prepareStatement("INSERT INTO short(user_id, link, expired_date, max_count) VALUES (?, ?, ?, ?)");
            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, in.getOriginalLink());
            preparedStatement.setTimestamp(3, date);
            preparedStatement.setInt(4, in.getMaxVisits());

            // Выолнили вставку и закрыли соединение
            rs = preparedStatement.executeQuery();
            rs.close();
            preparedStatement.close();
        } catch (SQLException e) { // Отловили ошибки
            e.printStackTrace();
            return errorCode + "<br>SQL Error!";
        }

        //TODO Писать в аналитку про создание ссылки

        return code; // И возращаем саму ссылки
    }

    /**
     * Метод, который возращает оригинальную ссылку по коду
     * @param data данные об переходе
     * @return оригинальная ссылка или сообщение об ошибке
     */
    String Get (Data data) {
        // TODO Комментарии

        String code = data.getCode();

        int id = Shortner.GetID(code); // Попытались код преобразовать к id

        if (id == -1 || code.equals("ERROR")) { // Если ошибка то вернули
            return errorCode + "<br>Invalid code!";
        }

        ResultSet rs;
        PreparedStatement preparedStatement;
        Integer curID;

        try { // Пойти по этому id в БД и получить оттуда строку, проверить что она валидная(отдает по этому id true или false) и ее можно отдавать обратно.

            preparedStatement = connection.prepareStatement("SELECT user_id FROM status ORDER BY user_id DESC LIMIT 1");
            rs = preparedStatement.executeQuery();

            rs.next();
            curID = rs.getInt(1);

            rs.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return errorCode + "<br>SQL Error!";
        }

        if ( id > curID ) {
            return errorCode + "<br>Invalid link!";
        }


        String answer;
        try { // Получили оригинальную ссылку

            preparedStatement = connection.prepareStatement("SELECT link FROM short WHERE user_id = ? ORDER BY user_id DESC LIMIT 1");
            preparedStatement.setInt(1, id);
            rs = preparedStatement.executeQuery();

            answer = rs.getString(1);

            rs.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return errorCode + "<br>SQL Error!";
        }

        try { // Запись аналитики - писать его в БД

            preparedStatement = connection.prepareStatement("INSERT INTO analitics (short_id, visit_time, ip, user_agent) VALUES (?, ?, ?, ?)");
            preparedStatement.setInt(1, id);
            preparedStatement.setTimestamp( 2, new Timestamp( System.currentTimeMillis() ) );
            preparedStatement.setString(3, data.getIp());
            preparedStatement.setString(4, data.getBrowser());
            rs = preparedStatement.executeQuery();
            rs.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return errorCode + "<br>SQL Error!";
        }

        return answer; // Вернули оригинальную ссылку для редиректа
    }

}
