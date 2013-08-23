package org.set4j.objects.mix;

import org.set4j.Initializer;
import org.set4j.Set4Module;
//import org.set4j.Set4PropFile;
import org.set4j.objects.WithFields_Sub1;
import org.set4j.objects.WithFields_Sub2;

/**
 * @author Tomas Mikenda
 *
 */
//@Set4PropFile()
public abstract class ConfBase
{
	@Set4Module
	WithFields_Sub1 sub1 = null;
	
	@Set4Module
	WithFields_Sub2 sub2 = null;
	
	@Set4Module
	SubModIfBase mod = null;
	
	protected void init()
	{
		Initializer.init(this);
	}
	
	public WithFields_Sub1 getSub1()
    {
    	return sub1;
    }

	public WithFields_Sub2 getSub2()
    {
    	return sub2;
    }
	
	public SubModIfBase getMod()
    {
    	return mod;
    }
	
}
