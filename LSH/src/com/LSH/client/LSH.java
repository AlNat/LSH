package com.LSH.client;

import com.LSH.client.DataType.PutLinkData;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.rpc.AsyncCallback;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Date;

/**
 * Created by @author AlNat on 26.11.2016.
 * Licensed by Apache License, Version 2.0
 *
 * Класс, отвечающий за главную страницу UI
 */
@SuppressWarnings("Convert2Lambda")
public class LSH implements EntryPoint {

    private static final String errorCode = "Error!"; // Код ошибки
    private static final int COOKIE_TIMEOUT = 1000 * 60 * 60 * 24; // Время жизни кук - 1000 миллиисекунд, 60 секунд, 60 минут, 24 часа - сутки
    private static final String cookieName = "LSHLogin"; // Имя куки для логина
    private String login; // Логин вошедшего пользователя


    /* Набор полей для простом сокращении */
    private final TextBox simpleOriginalLink = new TextBox(); // Оригинальная ссылка
    private final Button simpleShortButton = new Button("Get Short Link"); // Кнопка получить короткую ссылку
    private final Button simpleCopyButton = new Button("Copy to clipboard"); // Кнопа копировать в буфер обмена
    private final HTML simpleShortText = new HTML("Your shortlink — "); // Текст перед короткой ссылкой
    private final HTML simpleShortLink = new HTML(""); // Сама ссылка


    /* Набор полей для управляемого сокращения */
    private final Button complexShortButton = new Button("Get Short Link"); // Кнопка получить короткую ссылку
    private final Button complexCopyButton = new Button("Copy to clipboard"); // Кнопа копировать в буфер обмена
    private final HTML complexShortText = new HTML("Your shortlink — "); // Текст перед короткой ссылкой
    private final HTML complexShortLink = new HTML(""); // Сама ссылка

    private final HTML complexText = new HTML("Link:"); // Текст перед полем для оригинальной ссылки
    private final TextBox complexOriginalLink = new TextBox(); // Оригинальная ссылка

    private final HTML complexTimeText = new HTML("Set link live duration:"); // Текст перед полем для времени жизни ссылки
    private final ListBox complexTime = new ListBox(); // Поле для указания времени жизни ссылки
    private final HTML complexCountText = new HTML("Set count of visits:<br>(0 for unlimited)"); // Текст перед полем для кол-во переходов
    private final IntegerBox complexCount = new IntegerBox(); // Поле ввода кол-во переходов

    private final HTML complexNameText = new HTML("Customize link:<br>(i, l, o, 1, 0 - illegal)"); // Текст перед полем для кастомизации ссыли
    private final TextBox complexName = new TextBox(); // Само поле кастомизации ссылки
    private final HTML complexPasswordText = new HTML("Set password:"); // Текст перед полем для пароля ссыли
    private final TextBox complexPassword = new PasswordTextBox(); // Само поле пароля ссылки


    /* Набор полей для логина */
    private final LoginDialog dialog = new LoginDialog(); // Диалог для входа пользователя
    private final HTML loginLabel = new HTML(); // Поле для выода информации
    private final Button loginButton = new Button("Login"); // Кнопка логина
    private final Button logoutButton = new Button("Log out"); // Кнопка выхода


    /**
     * Основной метод в UI
     */
    public void onModuleLoad() {

        /* Создаем простое сокращение */

        String linkWidth = String.valueOf(Window.getClientWidth() / 4);

        // HP = HorizontalPanel; VP = VerticalPanel
        final HorizontalPanel simpleDataHP = new HorizontalPanel(); // Панель для приема ссылки
        final HorizontalPanel simpleAnswerHP = new HorizontalPanel(); // Панель для выдачи ответа
        final VerticalPanel simpleVP = new VerticalPanel(); // Панель для хранения того, что выше

        simpleShortButton.addClickHandler(new SimpleClickHandler() ); // Навесили на кнопку хендлер нажатий
        simpleOriginalLink.addKeyDownHandler(new EnterKeyListener(simpleShortButton)); // Навесили на поле хендлер нажатия Enter
        simpleCopyButton.addClickHandler(new CopyClickHandler("simpleAnswer")); // Навесили на кнопку копирование в буфер хендлер нажатия

        simpleOriginalLink.setWidth(linkWidth); // Установили шириину поля
        simpleDataHP.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER); // Выравнивание
        simpleDataHP.setSpacing(5); // Отступ

