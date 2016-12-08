package com.LSH.client.DataType;

import java.io.Serializable;

/**
 * Created by @author AlNat on 01.12.2016.
 * Licensed by Apache License, Version 2.0
 *
 * Класс обертка для сообщения между клиентом и сервером
 * для передачи данных об управляемом сворачивании ссылки
 */
public class PutLinkData implements Serializable {

    /* Геттеры и сеттеры */
    public String getOriginalLink() {
        return originalLink;
    }

    public void setOriginalLink(String originalLink) {
        this.originalLink = originalLink;
    }

    public String getShortLink() {
        return shortLink;
    }

    public String getTtl() {
        return ttl;
    }

    public Integer getMaxVisits() {
        return maxVisits;
    }


    /* Данные */
    private String originalLink; // Оригинальный линк
    private String shortLink; // Короткая ссылка - мнемоническая
    private String ttl; // Время жизни ссылки
    private Integer maxVisits; // Максимальное кол-во визитов


    /* Конструкторы */
    PutLinkData() {
        originalLink = "";
    }

    public PutLinkData(String originalLink) {
        this.originalLink = originalLink;
        shortLink = "NULL"; // Кодовое слово
        ttl = "1 week"; // Неделю по дефолту
        maxVisits = 0; // Бесконечное по дефолту
    }

    public PutLinkData(String originalLink, String ttl, Integer maxVisits) {
        this.originalLink = originalLink;
        shortLink = "NULL"; // Кодовое слово
        this.ttl = ttl;
        this.maxVisits = maxVisits;
    }

    public PutLinkData(String originalLink, String ttl, Integer maxVisits, String shortLink) {
        this.originalLink = originalLink;
        this.shortLink = shortLink;
        this.ttl = ttl;
        this.maxVisits = maxVisits;
    }

    public String toLog () {
        return "{originalLink = " + originalLink + "; shortLink = " + shortLink + "; ttl = " + ttl + "; maxVisits = " + maxVisits + "}";
    }

}