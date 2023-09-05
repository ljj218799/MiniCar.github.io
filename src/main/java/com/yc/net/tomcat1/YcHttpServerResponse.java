package com.yc.net.tomcat1;
/**
 * 根据request请求信息做响应
 *                  1xx:
 *                  2xx: 正常情况
 *                  3xx: 重定向，缓存
 *                  4xx: 没有资源
 *                  5xx: 服务器内部错误
 */

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.SimpleFormatter;

public class YcHttpServerResponse {
    private YcHttpServerRequest request;
    private OutputStream oos;

    public YcHttpServerResponse(YcHttpServerRequest request, OutputStream oos) {
        this.request = request;
        this.oos = oos;
    }

    public void send(){
        String uri = this.request.getRequestURI();    //wowotuan/index.html
        String realPath = this.request.getRealPath();  //服务器路径   D:\IDEA\yc119_net\webapps

        File f = new File(realPath,uri);
        byte[] fileContent = null;
        String responseProtocol = null;
        if(!f.exists()){
            //文件不存在   4xx
            fileContent = readFile( new File( realPath+ File.separator ,"/404.html" ) );
            responseProtocol = gen404( fileContent );
        }else{
            //文件存在    2xx
            fileContent = readFile(new File(realPath,uri));
            responseProtocol = gen200(fileContent);
        }
        try {
            oos.write(responseProtocol.getBytes());  //响应头域
            oos.flush();
            oos.write(fileContent);                 //响应内容
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(oos!=null){
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String gen200(byte[] fileContent) {
        String protocol200 = "";
        //要先获取请求的资源类型
        String uri = this.request.getRequestURI();

        int index = uri.lastIndexOf(".");
        if(index>=0){
            index+=1;
        }
        String fileExtension = uri.substring(index);
        //判断后缀名
        if("jpg".equalsIgnoreCase(fileExtension)){
            protocol200 = "HTTP/1.1 200 OK\r\nContent-Type: image/jpeg\r\nContent-Length: "+fileContent.length+"\r\n\r\n";
        }else if("css".equalsIgnoreCase(fileExtension)){
            protocol200 = "HTTP/1.1 200 OK\r\nContent-Type: text/css\r\nContent-Length: "+fileContent.length+"\r\n\r\n";
        }else if("js".equalsIgnoreCase(fileExtension)){
            protocol200 = "HTTP/1.1 200 OK\r\nContent-Type: application/javascript\r\nContent-Length: "+fileContent.length+"\r\n\r\n";
        }else if("gif".equalsIgnoreCase(fileExtension)){
            protocol200 = "HTTP/1.1 200 OK\r\nContent-Type: image/gif\r\nContent-Length: "+fileContent.length+"\r\n\r\n";
        }else if("png".equalsIgnoreCase(fileExtension)){
            protocol200 = "HTTP/1.1 200 OK\r\nContent-Type: image/png\r\nContent-Length: "+fileContent.length+"\r\n\r\n";
        } else{
            protocol200 = "HTTP/1.1 200 OK\r\nContent-Type: text/html\r\nContent-Length: "+fileContent.length+"\r\n\r\n";
        }
        return protocol200;
    }

    private String gen404(byte[] fileContent) {
        //请求头部分
        String protocol404 = "HTTP/1.1 404 Not Found\r\nContent-Type: text/html; charset=utf-8\r\nContent-Length: "+fileContent.length+"\r\n"+"Date: "+ new Date()+"\r\n";
        protocol404+="Server: kitty server\r\n\r\n";    //空行
        return protocol404;
    }

    //读取本地文件  转为字节数组
    private byte[] readFile(File file) {
        ByteArrayOutputStream baos = baos = new ByteArrayOutputStream();
        try(
            FileInputStream fis = new FileInputStream(file)){
            byte[] bs = new byte[1024];
            int len = -1;
            while((len=fis.read(bs,0,bs.length))!=-1){
                baos.write(bs,0,len);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return baos.toByteArray();
    }
}
