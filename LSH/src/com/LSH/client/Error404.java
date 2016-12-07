package com.LSH.client;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.core.client.EntryPoint;

/**
 * Created by @author AlNat on 06.12.2016.
 * Licensed by Apache License, Version 2.0
 */
public class Error404 implements EntryPoint {

    private static String errorCode = "Error!"; // Код ошибки

    private String url; // Урл, по которому пользователь пришел
    private String ip; // IP адресс пользователя
    private String browser; // user-agent пользователя
    private GetLinkData getLinkData; // Блок данных про пользователя
    private HTML label; // Место вывода ошибок

    public void onModuleLoad() {

        // Настроили поле для ошибо
        label = new HTML();
        label.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

        // Получили данные про пользоватеья
        url = Window.Location.getHref();
        browser = Window.Navigator.getUserAgent();
        ip = "192.168.0.1"; //getIP(); // TODO fix - сделать нормальное получение ip
        getLinkData = new GetLinkData(url, ip, browser);

        // Пошли на сервер
        LSHServiceInterface.App.getInstance().getOriginal(getLinkData, new AsyncCallback<String>() {
            @Override
            public void onFailure(Throwable caught) { // Если не смогли соединиться
                Print404("Server didn't answer!");
            }

            @Override
            public void onSuccess(String result) { // Если получили ответ
                if (result.startsWith(errorCode)) { // Если там ошибка то напечатали ее
                    Print404(result);
                } else { // Иначе редиректим пользователя
                    Window.Location.assign(result);
                }
            }
        });

        RootPanel.get().add(label);

    }

    /**
     * Функция вывода ошибок
     * @param result код ошибки
     */
    private void Print404 (String result) {
        Window.setTitle("Page 404");
        label.setHTML("<h1>404 Page!</h1> <br>" + result);
    }

    /**
     * Функция получающая ip пользователя
     * @return ip
     */
    private native String getIP ()  /*-{
        return request.getRemoteAddr();
    }-*/;

}
