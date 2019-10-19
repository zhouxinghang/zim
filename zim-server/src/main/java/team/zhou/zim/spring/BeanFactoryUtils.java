package team.zhou.zim.spring;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.stereotype.Component;

/**
 * @author zhouxinghang
 * @date 2019-10-19
 */
@Component
public class BeanFactoryUtils implements BeanFactoryAware {

    private static BeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        BeanFactoryUtils.beanFactory = beanFactory;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(String beanName) {
        return (T) beanFactory.getBean(beanName);
    }

    public static <T> T getBean(Class<T> requiredType) {
        return beanFactory.getBean(requiredType);
    }

}
