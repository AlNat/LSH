package com.LSH.server;

import java.util.ArrayList;

/**
 * Created by @author AlNat on 21.10.2016.
 * Licensed by Apache License, Version 2.0
 */
public class Normalizer {

    // TODO Подумать как это долно быть реализованно

    private static ArrayList<String> stoplist = new ArrayList<>();

    private Normalizer () {
        stoplist.add("CREATE");
        stoplist.add("UPDATE");
        stoplist.add("DELETE");
        stoplist.add("TRUNK");
    }

    public static String Normolize (String in) {

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
