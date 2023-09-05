package com.yc.net.tomcat2.javax.servlet;

/**
 * 服务器端接口
 */
public interface YcServlet {
    public void init();   //初始化方法 --》 构造方法后调用
    public void destroy(); //销毁方法  --》 服务器关闭请调用
    public void service(YcServletRequest servletRequest, YcServletResponse servletResponse);
}
