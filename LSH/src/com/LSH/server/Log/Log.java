package com.LSH.server.Log;

/**
 * Created by @author AlNat on 06.12.2016.
 * Licensed by Apache License, Version 2.0
 */
public class Log {
    // TODO Везде, где код ошибки и просто возврат - писать данные в лог
    // TODO Формат

    public static final Log instance = new Log();

    String filename = "C:\\Users\\AlNat\\Source\\Studi\\Diplom\\log.txt"; // TODO брать его из окружения

    Log () {
        // TODO Init file write
    }


    void WriteEvent (LogEvent logEvent) {
        // TODO Write new line to file
    }

}
