package org.set4j.objects;

import org.set4j.Set4Module;

public class Method_Main
{
	WithFields_Sub1 sub1 = null;
	WithFields_Sub2 sub2 = null;
	TestModuleTreeMethods sub3 = null;
	
	@Set4Module
	public WithFields_Sub1 getSub1()
    {
    	return sub1;
    }
	public void setSub1(WithFields_Sub1 sub1)
    {
    	this.sub1 = sub1;
    }
	@Set4Module
	public WithFields_Sub2 getSub2()
    {
    	return sub2;
    }
	public void setSub2(WithFields_Sub2 sub2)
    {
    	this.sub2 = sub2;
    }
	public TestModuleTreeMethods getSub3()
    {
    	return sub3;
    }
	public void setSub3(TestModuleTreeMethods sub3)
    {
    	this.sub3 = sub3;
    }
}