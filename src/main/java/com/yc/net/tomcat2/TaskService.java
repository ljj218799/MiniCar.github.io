package com.yc.net.tomcat2;

import com.yc.net.tomcat2.javax.servlet.YcServletContext;
import com.yc.net.tomcat2.javax.servlet.http.YcHttpServletRequest;
import com.yc.net.tomcat2.javax.servlet.http.YcHttpServletResponse;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class TaskService implements Runnable {

    private Logger logger = Logger.getLogger(TaskService.class);
    private boolean flag = true;

    private Socket s;
    private InputStream iis;
    private OutputStream oos;
    public TaskService(Socket s){
        this.s = s;
        try {
            this.iis = s.getInputStream();
            this.oos = s.getOutputStream();
        }catch (Exception e){
            e.printStackTrace();
            logger.error("socket获取流异常");
            flag = false;
        }
    }
    @Override
    public void run() {

        //TODO:Connection: keep-alive 的实现
        //通过输入流读取客户端的请求并解析
        if(this.flag){
            //解析出所有的请求信息   ,存在HttpServerRequest对象中
            YcHttpServletRequest request = new YcHttpServletRequest(this.s,this.iis);
            //System.out.println("request = " + request);
            //响应   本地地址+资源地址   读取文件  拼接http 以流的形式回传给客户端
            YcHttpServletResponse response = new YcHttpServletResponse(request,this.oos);
            Processor processor = null;
            //requestURI-->wowotuan/hello
            //contextPath-->/wowotuan
            //根据 request 中的 (requestURI - contextPath) --> /hello 来判断 是哪种资源
            int contextPathLength = request.getContextPath().length();
            String s = request.getRequestURI().substring(contextPathLength);
            if(YcServletContext.servletClass.containsKey(s)){
                //动态请求
                processor = new DynamicProcessor();
            }else{
                //静态请求或都不是
                processor = new StaticProcessor();
            }
            processor.process(request,response);

//            response.send();
        }
        try {
            this.iis.close();
            this.oos.close();
            this.s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
