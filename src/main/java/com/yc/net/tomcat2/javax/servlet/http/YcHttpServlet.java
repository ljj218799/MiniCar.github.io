package com.yc.net.tomcat2.javax.servlet.http;

import com.yc.net.tomcat2.javax.servlet.YcServlet;
import com.yc.net.tomcat2.javax.servlet.YcServletRequest;
import com.yc.net.tomcat2.javax.servlet.YcServletResponse;

public abstract class YcHttpServlet implements YcServlet {

    @Override
    public void init() {

    }

    @Override
    public void destroy() {

    }

    protected void doGet(YcHttpServletRequest req, YcHttpServletResponse resp) {}

    protected void doHead(YcHttpServletRequest req, YcHttpServletResponse resp) {}

    protected void doPost(YcHttpServletRequest req, YcHttpServletResponse resp) {}

    protected void doDelete(YcHttpServletRequest req, YcHttpServletResponse resp) {}

    protected void doOptions(YcHttpServletRequest req, YcHttpServletResponse resp) {}

    protected void doTrace(YcHttpServletRequest req, YcHttpServletResponse resp) {}


    @Override  //在service 中判断method 是什么，在调用对象的doXxx方法
    public void service(YcServletRequest request, YcServletResponse response) {
        //从request 中取出method （http协议特有)
        String method = ((YcHttpServletRequest) request).getMethod();
        if("get".equalsIgnoreCase(method)){
            doGet((YcHttpServletRequest)request,(YcHttpServletResponse)response);
        }else if("post".equalsIgnoreCase(method)){
            doPost((YcHttpServletRequest)request,(YcHttpServletResponse)response);
        }else if("head".equalsIgnoreCase(method)){
            doHead((YcHttpServletRequest)request,(YcHttpServletResponse)response);
        }else if("delete".equalsIgnoreCase(method)){
            doDelete((YcHttpServletRequest)request,(YcHttpServletResponse)response);
        }else if("options".equalsIgnoreCase(method)){
            doOptions((YcHttpServletRequest)request,(YcHttpServletResponse)response);
        }else if("trace".equalsIgnoreCase(method)){
            doTrace((YcHttpServletRequest)request,(YcHttpServletResponse)response);
        }else{
            //TODO: 错误协议
        }
    }

    public void service(YcHttpServletRequest request, YcHttpServletResponse response) {
        service(request,response);
    }

}
