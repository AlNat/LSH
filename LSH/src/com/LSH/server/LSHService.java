package com.LSH.server;

import com.LSH.client.GetLinkData;
import com.LSH.client.PutLinkData;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.LSH.client.LSHServiceInterface;

/**
 * Основной класс сервера
 */
public class LSHService extends RemoteServiceServlet implements LSHServiceInterface {

    static String errorCode = "Error!";
    public static String siteLink = "http://127.0.0.1:8888/"; // Префикс ссылки.
    //TODO На будущее - Подумать, как ее можно получать при настройке приложения

    /**
     * Метод сокращающий ссылу
     * @param msg сообщение с клиента с данными ссылки
     * @return короткую ссылку или сообщение об ошибке
     */
    public String getShort(PutLinkData msg) {

        if (msg.getMaxVisits() == null) { // Если кол-во визитов не установлено - возращаем ошибку
            return errorCode + "<br>Sorry, but count of visits must be initialized!";
        }

        String link = msg.getOriginalLink(); // Получаем оригинальный линк
        String norm = Normalizer.Normalize(link); // Нормализауем его

        if ( norm.equals(errorCode) ) { // Если нормализация не удалась, то возращаем ошибку
            return errorCode + "<br>Sorry, but link are illegal!";
        }

        msg.setOriginalLink(norm); // Иначе приводим ссылку к нормализованному виду
        String answer = DBConnect.instance.Put(msg); // И посылаем ее в БД
        if (answer.startsWith(errorCode) ) { // Если есть ошибка, то возращаем ее
            return answer;
        } else { // Иначе отдаем полную короткую ссылку вида : http://www.site.com/#short
            return siteLink + answer;
        }

    }

    /**
     * Функция, принимающая короткую ссылку и возращающая или ошибку или оригинальный линк
     * @param getLinkData данные об переходе
     * @return оригинальную ссылку или код ошибки
     */
    public String getOriginal (GetLinkData getLinkData) {

        String code = getLinkData.getCode();
        code = Normalizer.ShortNormalize(code); // Нормализуем код

        if (code.equals(errorCode)) { // Если это ошибка то вернули ее
            return errorCode + "<br>Invalid code!";
        }

        getLinkData.setCode(code); // Установили нормализованный код

        return DBConnect.instance.Get(getLinkData); // Возращаем ответ - там будет или ошибка или нормальный код для редиректа

    }


}