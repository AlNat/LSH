package com.LSH.server;

import com.LSH.client.Administration.AdministrationServiceInterface;
import com.LSH.client.Administration.LinkData;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import java.util.LinkedList;

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
     * @return данные
     */
    @Override
    public LinkedList<LinkData> getData(String login) {
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
}
