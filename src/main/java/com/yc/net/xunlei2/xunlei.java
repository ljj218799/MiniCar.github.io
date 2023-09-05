package com.yc.net.xunlei2;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 迅雷http下载工具
 */
public class xunlei {

    //获取下载进度  方案二：使用Callable
    private static int total = 0;

    public static void main(String[] args) {

        String url="http://dl.baofeng.com/baofeng5/bf5_new.exe";                // 104595248字节
        //1.带下载文件大小
        long fileSize = getFileSize(url);
        System.out.println("fileSize = " + fileSize);

        //2.获取新文件名和用户路径
        String newFileName = getNewFileName(url);
        System.out.println("newFileName = " + newFileName);

        String newFilePath = getNewFilePath(newFileName);
        System.out.println("newFilePath = " + newFilePath);
        //3.创建空文件并占好位置
        createNewFile(fileSize,newFilePath);


        //线程数的确定
        int threadSize = Runtime.getRuntime().availableProcessors();
        //计算每个线程要下载的大小
        long sizePerThread = getSizePerThread(threadSize,fileSize);
        System.out.println("共"+threadSize+"个线程，每个线程下载最多:"+sizePerThread+"字节");
        //循环生成线程
        List<FutureTask<Integer>> list = new ArrayList<>();
        for(int i = 0 ;i<threadSize;i++){

            DownloadTask task = new DownloadTask(i,fileSize,threadSize,sizePerThread,url,newFilePath);
            FutureTask<Integer> ft = new FutureTask<>(task);
            list.add(ft);
            Thread t = new Thread(ft);
            t.start();
        }
        //获取每个futureTask的下载结果
        for(FutureTask<Integer> futureTask:list){
            try {
                Integer integer = futureTask.get();//get()式阻塞式的方法   返回futureTask 的outcome
                total+=integer;
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        System.out.println("共下载了"+total);
    }

    //获取带下载文件大小
    public static long getFileSize(String url){
        long fileSie = 0;
        try {
            URL u = new URL(url);
            HttpURLConnection huc = (HttpURLConnection) u.openConnection();
            //试探一个文件是否存在，一个图片是否存在，但是不去真正的去下载
            huc.setRequestMethod("HEAD");   //请求行： HEAD /xxx HTTP/1.1

            huc.connect();
            fileSie = huc.getContentLength();
        }catch (Exception e){
            e.printStackTrace();
        }
        return fileSie;

    }
    //获取新文件名
    public static String getNewFileName(String url){

        Date d = new Date();
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        String prefix = df.format(d);

        //后缀名
        String suffix = url.substring(url.lastIndexOf("."));
        return prefix+suffix;

    }
    //获取新文件的用户路径
    public static String getNewFilePath(String  newFileName){
        String userName = System.getProperty("user.home");
        return userName+ File.separator+newFileName;
    }
    //创建空文件。占好位置
    public static void createNewFile(long fileSize,String newfilePath){
        try(RandomAccessFile raf = new RandomAccessFile(newfilePath,"rw");) {
            raf.setLength(fileSize);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //计算每个线程要下载的大小
    public static long getSizePerThread(int threadSize,long fileSize){
        return fileSize%threadSize==0? fileSize/threadSize: fileSize/threadSize+1;
    }
}

class DownloadTask implements Callable<Integer> {
    private int i;
    private long fileSize;
    private int threadSize;
    private long sizePerThread;
    private String url;
    private String newFilePath;


    public DownloadTask(int i, long fileSize, int threadSize, long sizePerThread, String url, String newFilePath) {
        this.i = i;
        this.fileSize = fileSize;
        this.threadSize = threadSize;
        this.sizePerThread = sizePerThread;
        this.url = url;
        this.newFilePath = newFilePath;
    }

    @Override
    public Integer call() {
        //计算 下载的起止
        long start = i*sizePerThread;
        long end = (i+1)*sizePerThread-1;

        RandomAccessFile raf = null;
        InputStream iis = null;
        try {
            //利用RandomAccessFile 在 newFilePath 中寻找保存位置
            raf = new RandomAccessFile(newFilePath, "rw");
            raf.seek(start);

            //利用 http 的请求头域 Range 服务器下载指定位置的内容
            URL u = new URL(url);
            HttpURLConnection con = (HttpURLConnection) u.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Range", "bytes=" + start + "-" + end);
            con.setConnectTimeout(10 * 1000);

            int smallcount=0;
            //再开始下载
            iis = new BufferedInputStream(con.getInputStream());
            byte[] bytes = new byte[1024];
            int len = -1;
            while ((len = iis.read(bytes, 0, bytes.length)) != -1) {
                raf.write(bytes, 0, len);
                smallcount+=len;
            }
            System.out.println("线程"+i+"下载完毕"+smallcount);
            return smallcount;     //保存到FutureTask 的outcome中
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                if(raf!=null) {
                    raf.close();
                }
                if(iis!=null) {
                    iis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }return 0;
    }
}
