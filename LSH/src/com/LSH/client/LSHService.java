package com.LSH.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("LSHService")
public interface LSHService extends RemoteService {

    String getSimpleShort(String msg);
    String getComplexShort(Message msg);

    class App {
        private static LSHServiceAsync ourInstance = GWT.create(LSHService.class);
        static synchronized LSHServiceAsync getInstance() {
            return ourInstance;
        }
    }
}
