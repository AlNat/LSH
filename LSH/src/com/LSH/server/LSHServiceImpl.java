package com.LSH.server;

import com.LSH.client.Message;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.LSH.client.LSHService;

// TODO Комментарии + JavaDoc
public class LSHServiceImpl extends RemoteServiceServlet implements LSHService {

    public String getShort(Message msg) {

        /*
        // TODO расскоментировать
        String link = msg.getOriginalLink();
        String norm = Normalizer.Normalize(link);

        if ( norm.equals("ERROR!") ) {
            return "Sorry, but link are illegal!";
        } else {
            msg.setOriginalLink(norm);
            return DBConnect.Put(msg);
        }
        */
        return Shortner.GetShort(Integer.parseInt(msg.getOriginalLink()));
    }


}