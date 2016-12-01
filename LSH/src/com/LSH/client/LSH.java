package com.LSH.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ClickEvent;

/**
 * Класс, отвечающий за главную страницу UI
 */
public class LSH implements EntryPoint {

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {

        // TODO Сложную панель
        // TODO Выравниваение и красату
        HorizontalPanel simpleShortHP = new HorizontalPanel();
        VerticalPanel simpleShortVP = new VerticalPanel();
        final Button simpleButton = new Button("Get Short Link");
        final HTML simpleError = new HTML("");
        final TextBox simpleOriginalLink = new TextBox();
        final HTML simpleShortLink = new HTML("");

        simpleButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (simpleError.getText().equals("")) {
                    LSHService.App.getInstance().getSimpleShort(simpleOriginalLink.getText(), new AsyncCallback<String>() {
                        @Override
                        public void onFailure(Throwable caught) {
                            simpleError.setText("Cannot connect to server!");
                        }
                        @Override
                        public void onSuccess(String result) {
                            simpleShortLink.setText(result);

                            // ToDO Классть ссылку в буфер обмена
                            // TODO по enter тоже отлавливать
                        }
                    });
                } else {
                    simpleError.setText("Cannot connect");
                }
            }
        });

        simpleShortHP.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        simpleShortHP.add(simpleOriginalLink);
        simpleShortHP.add(simpleButton);

        simpleShortVP.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        simpleShortVP.add(simpleShortHP);
        simpleShortVP.add(simpleShortLink);
        simpleShortVP.add(simpleError);



        RootPanel.get("SimpleShort").add(simpleShortVP);
    }


// TODO Вынести классы во вне
    private static class MyAsyncCallback implements AsyncCallback<String> {
        private Label label;

        public MyAsyncCallback(Label label) {
            this.label = label;
        }

        public void onSuccess(String result) {
            label.getElement().setInnerHTML(result);
        }

        public void onFailure(Throwable throwable) {
            label.setText("Failed to receive answer from server!");
        }
    }
}
