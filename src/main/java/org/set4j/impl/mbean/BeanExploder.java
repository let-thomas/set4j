package org.set4j.impl.mbean;

import org.set4j.Set4JException;
import org.set4j.impl.ClassHandler;
import org.set4j.impl.Log;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Tomas Mikenda
 *
 */
public class BeanExploder
{
    private static final String ROOTNODE = "#root#";
	public void createBeans(ClassHandler handler) throws MalformedObjectNameException, MBeanRegistrationException, InstanceAlreadyExistsException, NotCompliantMBeanException
    {
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

        // prepare list of modules and its values
        HashMap<String, List<String>> modules = new HashMap<String, List<String>>();
        for (String name : handler.getRegistry().keySet())
        {
            String node = packageName(name);
            List<String> list = modules.get(node);
            if (list == null)
            {
                list = new ArrayList<String>();
                modules.put(node, list);
            }
            list.add(name);
        }

        String rootName = stripName(handler.getSourceClass().getName());
        for (String moduleName : modules.keySet())
        {
            StringBuffer objName = new StringBuffer().append("set4j:");
            if (moduleName == ROOTNODE) objName.append("type=").append(rootName).append(",name=").append(rootName);
            else objName.append("type=").append(rootName).append(",name=").append(moduleName);
            Log.debug("Register mbean - " + objName.toString());
            ObjectName name = new ObjectName(objName.toString());
            mbs.registerMBean(new Set4JMBean(handler, moduleName, modules.get(moduleName)), name);
        }

/*
        //handler.getMBeanInfo()
        HashMap<String, ClassHandler> modules = handler.getModules();
        if (parent == null || parent.length() == 0) parent="type=";
        else parent += '.';
        for (String modName : modules.keySet())
        {
            createBeans(modules.get(modName), parent + modName);
        }
*/
	}

    private String packageName(String name)
    {
        if (name.contains("."))
        {
            return name.substring(0, name.lastIndexOf('.'));
        } else return ROOTNODE;
    }
    private String stripName(String name)
    {
        if (name.contains("."))
        {
            return name.substring(name.lastIndexOf('.')+1);
        } else return ROOTNODE;
    }

}
