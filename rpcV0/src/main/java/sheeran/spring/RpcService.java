package sheeran.spring;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Component
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Inherited
public @interface RpcService {
    String version() default "";

    String group() default "";

    String name() default "";
}
