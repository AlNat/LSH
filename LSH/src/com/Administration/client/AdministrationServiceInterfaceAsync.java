package com.Administration.client;

import com.Administration.client.DataType.LinkData;
import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.Date;

/**
 * Интерфейс, описывающий поведения сервера
 */
interface AdministrationServiceInterfaceAsync {
    void getData(String login, AsyncCallback<LinkData[]> async);
    void isUser(String login, String password, AsyncCallback<Boolean> async);

    void setOriginalLink (int id, String link, AsyncCallback<Boolean> async);
    void setExpiredDate (int id, Date date, AsyncCallback<Boolean> async);
    void setMaxCount (int id, int count, AsyncCallback<Boolean> async);
    void setPassword (int id, String password, AsyncCallback<Boolean> async);
    void deleteLink (int id, AsyncCallback<Boolean> async);
}
