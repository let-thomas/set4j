package org.set4j;

import org.set4j.impl.ClassHandler;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import java.util.Properties;

/**
 * @author Tomas Mikenda
 */
public class SpringPropertyResolver   extends PropertyPlaceholderConfigurer
{
    private Class mCfgClass = null;
    // setProperties in constructor?
    public void setConfigClass(Class cls)
    {
        mCfgClass = cls;
        setSystemPropertiesMode(SYSTEM_PROPERTIES_MODE_NEVER);
    }

    @Override
    protected String resolvePlaceholder(String placeholder, Properties props)
    {
        String value = null;
        if (ClassHandler.getRegistry().containsKey(placeholder))
        {
            Object objValue = ClassHandler.getRegistry().get(placeholder).getValue();
            value = objValue == null ? null : objValue.toString();
        }

        return value;
    }


}
