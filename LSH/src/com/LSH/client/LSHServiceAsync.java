package com.LSH.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface LSHServiceAsync {
    void getShort(Message msg, AsyncCallback<String> async);
}
