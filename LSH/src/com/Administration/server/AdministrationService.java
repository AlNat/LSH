package com.Administration.server;

import com.Administration.client.AdministrationServiceInterface;
import com.Administration.client.DataType.LinkData;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import java.util.Date;

/**
 * Created by @author AlNat on 21.12.2016.
 * Licensed by Apache License, Version 2.0
 *
 * Сервис администрирования
 */
public class AdministrationService extends RemoteServiceServlet implements AdministrationServiceInterface {

    /**
     * Функция, возращающая набор данных об ссылках пользователя
     * @param login логин пользователя
     * @return лист с данными
     */
    @Override
    public LinkData[] getData(String login) {
        return DBConnect.instance.getData (login);
    }

    /**
     * Функция, аутентифицирующая пользоватедя
     * @param login логин
     * @param password пароль
     * @return true если есть такой пользователь с таким паролем, иначе false
     */
    @Override
    public Boolean isUser (String login, String password) {
        return DBConnect.instance.isUser(login, password);
    }


    // Функции, изменяющие данные

    /**
     * Функция, устанавливающая оригинальный линк
     * @param id записи
     * @param link новый линк
     * @return true если запись успешна, иначе false
     */
    @Override
    public Boolean setOriginalLink (int id, String link) {
        return DBConnect.instance.setOriginalLink(id, link);
    }

    /**
     * Функция, устанавливающая срок действия
     * @param id записи
     * @param date новая дата
     * @return true если запись успешна, иначе false
     */
    @Override
    public Boolean setExpiredDate (int id, Date date) {
        return DBConnect.instance.setExpiredDate(id, date);
    }


    /**
     * Функция, устанавливающая максимальное кол-во переходов
     * @param id записи
     * @param count новое кол-во
     * @return true если запись успешна, иначе false
     */
    @Override
    public Boolean setMaxCount (int id, int count) {
        return DBConnect.instance.setMaxCount(id, count);
    }

    /**
     * Функция, устанавливающая пароль на ссылку
     * @param id записи
     * @param password новый пароль
     * @return true если запись успешна, иначе false
     */
    @Override
    public Boolean setPassword (int id, String password) {
        return DBConnect.instance.setPassword(id, password);
    }

    /**
     * Функция, удаляющая запись
     * @param id записи
     * @return true если удаление успешна, иначе false
     */
    @Override
    public Boolean deleteLink (int id) {
        return DBConnect.instance.deleteLink(id);
    }

}
