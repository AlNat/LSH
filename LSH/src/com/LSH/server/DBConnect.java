package com.LSH.server;

import com.LSH.client.Message;
import static com.LSH.server.LSHService.errorCode;


// TODO Сама имплементация
// TODO Instance класса и соединение с БД под своим акком
/**
 * Created by @author AlNat on 16.09.2016.
 * Licensed by Apache License, Version 2.0
 *
 * Класс соединения приложения с БД
 */
class DBConnect {

    //TODO instance pattern to jdbc

    /*

    String url = "jdbc:postgresql://localhost/test";
    Properties props = new Properties();
    props.setProperty("user","fred");
    props.setProperty("password","secret");
    props.setProperty("ssl","true");
    Connection conn = DriverManager.getConnection(url, props);

     */

    /**
     * Функция проверки - занят ли этот код
     * @param code мнемноничесая ссылки
     * @return -2 если занят. -1 при ошибке кода. id от кода в другом случае
     */
    private static Integer CheckAvilability (String code) {
        // TODO Проверить занятости этого id
        Integer id = Shortner.GetID(code);

        if (id == -1) {
            return -1;
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
    static String Put(Message in) {

        int id;
        String code;

        if (!in.getShortLink().equals("NULL")) {
            code = in.getShortLink();
            Integer answer = CheckAvilability(code);
            if (answer == -1) {
                return errorCode + "<br>Invalid code!";
            } else if (answer == -2) {
                return errorCode + "<br>Unfortunately, your memo is not available";
            } else {
                id = answer;
            }
        } else {
            id = 0; // Postgres - GET NEXT ID TODO Получить новый id из базы
            code = Shortner.GetShort(id); // Здесь проверка не нужна, ведь из базы гарантирован нормальный id

        }

        // TODO записать новую строчку в бд. Попутно отловив ошибки

        return code;
    }

    /**
     * Метод, который возращает оригинальную ссылку по коду
     * @param code короткая ссылка
     * @return оригинальная ссылка или сообщение об ошибке
     */
    static String Get (String code) {

        code = Normalizer.ShortNormalize(code);
        String answer = "";
        int id = Shortner.GetID(code);

        if (id == -1 || code.equals("ERROR")) {
            return errorCode + "<br>Invalid code!";
        }

        // TODO Пойти по этому id в БД и получить оттуда строку, проверить что она валидная и ее можно отдавать обратно -> Отдельная таблица valid.
        // TODO После чего взять оттуда ссылку и вернуть ее. И записать данные о том, кто ходил за ссылкой

        // answer = ;

        return answer;
    }

}
