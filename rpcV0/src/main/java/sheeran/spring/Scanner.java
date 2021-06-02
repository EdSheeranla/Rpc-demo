package sheeran.spring;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;

import java.util.Objects;
import java.util.Set;

public class Scanner extends ClassPathBeanDefinitionScanner {
    private RpcServiceFactoryBean<Object> rpcServiceFactoryBean = new RpcServiceFactoryBean<>();
    public Scanner(BeanDefinitionRegistry registry) {
        super(registry);
    }

    @Override
    protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Set<BeanDefinitionHolder> beanDefinitionHolderSet = super.doScan(basePackages);
        processBeanDefinition(beanDefinitionHolderSet);
        return beanDefinitionHolderSet;
    }

    private void processBeanDefinition(Set<BeanDefinitionHolder> beanDefinitionHolderSet) {
        GenericBeanDefinition definition;
        for (BeanDefinitionHolder beanDefinitionHolder:beanDefinitionHolderSet){
            definition =(GenericBeanDefinition) beanDefinitionHolder.getBeanDefinition();

            definition.getConstructorArgumentValues().addGenericArgumentValue(Objects.requireNonNull(definition.getBeanClassName()));
            definition.setBeanClass(rpcServiceFactoryBean.getClass());
            definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
        }
    }

    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return beanDefinition.getMetadata().isInterface()&&beanDefinition.getMetadata().isIndependent();
    }
}
