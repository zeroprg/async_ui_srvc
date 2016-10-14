/*
 * Copyrigh (c) 2006 TSI Terminal Systems Inc. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of TSI
 * Terminal Systems Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with TSI.
 *
 * Company : TSI Terminal Systems Inc
 * author : Chris Postle (cpostle@tsi.bc.ca)
 *
 * CopyrightVersion 1.09
 *
 * NOTE - 
 * 
 * Project items within the WebServer, Utilities and SNMP package are open source software where copyright 
 * is retained by C.Postle.
 * 
 * They are used within this project by permission and no rights are surrendered to TSI therewith. 
 *
 */

package net.bloberry.async_ui.dee.utilities;

import org.apache.log4j.Logger;


/**

<message MSID="478" type="1630" ack="Y">
<che CHID="RTG22" equipType="RTG" status="Working" DSTA="L1PD" APOW="" ENTR="OPT" MNTR="N" SPOS="2">
<pool name="RTG"><list count="0" type="pow"/>
</pool>
<job MVKD="RECV" age="10" target="GVDU5000998" TRNS="8149"/>
</che>
</message>

This class allows line level byte messages to be written into message objects if they are available using setFromRawMessage (and getAsRawMessage) 
or for serial processing the method assembleMessage may be used to assemble received bytes into a logically complete message.

Once assembled in this way you can use get and set raw message on the message.
 
@author chris postle 
*/
public class XMLStringParser 
{
	
	 private static Logger log = Logger.getLogger(XMLStringParser.class);
	
 	 private String MSID=null;
 	 private String type=null;
 	 private String ack=null;
 	 private int length =0;
     
	
    /*
     * Message types supported
     */
        
    protected int getLength() {
		return length;
	}

    /*
     * Instance fields
     */

    
    /**
     * Default constructor - default the message.
     */
    public XMLStringParser()
    {
        super();
    }

    /**
     * Read element with @param tagName 
     * @param strMessage
     * @param tagName
     * @return
     */
    public static String readElement(String strMessage,String tagName){
    	String field=null;
    	if (strMessage==null) return null;
    	int start=strMessage.indexOf("<"+tagName);
    	if(start!=-1)
    	{
    		start=start+tagName.length();
    		int end=strMessage.indexOf("</"+tagName+">",start+1);
    		if(end==-1){
    			end=strMessage.indexOf("/>",start+1);
    			end=end+2;
    		}else{
    			end=end+tagName.length()+3;
    		}
    		if(end!=-1)
    	    {
    			field=strMessage.substring(start-tagName.length(),end);
    	    }
    	}
    	return field;
    }
    
    /**
     * Read element in with number @param childNumber in list of sibling started from 0
     * @param strMessage
     * @param tagName
     * @param childNumber
     * @return
     */
    public static String readElementInSiblingOrder(String strMessage,String tagName,int childNumber){
    	String child=readElement(strMessage,tagName);
    	if(childNumber==0) return child;
    	int i=0;
    	while(child!=null){
    		i++;
    		int start=strMessage.indexOf(child);
    		String startStr="";
    		String endStr="";
    		if(start>0){
    			startStr=strMessage.substring(0,start);
    			strMessage=strMessage.substring(start);
    		}
			endStr=strMessage.substring(child.length()-1);
    		strMessage=startStr+endStr;
    		child=readElement(strMessage,tagName);
    		if(child==null) return child;
    		if(i==childNumber)  return child;
    	}
    	return null;
    }


    /*
     * Return a field from a valid XML message - returns null if not valid
     */
    /**
     * 
     * @param strMessage
     * @param tagName
     * @return
     */
    public static String readTagContent(String strMessage,String tagName){
        String field=null;
        if (strMessage==null) return null;
    	int start=strMessage.indexOf("<"+tagName);
    	if(start!=-1)
    	{
    		start=start+tagName.length();
    		start=strMessage.indexOf(">",start)+1;
    		
    		int end=strMessage.indexOf("</"+tagName+">",start+1);
    		if(end!=-1)
    	    {
    	    	field=strMessage.substring(start,end);
    	    }
    	}
    	return field;
    }
    
