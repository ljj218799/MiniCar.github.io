package com.yc.net.tomcat1;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class Test {
    public static void main(String[] args) {
        int port = 0;
        String serverXmlPath = System.getProperty("user.dir")+ File.separator+"conf"+File.separator+"server.xml";
        try(InputStream iis = new FileInputStream(serverXmlPath)){

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = factory.newDocumentBuilder();
            Document doc = documentBuilder.parse(iis);

            NodeList nl = doc.getElementsByTagName("Connector");
            for(int i = 0;i<nl.getLength();i++){
                Element node = (Element) nl.item(i);
                port = Integer.parseInt(node.getAttribute("port"));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println(port);
    }
}
