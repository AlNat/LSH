package com.LSH.client;

import java.io.Serializable;

/**
 * Created by @author AlNat on 01.12.2016.
 * Licensed by Apache License, Version 2.0
 *
 * Класс обертка для сообщения между клиентом и сервером
 * для передачи данных об управляемом сворачивании ссылки
 */
public class Message implements Serializable {

    /* Геттеры и сеттеры */

    public String getOriginalLink() {
        return originalLink;
    }

    public String getShortLink() {
        return shortLink;
    }

    void setShortLink(String shortLink) {
        if (!shortLink.isEmpty()) {
            this.shortLink = shortLink;
        }
    }

    public String getTtl() {
        return ttl;
    }

    void setTtl(String ttl) {
        this.ttl = ttl;
    }

    public Integer getMaxVisits() {
        return maxVisits;
    }

    void setMaxVisits(Integer maxVisits) {
        this.maxVisits = maxVisits;
    }

    private String originalLink;
    private String shortLink;
    private String ttl;
    private Integer maxVisits;

    Message () {
        originalLink = "NONE";
    }

    Message(String originalLink) {
        this.originalLink = originalLink;
        shortLink = "NONE"; // Кодовое слово
        ttl = "1 week"; // Неделю по дефолту
        maxVisits = 0;
    }



}
