package com.LSH.client;

import com.LSH.server.DataType.GetLinkData;
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

    private HTML label; // Место вывода ошибок

    public void onModuleLoad() {

        // Настроили поле для ошибо
        label = new HTML();
        label.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

        // Получили данные про пользоватеья
        String url = Window.Location.getHref();// URL, по которому пользователь пришел
        String browser = Window.Navigator.getUserAgent(); // user-agent пользователя
        String ip = getIP(); // IP адресс пользователя
        GetLinkData getLinkData = new GetLinkData(url, ip, browser); // Блок данных про пользователя

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
        Window.setTitle("404 - Page Not Found");

        if (result.startsWith(errorCode)) { // Оберазли фразу errorCode
            result = result.substring(errorCode.length());
        }

        label.setHTML("<h1>404 Page!</h1><br>" + result);
    }

    /**
     * Функция получающая ip пользователя
     * @return ip
     */
    private native String getIP () /*-{
        return $wnd.userip;
    }-*/;

}