        // Добавили данные
        simpleDataHP.add(simpleOriginalLink);
        simpleDataHP.add(simpleShortButton);

        // Аналогично, настроили
        simpleAnswerHP.setSpacing(5);
        simpleAnswerHP.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

        // И добавили
        simpleAnswerHP.add(simpleShortText);
        simpleAnswerHP.add(simpleShortLink);
        simpleAnswerHP.add(simpleCopyButton);

        // Тут тоже
        simpleVP.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        simpleVP.setSpacing(10);
        simpleVP.add(simpleDataHP);
        simpleVP.add(simpleAnswerHP);

        // Установили кнопки в невидимость
        simpleShortText.setVisible(false);
        simpleCopyButton.setVisible(false);

        /* Создаем управляемое сокращение */
        // Панели для хранения
        final HorizontalPanel complexLinkHP = new HorizontalPanel(); // Строка с оригинальной ссылокой
        final HorizontalPanel complexDataHP = new HorizontalPanel(); // Данные
        final HorizontalPanel complexOptionalData = new HorizontalPanel(); // Опциональные данные
        final HorizontalPanel complexAnswerHP = new HorizontalPanel(); // Ответ

        final VerticalPanel complexVP = new VerticalPanel(); // Хранение того, что выше
        final VerticalPanel complexOptionalVP = new VerticalPanel(); // Хранение того, что выше

        EnterKeyListener complexKey = new EnterKeyListener(complexShortButton); // Хендлер наатия на клавишу Enter
        complexCopyButton.addClickHandler(new CopyClickHandler("complexAnswer"));

        // Заполняем данными поле времени жизни ссылки
        complexTime.addItem("1 hour");
        complexTime.addItem("12 hours");
        complexTime.addItem("1 day");
        complexTime.addItem("1 week");
        complexTime.addItem("1 month");
        complexTime.addItem("Unlimited");
        complexTime.setSelectedIndex(3);

        // Настроили другие поля
        complexOriginalLink.setWidth(linkWidth);
        complexCount.setWidth("40px");
        complexCount.setValue(10);

        complexShortButton.addClickHandler(new ComplexClickHandler()); // Добавили хендлер нажатия на кнопку
        // Добавляем хендлер нажатия клавишы Enter ко всем полям
        complexOriginalLink.addKeyDownHandler(complexKey);
        complexName.addKeyDownHandler(complexKey);
        complexCount.addKeyDownHandler(complexKey);
        complexTime.addKeyDownHandler(complexKey);
        complexPassword.addKeyDownHandler(complexKey);


        // Настроили и добавили данные к панелям
        complexLinkHP.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        complexLinkHP.setSpacing(5);
        complexLinkHP.add(complexText);
        complexLinkHP.add(complexOriginalLink);

        complexDataHP.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        complexDataHP.setSpacing(5);
        complexDataHP.add(complexTimeText);
        complexDataHP.add(complexTime);
        complexDataHP.add(complexCountText);
        complexDataHP.add(complexCount);

        complexCount.setAlignment(ValueBoxBase.TextAlignment.CENTER); // Выровняли число переходов
        complexCount.setMaxLength(5); // Настроили кол-во символов

        complexOptionalData.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        complexOptionalData.setSpacing(5);
        complexOptionalData.add(complexNameText);
        complexOptionalData.add(complexName);
        complexOptionalData.add(complexPasswordText);
        complexOptionalData.add(complexPassword);

        complexAnswerHP.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        complexAnswerHP.setSpacing(5);
        complexAnswerHP.add(complexShortText);
        complexAnswerHP.add(complexShortLink);
        complexAnswerHP.add(complexCopyButton);


        complexVP.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        complexVP.setSpacing(5);
        complexVP.add(complexLinkHP);
        complexVP.add(complexDataHP);

        complexOptionalVP.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        complexOptionalVP.setSpacing(5);
        complexOptionalVP.add(complexOptionalData);
        complexOptionalVP.add(complexShortButton);
        complexOptionalVP.add(complexAnswerHP);

