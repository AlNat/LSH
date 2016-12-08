package com.LSH.client;

import com.LSH.server.DataType.GetLinkData;
import com.LSH.server.DataType.PutLinkData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * Интрефейс для клиента
 */
@RemoteServiceRelativePath("LSHService")
public interface LSHServiceInterface extends RemoteService {

    String getShort(PutLinkData msg);
    String getOriginal (GetLinkData getLinkData);

    class App {
        private static LSHServiceInterfaceAsync ourInstance = GWT.create(LSHServiceInterface.class);
        static synchronized LSHServiceInterfaceAsync getInstance() {
            return ourInstance;
        }
    }
}
