package com.yc.net.tomcat2.javax.servlet;

import java.io.OutputStream;
import java.io.PrintWriter;

public interface YcServletResponse {
    public void send();

    public OutputStream getOutputStream();

    public PrintWriter getPrintWriter();
}
