版 本2:   servlet服务器，提供动态资源(  class字节码  ->  xxxxServlet.class   )  访问.

1. servlet动态资源实际上是一个java代码 ,运行在jvm中.
2. 服务器开发商. (   接收参数-> parameterMap),
   应用网站的开发人员  -> servlet( parameterMap ->取参数 ),html,css...
   客户端:

   ->  sun公司制订:   j2EE标准  (  servlet, filter, listener,.... jdbc, 联接池,jndi, .... )
3. 静态资源和动态资源支持.



要解决的问题:
 1.servlet的读取。
   服务器启动时, 扫描类路径: 所有的.class文件. 判断哪些类上有   @YcWebServlet, 有则保存到
                     Map<String,   Servlet的class对象   >
        //                <地址,      Servlet的class对象>
        //                <"/hello"     HelloServlet的class对象 >

   技术解决方案:
     (1) 针对
        @WebServlet("/hello")
        public class HelloServlet extends HttpServlet
      注解解析(   @YcWebServlet  +   且继承自 HttpServlet  )  .
     (2)类扫描: 递归扫描.    字节码加载.
     (3) Map<String,   Servlet的class对象   >
         ->  j2ee作用域对象:
                   YcHttpServletRequest (基于一次请求) ->  YcHttpSession (会话) ->   YcServletContext  (整个程序)
