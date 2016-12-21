package com.Administration.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import java.util.LinkedList;

/**
 * Интрефейс для клиента
 */
//@RemoteServiceRelativePath("AdministrationService")
public interface AdministrationServiceInterface extends RemoteService {

    LinkedList<LinkData> getData(String login);
    Boolean isUser (String login, String password);

    class App {
        private static AdministrationServiceInterfaceAsync ourInstance = GWT.create(AdministrationServiceInterface.class);
        static synchronized AdministrationServiceInterfaceAsync getInstance() {
            return ourInstance;
        }
    }
}
