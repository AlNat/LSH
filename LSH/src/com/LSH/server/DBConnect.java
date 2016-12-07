package com.LSH.server;

import com.LSH.client.GetLinkData;
import com.LSH.client.PutLinkData;
import static com.LSH.server.LSHService.errorCode;

import java.sql.*;
import java.util.Properties;

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

        // TODO Подумать, как брать эти данные из окружения
        String url = "jdbc:postgresql://localhost/LSH";
        String login = "LSH";
        String password = "LSH";
        Properties props;

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

        if (id == -1) { // Если код ошибочный то вернули ошибку -1
            return -1;
        }

        try {
            // Создаем запрос и выполняем его
            PreparedStatement st = connection.prepareStatement("SELECT valid FROM status WHERE user_id = ? ORDER BY user_id DESC LIMIT 1", ResultSet.TYPE_SCROLL_INSENSITIVE);
            st.setInt(1, id);
            ResultSet rs = st.executeQuery();

            // Получаем ответ
            rs.next();
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
                resultSet = statement.executeQuery("SELECT get_next_id()");

                // Получили его
                resultSet.next();
                id = resultSet.getInt(1);

                // И закрыли соединение
                resultSet.close();
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
                date = new Timestamp(Long.MAX_VALUE); // ТК это максимально возможное время в Java
                break;
                //date = new Timestamp(Timestamp.parse("infinity")); // Есть такой вариант, но какая разница?
            default:
                date = new Timestamp(System.currentTimeMillis()); // По дефолту будем делать ссылку не валидной
        }


        try { // Пишем в базу
            // Создали соединение
            preparedStatement = connection.prepareStatement("INSERT INTO short(user_id, link, expired_date, max_count, current_count) VALUES (?, ?, ?, ?, ?)");
            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, in.getOriginalLink());
            preparedStatement.setTimestamp(3, date);
            preparedStatement.setInt(4, in.getMaxVisits());
            preparedStatement.setInt(5, 0);


            // Выолнили вставку и закрыли соединение
            boolean tt = preparedStatement.execute();
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
     * @param getLinkData данные об переходе
     * @return оригинальная ссылка или сообщение об ошибке
     */
    String Get (GetLinkData getLinkData) {

        String code = getLinkData.getCode(); // Получили код

        int id = Shortner.GetID(code); // Попытались код преобразовать к id

        if (id == -1 || code.equals("ERROR")) { // Если ошибка то вернули
            return errorCode + "<br>Invalid code!";
        }

        ResultSet resultSet;
        PreparedStatement preparedStatement;

        try { // Проверили, что этот id вообще есть

            // Создали и выполнили запрос
            preparedStatement = connection.prepareStatement("SELECT valid FROM status WHERE user_id = ? ORDER BY user_id DESC LIMIT 1");
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();

            // Получили ответ
            resultSet.next();
            boolean t = resultSet.getBoolean(1);

            if (!t) { // Если этот id false = свободен, то выдаем ошибку
                return errorCode + "<br>Invalid code!";
            }

            // Закрыли соединение
            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) { // Вывели ошибки
            e.printStackTrace();
            return errorCode + "<br>SQL Error!";
        }

        Integer tableID; // id в таблице для foregin key в аналитике
        Integer curCount; // Текущее кол-во переходов
        String answer; // Сам линк
        try { // Получили оригинальную ссылку

            preparedStatement = connection.prepareStatement("SELECT id, link, current_count FROM short WHERE user_id = ? ORDER BY user_id DESC LIMIT 1");
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();

            resultSet.next();
            answer = resultSet.getString("link");
            tableID = resultSet.getInt("id");
            curCount = resultSet.getInt("current_count");

            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return errorCode + "<br>SQL Error!";
        }

        try { // Обновили ко-во переходов
            preparedStatement = connection.prepareStatement("UPDATE short SET current_count = ? WHERE id = ?");
            preparedStatement.setInt(1, curCount + 1);
            preparedStatement.setInt(2, tableID);
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return errorCode + "<br>SQL Error!";
        }

        try { // Запись аналитики

            // Вставили данные в таблицу аналитики
            preparedStatement = connection.prepareStatement("INSERT INTO analitics (short_id, visit_time, ip, user_agent) VALUES (?, ?, ?::cidr, ?)");
            preparedStatement.setInt(1, tableID);
            preparedStatement.setTimestamp(2, new Timestamp( System.currentTimeMillis() ) );
            preparedStatement.setString(3, getLinkData.getIp());
            preparedStatement.setString(4, getLinkData.getBrowser());
            preparedStatement.execute();

            preparedStatement.close(); // Закрыли соединение
        } catch (SQLException e) { // Вывели ошибки
            e.printStackTrace();
            return errorCode + "<br>SQL Error!";
        }

        return answer; // Вернули оригинальную ссылку для редиректа
    }

}
