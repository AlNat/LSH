package com.LSH.server;

import com.LSH.client.Message;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.LSH.client.LSHService;

public class LSHServiceImpl extends RemoteServiceServlet implements LSHService {

    public String getSimpleShort(String msg) {

        /*
        String answer = Shortner.GetShort(Integer.parseInt(msg));

        answer = Normalizer.Normalize(answer);

        return answer;*/

        return Shortner.GetShort(Integer.parseInt(msg));
    }

    public String getComplexShort(Message msg) {
        String status = "OK";

        return status;
    }


}