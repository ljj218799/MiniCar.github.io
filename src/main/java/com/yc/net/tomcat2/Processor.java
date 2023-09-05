package com.yc.net.tomcat2;

import com.yc.net.tomcat2.javax.servlet.YcServletRequest;
import com.yc.net.tomcat2.javax.servlet.YcServletResponse;

/**
 * 资源处理接口
 */
public interface Processor {
    /**
     * 处理方法
     * @param request
     * @param response
     */
    public void process(YcServletRequest request, YcServletResponse response);
}
