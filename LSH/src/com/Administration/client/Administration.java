package com.Administration.client;


import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.i18n.client.DateTimeFormat;

import com.google.gwt.cell.client.*;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.SimplePager;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;

import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;

import java.math.BigInteger;
import java.security.MessageDigest;

import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;


/**
 * Класс, отвечающий за страницу администрированич
 */
public class Administration implements EntryPoint {

    // TODO Комментарии!!!
    // http://samples.gwtproject.org/samples/Showcase/Showcase.html#!CwCellTable
    
    private String login;
    private String password;

    private HTML label;
    private PasswordDialog dialog;

    private LinkedList<LinkData> list;
    private CellTable <LinkData> cellTable;
    private ListDataProvider<LinkData> dataProvider;
    private ProvidesKey<LinkData> KEY_PROVIDER;
    private ListHandler<LinkData> sortHandler;
    private SimplePager pager; // TODO добавить  таблице

    /**
     * Основной метод в UI
     */
    public void onModuleLoad() {

        label = new HTML();
        label.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        dialog = new PasswordDialog();
        cellTable = new CellTable<>();

        KEY_PROVIDER = new ProvidesKey<LinkData>() {
            @Override
            public Object getKey(LinkData item) {
                return item == null ? null : item.getId();
            }
        };

        dataProvider = new ListDataProvider<>();
        sortHandler = new ListHandler<>(dataProvider.getList());
        cellTable.addColumnSortHandler(sortHandler);

        //initTable();

        RootPanel.get().add(label);
        RootPanel.get("Data").add(cellTable);
        // Придется делать отдельное приложение от слова совсем
        // И писать взаимодействие сервлетов
        // TODO Придумать способ поставить это на другую страницу - Administration.html

        dialog.show();
        cellTable.setVisible(false);

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
                            label.setHTML("<h3>Server error!</h3><br>");
                        }

                        @Override
                        public void onSuccess(Boolean result) {
                            if (result) {
                                dialog.hide();
                                GetData();
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

    /**
     *
     */
    private void GetData () {
        AdministrationServiceInterface.App.getInstance().getData(login, new AsyncCallback<LinkedList<LinkData>>() {
            @Override
            public void onFailure(Throwable caught) {
                label.setHTML("<h3>Server error!</h3><br>");
            }

            @Override
            public void onSuccess(LinkedList<LinkData> result) {
                list = result;
            }
        });

        // TODO засовывать данные в таблицу
        initTable();
        cellTable.setVisible(true);

    }


    /**
     *
     */
    private void initTable() {

        // TODO Выделение мышкой
        // TODO Добавить сортировки

        /// Колонка с коротким кодом


        Column<LinkData, String> codeColumn = new Column<LinkData, String>(
                new TextCell()) {
            @Override
            public String getValue(LinkData object) {
                return object.getCode();
            }
        };

        codeColumn.setSortable(true);
        sortHandler.setComparator(codeColumn, new Comparator<LinkData>() {
            @Override
            public int compare(LinkData o1, LinkData o2) {
                return o1.getCode().compareTo(o2.getCode());
            }
        });
        cellTable.addColumn(codeColumn, "Short Code");
        cellTable.setColumnWidth(codeColumn, 20, Style.Unit.PCT);


        // Оригинальная ссылка
        Column<LinkData, String> originalLinkColumn = new Column<LinkData, String>(
                new EditTextCell()) {
            @Override
            public String getValue(LinkData object) {
                return object.getLink();
            }
        };

        originalLinkColumn.setSortable(true);
        sortHandler.setComparator(originalLinkColumn, new Comparator<LinkData>() {
            @Override
            public int compare(LinkData o1, LinkData o2) {
                return o1.getLink().compareTo(o2.getLink());
            }
        });
        cellTable.addColumn(originalLinkColumn, "Original Link");

        originalLinkColumn.setFieldUpdater(new FieldUpdater<LinkData, String>() {
            @Override
            public void update(int index, LinkData object, String value) { // Called when the user changes the value.
                if ( UpdateLink(object, value) ) {
                    // TODO обновлять на сервере
                    object.setLink(value);
                    dataProvider.refresh();
                } else {
                    label.setHTML("Connection error!<br>Can't update data!");
                }
            }
        });
        cellTable.setColumnWidth(originalLinkColumn, 20, Style.Unit.PCT);



        Column<LinkData, Date> createTimeColumn = new Column<LinkData, Date>(new DateCell()) {
            @Override
            public Date getValue(LinkData object) {
                return object.getCreateDate();
            }
        };
        cellTable.addColumn(createTimeColumn, "Create time");


        DateTimeFormat dateFormat = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_MEDIUM);
        Column<LinkData, Date> expiredDateColumn = new Column<LinkData, Date>(new DatePickerCell(dateFormat)) {
            @Override
            public Date getValue(LinkData object) {
                return object.getCreateDate();
            }
        };
        cellTable.addColumn(expiredDateColumn, "Expired Date");
        expiredDateColumn.setFieldUpdater(new FieldUpdater<LinkData, Date>() {
            @Override
            public void update(int index, LinkData object, Date value) {
                if ( UpdateDate(object, value) ) {
                    // TODO обновлять на сервере
                    object.setExpiredDate(value);
                    dataProvider.refresh();
                } else {
                    label.setHTML("Connection error!<br>Can't update data!");
                }
            }
        });


        Column<LinkData, Number> currentCountColumn = new Column<LinkData, Number>(new NumberCell()) {
            @Override
            public Number getValue(LinkData object) {
                return object.getCurrentCount();
            }
        };
        cellTable.addColumn(currentCountColumn, "Current visits");

        // TODO Поорать на GWT
        Column<LinkData, String> maxCountColumn = new Column<LinkData, String>(
                new EditTextCell()) {
            @Override
            public String getValue(LinkData object) {
                return object.getMaxCount().toString(); // Потому что GWT!!!
            }
        };

        cellTable.addColumn(originalLinkColumn, "Max Visits");

        originalLinkColumn.setFieldUpdater(new FieldUpdater<LinkData, String>() {
            @Override
            public void update(int index, LinkData object, String value) {
                Integer t = Integer.parseInt(value);
                if (t == null || t < 0 ) {
                    label.setHTML("Wrong maximum count!");
                }

                if ( UpdateLink(object, t) ) {
                    // TODO обновлять на сервере
                    object.setMaxCount(t);
                    dataProvider.refresh();
                } else {
                    label.setHTML("Connection error!<br>Can't update data!");
                }
            }
        });
        cellTable.setColumnWidth(originalLinkColumn, 20, Style.Unit.PCT);



        Column<LinkData, String> passwordColumn = new Column<LinkData, String>(new EditTextCell()) {
            @Override
            public String getValue(LinkData object) {
                return "*********";
            }
        };

        passwordColumn.setFieldUpdater(new FieldUpdater<LinkData, String>() {
            @Override
            public void update(int index, LinkData object, String value) {

                // TODO обновлять на сервере
                if ( UpdatePassword(object, value) ) {
                    object.setPassword(getMD5(value));
                    dataProvider.refresh();
                } else {
                    label.setHTML("Connection error!<br>Can't update data!");
                }
            }
        });
        cellTable.setColumnWidth(passwordColumn, 20, Style.Unit.PCT);

    }

    /**
     * Функция, получения MD5 хэша от строки + соль
     * @param in входная строка
     * @return хэш строки или null если ошибка
     */
    private static String getMD5 (String in) {

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


}
