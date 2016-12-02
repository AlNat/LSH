package com.LSH.server;

import java.util.ArrayList;

/**
 * Created by @author AlNat on 21.10.2016.
 * Licensed by Apache License, Version 2.0
 *
 * Класс, реализующий нормализатор ссылок - приведения их к виду
 * http://www.site.com/
 */
public final class Normalizer {

    private static ArrayList<String> stoplist = new ArrayList<>(); // Стопслова

    /**
     * Конструктор, заполняющий стоп-слова
     */
    private Normalizer () {
        // TODO Доделать стоп листы
        stoplist.add(" CREATE ");
        stoplist.add(" UPDATE ");
        stoplist.add(" DELETE ");
        stoplist.add(" TRUNK ");

        stoplist.add(" NONE ");
    }

    /**
     * Основной метод нормализации оригинальных ссылок
     * @param in строка с ссылкой
     * @return нормализованная ссылки или ошибка
     */
    public static String Normalize (String in) {

        String s = "";

        if (stoplist.contains(in.toUpperCase())) {
            s = "ERROR";
        } else {
            // TODO Приводить ссылу к виду hhtp://www.
            //s = in.startsWith("http:\\\\www.");
        }

        return s;
    }

    /**
     * Метод принимающий короткие ссылик и возращающий их короткий код
     * @param in ссылка
     * @return Короткий код
     */
    public static String ShortNormalize (String in) {

        String s = "";

        if (stoplist.contains(in.toUpperCase())) {
            s = "ERROR";
        } else {
            // TODO Приводить ссылу к виду hhtp://www.
            //s = in.startsWith("http:\\\\www.");
        }

        return s;

    }

}
