package com.LSH.client.Administration;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Интерфейс, оописывающий поведения сервера
 */
public interface AdministrationServiceInterfaceAsync {
    void getData(String code, AsyncCallback<LinkData> async);
}
