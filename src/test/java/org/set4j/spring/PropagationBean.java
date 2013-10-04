package org.set4j.spring;

import org.set4j.propfile.Customer;

/**
 * @author Tomas Mikenda
 */
public class PropagationBean
{
    private Customer mCustomer;
    private String mEnvironment;
    private boolean mEnabled;
    private int mFreq;

    public Customer getCustomer()
    {
        return mCustomer;
    }

    public void setCustomer(Customer mCustomer)
    {
        this.mCustomer = mCustomer;
    }

    public boolean isEnabled()
    {
        return mEnabled;
    }

    public void setEnabled(boolean mEnabled)
    {
        this.mEnabled = mEnabled;
    }

    public String getEnvironment()
    {
        return mEnvironment;
    }

    public void setEnvironment(String mEnvironment)
    {
        this.mEnvironment = mEnvironment;
    }

    public int getFreq()
    {
        return mFreq;
    }

    public void setFreq(int mFreq)
    {
        this.mFreq = mFreq;
    }
}
