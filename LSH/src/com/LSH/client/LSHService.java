package com.LSH.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("LSHService")
public interface LSHService extends RemoteService {

    String getMessage(String msg);

    public static class App {
        private static LSHServiceAsync ourInstance = GWT.create(LSHService.class);

        public static synchronized LSHServiceAsync getInstance() {
            return ourInstance;
        }
    }
}
