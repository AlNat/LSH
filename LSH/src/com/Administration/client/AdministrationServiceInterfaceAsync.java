package com.Administration.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.Date;
import java.util.LinkedList;

/**
 * Интерфейс, оописывающий поведения сервера
 */
public interface AdministrationServiceInterfaceAsync {
    void getData(String login, AsyncCallback<LinkedList<LinkData>> async);
    void isUser(String login, String password, AsyncCallback<Boolean> async);

    void setOriginalLink (int id, String link, AsyncCallback<Boolean> async);
    void setExpiredDate (int id, Date date, AsyncCallback<Boolean> async);
    void setMaxCount (int id, int count, AsyncCallback<Boolean> async);
    void setPassword (int id, String password, AsyncCallback<Boolean> async);
    void deleteLink (int id, AsyncCallback<Boolean> async);
}
