package cn.lmh.rpc.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 被该注解标记的服务可提供远程RPC访问功能
 * @author lmhmhl
 * @date 2020/7/26 13:11
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
@Documented
public @interface Service {
    String value() default "";
}
