package com.yc.net.tomcat2.javax.servlet;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Target  表明该注解可以应用的java元素类型：   TYPE	->应用于类、接口（包括注解类型）、枚举
 * @Retention  表明该注解的生命周期    RUNTIME ->由JVM 加载，包含在类文件中，在运行时可以被获取到
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface YcWebServlet {
    String value() default "";
}

//@YcWbeServlet(value="/hello")
