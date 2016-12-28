package com.LSH.client;

import com.LSH.client.DataType.GetLinkData;
import com.LSH.client.DataType.ReturnLinkData;
import com.LSH.client.DataType.PutLinkData;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Интерфейс, описывающий поведения сервера
 */
interface LSHServiceInterfaceAsync {
    void getShort(PutLinkData msg, AsyncCallback<String> async);
    void getOriginal(GetLinkData getLinkData, AsyncCallback<ReturnLinkData> async);
    void Login(String userLogin, String userPassword, AsyncCallback<String> asyncCallback);
}
