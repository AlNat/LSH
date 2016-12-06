package com.LSH.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Класс, отвечающий за главную страницу UI
 */
public class LSH implements EntryPoint {

    private static String errorCode = "Error!"; // Код ошибки

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

    private final HTML complexCountText = new HTML("Set count of visits <br>(0 for unlimited):"); // Тест перед полем для кол-во переходов
    private final IntegerBox complexCount = new IntegerBox(); // Поле ввода кол-во переходов
    private final HTML complexNameText = new HTML("Customize link:"); // Текст перед полем для кастомизации ссыли
    private final TextBox complexName = new TextBox(); // Само поле кастомизации ссылки

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
        final HorizontalPanel complexDataHP = new HorizontalPanel(); // Данные
        final HorizontalPanel complexData2HP = new HorizontalPanel(); // Данные 2
        final HorizontalPanel complexAnswerHP = new HorizontalPanel(); // Ответ
        final VerticalPanel complexVP = new VerticalPanel(); // Хранение того, что выше

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

        // Настроили и добавили данные к панелям
        complexDataHP.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        complexDataHP.setSpacing(5);
        complexDataHP.add(complexText);
        complexDataHP.add(complexOriginalLink);
        complexDataHP.add(complexTimeText);
        complexDataHP.add(complexTime);

        complexData2HP.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        complexData2HP.setSpacing(5);
        complexData2HP.add(complexCountText);
        complexData2HP.add(complexCount);
        complexData2HP.add(complexNameText);
        complexData2HP.add(complexName);

        complexAnswerHP.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        complexAnswerHP.setSpacing(5);
        complexAnswerHP.add(complexShortText);
        complexAnswerHP.add(complexShortLink);
        complexAnswerHP.add(complexCopyButton);

        complexVP.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        complexVP.setSpacing(15);
        complexVP.add(complexDataHP);
        complexVP.add(complexData2HP);
        complexVP.add(complexShortButton);
        complexVP.add(complexAnswerHP);

        // Скрыли кнопки ответа
        complexShortText.setVisible(false);
        complexCopyButton.setVisible(false);

        // Устанавливаем наши панель на страницу
        RootPanel.get("SimpleShort").add(simpleVP);
        RootPanel.get("ComplexShort").add(complexVP);
    }

    /**
     * Класс, который реагирует на клик на кнопку получить простую короткую ссылку
     */
    private class SimpleClickHandler implements ClickHandler {
        public void onClick(ClickEvent event) {

            PutLinkData putLinkData = new PutLinkData(simpleOriginalLink.getText()); // Формируем сообщение на сервер

            LSHServiceInterface.App.getInstance().getShort(putLinkData, new AsyncCallback<String>() { // Отпраляем сообщение и получаем ответ
                @Override
                public void onFailure(Throwable caught) { // При неудачном соединению с сервером
                    simpleShortLink.setText("Cannot connect to server!");
                    }
                @Override
                public void onSuccess(String result) { // При удаче
                    if (result.startsWith(errorCode)) { // Если вернули код ошиби то показываем его
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

            // И отправляем его. Что внутри - см выше
            LSHServiceInterface.App.getInstance().getShort(putLinkData, new AsyncCallback<String>() {
                @Override
                public void onFailure(Throwable caught) {
                    complexShortLink.setText("Cannot connect to server!");
                }
                @Override
                public void onSuccess(String result) {
                    if (result.startsWith(errorCode)) {
                        complexShortLink.setHTML(result);
                    } else {
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
        String id; // Простая или управляемая ссылка долна быть скопирована

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
        Button button; // Кнопка

        EnterKeyListener (Button button) {
            this.button = button;
        }

        @Override
        public void onKeyDown(KeyDownEvent event) {
            if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                button.click(); // Наимаем на кнопку по наатию клавиши Enter
            }
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

}
