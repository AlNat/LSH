package com.LSH.server;

import com.LSH.client.Administration.AdministrationServiceInterface;
import com.LSH.client.Administration.LinkData;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Created by @author AlNat on 21.12.2016.
 * Licensed by Apache License, Version 2.0
 */
public class AdministrationService extends RemoteServiceServlet implements AdministrationServiceInterface {

    // TODO Пользователя для администрирования + таблицу users
    // http://samples.gwtproject.org/samples/Showcase/Showcase.html#!CwDataGrid

    @Override
    public LinkData getData(String msg) {

        // TODO Подключение к БД и возврат набора данных про ссылки этого пользователя

        return null;
    }
}
