package com.LSH.server;

/**
 * Created by @author AlNat on 14.09.2016.
 * Licensed by Apache License, Version 2.0
 *
 *
 * ShortURL.GetShort() takes an ID and turns it into a short string
 * ShortURL.GetURL() takes a short string and turns it into an ID
 *
 * Features:
 * + large alphabet (51 chars)
 * + removed 'a', 'e', 'i', 'o' and 'u'
 * + removed 'I', 'l', '1', 'O' and '0'
 *
 * Example output:
 * 123456789 <=> pgK8p
 */
public class Shortner {

    private static final String ALPHABET = "23456789bcdfghjkmnpqrstvwxyzBCDFGHJKLMNPQRSTVWXYZ-_";
    private static final int BASE = ALPHABET.length();

    /**
     * Фунция принимает id записи в БД и возвращает его коротий код
     * @param id целочисленное > 0
     * @return короткий цифробуквенный код или пусто если ошибка
     */
    public static String GetShort(int id) {

        StringBuilder str = new StringBuilder();
        while (id > 0) {
            str.insert(0, ALPHABET.charAt(id % BASE));
            id = id / BASE;
        }
        return str.toString();

    }

    /**
     * Функция преобразует код в id
     * @param code цифробуквенный код (короткая ссылка)
     * @return id записи в БД
     */
    public static int GetURL(String code) {

        int link = 0;
        for (int i = 0; i < code.length(); i++) {
            link = link * BASE + ALPHABET.indexOf(code.charAt(i));
        }

        return link;

    }

}
