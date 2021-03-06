package com.Administration.client.DataType;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * Created by @author AlNat on 21.12.2016.
 * Licensed by Apache License, Version 2.0
 */
public class LinkData implements Serializable, Comparable<LinkData> {

    public Integer getId () {
        return id;
    }

    public void setId (Integer id) {
        this.id = id;
    }

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

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate (Date createDate) {
        this.createDate = createDate;
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
    private Integer id;
    private String code; // Буквенное представление кода
    private String link;
    private Date expiredDate;
    private Date createDate;
    private Integer maxCount;
    private Integer currentCount;
    private String password;

    public LinkData () {

    }

    @Override
    public int compareTo(LinkData o) {
        return (o == null || o.id == null) ? -1 : -o.id.compareTo(id);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof LinkData && Objects.equals(id, ((LinkData) o).id);
    }

    @Override
    public int hashCode() {
        return id;
    }

}
