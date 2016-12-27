package com.Administration.client;

import com.Administration.client.DataType.LinkData;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.i18n.client.DateTimeFormat;

import com.google.gwt.cell.client.*;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.SimplePager;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;

import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;


/**
 * Created by @author AlNat on 21.12.2016.
 * Licensed by Apache License, Version 2.0
 *
 * Класс, отвечающий за страницу администрирования
 */
@SuppressWarnings("Convert2Lambda")
public class Administration implements EntryPoint {

    // Данные для логина
    private String login; // Данные логина
    private String password; // Данные пароля
    private PasswordDialog dialog; // Далоговое окно с вводом логина и пароля

    private HTML label; // Лебл с ошибками

    private CellTable <LinkData> cellTable; // Таблица
    private LinkedList<LinkData> list; // Данные
    private ListDataProvider<LinkData> dataProvider; // Провайдер данных
    private ListHandler<LinkData> sortHandler; // Сортировщик
    private SimplePager pager; // Pager - управление страницами данных


    /**
     * Основной метод в UI
     */
    public void onModuleLoad() {

        label = new HTML();
        label.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

        dialog = new PasswordDialog();
        list = new LinkedList<>(); // Создали лист с данными

        ProvidesKey<LinkData> KEY_PROVIDER = new ProvidesKey<LinkData>() { // Создали провайдер ключей - как будут браться ключи у объекта
            @Override
            public Object getKey(LinkData item) {
                return item == null ? null : item.getId();
            }
        };
        cellTable = new CellTable<>(KEY_PROVIDER); // Создали саму таблицу

        sortHandler = new ListHandler<>(list);
        cellTable.addColumnSortHandler(sortHandler);

        SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
        pager = new SimplePager(SimplePager.TextLocation.CENTER, pagerResources, false, 0, true); // Создали pager - управление страницами
        pager.setDisplay(cellTable); // Установили, что pager правляет имеенно этой таблицей

        dataProvider = new ListDataProvider<>(); // Провайдер данных в таблице
        dataProvider.addDataDisplay(cellTable); // Установили, что данные относяться именно к этой таблице


        initTable(); // Создаем таблицу
        cellTable.setWidth("100%");
        cellTable.setHeight("80%");
        cellTable.setAutoHeaderRefreshDisabled(true);
        cellTable.setAutoFooterRefreshDisabled(true);

        VerticalPanel VP = new VerticalPanel();
        VP.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        VP.add(cellTable);
        VP.add(pager);
        VP.add(label);
        RootPanel.get("Data").add(VP);

        // Показали диалог и скрыли таблицу
        dialog.show();
        dialog.center();

        cellTable.setVisible(false);
        pager.setVisible(false);
    }


    /**
     * Диалоговое окно для ввода логина и пароля
     */
    private class PasswordDialog extends DialogBox {

        PasswordDialog() { // Конструктор
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
            panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

            button.addClickHandler(new ClickHandler() { // Повесли обработчик нажатия на кнопку
                public void onClick(ClickEvent event) {

                    login = loginTextBox.getText();
                    password = getMD5(passwordTextBox.getText()); // Получили хэш текст пароля

                    // Пошли на сервер
                    AdministrationServiceInterface.App.getInstance().isUser(login, password, new AsyncCallback<Boolean>() {
                        @Override
                        public void onFailure(Throwable caught) { // При ошибке говорим
                            label.setHTML("<h3>Server error!</h3><br>");
                        }

                        @Override
                        public void onSuccess(Boolean result) { // Если есть ответ
                            if (result) { // Если зашли под этим логином и паролем, то скрыли диалог и пошли за данными
                                dialog.hide();
                                GetData();
                            } else { // Иначе сказали про ошибку
                                label.setHTML("<h3>Incorrect username or password!</h3><br>");
                            }
                        }
                    });

                }
            });

            setWidget(panel);

        }


    }


    /**
     * Функция получения данных с сервера
     */
    private void GetData () {
        // Пошли на сервер за кучей данных
        AdministrationServiceInterface.App.getInstance().getData(login, new AsyncCallback<LinkData[]>() {
            @Override
            public void onFailure(Throwable caught) { // Если не смогли соедениться
                label.setHTML("<h3>Server error!</h3><br>");
            }

            @Override
            public void onSuccess(LinkData[] result) { // Получили данные
                AddData (result);
            }
        });

    }


    /**
     * Функция добавления данных в таблицу
     * @param linksData данные
     */
    private void AddData (LinkData[] linksData) {

        label.setHTML("");

        Collections.addAll(list, linksData); // Добавили данные
        dataProvider.setList(list); // Засовываем данные в таблицу

        sortHandler.setList(list); // Обновли сортировщик

        cellTable.setVisible(true); // И показываем ее
        pager.setVisible(true);
    }


