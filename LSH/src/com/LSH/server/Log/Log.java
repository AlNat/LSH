package com.LSH.server.Log;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by @author AlNat on 06.12.2016.
 * Licensed by Apache License, Version 2.0
 */
public class Log {

    public static final Log instance = new Log();

    private String filename = "C:\\Users\\AlNat\\Source\\Studi\\Diplom\\log.txt"; // TODO брать его из окружения

    Log () {
        File file = new File(filename); // Создаем ссылку на файл

        if (!file.exists()) { // Если файл не существует
            try {
                file.createNewFile(); // То создадим его
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    void WriteEvent (LogEvent logEvent) {
        Path path = Paths.get(filename);
        try {
            Files.write(path, logEvent.Write().getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
