package com.LSH.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.LSH.client.LSHService;

public class LSHServiceImpl extends RemoteServiceServlet implements LSHService {

    private Shortner shortner;

    public void Init () {
        shortner = new Shortner();
    }

    public String getMessage(String msg) {

        shortner.hashCode();

        return "Client said: \"" + msg + "\"<br>Server answered: \"Hi!\"";
    }
}