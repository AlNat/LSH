package com.LSH.server;

import java.util.ArrayList;

/**
 * Created by @author AlNat on 21.10.2016.
 * Licensed by Apache License, Version 2.0
 */
// TODO Комментари + JavaDoc
// TODO Сама имплементация
public final class Normalizer {

    private static ArrayList<String> stoplist = new ArrayList<>();

    private Normalizer () {
        // TODO Доделать стоп листы
        stoplist.add(" CREATE ");
        stoplist.add(" UPDATE ");
        stoplist.add(" DELETE ");
        stoplist.add(" TRUNK ");

        stoplist.add(" NONE ");
    }

    public static String Normalize (String in) {

        String s = "";

        if (stoplist.contains(in.toUpperCase())) {
            s = "ERROR";
        } else {
            //s = in.startsWith("http:\\\\www.");
        }

        System.out.println(s);

        return s;
    }

}
