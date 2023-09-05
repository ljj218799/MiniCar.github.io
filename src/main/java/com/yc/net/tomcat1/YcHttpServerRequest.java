package com.yc.net.tomcat1;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.InputStream;
import java.net.Socket;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

public class YcHttpServerRequest {
    private Logger logger = Logger.getLogger(YcHttpServerRequest.class);

    private Socket s;
    private InputStream iis;
    private String method;

    private String requestURL; //定位符--http://localhost:8080/res/doUpload.action?name=a&pwd=b
    private String requestURI; //标识符--/res/doUpload.action
    private String contextPath;//上下文--/res
    private String queryString;//请求字符串 参数--name=a&pwd=b
    private Map<String,String[]> parameterMap = new ConcurrentHashMap<>();

    private String scheme;    //协议类型  http//
    private String protocol;  //协议版本
    private String realPath;  //项目真实路径

    public YcHttpServerRequest(Socket s, InputStream iis){
        this.iis = iis;
        this.s = s;
        this.parseRequest();
    }

    //解析方法
    private void parseRequest(){
        String requestInfoString = readFormInputStream();  //从输入流读取http请求信息（文字）
        if(requestInfoString==null||"".equals(requestInfoString)){
            throw new RuntimeException("读取输入流失败");

        }

        //解析请求头
        parseRequestInfoString(requestInfoString);

    }

    //从输入流读取http请求信息
    private String readFormInputStream(){
        int length = -1;
        StringBuffer sb = null;
        byte[] bs = new byte[300*1024];  //TODO:300k 足够存除文件上传之外的请求
        try {
            length = this.iis.read(bs, 0, bs.length);

            //将byte[] --> String
            sb = new StringBuffer();
            for (int i = 0; i < bs.length; i++) {
                sb.append((char) bs[i]);

            }
        }catch (Exception e){
            logger.error("读取请求信息失败");
            e.printStackTrace();
        }
        return sb.toString();
    }


    //解析http请求头（存各种信息）

    /**
     *
     * @param requestInfoString  Http请求协议
     *                           method 资源地址 协议版本
     *                           请求头域 键:值
     *                           空行
     *                           请求实体
     */
    private void parseRequestInfoString(String requestInfoString){
        StringTokenizer st = new StringTokenizer(requestInfoString);  //按 空格 切割
        this.method = st.nextToken();
        this.requestURI = st.nextToken();
        //requestURI要考虑地址栏的参数   /res/doUpload.action?name=a&pwd=b
        int questionIndex = this.requestURI.lastIndexOf("?");
        if(questionIndex>=0){
            //有地址栏参数  --》 存到  queryString中
            this.queryString = this.requestURI.substring(questionIndex+1);
            this.requestURI = this.requestURI.substring(0,questionIndex);
        }
        //协议版本  HTTP/1.1
        this.protocol = st.nextToken();
        //HTTP
        this.scheme = this.protocol.substring(0,this.protocol.indexOf("/"));
        //requestURI: /res/doUpload.action
        //contextPath: /res
        int slashIndex = this.requestURI.indexOf("/",1);
        if(slashIndex>=0){
            this.contextPath = requestURI.substring(0,slashIndex);
        }else{
            this.contextPath = requestURI;
        }

        this.requestURL = this.scheme+"://"+this.s.getLocalSocketAddress()+this.requestURI;
        //参数处理   name=a&pwd=b&like=a,b,c
        if(this.queryString!=null&&this.queryString.length()>0){
            String [] ps = this.queryString.split("&");
            for(String s:ps){
                String[] params = s.split("=");
                this.parameterMap.put(params[0],params[1].split(","));
            }
            //TODO: post的实体中还有参数
        }

        //realPath  项目的真实路径
        this.realPath = System.getProperty("user.dir")+ File.separator+"webapps";
    }

    public String[] getParameterValues(String name){
        if(parameterMap==null||parameterMap.size()<=0){
            return null;
        }
        String [] values = this.parameterMap.get(name);
        if(values==null||values.length<=0){
            return null;
        }
        return values;
    }

    public String getParameter(String name){
        String[] values = this.getParameterValues(name);
        if(values==null||values.length<=0){
            return null;
        }
        return values[0];
    }

    public String getMethod(){
        return this.method;
    }

    public InputStream getIis() {
        return iis;
    }

    public String getRequestURL() {
        return requestURL;
    }

    public String getRequestURI() {
        return requestURI;
    }

    public String getContextPath() {
        return contextPath;
    }

    public String getQueryString() {
        return queryString;
    }

    public Map<String, String[]> getParameterMap() {
        return parameterMap;
    }

    public String getScheme() {
        return scheme;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getRealPath() {
        return realPath;
    }

    @Override
    public String toString() {
        return "YcHttpServletRequest{" +
                "  method='" + method + '\'' +
                ", requestURL='" + requestURL + '\'' +
                ", requestURI='" + requestURI + '\'' +
                ", contextPath='" + contextPath + '\'' +
                ", queryString='" + queryString + '\'' +
                ", parameterMap=" + parameterMap +
                ", scheme='" + scheme + '\'' +
                ", protocol='" + protocol + '\'' +
                ", realPath='" + realPath + '\'' +
                '}';
    }
}