        // Скрыли кнопки ответа
        complexShortText.setVisible(false);
        complexCopyButton.setVisible(false);


        /* Настроили поля для логина */

        loginButton.addClickHandler(new ClickHandler() { // При нажатии на кнопку логина показываем диалог логина
            @Override
            public void onClick(ClickEvent event) {
                dialog.Login();
            }
        });

        logoutButton.addClickHandler(new ClickHandler() { // Кнопка выхода
            @Override
            public void onClick(ClickEvent event) { // При нажатии
                Cookies.removeCookie(cookieName); // Удаляем куку об пользователе

                // И возвращаем все к исходному состоянию
                loginButton.setVisible(true);
                loginLabel.setHTML("");
                loginLabel.setVisible(false);
                logoutButton.setVisible(false);
            }
        });


        // Настроили и добавили данные в панель
        final HorizontalPanel loginHP = new HorizontalPanel();
        loginHP.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        loginHP.setSpacing(5);
        loginHP.add(loginLabel);
        loginHP.add(logoutButton);
        loginHP.add(loginButton);

        logoutButton.setVisible(false);
        dialog.hide(); // Диалог логина по умолчанию скрыт


        // Устанавливаем наши панель на страницу
        RootPanel.get("Login").add(loginHP);
        RootPanel.get("SimpleShort").add(simpleVP);
        RootPanel.get("ComplexShort").add(complexVP);
        RootPanel.get("ComplexShortOptional").add(complexOptionalVP);

    }

    /**
     * Класс, который реагирует на клик на кнопку получить простую короткую ссылку
     */
    private class SimpleClickHandler implements ClickHandler {
        public void onClick(ClickEvent event) {

            PutLinkData putLinkData = new PutLinkData(simpleOriginalLink.getText()); // Формируем сообщение на сервер
            putLinkData.setBrowser(Window.Navigator.getUserAgent()); // user-agent пользователя
            putLinkData.setIp(getIP()); // IP адрес пользователя

            if (login != null) {
                putLinkData.setUserLogin(login); // Если вошли под логином то пишем его
            }

            LSHServiceInterface.App.getInstance().getShort(putLinkData, new AsyncCallback<String>() { // Отпраляем сообщение и получаем ответ
                @Override
                public void onFailure(Throwable caught) { // При неудачном соединению с сервером
                    simpleShortLink.setText("Cannot connect to server!");
                    }
                @Override
                public void onSuccess(String result) { // При удаче
                    if (result.startsWith(errorCode)) { // Если вернули код ошибки то показываем его
                        simpleShortLink.setHTML(result);
                    } else {
                        // Иначе активируем кнопки про короткую ссылку
                        simpleShortText.setVisible(true);
                        simpleCopyButton.setVisible(true);
                        simpleShortLink.setText(result); // Показываем саму ссылку
                        simpleShortLink.getElement().setAttribute("id", "simpleAnswer"); // И навшиваем на него аттрибут - для работы кнопки копировать
                    }
                }
            });
        }
    }

    /**
     * Класс, который реагирует на клик на кнопку получить управляемую короткую ссылку
     */
    private class ComplexClickHandler implements ClickHandler {
        public void onClick(ClickEvent event) {

            // Формируем 'пакет' данных на сервер
            PutLinkData putLinkData;
            if (complexName.getText().isEmpty()) { // Вызываем конструктор в зависимости от того, есть ли мнемоника
                putLinkData = new PutLinkData( complexOriginalLink.getText(), complexTime.getSelectedItemText(), complexCount.getValue());
            } else {
                putLinkData = new PutLinkData( complexOriginalLink.getText(), complexTime.getSelectedItemText(), complexCount.getValue(), complexName.getText());
            }
            putLinkData.setBrowser(Window.Navigator.getUserAgent()); // user-agent пользователя
            putLinkData.setIp(getIP()); // IP адрес пользователя

            String t = complexPassword.getText();
            if (!t.isEmpty()) {
                putLinkData.setPassword(getMD5(t));
            }

            putLinkData.setUserLogin(login);

            // И отправляем его
            LSHServiceInterface.App.getInstance().getShort(putLinkData, new AsyncCallback<String>() {
                @Override
                public void onFailure(Throwable caught) {
                    complexShortLink.setText("Cannot connect to server!");
                }
                @Override
                public void onSuccess(String result) {
                    if (result.startsWith(errorCode)) { // Если ошибка то пишем ее
                        complexShortLink.setHTML(result);
                    } else { // Иначе активируем кнопки
                        complexShortText.setVisible(true);
                        complexCopyButton.setVisible(true);
                        complexShortLink.setText(result);
                        complexShortLink.getElement().setAttribute("id", "complexAnswer");
                    }
                }
            });
        }
    }

    /**
     * Класс, которые реагирует на нажатие кнопи скопировать в буфер обмена
     */
    private class CopyClickHandler implements ClickHandler {
        final String id; // Простая или управляемая ссылка долна быть скопирована

        CopyClickHandler (String id) { // Получаем id
            this.id = id;
        }

        @Override
        public void onClick(ClickEvent event) { // При клике
            copyToClipboard(id); // Вызываем функцию копирования
        }
    }

    /**
     * Кей-хендлер, нажимающий кнопку(переданную в конструкторе) по нажатию Enter
     */
    private static class EnterKeyListener implements KeyDownHandler {
        final Button button; // Кнопка

        EnterKeyListener (Button button) {
            this.button = button;
        }

        @Override
        public void onKeyDown(KeyDownEvent event) {

            if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                button.click(); // Нажимаем на кнопку по нажатию клавиши Enter
            }
        }

    }

    /**
     * Диалоговое окно для ввода логина и пароля
     */
    private class LoginDialog extends DialogBox {

        boolean textFlag = false; // Флаг 1 входа для очистки
        boolean passFlag = false;

        /**
         * Конструктор
         */
        LoginDialog() {

            setHTML("Please, input login and password<br>If you aren't registered, new user will be created");
            setAnimationEnabled(true);
            setGlassEnabled(true);

            // Панели
            final HorizontalPanel panelData = new HorizontalPanel(); // Панель ввода
            final HorizontalPanel panelButtons = new HorizontalPanel(); // Панель кнопок
            final HTML label = new HTML(); // Место для ошибок

            // Данные
            final Button buttonLogin = new Button("Login"); // Кнопка для входа
            final Button buttonCancel = new Button("Cancel"); // Кнопка для отмены

            final TextBox loginTextBox = new TextBox(); // Место ввода логина
            loginTextBox.setText("Login");
            final PasswordTextBox passwordTextBox = new PasswordTextBox(); // Место ввода пароля
            passwordTextBox.setText("Password");


            loginTextBox.addKeyDownHandler(new KeyDownHandler() { // Повесели хэндлер кликов
                @Override
                public void onKeyDown(KeyDownEvent event) {
                    if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                        buttonLogin.click(); // Нажимаем на кнопку по нажатию клавиши Enter
                    }
                }
            });

            loginTextBox.addClickHandler(new ClickHandler() { // Чистим поле по первому клику по нему
                @Override
                public void onClick(ClickEvent event) {
                    if (!textFlag) {
                        loginTextBox.setText("");
                        textFlag = true;
                    }
                }
            });


            passwordTextBox.addKeyDownHandler(new KeyDownHandler() { // Повесели хэндлер кликов
                @Override
                public void onKeyDown(KeyDownEvent event) {
                    if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                        buttonLogin.click(); // Нажимаем на кнопку по нажатию клавиши Enter
                    }
                }
            });

            passwordTextBox.addClickHandler(new ClickHandler() { // Чистим поле по первому клику по нему
                @Override
                public void onClick(ClickEvent event) {
                    if (!passFlag) {
                        passwordTextBox.setText("");
                        passFlag = true;
                    }
                }
            });


            // Добавили поля для ввода в панель
            panelData.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
            panelData.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
            panelData.add(loginTextBox);
            panelData.add(passwordTextBox);

            // Добавили кнопки в панель
            panelButtons.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
            panelButtons.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
            panelButtons.add(buttonLogin);
            panelButtons.add(buttonCancel);

            // При нажатии на кнопку логин идем на сервер
            buttonLogin.addClickHandler(new ClickHandler() { // Повесли обработчик нажатия на кнопку
                public void onClick(ClickEvent event) {

                    final String userLogin = loginTextBox.getText(); // Получили логин
                    final String userPassword = getMD5(passwordTextBox.getText()); // Получили хэш текст пароля

                    // Пошли на сервер
                    LSHServiceInterface.App.getInstance().Login(userLogin, userPassword, new AsyncCallback<String>() {
                        @Override
                        public void onFailure(Throwable caught) { // При ошибке говорим
                            label.setHTML("<h4>Server error!</h4><br>");
                        }

                        @Override
                        public void onSuccess(String result) { // Если есть ответ
                            if (result.equals("OK")) { // Если зашли под этим логином и паролем, то скрыли диалог и показали, что вошли

                                GoodLogin(userLogin);

                            } else { // Иначе сказали про ошибку
                                String t = result.substring(errorCode.length());
                                label.setHTML(t);
                            }
                        }
                    });

                }
            });

            // При нажатии на кнопку отменить скрываем диалог
            buttonCancel.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    LoginDialog.this.hide();
                }
            });


            // Создали панель для вывода всех данных и поместили туда все
            VerticalPanel panel = new VerticalPanel();
            panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
            panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
            panel.add(panelData);
            panel.add(panelButtons);
            panel.add(label);

            setWidget(panel); // Установили панель в этот виджет

        }

        /**
         * Если залогинились
         * @param userLogin логин пользователя
         */
        void GoodLogin(String userLogin) {
            // Изменили интерфейс
            login = userLogin;
            loginButton.setVisible(false);
            loginLabel.setVisible(true);
            logoutButton.setVisible(true);
            loginLabel.setHTML("You're login as <br><a href=\"Administration.html\" color=lightseagreen;>" + login + "</a>");

            PutLoginCookie (userLogin); // Положили куку о том, что мы вошли

            LoginDialog.this.hide(); // Скрыли диалог
        }

        /**
         * Функция, проверяющая, залогинен ли пользователь
         * @return true если да, false если нет
         */
        boolean isLogin () {
            return getCookieLogin() != null;
        }

        /**
         * Функция, получающая куку с логином
         * @return Строку с логином
         */
        String getCookieLogin () {
            return Cookies.getCookie(cookieName);
        }

        /**
         * Функция, которая кладет куку с логином
         * @param login имя пользователя
         */
        void PutLoginCookie (String login) {
            Date expires = new Date((new Date()).getTime() + COOKIE_TIMEOUT);
            Cookies.setCookie(cookieName, login, expires);
        }


        /**
         * Функция инициализации входа
         */
        void Login() {

            if (isLogin()) { // Если мы уже вошли
                GoodLogin(getCookieLogin()); // То обновили куку об этом
            } else { // Иначе показали диалог входа
                LoginDialog.this.show();
                LoginDialog.this.center();
            }

        }


    }

    /**
     * Функция, получения MD5 хэша от строки + соль
     * @param in входная строка
     * @return хэш строки или null если ошибка
     */
    static String getMD5 (String in) {

        if (in.isEmpty()) {
            return null;
        }

        String salt = "SaltSalt";
        in = salt.toUpperCase() + in + salt.toLowerCase(); // "Солим" пароль
        // См https://ru.wikipedia.org/wiki/Соль_(криптография)

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(in.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            String hashText = number.toString(16);
            while (hashText.length() < 32) {
                hashText = "0" + hashText;
            }
            return hashText;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * Функция копирующая в буфер обмена текст по id
     * @param inName значение атрибута id
     */
    public static native void copyToClipboard(String inName) /*-{
        var selection = $wnd.getSelection();
        var text =  $doc.getElementById(inName);
        var range = $doc.createRange();
        range.selectNodeContents(text);
        selection.removeAllRanges();
        selection.addRange(range);
        $doc.execCommand('copy');
        selection.removeAllRanges();
    }-*/;

    /**
     * Функция получающая ip пользователя
     * @return ip
     */
    private native String getIP () /*-{
        return $wnd.userip;
    }-*/;


}
