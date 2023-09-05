package com.yc.net.tomcat1;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TomcatServer {
    static Logger logger = Logger.getLogger(TomcatServer.class);
    public static void main(String[] args) {
        //创建日志对象

        logger.trace("服务器启动");

        TomcatServer ts = new TomcatServer();
        int port = ts.parsePortFormXml();

//        logger.debug("服务器配置的端口为："+port);

        ts.startServer(port);

    }
    //解析端口号
    private int parsePortFormXml(){
        int port = 8080;
        //方案一： 根据字节码的路径  （Target/classes)
//        TomcatServer.class.getClassLoader().getResourceAsStream( );

        //方案二：
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

        return port;
    }
    //服务器启动
    private void startServer(int port){

        boolean flag = true;

        try(ServerSocket ss = new ServerSocket(port)){
            logger.debug("服务器启动成功,配置的端口号为:" + port);
            //TODO: 可以读取server.xml中的是否开启线程池配置来决定是否使用线程池
            while (flag) {
                try {
                    Socket s = ss.accept();
                    logger.debug("客户端:" + s.getRemoteSocketAddress() + "连接上了服务器");

                    TaskService task = new TaskService(s);
                    Thread t = new Thread(task);
                    t.start();
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error("客户端连接失败。。。");
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
            logger.error("服务器套接字创建失败。。。");
        }
    }
}
