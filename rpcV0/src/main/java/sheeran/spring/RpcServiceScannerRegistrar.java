package sheeran.spring;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.stereotype.Component;


public class RpcServiceScannerRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {
    private static final Logger logger = Logger.getLogger(RpcServiceScannerRegistrar.class);
    private static final String SERVICE_BASE_PACKAGE = "serviceBasePackage";
    private static final String SPRING_BEAN_BASE_PACKAGE = "sheeran";
    private ResourceLoader resourceLoader;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes annotationAttrs = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(RpcServiceScan.class.getName()));
        String[] rpcScanBasePackages = new String[0];
        if (annotationAttrs !=null){
            rpcScanBasePackages = annotationAttrs.getStringArray(SERVICE_BASE_PACKAGE);
        }
        if (rpcScanBasePackages.length==0){
            rpcScanBasePackages = new String[]{((StandardAnnotationMetadata)importingClassMetadata).getIntrospectedClass().getPackage().getName()};
        }
        CustomScanner serviceScanner = new CustomScanner(registry,RpcService.class);
        CustomScanner springScanner = new CustomScanner(registry, Component.class);
        if (resourceLoader!=null){
            serviceScanner.setResourceLoader(resourceLoader);
            springScanner.setResourceLoader(resourceLoader);
        }
        int springBeanAmount = springScanner.scan(SPRING_BEAN_BASE_PACKAGE);
        logger.info("注册了"+springBeanAmount+"个SpringBean");
        int serviceBeanAmount = serviceScanner.scan(rpcScanBasePackages);
        logger.info("注册了"+serviceBeanAmount+"个ServiceBean");
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
