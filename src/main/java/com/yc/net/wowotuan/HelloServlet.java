package com.yc.net.wowotuan;

import com.yc.net.tomcat2.javax.servlet.YcWebServlet;
import com.yc.net.tomcat2.javax.servlet.http.YcHttpServlet;
import com.yc.net.tomcat2.javax.servlet.http.YcHttpServletRequest;
import com.yc.net.tomcat2.javax.servlet.http.YcHttpServletResponse;

import java.io.PrintWriter;

@YcWebServlet("/hello")
public class HelloServlet extends YcHttpServlet {
    public HelloServlet() {
        System.out.println("HelloServlet 的构造方法");
    }

    @Override
    public void init() {
        System.out.println("HelloServlet 的初始化方法");
    }

    @Override
    protected void doGet(YcHttpServletRequest req, YcHttpServletResponse resp) {
        System.out.println("hello");
//        req.setContentType("application/json;charset=utf-8");
        String result = "hello ,你好";
        PrintWriter out = resp.getPrintWriter();
        //TODO: 标准的Tomcat 是由服务器来完成响应的构建
        out.print("HTTP/1.1 200 OK\r\nContent-Type: text/html;charset=utf-8\r\nContent-Length: "+result.getBytes().length+"\r\n\r\n");
        out.println(result);
        out.flush();
    }

    @Override
    protected void doPost(YcHttpServletRequest req, YcHttpServletResponse resp) {
        super.doPost(req, resp);
    }
}
