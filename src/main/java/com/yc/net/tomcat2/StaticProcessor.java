package com.yc.net.tomcat2;

import com.yc.net.tomcat2.Processor;
import com.yc.net.tomcat2.javax.servlet.YcServletRequest;
import com.yc.net.tomcat2.javax.servlet.YcServletResponse;
import com.yc.net.tomcat2.javax.servlet.http.YcHttpServletResponse;

/**
 * 静态资源处理类
 */
public class StaticProcessor implements Processor {

    @Override
    public void process(YcServletRequest request, YcServletResponse response) {
//        YcHttpServletResponse response1 = (YcHttpServletResponse)response;
//        response1.send();
        response.send();
    }
}
