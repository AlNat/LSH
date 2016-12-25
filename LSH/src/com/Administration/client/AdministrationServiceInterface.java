package com.Administration.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import java.util.Date;
import java.util.LinkedList;

/**
 * Интрефейс для клиента
 */
@RemoteServiceRelativePath("AdministrationService")
public interface AdministrationServiceInterface extends RemoteService {

    LinkedList<LinkData> getData(String login);
    Boolean isUser (String login, String password);

    Boolean setOriginalLink (int id, String link);
    Boolean setExpiredDate (int id, Date date);
    Boolean setMaxCount (int id, int count);
    Boolean setPassword (int id, String password);
    Boolean deleteLink (int id);

    class App {
        private static AdministrationServiceInterfaceAsync ourInstance = GWT.create(AdministrationServiceInterface.class);
        static synchronized AdministrationServiceInterfaceAsync getInstance() {
            return ourInstance;
        }
    }
}
