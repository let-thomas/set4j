package org.set4j.impl;

import java.util.Comparator;

import org.set4j.Set4JException;
import org.set4j.Set4Value;

/**
 * @author Tomas Mikenda
 *
 */
public class PropComp implements Comparator<Set4Value>
{

	/* (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(Set4Value arg0, Set4Value arg1)
    {
	    int level0 = arg0.when().length;
	    int level1 = arg1.when().length;
	    
	    // check if when is not default
	    if (level0 == 1 && arg0.when()[0].what().length() == 0) level0 = 0;
	    if (level1 == 1 && arg1.when()[0].what().length() == 0) level1 = 0;
	    
	    int diff = level0 - level1;
	    if (diff == 0)
	    {
	    	// same level condition
	    	// this is not good enough but good for now
	    	diff = arg0.hashCode() - arg1.hashCode();
	    }
	    
	    return diff;
    }

}
