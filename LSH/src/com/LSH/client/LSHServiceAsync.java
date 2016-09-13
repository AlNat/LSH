package com.LSH.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface LSHServiceAsync {
    void getMessage(String msg, AsyncCallback<String> async);

    void Init (AsyncCallback<Void> asyncCallback);

}
