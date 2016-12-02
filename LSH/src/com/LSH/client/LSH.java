package com.LSH.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.rpc.AsyncCallback;

//TODO Комментарии обычные и JavaDoc. Рефакторинг -  Переименовать и перегруппировать. Может вынести

/**
 * Класс, отвечающий за главную страницу UI
 */
public class LSH implements EntryPoint {

    // Набор полей в простом сокращении
    private final TextBox simpleOriginalLink = new TextBox();
    private final Button simpleShortButton = new Button("Get Short Link");
    private final Button simpleCopyButton = new Button("Copy to clipboard");
    private final HTML simpleShortText = new HTML("Your shortlink - ");
    private final HTML simpleShortLink = new HTML("");

    // Набор полей в управляемом сокращении
    private final Button complexShortButton = new Button("Get Short Link");
    private final Button complexCopyButton = new Button("Copy to clipboard");
    private final HTML complexShortText = new HTML("Your shortlink - ");
    private final HTML complexShortLink = new HTML("");

    private final HTML complexText = new HTML("Link:");
    private final TextBox complexOriginalLink = new TextBox();
    private final HTML complexTimeText = new HTML("Set link live duration:");
    private final ListBox complexTime = new ListBox();

    private final HTML complexCountText = new HTML("Set count of visits <br>(0 for unlimited):");
    private final IntegerBox complexCount = new IntegerBox();
    private final HTML complexNameText = new HTML("Customize link:");
    private final TextBox complexName = new TextBox();

