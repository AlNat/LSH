package com.LSH.server;

import static com.LSH.server.LSHService.errorCode;

/**
 * Created by @author AlNat on 14.09.2016.
 * Licensed by Apache License, Version 2.0
 *
 * По сути класс перевод из одной системы исчисления (10-ичной)
 * в другую - мою. В ней набор символов - ALPHABET
 *
 * Из алфавита удалены: 'I', 'i', 'l', '1', 'O', 'o', '0',
 * По причинам их похожести и возможности спутать
 */
public final class Shortner {

    private static final String ALPHABET = "_abcdefghjkmnpqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ23456789-";
    private static final int BASE = ALPHABET.length();


    /**
     * Фунция принимает id записи в БД и возвращает его коротий код
     * Она считает все _ слева незначащими как 0100 = 100, так и __20 = 20
     * @param id целочисленное > 0
     * @return короткий цифробуквенный код или пусто если ошибка
     */
    public static String GetShort(long id) {

        if (id < 0) {
            return errorCode;
        }

        StringBuilder str = new StringBuilder();
        while (id > 0) {
            // Переводим число в код по разрядам
            Long t = id % BASE;
            str.insert(0, ALPHABET.charAt(t.intValue()));
            id /= BASE;
        }
        return str.toString();

    }

    /**
     * Функция преобразует код в id
     * @param code цифробуквенный код (короткая ссылка)
     * @return id записи в БД или -1 если ошибка
     */
    public static long GetID(String code) {

        long link = 0;
        for (int t = 0; t < code.length(); t++) { // Идем по разрядам

            char c = code.charAt(t);

            // Проверяем на то, что код валидный
            if (!ALPHABET.contains(Character.toString(c))) { // Если этого символа нет, то выходим с ошибкой
                return -1;
            }

            link = link * BASE + ALPHABET.indexOf(c);
        }

        return link;

    }

}
