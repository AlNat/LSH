package com.LSH.client.DataType;

import java.io.Serializable;

/**
 * Created by @author AlNat on 05.12.2016.
 * Licensed by Apache License, Version 2.0
 *
 * Класс данных об пользователе, перешедшем по ссылке + сама ссылка
 */
public class GetLinkData implements Serializable {

    // Геттеры
    public String getCode() {
        return code;
    }

    public String getIp() {
        return ip;
    }

    public String getBrowser() {
        return browser;
    }

    public void setCode(String code) {
        this.code = code;
    }

    // Данные
    private String code; // Короткий код
    private String ip; // Ip перехода
    private String browser; // User-agent

    public GetLinkData() {

    }

    public GetLinkData(String code, String ip, String browser) {
        this.code = code;
        this.ip = ip;
        this.browser = browser;
    }

    public String toLog () {
        return "{code = " + code + "; ip = " + ip + "; browser = " + browser + "}";
    }

}