    /**
     * Основной метод в UI - аналог конструктора в GWT
     */
    public void onModuleLoad() {

        // Создаем простое сокращение
        final HorizontalPanel simpleShortHP = new HorizontalPanel();
        final HorizontalPanel simpleShortHP2 = new HorizontalPanel(); // TODO Rename!
        final VerticalPanel simpleShortVP = new VerticalPanel();

        simpleShortButton.addClickHandler(new SimpleClickHandler() );
        simpleOriginalLink.addKeyDownHandler(new EnterKeyListener(simpleShortButton));
        simpleOriginalLink.setWidth("200px");

        simpleShortHP.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        simpleShortHP.setSpacing(5);
        simpleShortHP.add(simpleOriginalLink);
        simpleShortHP.add(simpleShortButton);

        simpleShortHP2.setSpacing(5);
        simpleShortHP2.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        simpleShortHP2.add(simpleShortText);
        simpleShortHP2.add(simpleShortLink);
        simpleShortHP2.add(simpleCopyButton);

        simpleShortVP.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        simpleShortVP.setSpacing(10);
        simpleShortVP.add(simpleShortHP);
        simpleShortVP.add(simpleShortHP2);

        simpleShortText.setVisible(false);
        simpleCopyButton.setVisible(false);
        simpleCopyButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                copyToClipboard(simpleShortLink.getText());
            }
        });



        // Создаем управляемое сокращение
        final HorizontalPanel complexShortHP = new HorizontalPanel();
        final HorizontalPanel complexShortHP2 = new HorizontalPanel(); // TODO Rename
        final HorizontalPanel complexShortHP3 = new HorizontalPanel(); // TODO Rename
        final VerticalPanel complexShortVP = new VerticalPanel();
        EnterKeyListener complexKey = new EnterKeyListener(complexShortButton);

        // Заполняем данными
        complexTime.addItem("1 hour");
        complexTime.addItem("12 hours");
        complexTime.addItem("1 day");
        complexTime.addItem("1 week"); // TODO по дефолту выбирать эту строку
        complexTime.addItem("1 month");
        complexTime.addItem("Unlimited");
        complexTime.setSelectedIndex(2);

        complexOriginalLink.setHeight("200px");
        complexCount.setWidth("40px");
        complexCount.setValue(10);

        complexShortButton.addClickHandler(new ComplexClickHandler()); // Добавили хендлер наатия на кнопку
        // Добавляем хендлер нажатия клавишы Enter ко всем полям
        complexOriginalLink.addKeyDownHandler(complexKey);
        complexName.addKeyDownHandler(complexKey);
        complexCount.addKeyDownHandler(complexKey);
        complexTime.addKeyDownHandler(complexKey);

        complexShortHP.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        complexShortHP.setSpacing(8);
        complexShortHP.add(complexText);
        complexShortHP.add(complexOriginalLink);
        complexShortHP.add(complexTimeText);
        complexShortHP.add(complexTime);

        complexShortHP2.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        complexShortHP2.setSpacing(8);
        complexShortHP2.add(complexCountText);
        complexShortHP2.add(complexCount);
        complexShortHP2.add(complexNameText);
        complexShortHP2.add(complexName);

        complexShortHP3.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        complexShortHP3.setSpacing(8);
        complexShortHP3.add(complexShortText);
        complexShortHP3.add(complexShortLink);
        complexShortHP3.add(complexCopyButton);

        complexShortVP.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        complexShortVP.setSpacing(15);
        complexShortVP.add(complexShortHP);
        complexShortVP.add(complexShortHP2);
        complexShortVP.add(complexShortButton);
        complexShortVP.add(complexShortHP3);

        complexShortText.setVisible(false);
        complexCopyButton.setVisible(false);
        complexCopyButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                copyToClipboard(simpleShortLink.getText());
            }
        });

        RootPanel.get("SimpleShort").add(simpleShortVP);
        RootPanel.get("ComplexShort").add(complexShortVP);
    }

    /**
     * Класс, который реагирует на клик на кнопку получить короткую ссылку
     */
    private class SimpleClickHandler implements ClickHandler {
        public void onClick(ClickEvent event) {

            Message message = new Message(simpleOriginalLink.getText());

            LSHService.App.getInstance().getShort(message, new AsyncCallback<String>() {
                @Override
                public void onFailure(Throwable caught) {
                    simpleShortLink.setText("Cannot connect to server!");
                    }
                @Override
                public void onSuccess(String result) {
                    if (result.startsWith("ERROR")) {
                        simpleShortLink.setText(result);
                    } else {
                        simpleShortText.setVisible(true);
                        simpleCopyButton.setVisible(true);
                        simpleShortLink.setText(result);
                        simpleShortLink.getElement().setAttribute("id", "simpleAnswer");
                        copyToClipboard("simpleAnswer");
                        copyToClipboard(result);
                    }
                }
            });
        }
    }

    private class ComplexClickHandler implements ClickHandler {
        public void onClick(ClickEvent event) {

            // Формируем 'пакет' данных на сервер
            Message message;
            if (complexName.getText().isEmpty()) { // Вызываем конструктор в зависимости от того, есть ли мнемоника
                message = new Message( complexOriginalLink.getText(), complexTime.getSelectedItemText(), complexCount.getValue());
            } else {
                message = new Message( complexOriginalLink.getText(), complexTime.getSelectedItemText(), complexCount.getValue(), complexName.getText());
            }

            // И отправляем его
            LSHService.App.getInstance().getShort(message, new AsyncCallback<String>() {
                @Override
                public void onFailure(Throwable caught) {
                    complexShortLink.setText("Cannot connect to server!");
                }
                @Override
                public void onSuccess(String result) {
                    if (result.startsWith("ERROR")) {
                        complexShortLink.setText(result);
                    } else {
                        complexShortText.setVisible(true);
                        complexCopyButton.setVisible(true);
                        complexShortLink.setText(result);
                        complexShortLink.getElement().setAttribute("id", "complexAnswer");
                        copyToClipboard("complexAnswer");
                    }
                }
            });
        }
    }

    private static class EnterKeyListener implements KeyDownHandler {
        Button button;

        EnterKeyListener (Button button) {
            this.button = button;
        }

        @Override
        public void onKeyDown(KeyDownEvent event) {
            if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                button.click(); // Обрабатываем наатие на клавишу Enter вместо кнопки
            }
        }

    }

    // TODO Доделать
    public static native void copyToClipboard(String result) /*-{
        var area = document.getElementById("simpleAnswer");
        area.focus();
        area.select();
        document.execCommand("copy", false, null);
    }-*/;

}
