package com.LSH.client.Administration;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * Интрефейс для клиента
 */
@RemoteServiceRelativePath("AdministrationService")
public interface AdministrationServiceInterface extends RemoteService {

    LinkData getData(String msg);

    class App {
        private static AdministrationServiceInterfaceAsync ourInstance = GWT.create(AdministrationServiceInterface.class);
        static synchronized AdministrationServiceInterfaceAsync getInstance() {
            return ourInstance;
        }
    }
}
