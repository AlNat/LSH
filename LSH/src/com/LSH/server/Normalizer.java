package com.LSH.server;

import static com.LSH.server.LSHService.errorCode;
import static com.LSH.server.LSHService.siteLink;

/**
 * Created by @author AlNat on 21.10.2016.
 * Licensed by Apache License, Version 2.0
 *
 * Класс, реализующий нормализатор ссылок
 */
public final class Normalizer {

    private static String stopSymbols = "\n\t;'\'\"^{}[]<>|`";
    // См https://stackoverflow.com/questions/1547899/which-characters-make-a-url-invalid

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


        String[] protocol = {"ftp://", "http://", "https://"}; // Протоколы

        if (in.startsWith(protocol[0]) ||
            in.startsWith(protocol[1]) ||
            in.startsWith(protocol[2])
            ) { // Если ссылка начинаеться с протоколов, то вернули ее
            return in;
        } else if (in.startsWith("www.")) { // Если она начинаеться с www
            return "http://" + in; // То добавили к ней https://
        } else { // Если это просто адрес сайт
            return "http://www." + in; // То привели к виду https://www.site etc...
        }
    }

    /**
     * Метод принимающий короткие ссылкы и возращающий их короткий код
     * Зависит от @see siteLink
     * @param in ссылка
     * @return Короткий код
     */
    public static String ShortNormalize (String in) {

        if (in.length() < 3 || in.trim().isEmpty()) { // Если передали пустую строку
            return errorCode;
        }

        if (isContainsStopSymbols(in)) { // Если есть стопсимволы
            return errorCode;
        }

        if (in.contains(" ")) { // Если есть пробелы
            return errorCode;
        }

        if (in.startsWith(siteLink)) { // Если ссылка полная - то обрезали ее и вернули код
            return in.substring(siteLink.length());
        }

        // Обрезаем
        in = in.replace("http://", ""); // Убрали протокол
        in = in.replace("https://", ""); // Убрали протокол
        in = in.replace("www.", ""); // Убрали адрес
        in = in.replace(siteLink.substring(7), ""); // Убрали наш адрес без http

        return in; // Вернули код

    }

    /**
     * Функция, проверяющая встречаються ли стоп-символы в строке
     * @param in входная строка
     * @return true если встречаються, false если нет
     */
    private static boolean isContainsStopSymbols (String in) {

        char s[] = in.toCharArray(); // Разбиавем строку на символы
        for (char c: s) { // Идем по всем символам
            if (stopSymbols.contains(String.valueOf(c))) { // Если они встречаються
                return true; // То выдаем true
            }
        }
        return false; // Выдаем false если нет

        // Почем- то, это работало не всегда. Ну вот такие регулярки
        /*
        Pattern p = Pattern.compile(stopSymbols);
        Matcher m = p.matcher(in);
        return m.find();
        */
    }

}
