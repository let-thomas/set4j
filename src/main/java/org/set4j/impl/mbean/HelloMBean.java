package org.set4j.impl.mbean;

/**
 * @author Tomas Mikenda
 *
 */
public interface HelloMBean
{
    public void sayHello(); 
    public int add(int x, int y); 
    
    public String getName(); 
     
    public int getCacheSize(); 
    public void setCacheSize(int size); 
    
    void setX(String x);
}
