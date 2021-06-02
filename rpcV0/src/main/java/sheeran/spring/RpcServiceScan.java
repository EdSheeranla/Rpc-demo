package sheeran.spring;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(RpcServiceScannerRegistrar.class)
public @interface RpcServiceScan {
    String[] serviceBasePackage();
}
