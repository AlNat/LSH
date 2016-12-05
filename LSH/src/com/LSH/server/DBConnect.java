package com.LSH.server;

import com.LSH.client.Message;
import static com.LSH.server.LSHService.errorCode;
import java.sql.*;

// TODO Сама имплементация
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
        // TODO Проверить занятости этого id
        Integer id = Shortner.GetID(code);
        //Statment statment

        if (id == -1) {
            return -1;
        }

        try {
            //connection
        } catch (SQLException e) {
            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
        }

        /*
        if (id = )
            id = -2;
        */

        return id;
    }

    /**
     * Метод, который кладет в БД новую ссылки и
     * @param in сообщение с данными ссылки
     * @return Код ошибки или короткую ссылку
     */
    String Put(Message in) {

        int id; // id Ссылки
        String code; // Короткий код

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
            id = 0; // Получаем новый id из базы
            // Postgres - GET NEXT ID TODO Получить новый id из базы
            code = Shortner.GetShort(id); // Соращаем его в код
            // Здесь проверка не нужна, ведь из базы гарантирован нормальный id

        }

        // TODO записать новую строчку в бд. Попутно отловив ошибки

        return code; // И возращаем саму ссылки
    }

    /**
     * Метод, который возращает оригинальную ссылку по коду
     * @param code короткая ссылка
     * @return оригинальная ссылка или сообщение об ошибке
     */
    String Get (String code) {

        code = Normalizer.ShortNormalize(code); // Нормализуем код

        if (code.equals(errorCode)) { // Если это ошибка то вернули ее
            return errorCode + "<br>Invalid code!";
        }

        int id = Shortner.GetID(code); // Попытались код преобразовать к id

        if (id == -1 || code.equals("ERROR")) { // Если ошибка то вернули
            return errorCode + "<br>Invalid code!";
        }

        String answer = "";
        // TODO Пойти по этому id в БД и получить оттуда строку, проверить что она валидная и ее можно отдавать обратно -> Отдельная таблица valid.
        // TODO После чего взять оттуда ссылку и вернуть ее. И записать данные о том, кто ходил за ссылкой

        // answer = ;

        return answer; // Вернули оригинальную ссылку для редиректа
    }

}