    /**
     * Read first tag name  in xml provided 
     * @param strMessage
     * @return
     */
    public static String readFirstTagName(String strMessage){
        String field=null;
        if (strMessage==null) return null;
    	int start=strMessage.indexOf("<");
    	if(start!=-1 && strMessage.length()>start+1)
    	{
    		field=strMessage.substring(start+1);
    		while(field.length()>0 && field.charAt(0)==' '){
    			field=field.substring(1);
    		}
    		int end=strMessage.indexOf(" ")-1;
    		if(end!=-1)
    	    {
    	    	field=field.substring(0,end);
    	    }
    	}
    	return field;
    }
    
   /**
    * Read attribute from  @param element
    * @param element
    * @param attributeName
    * @return
    */
    public static String readAttributeFromElement(String element,String attributeName){
        String field=null;
        if (element==null) return null;
    	int start=element.indexOf(attributeName);
    	if(start!=-1)
    	{
    		start=start+attributeName.length();
    		start=element.indexOf("=",start)+1;
    		start=element.indexOf("\"",start);

    		int end=element.indexOf("\"",start+1);

    		if(end!=-1)
    	    {
    	    	field=element.substring(start+1,end);
    	    }
    	}
    	return field;
    }
    

    
    protected String getMessageType()
    {
       return type;
    }    

    protected int getMessageID()
    {
        return Integer.parseInt(MSID);
    }    
    

    protected String getMessageAck()
    {
        return ack;
    }    

