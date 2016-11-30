package com.LSH.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ClickEvent;

/**
 * Класс UI
 */
public class LSH implements EntryPoint {

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        final Button button = new Button( "Message");
        final Label label = new Label();
        final TextBox tx = new TextBox();

        button.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (label.getText().equals("")) {
                    LSHService.App.getInstance().getMessage(tx.getText(), new MyAsyncCallback(label));
                } else {
                    label.setText("");
                }
            }
        });

        HorizontalPanel simpleShort = new HorizontalPanel();
        simpleShort.add(button);
        simpleShort.add(tx);
        simpleShort.add(label);

        RootPanel.get("SimpleShort").add(simpleShort);
    }

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
