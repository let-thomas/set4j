package org.set4j;


/**
 * @author Tomas Mikenda
 *
 */
public interface Settings extends SetBase
{
	/*
	private static Settings singleton = null;
	public static synchronized Settings getInstance()
	{
		if (singleton == null) singleton = Initializer.init(Settings.class);
		return singleton;
	}
	public Settings(int x)
    {
	    super();
    }
	*/
	@Set4Module
	ModuleExt module();
}

