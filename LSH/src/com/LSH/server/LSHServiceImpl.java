package com.LSH.server;

import com.LSH.client.Message;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.LSH.client.LSHService;

/**
 * Основной класс сервера
 */
public class LSHServiceImpl extends RemoteServiceServlet implements LSHService {

    static String errorCode = "Error!";
    private static String site = "www.site.com/#"; // Префикс ссылки.
    //TODO На будущее - Подумать, как ее можно получать при настройке приложения

    /**
     * Метод сокращающий ссылу
     * @param msg сообщение с клиента с данными ссылки
     * @return короткую ссылку или сообщение об ошибке
     */
    public String getShort(Message msg) {

        if (msg.getMaxVisits() == null) { // Если кол-во визитов не установлено - возращаем ошибку
            return errorCode + "<br>Sorry, but count of visits must be initialized!";
        }

        String link = msg.getOriginalLink(); // Получаем оригинальный линк
        String norm = Normalizer.Normalize(link); // Нормализауем его

        if ( norm.equals(errorCode) ) { // Если нормализация не удалась, то возращаем ошибку
            return errorCode + "<br>Sorry, but link are illegal!";
        }

        msg.setOriginalLink(norm); // Иначе приводим ссылку к нормализованному виду
        String answer = DBConnect.Put(msg); // И посылаем ее в БД
        if (answer.startsWith(errorCode) ) { // Если есть ошибка, то возращаем ее
            return answer;
        } else { // Иначе отдаем полную короткую ссылку вида : www.site.com/short
            return site + answer;
        }

    }


}