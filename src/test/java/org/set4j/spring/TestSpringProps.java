package org.set4j.spring;

import static junit.framework.Assert.*;
import org.junit.Test;
import org.set4j.Initializer;
import org.set4j.propfile.MainS;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


import java.util.Properties;

/**
 * @author Tomas Mikenda
 */
public class TestSpringProps
{
    @Test
    public void testSpring()
    {
        // load config
        Properties p = new Properties();
        p.put("customer", "comp1");
        p.put("env", "local");
        p.put("set4j.loglevel","trace");

        MainS s = Initializer.init(MainS.class, p);

        assertEquals("serverpf-full", s.getPrintingServer());
        assertEquals("roger", s.getA().viewName());
        assertEquals(true, s.getA().isEnabled());

        //
        ApplicationContext context =
                new ClassPathXmlApplicationContext(new String[] {"/org/set4j/spring/test_spring_props.xml"});

        PropagationBean bean = (PropagationBean)context.getBean("propagation");

        assertEquals(bean.getCustomer(), s.getCustomer());
        assertEquals(bean.getEnvironment(), s.getEnv());
        assertEquals(bean.isEnabled(), s.getA().isEnabled());
        assertEquals(bean.getFreq(), s.getA().getSubA().getUpdateFreq());
    }
}