    public static String changeMSID(String xmlRdtMessageStr, String MSID)
    {
    	String fieldId="MSID";
    	String result=null;
    	
    	int start=xmlRdtMessageStr.indexOf(fieldId);
    	if(start!=-1)
    	{
    		/* find the field id */
    		start=start+fieldId.length();
    		/* Find next equals from the end of the field id */
    		start=xmlRdtMessageStr.indexOf("=",start)+1;
    		/* Find the next quote from the end of the equals - will be the field value */
    		start=xmlRdtMessageStr.indexOf("\"",start);
    		
    	    int end=xmlRdtMessageStr.indexOf("\"",start+1);

    	    if(end!=-1)
    	    {
    	    	String left=xmlRdtMessageStr.substring(0,start+1);
    	    	String right=xmlRdtMessageStr.substring(end);
    	    	result=left+MSID+right;
    	    }
    	}
    	
    	return result;
    	
    }
    
    

	
	public String toString() 
	{
		return " type="+getMessageType()+" MSID="+getMessageID();
	}


	
    /**
     * Test routine
     * @param args
     */
    public static void main(String[] args)
    {
    	
        String testString="<message MSID=\"478\" type=\"1630\" ack=\"Y\">" +
        		"<che CHID=\"RTG22\" equipType=\"RTG\" status=\"Working\" DSTA=\"L1PD\" APOW=\"\" ENTR=\"OPT\" MNTR=\"N\" SPOS=\"2\">" +
        		"<pool name=\"RTG\"><list count=\"0\" type=\"pow\"/>" +
        		"</pool>" +
        		"<job MVKD=\"RECV\" age=\"10\" target=\"GVDU5000998\" TRNS=\"8149\"/>" +
        		"</che>" +
        		"</message>"+ 
        "<message MSID=\"479\" type=\"1630\" ack=\"Y\">" +
		"<che CHID=\"RTG22\" equipType=\"RTG\" status=\"Working\" DSTA=\"L1PD\" APOW=\"\" ENTR=\"OPT\" MNTR=\"N\" SPOS=\"2\">" +
		"<pool name=\"RTG\"><list count=\"0\" type=\"pow\"/>" +
		"</pool>" +
		"<job MVKD=\"RECV\" age=\"10\" target=\"GVDU5000998\" TRNS=\"8149\"/>" +
		"</che>" +
		"</message>"+
		"sdvsdvsdvsdvsdvsdvsdvsdvsdvsdvdsvs"+
		"<message MSID=\"480\" type=\"1630\" ack=\"Y\">" +
		"<che CHID=\"RTG22\" equipType=\"RTG\" status=\"Working\" DSTA=\"L1PD\" APOW=\"\" ENTR=\"OPT\" MNTR=\"N\" SPOS=\"2\">" +
		"<pool name=\"RTG\"><list count=\"0\" type=\"pow\"/>" +
		"</pool>" +
		"<job MVKD=\"RECV\" age=\"10\" target=\"GVDU5000998\" TRNS=\"8149\"/>" +
		"</che>" +
		"</message>"+
		"<message MSID=\"481\" type=\"1630\" ack=\"Y\">" +
		"<che CHID=\"RTG22\" equipType=\"RTG\" status=\"Working\" DSTA=\"L1PD\" APOW=\"\" ENTR=\"OPT\" MNTR=\"N\" SPOS=\"2\">" +
		"<pool name=\"RTG\"><list count=\"0\" type=\"pow\"/>" +
		"</pool>" +
		"<job MVKD=\"RECV\" age=\"10\" target=\"GVDU5000998\" TRNS=\"8149\"/>" +
		"</che>" +
		"</message>"+ 
"<message MSID=\"482\" type=\"1630\" ack=\"Y\">" +
"<che CHID=\"RTG22\" equipType=\"RTG\" status=\"Working\" DSTA=\"L1PD\" APOW=\"\" ENTR=\"OPT\" MNTR=\"N\" SPOS=\"2\">" +
"<pool name=\"RTG\"><list count=\"0\" type=\"pow\"/>" +
"</pool>" +
"<job MVKD=\"RECV\" age=\"10\" target=\"GVDU5000998\" TRNS=\"8149\"/>" +
"</che>" +
"</ message>"+
"<message MSID=\"483\" type=\"1630\" ack=\"Y\">" +
"<che CHID=\"RTG22\" equipType=\"RTG\" status=\"Working\" DSTA=\"L1PD\" APOW=\"\" ENTR=\"OPT\" MNTR=\"N\" SPOS=\"2\">" +
"<pool name=\"RTG\"><list count=\"0\" type=\"pow\"/>" +
"</pool>" +
"<job MVKD=\"RECV\" age=\"10\" target=\"GVDU5000998\" TRNS=\"8149\"/>" +
"</che>" +
"</message>";		

        //Single message test
        testString="<message MSID = \"479\" type=\"1630\" ack=\"Y\">" +
		"<che CHID=\"RTG22\" equipType=\"RTG\" status=\"Working\" DSTA=\"L1PD\" APOW=\"\" ENTR=\"OPT\" MNTR=\"N\" SPOS=\"2\">" +
		"<pool name=\"RTG\"><list count=\"0\" type=\"pow\"/>" +
		"</pool>" +
		"<job MVKD=\"RECV\" age=\"10\" target=\"GVDU5000998\" TRNS=\"8149\"/>" +
		"</che>" +
		"</message>";
                
        byte[] byteTst= testString.getBytes();

        XMLStringParser test = new XMLStringParser();
        long start=System.currentTimeMillis();

// 2. change msid 
//log.debug(MessageXMLRDT.changeMSID(testString,"2000"));        
        
// 1. Set from raw test
//test.setFromRawMessage(byteTst);  
log.debug("-"+test.getMessageID()+"-"+test.getMessageType());
  
// 0. Parser test        
//        int idx=0;
//        byte byteRead=byteTst[idx];
//        while(idx<byteTst.length)
//        {
//        	if(test.assembleMessage(byteTst[idx]))
//        	{
//        		log.debug(test.getMessageType());
//        		log.debug(test.getMessageField("che CHID"));
//        		
//        		test.clear();
//        	}
//        	idx=idx+1;
//        }
//        
//        log.debug("Time to run = "+(System.currentTimeMillis()-start));
                
    }	
    public static int getErrorId(String error){
		int id=-1;
    	if(error!=null){
			String errorId=XMLStringParser.readAttributeFromElement(error, "msgID");
				id=Integer.parseInt(errorId);
		}
    	return id;
    }
    public static String getErrorDescription(String error){
    	return readTagContent(error,"errMsg");
    }
	
}
