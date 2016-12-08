package com.LSH.server;

/**
 * Created by @author AlNat on 07.12.2016.
 * Licensed by Apache License, Version 2.0
 */
class Config {

    // TODO комментарии

    private String filename = "C:\\Users\\AlNat\\Source\\Studi\\Diplom\\config.xml"; // TODO искать эти данные рядом с сервером

    // Инстанс - паттерн Синглтон
    public static final Config instance = new Config();

    // Геттеры
    public String getSiteLink() {
        return SiteLink;
    }

    public String getErrorCode() {
        return ErrorCode;
    }

    public String getLogin() {
        return Login;
    }

    public String getPassword() {
        return Password;
    }

    public String getURL() {
        return URL;
    }

    public String getLogFile() {
        return LogFile;
    }


    // Данные
    private String SiteLink;
    private String ErrorCode;
    private String Login;
    private String Password;
    private String URL;
    private String LogFile;

    private Config () {
        ReadConfig();
    }

    public void ReadConfig () {
        // TODO Читатать их из файла
        File file = new File(filename)
    }

}
