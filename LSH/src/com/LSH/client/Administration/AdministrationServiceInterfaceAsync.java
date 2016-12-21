package com.LSH.client.Administration;

import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.LinkedList;

/**
 * Интерфейс, оописывающий поведения сервера
 */
public interface AdministrationServiceInterfaceAsync {
    void getData(String login, AsyncCallback<LinkedList<LinkData>> async);
    void isUser(String login, String password, AsyncCallback<Boolean> async);
}
