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

import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;

// http://samples.gwtproject.org/samples/Showcase/Showcase.html#!CwCellTable

/**
 * Класс, отвечающий за страницу администрирования
 */
@SuppressWarnings("Convert2Lambda")
public class Administration implements EntryPoint {

    // Данные для логина
    private String login; // Данные логина
    private String password; // Данные пароля
    private PasswordDialog dialog; // Далоговое окно с вводом логина и пароля

    private HTML label; // Лебл с ошибками

    // Данные таблицы
    private LinkedList<LinkData> list; // Лист с данными с сервера
    private CellTable <LinkData> cellTable; // Таблица

    // TODO Пристроить это уже куда-нибудь
    private ListDataProvider<LinkData> dataProvider;
    private ProvidesKey<LinkData> KEY_PROVIDER;
    private ListHandler<LinkData> sortHandler;
    private SimplePager pager;

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

        RootPanel.get().add(label);
        RootPanel.get("Data").add(cellTable);
        // TODO Придумать способ поставить это на другую страницу - Administration.html

        // Показали диалог и скрыли таблицу
        dialog.show();
        cellTable.setVisible(false);

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
        AdministrationServiceInterface.App.getInstance().getData(login, new AsyncCallback<LinkedList<LinkData>>() {
            @Override
            public void onFailure(Throwable caught) { // Если не смогли соедениться
                label.setHTML("<h3>Server error!</h3><br>");
            }

            @Override
            public void onSuccess(LinkedList<LinkData> result) { // Получили данные
                list = result;
            }
        });

        initTable(); // Создаем таблицу
        dataProvider.setList(list);
        cellTable.setRowData(list); // Засовываем данные в таблицу
        // Оно так????

        cellTable.setVisible(true); // И показываем ее
    }


    /**
     * Инициализация столбцов таблицы
     */
    private void initTable() {

        // Колонка с коротким кодом
        Column<LinkData, String> codeColumn = new Column<LinkData, String>(
                new TextCell()) { // C видом ячеек - просто текст
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
        cellTable.addColumn(codeColumn, "Short Code"); // Добавили колонку с названием к таблице
        cellTable.setColumnWidth(codeColumn, 20, Style.Unit.PCT); // Настроили ее ширину


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

        originalLinkColumn.setFieldUpdater(new FieldUpdater<LinkData, String>() { // Создали хэндлер обновления
            @Override
            public void update(int index, LinkData object, String value) { // Вызывается при обновлении ячейки

                // Обновляем на сервере
                AdministrationServiceInterface.App.getInstance().setOriginalLink(object.getId(), value, new AsyncCallback<Boolean>() {
                    @Override
                    public void onFailure(Throwable caught) { // Если не смогли соедениться
                        label.setHTML("Connection error!<br>Can't update data!"); // Пишем об этом
                    }

                    @Override
                    public void onSuccess(Boolean result) { // Если сервер вернул ответ
                        if (!result) { // Если сервер не смог записать в базу
                            label.setHTML("Server error!"); // Пишем ошибку
                        } else { // Иначе устанавливаем значение и обновляем таблицу
                            object.setLink(value);
                            dataProvider.refresh();
                        }
                    }
                });

            }
        });
        cellTable.setColumnWidth(originalLinkColumn, 20, Style.Unit.PCT);


        // Время создания
        Column<LinkData, Date> createTimeColumn = new Column<LinkData, Date>(new DateCell()) {
            @Override
            public Date getValue(LinkData object) {
                return object.getCreateDate();
            }
        };
        createTimeColumn.setSortable(true);
        sortHandler.setComparator(createTimeColumn, new Comparator<LinkData>() {
            @Override
            public int compare(LinkData o1, LinkData o2) {
                return o1.getCreateDate().compareTo(o2.getCreateDate());
            }
        });
        cellTable.addColumn(createTimeColumn, "Create time");


        // Срок окончания
        DateTimeFormat dateFormat = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_MEDIUM);
        Column<LinkData, Date> expiredDateColumn = new Column<LinkData, Date>(new DatePickerCell(dateFormat)) {
            @Override
            public Date getValue(LinkData object) {
                return object.getCreateDate();
            }
        };

        expiredDateColumn.setSortable(true);
        sortHandler.setComparator(expiredDateColumn, new Comparator<LinkData>() {
            @Override
            public int compare(LinkData o1, LinkData o2) {
                return o1.getExpiredDate().compareTo(o2.getExpiredDate());
            }
        });
        cellTable.addColumn(expiredDateColumn, "Expired Date");

        expiredDateColumn.setFieldUpdater(new FieldUpdater<LinkData, Date>() {
            @Override
            public void update(int index, LinkData object, Date value) {
                AdministrationServiceInterface.App.getInstance().setExpiredDate(object.getId(), value, new AsyncCallback<Boolean>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        label.setHTML("Connection error!<br>Can't update data!");
                    }

                    @Override
                    public void onSuccess(Boolean result) {
                        if (!result) {
                            label.setHTML("Server error!");
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
                return o1.getCurrentCount().compareTo(o2.getCurrentCount());
            }
        });
        cellTable.addColumn(currentCountColumn, "Current visits");
        cellTable.setColumnWidth(currentCountColumn, 5, Style.Unit.PCT);


        // К сожалению, в GWT нет типа EditNumberCell. Вообще.
        // Только строковый тип изменяемой ячейки. Поэтому приходиться городить костыли.

        // Максимальное кол-во переходов
        Column<LinkData, String> maxCountColumn = new Column<LinkData, String>(
                new EditTextCell()) {
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
            public void update(int index, LinkData object, String value) {

                Integer t;
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
                        label.setHTML("Connection error!<br>Can't update data!");
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
        cellTable.setColumnWidth(maxCountColumn, 5, Style.Unit.PCT);


        // Пароль
        Column<LinkData, String> passwordColumn = new Column<LinkData, String>(new EditTextCell()) {
            @Override
            public String getValue(LinkData object) {
                return "*********";
            }
        };

        passwordColumn.setFieldUpdater(new FieldUpdater<LinkData, String>() {
            @Override
            public void update(int index, LinkData object, String value) {

                String t = getMD5(value);

                AdministrationServiceInterface.App.getInstance().setPassword(object.getId(), t, new AsyncCallback<Boolean>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        label.setHTML("Connection error!<br>Can't update data!");
                    }

                    @Override
                    public void onSuccess(Boolean result) {
                        if (!result) {
                            label.setHTML("Server error!");
                        } else {
                            object.setPassword(t);
                            dataProvider.refresh();
                        }
                    }
                });
            }
        });
        cellTable.setColumnWidth(passwordColumn, 15, Style.Unit.PCT);

        // TODO Кнопку с красным крестиком и подтвердением на него - удаление ссылки
        // http://samples.gwtproject.org/samples/Showcase/Showcase.html#!CwCellSampler


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
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }

    }


}
