package org.set4j.objects.mix;

import org.set4j.Set4Module;

/**
 * @author Tomas Mikenda
 *
 */
public class Conf extends ConfBase
{
	@Set4Module
	public SubModIfChild mod = null;

	
	public SubModIfChild getMod()
    {
    	return mod;
    }
	

	// singleton part
	private static Conf self = new Conf();
	private Conf()
	{
		init();
	}
	public static Conf getInstance()
	{
		return self;
	}
}
