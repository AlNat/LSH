package com.LSH.client.DataType;

import java.io.Serializable;

/**
 * Created by @author AlNat on 20.12.2016.
 * Licensed by Apache License, Version 2.0
 *
 * Класс обертка для данных на редирект
 */
public class ReturnLinkData implements Serializable {

    /* Геттеры и сеттеры */
    public String getOriginalLink() {
        return originalLink;
    }

    public void setOriginalLink(String originalLink) {
        this.originalLink = originalLink;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }


    /* Данные */
    private String originalLink; // Оригинальный линк
    private String password; // Пароль
    private String errorCode; // Код ошибки


    /* Конструкторы */
    public ReturnLinkData() {}

    public ReturnLinkData(String originalLink, String password) {
        this.originalLink = originalLink;
        this.password = password;
    }

    public ReturnLinkData(String errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public String toString () {
        return "{originalLink = " + originalLink +
                "; Password(MD5) = " + password +
                "; ErrorCode = " + errorCode +
                "}";
    }
}
