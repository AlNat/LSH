package com.LSH.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface LSHServiceAsync {
    void getSimpleShort(String msg, AsyncCallback<String> async);
    void getComplexShort(Message msg, AsyncCallback<String> async);

}
