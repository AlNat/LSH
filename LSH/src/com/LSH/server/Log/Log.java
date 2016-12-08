package com.LSH.server.Log;

import com.LSH.server.Config.Config;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import java.util.LinkedList;

/**
 * Created by @author AlNat on 06.12.2016.
 * Licensed by Apache License, Version 2.0
 *
 * Класс лога
 */
public class Log {

    // Инстанс - паттерн Синглтон
    public static final Log instance = new Log();

    private String filename;

    /**
     * Конструктор, создает или открывает файл лога
     */
    private Log () {
        filename = Config.instance.getLogFile();

        File file = new File(filename); // Создаем ссылку на файл


        if (!file.exists()) { // Если файл не существует
            try {
                file.createNewFile(); // То создадим его
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Функция, записывающая в лог данные
     * @param logEvent данные на запись
     */
    public void WriteEvent (LogEvent logEvent) {
        Path path = Paths.get(filename);

        // Создали массив данных для записи
        LinkedList<String> text = new LinkedList<>();
        text.add(logEvent.Write());
        text.add("\n");

        // Пишем
        try {
            Files.write(path, text, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
