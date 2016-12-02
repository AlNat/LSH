package com.LSH.server;

import com.LSH.client.Message;

// TODO Сама имплементация
// TODO Instance класса и соединение с БД под своим акком
/**
 * Created by @author AlNat on 16.09.2016.
 * Licensed by Apache License, Version 2.0
 *
 * Класс соединения приложения с БД
 */
class DBConnect {

    /**
     * Функция проверки - занят ли этот код
     * @param code мнемноничесая ссылки
     * @return -1 если занят. id от кода в другом случае
     */
    private static Integer CheckAvilability (String code) {
        // TODO Проверить занятости этого id
        Integer id = Shortner.GetID(code);

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

        if (!in.getShortLink().equals("NONE")) {
            code = in.getShortLink();
            Integer answer = CheckAvilability(code);
            if (answer == -1) {
                return "Unfortunately, your memo is not available";
            } else {
                id = answer;
            }
        } else {
            id = 0; // Postgres - GET NEXT ID TODO Получить новый id из базы
            code = Shortner.GetShort(id);
        }

        // TODO записать новую строчку в бд. Попутно отловив ошибки

        return code;
    }

    /**
     * Метод, который возращает оригинальную ссылку по коду
     * @param code короткая ссылка
     * @return оригинальная ссылка
     */
    static String Get (String code) {

        code = Normalizer.ShortNormalize(code);
        String answer = "";

        if (code.equals("Error")) {
            answer = code;
        } else {
            int id = Shortner.GetID(code);

            // TODO Пойти по этому id в БД и получить оттуда строку, проверить что она валидная и ее можно отдавать обратно -> Отдельная таблица valid.
            // TODO После чего взять оттуда ссылку и вернуть ее. И записать данные о том, кто ходил за ссылкой

            // answer = ;
        }



        return answer;
    }

}
