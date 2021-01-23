package cn.lmh.rpc.annotation;

import java.lang.annotation.*;

/**
 * @author LMH
 * @date 2020/8/19 16:33
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MessageProtocolAno {
    String value() default "";
}
