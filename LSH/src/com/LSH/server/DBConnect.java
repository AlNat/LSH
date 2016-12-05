package com.LSH.server;

import com.LSH.client.Message;
import static com.LSH.server.LSHService.errorCode;
import java.sql.*;

// TODO комментарии
/**
 * Created by @author AlNat on 16.09.2016.
 * Licensed by Apache License, Version 2.0
 *
 * Класс соединения приложения с БД
 */
class DBConnect {

    static final DBConnect instance = new DBConnect(); // Реализация паттерна Singleton

    // TODO Подумать, как брать эти данные из окружения
    private static String url = "jdbc:postgresql://localhost/LSH";
    private static String login = "LSH";
    private static String password = "LSH";
    Connection connection = null;


    private DBConnect () { // Конструктор
        try { // Попытались установить соединение с БД
            connection = DriverManager.getConnection(url, login, password);
        } catch (SQLException e) {
            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
        }
    }

    /**
     * Функция проверки - занят ли этот код
     * @param code мнемноничесая ссылки
     * @return -2 если занят. -1 при ошибке кода. id от кода в другом случае
     */
    private Integer CheckAvilability (String code) {
        Integer id = Shortner.GetID(code);

        if (id == -1) {
            return id;
        }

        try {
            PreparedStatement st = connection.prepareStatement("SELECT status FROM status WHERE user_id = ?");
            st.setInt(1, id);
            ResultSet rs = st.executeQuery();

            String answer = rs.getString(1);

            rs.close();
            st.close();

            switch (answer) {
                case "false":
                    return -2;
                case "true":
                case "":
                    return id;
                default:
                    return -1;
            }

        } catch (SQLException e) {
            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
        }

        return -2;

    }

    /**
     * Метод, который кладет в БД новую ссылки и
     * @param in сообщение с данными ссылки
     * @return Код ошибки или короткую ссылку
     */
    String Put(Message in) {

        int id = 0; // id Ссылки
        String code; // Короткий код

        Statement st = null;
        ResultSet rs;
        PreparedStatement st2 = null;

        if (!in.getShortLink().equals("NULL")) { // Если есть желаемый короткий код
            code = in.getShortLink(); // Получаем его
            Integer answer = CheckAvilability(code); // И проверяем его наличие в БД
            if (answer == -1) { // Если код невалидный то отвечаем
                return errorCode + "<br>Invalid code!";
            } else if (answer == -2) { // Если занят тоже
                return errorCode + "<br>Unfortunately, your memo is not available";
            } else { // Иначе берем его как новый id
                id = answer;
            }
        } else {

            try { // Получаем новый id из базы
                st = connection.createStatement();
                rs = st.executeQuery("SELECT get_next_id()");

                id = rs.getInt(1);
                rs.close();
                st.close();
            } catch (SQLException e) {
                e.printStackTrace();
                return errorCode + "<br>SQL Error!";
            }

        }

        // TODO parsing expireddate
        String date = "";

        code = Shortner.GetShort(id); // Сокращаем его в код

        try { // Пишем в базу

            st2 = connection.prepareStatement("INSERT INTO short(user_id, link, expired_date, max_count) VALUES (?, ?, ?, ?)");
            st2.setInt(1, id);
            st2.setString(2, in.getOriginalLink());
            st2.setString(3, date);
            st2.setInt(4, in.getMaxVisits());

            rs = st2.executeQuery();
            rs.close();
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return errorCode + "<br>SQL Error!";
        }

        return code; // И возращаем саму ссылки
    }

    /**
     * Метод, который возращает оригинальную ссылку по коду
     * @param code короткая ссылка
     * @return оригинальная ссылка или сообщение об ошибке
     */
    String Get (String code) {
        // TODO Запись аналитики - новый класс, заполнять на клиенте и передавать сюда, а тут уже писать его в БД

        code = Normalizer.ShortNormalize(code); // Нормализуем код

        if (code.equals(errorCode)) { // Если это ошибка то вернули ее
            return errorCode + "<br>Invalid code!";
        }

        int id = Shortner.GetID(code); // Попытались код преобразовать к id

        if (id == -1 || code.equals("ERROR")) { // Если ошибка то вернули
            return errorCode + "<br>Invalid code!";
        }

        String answer = "";
        // TODO Пойти по этому id в БД и получить оттуда строку, проверить что она валидная(отдает по этому id true или false) и ее можно отдавать обратно.
        // TODO После чего взять оттуда ссылку и вернуть ее.

        // answer = ;

        return answer; // Вернули оригинальную ссылку для редиректа
    }

}
