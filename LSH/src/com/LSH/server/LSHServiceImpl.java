package com.LSH.server;

import com.LSH.client.Message;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.LSH.client.LSHService;

/**
 * Основной класс сервера
 */
public class LSHServiceImpl extends RemoteServiceServlet implements LSHService {

    private static String site = "www.site.com/"; // Префикс ссылки

    /**
     * Метод сокращающий ссылу
     * @param msg сообщение с клиента с данными ссылки
     * @return короткую ссылку
     */
    public String getShort(Message msg) {

        /*
        // TODO расскоментировать
        String link = msg.getOriginalLink();
        String norm = Normalizer.Normalize(link);

        if ( norm.equals("ERROR!") ) {
            return "Sorry, but link are illegal!";
        } else {
            msg.setOriginalLink(norm);
            return site + DBConnect.Put(msg);
        }
        */
        return Shortner.GetShort(Integer.parseInt(msg.getOriginalLink()));
    }


}