    /**
     * Инициализация столбцов таблицы
     */
    private void initTable() {

        // TODO Разобраться, почему не сортирует столбцы

        // Колонка с коротким кодом
        Column<LinkData, String> codeColumn = new Column<LinkData, String>(new TextCell()) { // C видом ячеек - просто текст
            @Override
            public String getValue(LinkData object) { // Функция получения значения в ячейку
                return object.getCode();
            }
        };

        codeColumn.setSortable(true); // Разрешили сортировку
        sortHandler.setComparator(codeColumn, new Comparator<LinkData>() { // Установили компоратор
            // Это то, что будет сортировать объекты
            @Override
            public int compare(LinkData o1, LinkData o2) { // Функция сравнения
                return o1.getCode().compareTo(o2.getCode());
            }
        });

        cellTable.addColumn(codeColumn, "Short Link"); // Добавили колонку с названием к таблице


        // Оригинальная ссылка
        Column<LinkData, String> originalLinkColumn = new Column<LinkData, String>(new EditTextCell()) {
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

        originalLinkColumn.setFieldUpdater(new FieldUpdater<LinkData, String>() { // Создали хэндлер обновления
            @Override
            public void update(int index, final LinkData object, final String value) { // Вызывается при обновлении ячейки

                // Обновляем на сервере
                AdministrationServiceInterface.App.getInstance().setOriginalLink(object.getId(), value, new AsyncCallback<Boolean>() {
                    @Override
                    public void onFailure(Throwable caught) { // Если не смогли соедениться
                        label.setHTML("<h4>Connection error!<br>Can't update data!<h4>"); // Пишем об этом
                    }

                    @Override
                    public void onSuccess(Boolean result) { // Если сервер вернул ответ
                        if (!result) { // Если сервер не смог записать в базу
                            label.setHTML("<h4>Server error!<h4>"); // Пишем ошибку
                        } else { // Иначе устанавливаем значение и обновляем таблицу
                            object.setLink(value);
                            dataProvider.refresh();
                        }
                    }
                });

            }
        });



        DateTimeFormat dateFormat = DateTimeFormat.getFormat("dd MMM yyyy"); // Формат вывода даты

        // Время создания
        Column<LinkData, Date> createDateColumn = new Column<LinkData, Date>(new DateCell(dateFormat)) {
            @Override
            public Date getValue(LinkData object) {
                return object.getCreateDate();
            }
        };

        createDateColumn.setSortable(true);
        sortHandler.setComparator(createDateColumn, new Comparator<LinkData>() {
            @Override
            public int compare(LinkData o1, LinkData o2) {
                return o1.getCreateDate().compareTo(o2.getCreateDate());
            }
        });

        cellTable.addColumn(createDateColumn, "Create date");


        // Настроили ячейку выбора
        DatePickerCell cell = new DatePickerCell(dateFormat);

        // Срок окончания
        Column<LinkData, Date> expiredDateColumn = new Column<LinkData, Date>(cell) {
            @Override
            public Date getValue(LinkData object) {
                return object.getExpiredDate();
            }
        };

        expiredDateColumn.setSortable(true);
        expiredDateColumn.setDefaultSortAscending(false);
        sortHandler.setComparator(expiredDateColumn, new Comparator<LinkData>() {
            @Override
            public int compare(LinkData o1, LinkData o2) {
                return o1.getExpiredDate().compareTo(o2.getExpiredDate());
            }
        });

        cellTable.addColumn(expiredDateColumn, "Expired Date");

        expiredDateColumn.setFieldUpdater(new FieldUpdater<LinkData, Date>() {
            @Override
            public void update(int index, final LinkData object, final Date value) {
                AdministrationServiceInterface.App.getInstance().setExpiredDate(object.getId(), value, new AsyncCallback<Boolean>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        label.setHTML("<h4>Connection error!<br>Can't update data!<h4>");
                    }

                    @Override
                    public void onSuccess(Boolean result) {
                        if (!result) {
                            label.setHTML("<h4>Server error!<h4>");
                        } else {
                            object.setExpiredDate(value);
                            dataProvider.refresh();
                        }
                    }
                });
            }
        });


        // Кол-во переходов
        Column<LinkData, Number> currentCountColumn = new Column<LinkData, Number>(new NumberCell()) {
            @Override
            public Number getValue(LinkData object) {
                return object.getCurrentCount();
            }
        };

