package com.Administration.client;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by @author AlNat on 21.12.2016.
 * Licensed by Apache License, Version 2.0
 */
public class LinkData implements Serializable {

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Date getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(Date expiredDate) {
        this.expiredDate = expiredDate;
    }

    public Integer getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(Integer maxCount) {
        this.maxCount = maxCount;
    }

    public Integer getCurrentCount() {
        return currentCount;
    }

    public void setCurrentCount(Integer currentCount) {
        this.currentCount = currentCount;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Данные об создании ссылки
    private String code; // Буквенное представление кода
    private String link;
    private Date expiredDate;
    private Integer maxCount;
    private Integer currentCount;
    private String password;

    public LinkData () {

    }

}
