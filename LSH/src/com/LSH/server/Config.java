package com.LSH.server;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;


/**
 * Created by @author AlNat on 07.12.2016.
 * Licensed by Apache License, Version 2.0
 *
 * Класс, читающий конфиг файл и отдающий данные
 */
public class Config {

    private String filename = "C:\\Users\\AlNat\\Source\\Studi\\Diplom\\config.xml";
    // TODO искать эти данные рядом с сервером

    // Инстанс - паттерн Синглтон
    public static final Config instance = new Config();

    // Геттеры
    public String getSiteLink() {
        return SiteLink;
    }

    public String getLogFile() {
        return LogFile;
    }

    public String getLogin() {
        return Login;
    }

    public String getPassword() {
        return Password;
    }

    public String getURL() {
        return URL;
    }


    // Данные
    private String SiteLink;
    private String LogFile;
    private String Login;
    private String Password;
    private String URL;

    private Config () {
        ReadConfig();
    }

    private void ReadConfig () {

        try {

            File file = new File(filename);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true); // never forget this!
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);

            Node root = doc.getDocumentElement(); // Получили корень
            NodeList conf = root.getChildNodes(); // Получили дерево config
            for (int i = 0; i < conf.getLength(); i++) { // Идем по всем поднодам

                Node item = conf.item(i);

                if ( item.getNodeName().equals("SiteLink")) { // Если это адрес сайта, то устанавливаем
                    SiteLink = item.getChildNodes().item(0).getTextContent();
                } else if (item.getNodeName().equals("LogFile")) { // Если это адрес сайта, то устанавливаем
                    LogFile =  item.getChildNodes().item(0).getTextContent();
                } else if (item.getNodeName().equals("DB")) { // Если это нода - БД,

                    NodeList DB = item.getChildNodes();
                    for (int t = 0; t < DB.getLength(); t++) { // То проходим все ее потомки и устанавливаем
                        Node DBitem = DB.item(t);
                        if ( DBitem.getNodeName().equals("Login")) {
                            Login = DBitem.getChildNodes().item(0).getTextContent();
                        } else if (DBitem.getNodeName().equals("Password")) {
                            Password = DBitem.getChildNodes().item(0).getTextContent();
                        } else if (DBitem.getNodeName().equals("URL")) {
                            URL =  DBitem.getChildNodes().item(0).getTextContent();
                        }
                    }

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
