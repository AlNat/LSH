package com.LSH.client;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.core.client.EntryPoint;

/**
 * Created by @author AlNat on 06.12.2016.
 * Licensed by Apache License, Version 2.0
 */
public class Error404 implements EntryPoint {

    // TODO Test
    // TODO Подмена 404 страницы на эту

    private static String errorCode = "Error!"; // Код ошибки

    private String url;
    private String ip;
    private String browser;
    private GetLinkData getLinkData;
    private HTML label;

    public void onModuleLoad() {

        label = new HTML();
        url = Window.Location.getHref();
        browser = Window.Navigator.getUserAgent();
        ip = getIP(); // TODO check
        getLinkData = new GetLinkData(url, ip, browser);

        LSHServiceInterface.App.getInstance().getOriginal(getLinkData, new AsyncCallback<String>() {
            @Override
            public void onFailure(Throwable caught) {
                Print404("Server didn't answer!");
            }

            @Override
            public void onSuccess(String result) {
                if (result.startsWith(errorCode)) {
                    Print404(result);
                } else {
                    Window.Location.assign(result);
                }
            }
        });

        RootPanel.get().add(label);

    }

    private void Print404 (String result) {
        Window.setTitle("Page 404");
        label.setHTML(" <h1>404 Page!</h1> <br>" + result);
    }

    // TOADD to page
    /*
    <head>
        <script type="text/javascript" language="javascript">
            <%
                String ip_address = request.getRemoteAddr();
            %>
            var _ipAddress = <%= "\"" + ip_address + "\"" %>;
        </script>
    </head>
    */

    private native String getIP ()  /*-{
        return $wnd._ipAddress;
    }-*/;

}
