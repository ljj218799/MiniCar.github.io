package com.yc.net.tomcat2;

import com.yc.net.tomcat2.Processor;
import com.yc.net.tomcat2.javax.servlet.YcServlet;
import com.yc.net.tomcat2.javax.servlet.YcServletContext;
import com.yc.net.tomcat2.javax.servlet.YcServletRequest;
import com.yc.net.tomcat2.javax.servlet.YcServletResponse;
import com.yc.net.tomcat2.javax.servlet.http.YcHttpServletRequest;

import java.io.PrintWriter;

/**
 * 动态资源处理类
 */
public class DynamicProcessor implements Processor {
    @Override
    public void process(YcServletRequest request, YcServletResponse response) {
        //  request 中的参数已经解析好了
        /**
         * 1. 从request中取出requestURI (   /hello,  到ServletContext中的map 去取class文件
         * 2. 为了保证单例, 先看 另一个map中是否已经有这个class文件的实例 , a.如有 则直接取，在调用 service()
         *                                                         b.如没有 则先利用反射创建Servlet存贷另一个map
         *                                                         再利用 init() -> service()
         * 3.Servlet执行失败的情况  输出 500.html 响应给客户端
         */

        int contextPathLength = ((YcHttpServletRequest)request).getContextPath().length();
        String uri = ((YcHttpServletRequest)request).getRequestURI().substring(contextPathLength);
        YcServlet ycServlet = null;
        
        try {
            if (YcServletContext.servletInstance.containsKey(uri)) {
                //a.如有 则直接取，在调用 service()
                ycServlet = YcServletContext.servletInstance.get(uri);
            } else {
                //b.如没有 则先利用反射创建Servlet存到另一个map
                    Class clz = YcServletContext.servletClass.get(uri);
                    Object obj = clz.newInstance();   //调用此servlet 的构造方法
                    if (obj instanceof YcServlet) {
                        ycServlet = (YcServlet) obj;
                        //再利用 init() -> service()
                        ycServlet.init();
                        YcServletContext.servletInstance.put(uri, ycServlet);
                    }
                //调用service()
                //此servlet 就是客户端要访问的servlet HelloServlet  -->  YcHttpServlet 的service() --> 在根据method 判断调用HelloServlet 的 doXxx()
                ycServlet.service(request, response);

            }
        }catch (Exception e){
            String bodyEntity = e.toString();
            String protocol = gen500(bodyEntity);
            //以输出流返回客户端
            PrintWriter pr = response.getPrintWriter();
            pr.println(protocol);
            pr.println(bodyEntity);
            pr.flush();

        }
        
    }

    private String gen500(String bodyEntity) {
        String protocol500 = "HTTP/1.1 500 Internal Server Error\r\nContent-Type: text/html\r\nContent-Length: "+bodyEntity.getBytes().length+"\r\n\r\n";
        return protocol500;
    }
}
