package net.bloberry.async_ui.dpe.utils;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import net.bloberry.async_ui.dpe.models.Window;
import net.bloberry.async_ui.services.ServiceFactory;
import net.bloberry.async_ui.services.ServiceType;
import net.bloberry.async_ui.services.UIService;




public class PurgeWindowsThread extends Thread {
	private Map <String,Window> windowMap=null;
    private long purgeInterval;
	private boolean started=false;
	private boolean stopped=false;
	private long lastTimePurged=0;
    
    private PurgeWindowsThread(){
    }
    
    private static Logger log = Logger.getLogger(PurgeWindowsThread.class);
    private static PurgeWindowsThread client;
    
    public static synchronized PurgeWindowsThread getInstance(Map<String, Window> windowMap,long purgeInterval)
    {

		if(client==null || !client.isAlive() || client.isStopped()){
			client=new PurgeWindowsThread();
			client.windowMap = windowMap;
			client.purgeInterval=purgeInterval;
			client.start();
		}

		return client;
    	
    }
	public static PurgeWindowsThread getInstance(){
		if(client==null || !client.isAlive() || client.isStopped()){
			return null;
		}
		return client;
	}
	
	public boolean isStarted() {
		return started;
	}
	public boolean isStopped() {
		return stopped;
	}
	public void run () 
	{
		started=true;
		while(started)
		{
			try{
				
				if((System.currentTimeMillis()-lastTimePurged)>purgeInterval){
					try {
						if(windowMap!=null){
							Iterator<Entry<String, Window>> iter=windowMap.entrySet().iterator();
							while(iter.hasNext()){
								Entry<String, Window> entry=iter.next();
								Window window = entry.getValue();
								if(window.isDisposed()){
									String windowToken=entry.getKey();
									window.destroy();
									iter.remove();
									log.debug("Window "+windowToken+" is purged");
								}
							}
						}
					} catch ( Exception e ) {
						log.error("Error during ruging windows:",e);
					}
				}
				if(started)	{
        			synchronized (this)
        			{
        				wait(300);//TODO was 250
        			}
				}
			}catch(Throwable t){
				log.error("Exception in PurgeWindowsThread",t);
			}
			
		}
		stopped=true;
		log.info("PurgeWindowsThread is stopped");
	}


 	public void stopPurgeWindowsThread() {
		if(started && !stopped){
			log.info("Stopping PurgeWindowsThread...");

			started = false;
			while(!stopped){
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
			}
			windowMap=null;
		}
	}
	public static void main(String[] args) {
		
	}
}
