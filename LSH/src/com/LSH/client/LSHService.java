package com.LSH.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * Интрефейс для клиента
 */
@RemoteServiceRelativePath("LSHService")
public interface LSHService extends RemoteService {

    String getShort(Message msg);

    class App {
        private static LSHServiceAsync ourInstance = GWT.create(LSHService.class);
        static synchronized LSHServiceAsync getInstance() {
            return ourInstance;
        }
    }
}
