package com.LSH.client;

import com.LSH.client.DataType.GetLinkData;
import com.LSH.client.DataType.PutLinkData;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Интерфейс, оописывающий поведения сервера
 */
public interface LSHServiceInterfaceAsync {
    void getShort(PutLinkData msg, AsyncCallback<String> async);
    void getOriginal(GetLinkData getLinkData, AsyncCallback<String> async);
}
