package org.set4j;
/**
 * Company     : TietoEnator Oyj, 2006
 * Project     : XMM Batches
 * File        : ExceptionCmdLine.java
 * Package     : com.te.xmm
 * Description : 
 * Created     : 22.3.2007 mikentom
 * Inspected   :
 * Changed     :
 */

/**
 * @author mikentom
 * @date 22.3.2007
 */
public class ExceptionCmdLine extends RuntimeException
{
	private String mMessage = null;
	private String mElement = null;

	/** SUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * @param arg0
	 */
	public ExceptionCmdLine(String arg0)
	{
		super(arg0);
		mMessage = arg0;
	}
	/**
	 * @param Message
	 */
	public ExceptionCmdLine(String msg, String element)
	{
		super(msg);
		mMessage = msg;
		mElement = element;
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public ExceptionCmdLine(String arg0, Throwable arg1)
	{
		super(arg0, arg1);
	}
	/*
	@Override
    public String getMessage()
    {
	    return super.toString() + mElement==null ? "": " at " + mElement;
    }
    */
	
	@Override
    public String toString()
    {
	    return mMessage + (mElement==null ? "": " at '" + mElement + "'");
    }
	

}
