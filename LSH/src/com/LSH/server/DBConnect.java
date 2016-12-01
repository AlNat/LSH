package com.LSH.server;

import com.LSH.client.Message;

/**
 * Created by @author AlNat on 16.09.2016.
 * Licensed by Apache License, Version 2.0
 */


// TODO Комментари + JavaDoc
// TODO Сама имплементация
// TODO Instance класса и соединение с БД под своим акком
class DBConnect {

    static String CheckAvilability (String in) {
        return "";
    }

    static String Put(Message in) {

        int id;
        String code;

        if (!in.getShortLink().equals("NONE")) {
            code = in.getShortLink();
            String answer = CheckAvilability(code);
            if (answer.equals("NONE")) {
                return "Unfotenatly, your memo is zanyat"; // TODO НОРМАЛЬНО СДЕЛАТЬ!
            } else {
                id = Shortner.GetID(code);
            }
        } else {
            id = 0; // Postgres - GET NEXT ID TODO Получить новый id из базы
            code = Shortner.GetShort(id);
        }

        // TODO записать новую строчку в бд. Попутно отловив ошибки

        return code;
    }

    static String Get (String code) {

        int id = Shortner.GetID(code);

        // TODO Пойти по этому id в БД и получить оттуда строку, проверить что она валидная и ее можно отдавать обратно -> Отдельная таблица valid.
        // TODO После чего взять оттуда ссылку и вернуть ее. И записать данные о том, кто ходил за ссылкой

        return "";
    }

}
