package net.bloberry.async_ui.dee.utilities;

import java.util.ArrayList;


public class MessageList extends ArrayList 
{
	private static int INITIAL_SIZE = 100;
	protected Boolean LIST_LOCK = new Boolean(false);
		
	public MessageList()
	{
		super(INITIAL_SIZE);
	}
	
	/**
	 * Over-ridden from the ArrayList superclass in order to enforce synchronized access and 
	 * thread notification.
	 * @param message the object to insert into the list.
	 */
	public boolean add(Object message)
	{
		boolean returnValue;
		
		synchronized(LIST_LOCK)
		{
			returnValue = super.add(message);
		}
		this.notifyNewJob();
		return returnValue;
	}
	
	/**
	 * Adds a new object to this list without issuing a notification to any waiting thread.
	 * @param message
	 * @return
	 */
	public boolean addWithoutNotification(Object message)
	{
		boolean returnValue;
		
		synchronized(LIST_LOCK)
		{
			returnValue = super.add(message);
		}
		return returnValue;
	}
	
	/**
	 * @return the element in the first position of the list WITHOUT removing the element.
	 */
	public Object get(int index)
	{
		synchronized(LIST_LOCK)
		{
			if ( this.size() > index )
			{
				return super.get(index);
			} 
			else
			{
				return null;
			}
		}
	}
	
	/**
	 * @return - removes and returns the first item in the list or null if the list is empty.
	 */
	public Object getAndRemove()
	{
		synchronized(LIST_LOCK)
		{
			if ( this.size() > 0 )
			{
				Object message = super.get(0);
				this.remove(0);
				return message;
			} 
			else
			{
				return null;
			}
		}
	}
	
	/**
	 * Removes all elements from the list between start and end.
	 * @param start the start of the range to remove.
	 * @param end the end of the range to remove.
	 */
	public void removeRange(int start, int end)
	{
		synchronized(LIST_LOCK)
		{
			// Error condition, nothing will be done.
			if ( start > end )
			{
				return;
			}
			
			// If end is bigger than the current size of the list then remove everything.
			if ( this.size() < end )
			{
				super.clear();
			}
			else
			{
				super.removeRange(start, end);
			}
		}
	}
	
	/*public boolean addPriorityMessage(Object[] messages)
	{
		synchronized(LIST_LOCK)
		{
			int length = messages.length;
			for ( int i = 0; i < length; i++ )
			{
				super.add(0, messages[i]);
			}
		}
		this.notifyNewJob();
		return true;
	}
	
	public boolean addPriorityMessage(Object message)
	{
		synchronized(LIST_LOCK)
		{
			super.add(0, message);
		}
		this.notifyNewJob();
		return true;
	}*/
	
	/**
	 * @return the size of this list.
	 */
	public int size()
	{
		synchronized(LIST_LOCK)
		{
			return super.size();
		}
	}
	
	public void clear()
	{
		synchronized(LIST_LOCK)
		{
			super.clear();
		}
	}
	
	/********************************************************************************************************************************
	 * IT IS EXTREMELY IMPORTANT THAT THE FOLLOWING METHODS BE SYNCHRONIZED
	 * The reason is that the Job Dispatching thread waits on this list until something is added to the list at 
	 * which point the thread is woken up and the job is handled.
	 * In order for this to work the calling thread must own the object monitor for the list and one way to get
	 * the monitor is to call a synchronized method on the object.
	 ********************************************************************************************************************************/
	public synchronized void waitForNewJob()
	{
		try {
			if ( this.size() == 0 )
			{
				this.wait();
			}
		} catch ( InterruptedException ie ) {
			// Nothing we can do about this so just ignore..
		}
	}
	public synchronized void waitForNewJobEvenIfListIsNotEmpty()
	{
		try {
			this.wait();
		} catch ( InterruptedException ie ) {
			// Nothing we can do about this so just ignore..
		}
	}
	
	/**
	 * This is a very important method in this solution as it allows for clients to wait on a 
	 * list "atomically". This problem arose when sending messages and then waiting for the response.
	 * In that case client code would insert the first message into the list. This would cause the
	 * message to be sent. Now suppose the response comes back and is added to this list before 
	 * the client code invokes wait(time). Then the notification would be lost and the client code
	 * would wait the full time out before continuing. 
	 * 
	 * Symptons: When sending 15 messages the code would rapidly send 10 messages and then  
	 * everything would stop for 30 secs (value of the timeout). This was because the response came back
	 * and was inserted into the list and the notification issued (and subsequently lost) before the client 
	 * code had invoked wait.
	 * 
	 * Solution: Client code invokes this method which has the following attributes:
	 * 	1) Method is synchronized, so while executing, all other methods of this object are locked. Therefore
	 * the insertion of the response cannot happen while performing this method.
	 * 	2) Before waiting the check is made to ensure that the response has not come back yet.
	 * 	3) If everything is still pending then invoke wait and when the response arrives the notification 
	 * will wake up the correct thread for further processing.
	 * @param size the size of the list when the transaction is finished processing.
	 * @param timeToWait the time to wait before checking for completion of the transaction.
	 */
	public synchronized void checkSizeAndWait(int size, int timeToWait)
	{
		if ( this.size() == size )
		{
			return;
		}
		else
		{
			try {
				this.wait(timeToWait);
			} catch ( InterruptedException ie ) {
				// Nothing we can do about this so just ignore..
			}
		}
	}
	
	/**
	 * Notify the Job Dispatching thread about the new job that has been added to the list.
	 */
	public synchronized void notifyNewJob()
	{
		this.notify();
	}
	
	public synchronized void notifyAllOfNewJob()
	{
		this.notifyAll();
	}
	/**********************************************************************************************************************************/
	
	/*public static void main(String[] args)
	{
		MessageList theList = new MessageList();
		theList.add("This is data 1");
		theList.add("This is data 2");
		theList.add("This is data 3");
		theList.addPriorityMessage("This is priority data 4");
	}*/
}
