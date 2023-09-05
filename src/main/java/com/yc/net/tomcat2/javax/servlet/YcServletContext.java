package com.yc.net.tomcat2.javax.servlet;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 应用程序上下文类  --》常量类
 *
 */
public class YcServletContext {
    /**
     * servlet 的反射对象
     * <String ,Class>
     *  url     servlet字节码路径
     *  requestURI    -->  利用反射实例化成对象
     *
     */
    public static Map<String,Class> servletClass = new ConcurrentHashMap<>();

    /**
     * servlet 的对象
     * 每个Servlet 都是单例  ,当第一次访问这个servlet时 ，创建后保存在这个map中
     */
    public static Map<String,YcServlet> servletInstance = new ConcurrentHashMap<>();
}
