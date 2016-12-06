package com.LSH.server.Log;

import com.LSH.client.GetLinkData;
import com.LSH.client.PutLinkData;

import java.io.Serializable;

/**
 * Created by @author AlNat on 06.12.2016.
 * Licensed by Apache License, Version 2.0
 */
public class LogEvent implements Serializable {
    // TODO Сеттеры на data.
    // TODO Комменатрии
    // TODO Придумать типа id сессии или что-то подобное

    private String date;
    private String massage;
    private String type;
    private GetLinkData getLinkData = null;
    private PutLinkData putLinkData = null;

    String Write () {
        if (getLinkData != null) {
            // Write putLinkData
        } else if (putLinkData != null) {
            // Write getLinkData
        } else {
            // Write Other
        }
        return "";
    }

    LogEvent (String massage, String type) {
        this.massage = massage;
        this.type = type;
        //date = System.currentTimeMillis(); // TODO В нормальном виде
    }

}
