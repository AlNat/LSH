package com.LSH.server.Config;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileNotFoundException;


/**
 * Created by @author AlNat on 07.12.2016.
 * Licensed by Apache License, Version 2.0
 *
 * Класс, читающий конфигурационный файл и отдающий данные
 */
public class Config {

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
    private String Password; // TODO Подумать, как брать пароль в зашифрованном виде
    private String URL;

    // Конструктор, вызывающий чтение конфигурационного файла
    private Config () {
        ReadConfig();
    }

    /**
     * Функция, парсящая xml файл конфигурации и заполняющая поля из него
     */
    private void ReadConfig () {

        String filename = "File System Error";

        try {
            File currentDir = new File("."); // Определили теущий каталог
            String sDirSeparator = System.getProperty("file.separator"); // Определили разделитель - вид кавычек
            filename = currentDir.getCanonicalPath() + sDirSeparator + "config.xml";
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {

            //filename = "C:\\Users\\AlNat\\Source\\Studi\\Diplom\\config.xml"; // TODO delete - debug
            File file = new File(filename);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
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

        } catch (FileNotFoundException e) {
            System.out.println("Config File Not Found!");
            System.out.println("Please, sure that config file placed in:");
            System.out.println(filename);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}
