package com.LSH.client;

import com.LSH.client.DataType.GetLinkData;
import com.LSH.client.DataType.Link;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.core.client.EntryPoint;

import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * Created by @author AlNat on 06.12.2016.
 * Licensed by Apache License, Version 2.0
 *
 * Класс UI на 404 код ошибки
 */
public class Error404 implements EntryPoint {

    private static final String errorCode = "Error!"; // Код ошибки

    private HTML label; // Место вывода ошибок

    public void onModuleLoad() {

        // Настроили поле для ошибок
        label = new HTML();
        label.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

        // Получили данные про пользоватеья
        String url = Window.Location.getHref();// URL, по которому пользователь пришел
        String browser = Window.Navigator.getUserAgent(); // user-agent пользователя
        String ip = getIP(); // IP адресс пользователя
        GetLinkData getLinkData = new GetLinkData(url, ip, browser); // Блок данных про пользователя

        // Пошли на сервер
        LSHServiceInterface.App.getInstance().getOriginal(getLinkData, new AsyncCallback<Link>() {
            @Override
            public void onFailure(Throwable caught) { // Если не смогли соединиться
                Print404();
            }

            @Override
            public void onSuccess(Link link) { // Если получили ответ
                if (link.getErrorCode()!= null) { // Если там ошибка то напечатали ее
                    PrintError(link);
                } else {
                    if (link.getPassword().isEmpty()) { // Если пароля нет, то редиретим
                        Window.Location.assign(link.getOriginalLink());
                    } else { // Иначе просим ввести пароль
                        MyDialog d = new MyDialog(link);
                        d.show();
                        d.center();
                    }
                }
            }
        });

        RootPanel.get().add(label);

    }

    /**
     * Функция вывода ошибок
     */
    private void Print404 () {
        Window.setTitle("404 - Page Not Found");
        label.setHTML("<h1>404 Page!</h1><br>");
    }

    /**
     * Функция вывода ошибок
     * @param link данные ссылки
     */
    private void PrintError (Link link) {

        String result = link.getErrorCode();

        if (result.startsWith(errorCode)) { // Обрезали фразу errorCode
            result = result.substring(errorCode.length());
        }

        Window.setTitle("404 - Page Not Found");
        label.setHTML("<h1>404 Page!</h1><br>" + result);
    }

    /**
     * Функция получающая ip пользователя
     * @return ip
     */
    private native String getIP () /*-{
        return $wnd.userip;
    }-*/;

    /**
     * Функция, получения MD5 хэша от строи
     * @param in входная строка
     * @return хэш строки или null если ошибка
     */
    private String getMD5 (String in) {

        if (in.isEmpty()) {
            return null;
        }

        // TODO Подумать как это оптимизировать
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(in.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            String hashtext = number.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // TODO Комментарии
    private class MyDialog extends DialogBox {

        MyDialog(final Link link) {
            setHTML("<h3>Please, input password</h3>");
            setAnimationEnabled(true);
            setGlassEnabled(true);

            HorizontalPanel panel = new HorizontalPanel();
            final Button ok = new Button("OK");
            final TextBox textBox = new TextBox();

            textBox.addKeyDownHandler(new KeyDownHandler() {
                @Override
                public void onKeyDown(KeyDownEvent event) {
                    if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                        ok.click(); // Нажимаем на кнопку по нажатию клавиши Enter
                    }
                }
            });

            panel.add(textBox);
            panel.add(ok);

            // Иначе запрашиваем пароль и редиректим пользователя
            ok.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {

                    String t = getMD5(textBox.getText());

                    if (t.equals(link.getPassword())) {
                        Window.Location.assign(link.getOriginalLink());
                    } else { // Иначе ошибка
                        label.setHTML("<h1>Wrong password!</h1><br>");
                    }

                }
            });

            setWidget(panel);

        }
    }
}
