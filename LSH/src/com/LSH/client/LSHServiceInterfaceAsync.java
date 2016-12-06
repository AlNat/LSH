package com.LSH.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Интерфейс, оописывающий поведения сервера
 */
public interface LSHServiceInterfaceAsync {
    void getShort(PutLinkData msg, AsyncCallback<String> async);
    void getOriginal(GetLinkData getLinkData, AsyncCallback<String> async);
}
