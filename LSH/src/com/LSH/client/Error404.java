package com.LSH.client;

import com.LSH.client.DataType.GetLinkData;
import com.LSH.client.DataType.Link;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.core.client.EntryPoint;


/**
 * Created by @author AlNat on 06.12.2016.
 * Licensed by Apache License, Version 2.0
 *
 * Класс UI на 404 код ошибки
 */
public class Error404 implements EntryPoint {

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
                    if (link.getPassword() == null) { // Если пароля нет, то редиректим
                        Window.Location.assign(link.getOriginalLink());
                    } else { // Иначе просим ввести пароль
                        PasswordDialog d = new PasswordDialog(link);
                        d.show();
                        d.center();
                    }
                }
            }
        });

        RootPanel.get().add(label);

    }

    /**
     * Функция вывода 404 страницы
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

        Window.setTitle("404 - Page Not Found");
        label.setHTML("<h1>404 Page!</h1><br>" + result);
    }

    /**
     * Функция, получения MD5 хэша от строи
     * @param in входная строка
     * @return хэш строки или null если ошибка
     */
    private String getMD5(String in) {
        return LSH.getMD5(in);
    }

    /**
     * Диалоговое окно для ввода пароля
     */
    private class PasswordDialog extends DialogBox {

        PasswordDialog(final Link link) {
            setHTML("<h3>Please, input password</h3>");
            setAnimationEnabled(true);
            setGlassEnabled(true);

            // Данные
            HorizontalPanel panel = new HorizontalPanel();
            final Button button = new Button("OK");
            final TextBox textBox = new TextBox();

            textBox.addKeyDownHandler(new KeyDownHandler() { // Повесели хэндлер кликов
                @Override
                public void onKeyDown(KeyDownEvent event) {
                    if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                        button.click(); // Нажимаем на кнопку по нажатию клавиши Enter
                    }
                }
            });

            // Добавили поле и кнопку в панель
            panel.add(textBox);
            panel.add(button);

            button.addClickHandler(new ClickHandler() { // Повесли обработчик наатия на кнопку
                public void onClick(ClickEvent event) {

                    String t = getMD5(textBox.getText()); // Получили хэш текст пароля

                    if (t.equals(link.getPassword())) { // Если пароли совпадают
                        Window.Location.assign(link.getOriginalLink()); // Редиректим
                    } else { // Иначе ошибка
                        label.setHTML("<h1>Wrong password!</h1><br>");
                    }

                }
            });

            setWidget(panel);

        }
    }

    /**
     * Функция получающая ip пользователя
     * @return ip
     */
    private native String getIP () /*-{
        return $wnd.userip;
    }-*/;

}
