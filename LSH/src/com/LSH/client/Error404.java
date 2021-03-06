package com.LSH.client;

import com.LSH.client.DataType.GetLinkData;
import com.LSH.client.DataType.ReturnLinkData;
import com.google.gwt.core.client.Scheduler;
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
        LSHServiceInterface.App.getInstance().getOriginal(getLinkData, new AsyncCallback<ReturnLinkData>() {
            @Override
            public void onFailure(Throwable caught) { // Если не смогли соединиться
                Print404();
            }

            @Override
            public void onSuccess(ReturnLinkData link) { // Если получили ответ
                if (link.getErrorCode()!= null) { // Если там ошибка то напечатали ее
                    PrintError(link);
                } else {
                    if (link.getPassword() == null) { // Если пароля нет, то редиректим
                        Window.Location.assign(link.getOriginalLink());
                    } else { // Иначе просим ввести пароль
                        final PasswordDialog d = new PasswordDialog(link);

                        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() { // Центрируем курсор
                            @Override
                            public void execute() {
                                d.textBox.setFocus(true);
                            }
                        });

                        d.show();
                        d.center();
                    }
                }
            }
        });

        RootPanel.get().add(label); // Добавили label на страницу
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
    private void PrintError (ReturnLinkData link) {
        String result = link.getErrorCode();

        Window.setTitle("404 - Page Not Found");
        label.setHTML("<h1>404 Page!</h1><br>" + result + "<br><br> But, maybe, you want <br> to create a " +
                "<a href=\"LSH.html\">shortlink</a> ?");
    }

    /**
     * Функция, получения MD5 хэша от строки
     * @param in входная строка
     * @return хэш строки или null если ошибка
     */
    private String getMD5(String in) {
        return LSH.getMD5(in);
    }


    /**
     * Диалоговое окно для ввода пароля
     */
    @SuppressWarnings("Convert2Lambda")
    class PasswordDialog extends DialogBox {

        final PasswordTextBox textBox;

        PasswordDialog(final ReturnLinkData link) {

            setHTML("<h4>Please, input password</h4>");
            setAnimationEnabled(true);
            setGlassEnabled(true);

            // Данные
            HorizontalPanel panel = new HorizontalPanel();
            final Button button = new Button("OK");
            textBox = new PasswordTextBox();

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
                        label.setHTML("<h3>Wrong password!</h3><br>");
                    }

                }
            });

            setWidget(panel); // Установили панел в виджет
            textBox.setFocus(true);
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
