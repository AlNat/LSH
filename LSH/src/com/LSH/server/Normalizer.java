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
    private static String stopSymbols = "\'\"\n\t\\&@:;\\''"; // TODO Добавить
    // TODO Разобраться, почему в одной функции он работает, а в другой - нет

    /**
     * Конструктор, заполняющий стоп-слова
     */
    private Normalizer () {
        //А это точно надо? Нужно ли обрабатывать такую ситуацию? Или достаточно стоп-сиволов - кавычек и тд. Плюс проверка на пробелы
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

        if (in.length() < 3 || in.trim().isEmpty()) { // Если пустой запрос
            return errorCode;
        }

        if (isContainsStopSymbols(in)) { // Если есть стоп символы
            return errorCode;
        }

        if (in.contains(" ")) { // Если есть пробелы
            return errorCode;
        }

        /*
        if (stoplist.contains(in.toUpperCase())) { // Если есть стоп-слова
            return errorCode;
        }

        if (isContainsStopWords(in)) { // Если есть стоп-слова
            return errorCode;
        }*/


        String[] protocol = {"ftp://", "http://", "https://"}; // Протоколы

        if (in.startsWith(protocol[0]) ||
            in.startsWith(protocol[1]) ||
            in.startsWith(protocol[2])
            ) { // Если ссылка начинаеться с протоколов, то вернули ее
            return in;
        } else if (in.startsWith("www.")) { // Если она начинаеться с www
            return "https://" + in; // То добавили к ней https://
        } else { // Если это просто адрес сайт
            return "https://www." + in; // То привели к виду https://www.site etc...
        }
    }

    /**
     * Метод принимающий короткие ссылкы и возращающий их короткий код
     * Зависит от @see siteLink
     * @param in ссылка
     * @return Короткий код
     */
    public static String ShortNormalize (String in) {

        if (in.length() < siteLink.substring(7).length() || in.trim().isEmpty()) { // Если передали пустую строку
            return errorCode;
        }

        if (isContainsStopSymbols(in)) { // Если есть стопсимволы
            return errorCode;
        }

        if (in.contains(" ")) { // Если есть пробелы
            return errorCode;
        }

        /*
        if (stoplist.contains(in.toUpperCase())) { // Если есть стоп-слова
            return errorCode;
        }

        if (isContainsStopWords(in)) { // Если есть стоп-слова
            return errorCode;
        }*/

        if (in.startsWith(siteLink)) { // Если ссылка полная - то обрезали ее и вернули код
            return in.substring(siteLink.length());
        } else if (in.startsWith(siteLink.substring(7))) { // Если почти полная - без http, то обрезали
            // Тк http:// - 7 символов - обрезали и получили -> www.site.com/# - их режем и вернули коротий код
            return in.substring(14);
        } else { // Иначе это просто адрес сайта, поэтому обрезали его полностью
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


    private static boolean isContainsStopWords (String in) {
        ArrayList<String> s = new ArrayList<>();
        s.add("CREATE ");
        s.add("UPDATE ");
        s.add("DELETE ");
        s.add("DROP ");
        s.add("TRUNK ");

        s.add("NULL");

        boolean t = s.contains(in.toUpperCase());

        return t;
    }

}
