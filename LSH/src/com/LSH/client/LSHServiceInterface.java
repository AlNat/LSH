package com.LSH.client;

import com.LSH.client.DataType.GetLinkData;
import com.LSH.client.DataType.Link;
import com.LSH.client.DataType.PutLinkData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * Интрефейс для клиента
 */
@RemoteServiceRelativePath("LSHService")
public interface LSHServiceInterface extends RemoteService {

    String getShort(PutLinkData msg);
    Link getOriginal (GetLinkData getLinkData);

    class App {
        private static LSHServiceInterfaceAsync ourInstance = GWT.create(LSHServiceInterface.class);
        static synchronized LSHServiceInterfaceAsync getInstance() {
            return ourInstance;
        }
    }
}
