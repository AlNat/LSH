package com.LSH.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.LSH.client.LSHService;

public class LSHServiceImpl extends RemoteServiceServlet implements LSHService {

    public String getMessage(String msg) {

        String answer = Shortner.GetShort(Integer.parseInt(msg));

        answer = Normalizer.Normolize(answer);

        return answer;

        /*
        if (!isOk(answer) ) {
            return "Error Massage";
        } else {
            return "Id link \"" + msg + "\"<br>Short code: " + answer;
        }*/
    }


    // TODO струтура данных со стоп словами
    private boolean isOk (String in) {

        return true;
    }

}