        currentCountColumn.setSortable(true);
        sortHandler.setComparator(currentCountColumn, new Comparator<LinkData>() {
            @Override
            public int compare(LinkData o1, LinkData o2) {
                return o1.getCurrentCount() > o2.getCurrentCount() ? 1 : 0;
                //return o1.getCurrentCount().compareTo(o2.getCurrentCount());
            }
        });

        cellTable.addColumn(currentCountColumn, "Current visits");


        // К сожалению, в GWT нет типа EditNumberCell. Вообще.
        // Только строковый тип изменяемой ячейки. Поэтому приходиться городить костыли.

        // Максимальное кол-во переходов
        Column<LinkData, String> maxCountColumn = new Column<LinkData, String>(new EditTextCell()) {
            @Override
            public String getValue(LinkData object) {

                Integer t = object.getMaxCount();

                if (t == 0) {
                    return "Infinity";
                } else {
                    return t.toString(); // Потому что GWT!!!
                }

            }
        };

        maxCountColumn.setSortable(true);
        sortHandler.setComparator(maxCountColumn, new Comparator<LinkData>() {
            @Override
            public int compare(LinkData o1, LinkData o2) {
                return o1.getMaxCount().compareTo(o2.getMaxCount());
            }
        });

        cellTable.addColumn(maxCountColumn, "Max Visits");

        maxCountColumn.setFieldUpdater(new FieldUpdater<LinkData, String>() {
            @Override
            public void update(int index, final LinkData object, final String value) {

                final Integer t;
                if (value.equals("Infinity")) {
                    t = 0;
                } else {
                    t = Integer.parseInt(value);
                }

                if (t < 0) {
                    label.setHTML("Wrong maximum count!");
                }

                AdministrationServiceInterface.App.getInstance().setMaxCount(object.getId(), t, new AsyncCallback<Boolean>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        label.setHTML("<h4>Connection error!<br>Can't update data!<h4>");
                    }

                    @Override
                    public void onSuccess(Boolean result) {
                        if (!result) {
                            label.setHTML("Server error!");
                        } else {
                            object.setMaxCount(t);
                            dataProvider.refresh();
                        }
                    }
                });


            }
        });


        // Пароль
        Column<LinkData, String> passwordColumn = new Column<LinkData, String>(new EditTextCell()) {
            @Override
            public String getValue(LinkData object) {
                if (object.getPassword().equals("")) {
                    return "";
                } else { // Тк пароль у нас шифрован, то показывать хэши мы не будем
                    return "*********";
                }
            }
        };

        cellTable.addColumn(passwordColumn, "Password");

        passwordColumn.setFieldUpdater(new FieldUpdater<LinkData, String>() {
            @Override
            public void update(int index, final LinkData object, final String value) {

                AdministrationServiceInterface.App.getInstance().setPassword(object.getId(), getMD5(value), new AsyncCallback<Boolean>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        label.setHTML("<h4>Connection error!<br>Can't update data!<h4>");
                    }

                    @Override
                    public void onSuccess(Boolean result) {
                        if (!result) {
                            label.setHTML("<h4>Server error!<h4>");
                        } else {
                            object.setPassword(getMD5(value));
                            dataProvider.refresh();
                        }
                    }
                });
            }
        });


        // Удаление ссылки
        Column<LinkData, String> deleteColumn = new Column<LinkData, String>(new ButtonCell()) {
            @Override
            public String getValue(LinkData object) {
                return "Delete link"; // + object.getCode(); // + "' link";
            }
        };

        cellTable.addColumn(deleteColumn, "");

        deleteColumn.setFieldUpdater(new FieldUpdater<LinkData, String>() {
            @Override
            public void update(int index, final LinkData object, String value) {

                if (Window.confirm("Shortlink " + object.getCode() + " will be delete!") ) {
                    AdministrationServiceInterface.App.getInstance().deleteLink(object.getId(), new AsyncCallback<Boolean>() {
                        @Override
                        public void onFailure(Throwable caught) {
                            label.setHTML("<h4>Connection error!<br>Can't delete data!<h4>");
                        }

                        @Override
                        public void onSuccess(Boolean result) {
                            if (!result) {
                                label.setHTML("<h4>Server error!<h4>");
                            } else { // Удаляем данный объект из списка
                                dataProvider.getList().remove(object);
                                dataProvider.refresh();
                            }
                        }
                    });
                }
            }
        });

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
            byte[] messageDigestBytes = md.digest(in.getBytes());
            BigInteger number = new BigInteger(1, messageDigestBytes);
            String hashText = number.toString(16);

            while (hashText.length() < 32) {
                hashText = "0" + hashText;
            }
            return hashText;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }

    }

}