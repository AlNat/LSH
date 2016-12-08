package com.LSH.server.Log;

import com.LSH.client.GetLinkData;
import com.LSH.client.PutLinkData;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by @author AlNat on 06.12.2016.
 * Licensed by Apache License, Version 2.0
 */
class LogEvent implements Serializable {
    // TODO Комменатрии
    // TODO Придумать типа id сессии или что-то подобное

    private String date;
    private String massage;
    private String type;
    private String className;
    private GetLinkData getLinkData = null;
    private PutLinkData putLinkData = null;

    void setMassage(String massage) {
        this.massage = massage;
    }

    void setType(String type) {
        this.type = type;
    }

    void setClassName(String className) {
        this.className = className;
    }

    LogEvent (GetLinkData getLinkData) {
        this.getLinkData = getLinkData;
    }

    LogEvent (PutLinkData putLinkData) {
        this.putLinkData = putLinkData;
    }


    String Write () {

        date = new Date (System.currentTimeMillis() ).toString();
        String a = "[" + date + "] Event=" + type + "; ClassName=" + className + "; Message=" + massage + ";";

        if (getLinkData != null) {
            a += "Data:" + putLinkData.toLog() + ";";
        } else if (putLinkData != null) {
            a += "Data:" + putLinkData.toLog() + ";";
        }
        return a;
    }


}
