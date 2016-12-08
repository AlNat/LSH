package com.LSH.server.Log;

import com.LSH.client.DataType.GetLinkData;
import com.LSH.client.DataType.PutLinkData;

import java.util.Date;

/**
 * Created by @author AlNat on 06.12.2016.
 * Licensed by Apache License, Version 2.0
 *
 * Класс события лога - 1 строка об проишествии
 */
public class LogEvent {

    // Данные
    private String message;
    private String type;
    private String className;
    private GetLinkData getLinkData = null;
    private PutLinkData putLinkData = null;

    // Сеттеры
    public void setMessage(String message) {
        this.message = message;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    // Конструкторы
    public LogEvent (GetLinkData getLinkData) {
        this.getLinkData = getLinkData;
    }

    public LogEvent (PutLinkData putLinkData) {
        this.putLinkData = putLinkData;
    }

    public LogEvent () {

    }

    /**
     * Функция для записи данных в лог
     * @return строку данных лога
     */
    String Write () {

        String date = new Date (System.currentTimeMillis() ).toString();
        String a = "[" + date + "] Event = " + type + "; ClassName = " + className + "; Message = " + message + "; ";

        if (getLinkData != null) {
            a += "LinkData:" + getLinkData.toLog() + ";";
        } else if (putLinkData != null) {
            a += "LinkData:" + putLinkData.toLog() + ";";
        }
        return a;
    }


}
