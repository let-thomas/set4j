package org.set4j.impl.mbean;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.set4j.Initializer;

/**
 * @author Tomas Mikenda
 *
 */
public class Main
{

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception
	{
            
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer(); 
        ObjectName name = new ObjectName("com.example:type=Hello"); 
        Hello mbean = new Hello(); 
        mbs.registerMBean(mbean, name); 
        name = new ObjectName("com.example:name=Hellon, klic=hodnota");
        mbean = new Hello();
        mbs.registerMBean(mbean, name);
        name = new ObjectName("com.example:name=Hello,type=helot");
        mbean = new Hello();
        mbs.registerMBean(mbean, name);

        /*
        Setting s = Initializer.init(Setting.class);
        name = new ObjectName("com.example:type=Setting");
        mbs.registerMBean(s, name);
        */
        
        name = new ObjectName("com.example:type=Simple");
        mbs.registerMBean(new SimpleDynamic(), name);
        
        name = new ObjectName("com.example:name=in1,type=In");
        mbs.registerMBean(new CacheMBean(new Cache(), "reload", "blabla cache"), name);
        name = new ObjectName("com.example:name=inn,type=In.cache");
        mbs.registerMBean(new CacheMBean(new Cache(), "reload", "inner cache"), name);

        System.out.println("Waiting forever...");
        
        Thread.sleep(Long.MAX_VALUE);
        //Thread.currentThread().wait(Long.MAX_VALUE);
	}

	public static class Cache
	{
		public void reload()
		{
			System.out.println("cache reload");
		}
	}
}
