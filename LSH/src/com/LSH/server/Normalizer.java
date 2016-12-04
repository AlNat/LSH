package com.LSH.server;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.LSH.server.LSHService.errorCode;
import static com.LSH.server.LSHService.siteLink;

/**
 * Created by @author AlNat on 21.10.2016.
 * Licensed by Apache License, Version 2.0
 *
 * Класс, реализующий нормализатор ссылок
 */
public final class Normalizer {

    private static ArrayList<String> stoplist = new ArrayList<>(); // Стопслова
    private static String stopSymbols = "\'\"\'\n\t\\&@:;'"; // TODO Доделать

    /**
     * Конструктор, заполняющий стоп-слова
     */
    private Normalizer () {
        // TODO Доделать стоп листы
        stoplist.add("CREATE ");
        stoplist.add("UPDATE ");
        stoplist.add("DELETE ");
        stoplist.add("TRUNK ");

        stoplist.add("NULL");
    }

    /**
     * Основной метод нормализации оригинальных ссылок
     * @param in строка с ссылкой
     * @return нормализованная ссылки или ошибка
     */
    public static String Normalize (String in) {

        if (stoplist.contains(in.toUpperCase())) {
            return errorCode;
        }

        if (in.length() < 3 || in.trim().isEmpty()) {
            return errorCode;
        }

        if (isContainsStopSymbols(in)) {
            return errorCode;
        }

        String[] protocol = {"ftp://", "http://", "https://"};

        if (in.startsWith(protocol[0]) ||
            in.startsWith(protocol[1]) ||
            in.startsWith(protocol[2])
            ) {
            return in;
        } else if (in.startsWith("www.")) {
            return "https://" + in;
        } else {
            return "https://www." + in;
        }
    }

    /**
     * Метод принимающий короткие ссылкы и возращающий их короткий код
     * Зависит от @see siteLink
     * @param in ссылка
     * @return Короткий код
     */
    public static String ShortNormalize (String in) {

        if (stoplist.contains(in.toUpperCase())) {
            return errorCode;
        }

        if (in.length() < 9 || in.trim().isEmpty()) {
            return errorCode;
        }

        if (isContainsStopSymbols(in)) {
            return errorCode;
        }


        if (in.startsWith(siteLink)) {
            return in.substring(siteLink.length());
        } else if (in.startsWith(siteLink.substring(7))) { // Тк http:// - 7 символов - обрезали и получили -> www.site.com/# - их режем и вернули коротий код
            return in.substring(14);
        } else { // site.com/#
            return in.substring(10);
        }

    }

    /**
     * Функция, проверяющая встречаються ли стоп-символы в строке
     * @param in входная строка
     * @return true если встречаються, false если нет
     */
    private static boolean isContainsStopSymbols (String in) {
        Pattern p = Pattern.compile(stopSymbols);
        Matcher m = p.matcher(in);
        return m.find();
    }

}
