package com.LSH.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Интерфейс, оописывающий поведения сервера
 */
public interface LSHServiceInterfaceAsync {
    void getShort(Message msg, AsyncCallback<String> async);
    void getOriginal(Data data, AsyncCallback<String> async);
}
