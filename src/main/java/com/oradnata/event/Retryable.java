/**
 * 
 */
package com.oradnata.event;

/**
 * This is to retry the process from where it failed previously.
 */
public interface Retryable  extends Comparable {
	
	public String getRetryPosition();
	
	public boolean retryFromLastPostition(String position);	
	
	public void incrementRetryCount();
	
	public int getRetryCount();
	
	public int getMaxRetyCount();
	
	public String getJobDetails();
	
	public void postProcess();
}
