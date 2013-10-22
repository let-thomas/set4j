package org.set4j;

import org.set4j.impl.ClassHandler;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import java.util.Properties;

/**
 * @author Tomas Mikenda
 */
public class SpringPropertyResolver extends PropertyPlaceholderConfigurer
{
    private Class mCfgClass = null;
    private ClassHandler mClassHandler = null;

    // setProperties in constructor?
    public void setConfigClass(Class cls)
    {
        if (cls == null)
        {
            throw new RuntimeException("configClass cannot be null");
        }
        mCfgClass = cls;
        mClassHandler = Initializer.getHandlerForClass(cls);
        if (mClassHandler == null)
        {
            throw new RuntimeException("There is not any existing configuration for class " + cls.getName());
        }
        setSystemPropertiesMode(SYSTEM_PROPERTIES_MODE_NEVER);
    }

    @Override
    protected String resolvePlaceholder(String placeholder, Properties props)
    {
        String value = null;
        if (mClassHandler.getRegistry().containsKey(placeholder))
        {
            Object objValue = mClassHandler.getRegistry().get(placeholder).getValue();
            value = objValue == null ? null : objValue.toString();
        }

        return value;
    }


}
