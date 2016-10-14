package net.bloberry.async_ui.common;

import java.util.*;

public class WindowInfoList extends ArrayList<WindowInfo>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2209274842872604869L;

	public WindowInfoList(){
		// TODO Auto-generated constructor stub
	}
	public String toString(){
		StringBuffer ret=new StringBuffer();
		ret.append(WindowInfo.toStringNames()+"\n");
		Iterator<WindowInfo> iter=iterator();
		while(iter.hasNext()){
			ret.append(iter.next().toString()+"\n");
		}
		return ret.toString();
	}	
	public String toHtmlTableString(){
		StringBuffer ret=new StringBuffer();
		ret.append("<table>");
		ret.append("<tr><td>"+WorkInstruction.toStringNames().replace("\t", "</td><td>")+"</td></tr>");
		Iterator<WindowInfo> iter=iterator();
		while(iter.hasNext()){
			ret.append("<tr><td>"+iter.next().toString().replace("\t", "</td><td>")+"</td></tr>");
		}
		return ret.toString()+"</table>";
	}	

	
	
	public boolean equals(WindowInfoList otherWindowInfoList){
		if( otherWindowInfoList != null){
			return this.convertWindowInfoToJson().equals(otherWindowInfoList.convertWindowInfoToJson());
		} else  return false;
	}

	/**
	   *  Create array of arrays in JSON format from WorkList Java object
	   * @param worklist
	   * @return
	   */
		public String convertWindowInfoToJson() {
		    String result = ""; 
		    for (int index = 0; index < this.size() ; index++) 
		    {
		    	WindowInfo wi = this.get(index);
		    	result +=  wi.toJson() + (index==(this.size()-1)? "" : ",");
		    }
			return  "[" +  result  + "]";
		}

}
