版 本一:   http服务器，提供静态资源访问.

浏览器: http://localhost:8090/wowotuan/index.html
显示wowotuan页面.

分析协议 :

请求部分:  (浏览器自动实现)
    GET /wowotuan/index.html HTTP/1.1
    Referer: xxxx
    User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.16
    ....
    空行

服务器响应部分:
    HTTP/1.1 200 OK
    Accept-Ranges: bytes
	Content-Length: 92174
	Content-Type: text/html
	...
	空行
	响应实体(index.html的文本内容)
					
			
服务器功能:
1. 接收客户端的请求解析出它请求的文件名( /wowotuan/index.html )及相对路径  (   d:\IdeaProjects\yc119_net         \webapps  +   /wowotuan/index.html        ) .
                                                                       System.getProperty( "user.dir")
       解析请求头域.............
2. 查找这个文件是否存在，  不存在->  拼接404    页面
          存在 ->  
                1）读取这个资源  文件输入流.
                2) 构建响应协议
                          HTTP/1.1 200 OK
                          Content-Type:  浏览器根据响应中的  Content-Type来决定使用什么引擎来解析数据
                                        text/html:           html  -> html渲染
                                        text/css             :    css引擎
                                        text/javascript      :     js引擎
                                        image/png             图片:   图片引擎
                          Content-Length :

用到的技术:
			1. ServerSocket  ->  Socket  
			2. 多线程
			3. log4j
			4. dom解析


KittyServer:
	xml的解析端口; 
   ServerSocket ss=new ServerSocket(  端口) ;

    Socket s=ss.accept();
    Thread t=new Thread(  new 任务(  s )  );
    t.start();
    
    
注意的问题:
  1. HttpServletRequest类中的  private String readFromInputStream()方法，要一次读取所有的请求头数据. 
  
  
  
  
  
  Socket 取到，如何处理?
  
       HttpServletRequest对象 -> 处理请求 
           1) 解析请求头      GET /xxx/index.html?name=zy&age=20  HTTP/1.1

              /xxx/index.html   ->拼接地址. 
              保存: 
                 getContextPath()   :     /wowotuan
                 getHeader(String name)   getHeaderNames()     getHeaders(String name) 
                 getMethod() 
                 getRequestURL() 
                 getServletPath() 
                 getProtocol
                 getRealPath()

                 getParameter("")
         

        HttpServletResponse对象  -> 处理响应
                1)取request解析出来的文件的路径
                2)判断是否存在，不在，则404
                   在，则拼接响应。 
                        响应的资源的类型， Content-Type不一样. 

  
     
  
  
  
  
  
  
  
     
      