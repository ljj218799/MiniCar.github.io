package com.yc.net.tomcat2;

import com.yc.net.tomcat2.javax.servlet.YcServlet;
import com.yc.net.tomcat2.javax.servlet.YcServletContext;
import com.yc.net.tomcat2.javax.servlet.YcWebServlet;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TomcatServer {
    static Logger logger = Logger.getLogger(TomcatServer.class);
    public static void main(String[] args) {
        //创建日志对象

        logger.trace("服务器启动");

        TomcatServer ts = new TomcatServer();
        int port = ts.parsePortFormXml();

//        logger.debug("服务器配置的端口为："+port);

        ts.startServer(port);
//

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


        String packageName = "com.yc";
        String packagePath = packageName.replaceAll("\\.","/");    //将 . 替换换成 /
        //服务器启动时  扫描它所有的 classes 类 查找有 @YcWebServlet注解的类  ，存到map 中
        //jvm 类加载器
        try {
            Enumeration<URL> files = Thread.currentThread().getContextClassLoader().getResources(packagePath);
            while (files.hasMoreElements()) {
                URL url = files.nextElement();
                logger.info("正在扫描的包路径为：" + url.getFile());
                //查找此包下的所有文件
                findPackageClasses(url.getFile(), packageName);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
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


    /**
     *
     * @param packagePath   "com/yc"
     * @param packageName   "com.yc"
     */
    private void findPackageClasses(String packagePath, String packageName) {
        if(packagePath.startsWith("/")){
            packagePath = packagePath.substring(1);
        }
        //取这个路径下的所有字节码文件
        File file = new File(packagePath);
        File[] classfiles = file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                //  .class  和  目录文件
                if(pathname.getName().endsWith(".class")  ||  pathname.isDirectory()){
                    return true;
                }else {
                    return false;
                }
            }
        });
       // System.out.println(classfiles);
        //递归
        if(classfiles != null && classfiles.length>0){
            for(File file1:classfiles){
                //如果是目录
                if(file1.isDirectory()){
                    findPackageClasses(file1.getAbsolutePath(),packageName+"."+file1.getName());
                }else{
                    //是字节码文件，则利用类加载器加载 这个class 文件
                    try {
                        URLClassLoader uc = new URLClassLoader(new URL[]{});
                        Class cls = uc.loadClass(packageName + "." + file1.getName().replaceAll(".class", ""));
                        //筛选出有YcServlet注解的类
                        if(cls.isAnnotationPresent(YcWebServlet.class)){
                            logger.info("加载了一个类" + cls.getName());
                            //通过注解的  value 方法取出 url地址存到 YcServletContext 的servletClass Map中
                            YcWebServlet annotation =(YcWebServlet) cls.getAnnotation(YcWebServlet.class);
                            String url = annotation.value();
                            YcServletContext.servletClass.put(url,cls);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

            }
        }
    }
}
