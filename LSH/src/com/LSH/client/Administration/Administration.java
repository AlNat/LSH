package com.LSH.client.Administration;

import com.LSH.client.DataType.ReturnLinkData;
import com.LSH.client.LSH;
import com.LSH.client.LSHServiceInterface;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;

import java.util.LinkedList;

/**
 * Класс, отвечающий за страницу администрированич
 */
public class Administration implements EntryPoint {


    private String login;
    private String password;
    private LinkedList<LinkData> list;
    HTML label;
    PasswordDialog dialog;
    CellTable <LinkData> dataPanel;

    /**
     * Основной метод в UI
     */
    public void onModuleLoad() {


        // TODO login popup для логина.
        // TODO Таблица с данными ссылками пользователя
        // http://samples.gwtproject.org/samples/Showcase/Showcase.html#!CwCellTable
        // http://samples.gwtproject.org/samples/Showcase/Showcase.html#!CwCellSampler


        label = new HTML();
        label.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        dialog = new PasswordDialog();
        dataPanel = new CellTable<>();

        RootPanel.get().add(label);
        RootPanel.get("Data").add(dataPanel);
        /// Придеться делать отедльное приложение от слова совсем
        // И писать взаимодействие сервлетов


        dialog.show();
        dataPanel.setVisible(false);

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
     * Диалоговое окно для ввода логина и пароля
     */
    private class PasswordDialog extends DialogBox {

        PasswordDialog() {
            setHTML("<h3>Please, input login and password</h3>");
            setAnimationEnabled(true);
            setGlassEnabled(true);

            // Данные
            HorizontalPanel panel = new HorizontalPanel();
            final Button button = new Button("OK");
            final TextBox loginTextBox = new TextBox();
            final PasswordTextBox passwordTextBox = new PasswordTextBox();

            loginTextBox.addKeyDownHandler(new KeyDownHandler() { // Повесели хэндлер кликов
                @Override
                public void onKeyDown(KeyDownEvent event) {
                    if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                        button.click(); // Нажимаем на кнопку по нажатию клавиши Enter
                    }
                }
            });

            passwordTextBox.addKeyDownHandler(new KeyDownHandler() { // Повесели хэндлер кликов
                @Override
                public void onKeyDown(KeyDownEvent event) {
                    if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                        button.click(); // Нажимаем на кнопку по нажатию клавиши Enter
                    }
                }
            });

            // Добавили поле и кнопку в панель
            panel.add(loginTextBox);
            panel.add(passwordTextBox);
            panel.add(button);

            button.addClickHandler(new ClickHandler() { // Повесли обработчик наатия на кнопку
                public void onClick(ClickEvent event) {

                    login = loginTextBox.getText();
                    password = getMD5(passwordTextBox.getText()); // Получили хэш текст пароля

                    AdministrationServiceInterface.App.getInstance().isUser(login, password, new AsyncCallback<Boolean>() {
                        @Override
                        public void onFailure(Throwable caught) {

                        }

                        @Override
                        public void onSuccess(Boolean result) {
                            if (result) {
                                // TODO hide панель и показывать дата провайдер
                                dialog.hide();
                                dataPanel.setVisible(true);
                            } else {
                                label.setHTML("<h3>User not found!</h3><br>");
                            }
                        }
                    });

                }
            });

            setWidget(panel);

        }
    }

    
}
