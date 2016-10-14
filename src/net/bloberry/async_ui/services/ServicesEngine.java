package net.bloberry.async_ui.services;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.apache.log4j.Logger;

import net.bloberry.async_ui.common.DataExchangeConfigureException;

public class ServicesEngine {

	private static Logger log = Logger.getLogger(ServicesEngine.class);
	
	public final static String PROPERTIES_FILE = "resources.properties" ;
	//UI Event mechanism Properties
	public final static String EVENT_CONNECTION_TIMEOUT="event.connection.timeout";
	public final static String EVENT_RETRY_INTERVAL="event.retry.interval";
	public final static String EVENT_PURGE_TIMER_INTERVAL="event.purge.timer.interval";
	
	// Environment Properties
	private final static String N4_ENVIRONMENT_NAME = "n4.environment.name";
	private final static String MENU_PROPERTIES_FILE_NAME = "custom.menu.properties";
	
	private final static String REEFER_DAYTIME_JAVA_PATTERN="reefer.datetime.java.pattern";

	public static final String CONNECTION_LOST_MESSAGE = "connection.lost.message";
	
	private String version=null;
	private Properties properties=new Properties();
	private long startTime = 0;
	

	private static ServicesEngine servicesProxy;

	private ServicesEngine() {
	}
	
	public static ServicesEngine getInstance() throws DataExchangeConfigureException {
		if(servicesProxy==null){
			servicesProxy=new ServicesEngine();
		}
		return servicesProxy;
	}

	public void init() throws DataExchangeConfigureException{
		servicesProxy.configure();
		// DataExchangeService.getInstance().init();	
		// ArgoService.getInstance().init();
		// UIService.getInstance().init();
		 
	}
	
	
	
	private void configure() throws DataExchangeConfigureException{
		try{
	        Manifest manifest=null;
	        try{
	        	manifest=getManifest();
	        }catch(IOException e){
	        	String warPath=System.getProperty("catalina.home")+"/webapps/async_ui_srvc.war";
	            try{
		        	JarFile file= new JarFile(warPath);
		            manifest = file.getManifest();
	            }catch(IOException ex){
	            	log.debug("WAR does not exist! "+warPath);
	            }
	        }
	        if(manifest!=null){
	        	Attributes attrs = manifest.getMainAttributes();
	        	version=attrs.getValue("Specification-Version")+"("+attrs.getValue("Implementation-Version")+")";
	        }
	        startTime = System.currentTimeMillis();
	        reloadProperties();
	        
	        //SimpleDateFormat dt1 = new SimpleDateFormat(getProperty(REEFER_DAYTIME_JAVA_PATTERN));


	        
		}catch(DataExchangeConfigureException e){
			throw e;
		}catch(Throwable t){
			throw new DataExchangeConfigureException("Uncatched throwable occured",t);
		}
	}
	
	
	public void reloadProperties() throws DataExchangeConfigureException{
        try{
        	loadProperties();
		}catch (IOException e){
			throw new DataExchangeConfigureException("Cannot load property file: "+System.getProperty("catalina.home") + PROPERTIES_FILE,e);
		}
	}
	
	
	
	public String getElapsedTime(){
		Calendar calendar= Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis() - startTime - java.util.TimeZone.getDefault().getRawOffset());//+java.util.TimeZone.getDefault().getDSTSavings());
		return  calendar.get(Calendar.DAY_OF_YEAR)-1+" days, "+calendar.get(Calendar.HOUR_OF_DAY)+" hours, "+calendar.get(Calendar.MINUTE)+ " min, "+calendar.get(Calendar.SECOND)+" sec.";
	}
	
	
	
	public String getVersion() {
		return version;
	}

    private Manifest getManifest() throws IOException 
    {
    	Manifest manifest = null;
    	Enumeration<URL> resources = getClass().getClassLoader()
    			  .getResources("META-INF/MANIFEST.MF");
    			while (resources.hasMoreElements()) {
    			    try {
    			    	
    			       manifest = new Manifest(resources.nextElement().openStream());
    			      // check that this is your manifest and do what you need or get the next one
    			       log.info(" manifest has been read , manifest vsalues: " + manifest.getMainAttributes().values() );
    			    } catch (IOException E) {
    	            	log.debug("Manifest does not exist! "+manifest);
    			    }
    			}
		return manifest;
    } 			

    /**
    * Method used to load the specified property file into the specified property object.
    * @param theProperties the Properties object to load.
    * @param resourceFile the file where the properties are currently stored.
    * @param errorMessage the storage for any error messages which occur while loading.
    * @throws IOException 
     */
    private void loadProperties() throws IOException
    {
               properties = new Properties();
               InputStream is = Thread.currentThread().getContextClassLoader().
               getResourceAsStream(PROPERTIES_FILE);
                  // Remove anything currently in the properties file before loading it up. 
                  properties.clear();
                  properties.load(is);
                  is.close();
    }
    
	public String getProperty(String name){
		return trimProperty(properties.getProperty(name));
	}
	
	public String getEnviromentName(){
		String ret;
		ret= getProperty(N4_ENVIRONMENT_NAME);
		ret=ret+" ("+getVersion()+")";
	 	return ret;
	}
	/**
	 * Trims any junk off the end of the string if the passed in value is non - null.
	 * @param theProperty the string to trim.
	 * @return the trimmed string or null if the input is null.
	 */
	private String trimProperty(String theProperty)
	{
		if ( theProperty != null )
		{
			theProperty = theProperty.trim();
		}
		return theProperty;
	}
}
