package net.bloberry.async_ui.common;

import java.util.*;

public class WorkList extends ArrayList<WorkInstruction>{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2793141915247410961L;
	private String workListName;
	private String pow;

	public WorkList(String workListName,String pow) {
		this.workListName=workListName;
		this.pow=pow;
	}
	private WorkList(){
		
	}
	public String getWorkListName() {
		return workListName;
	}
	public String getPow() {
		return pow;
	}
	public String toString(){
		StringBuffer ret=new StringBuffer();
		ret.append(WorkInstruction.toStringNames()+"\n");
		Iterator<WorkInstruction> iter=iterator();
		while(iter.hasNext()){
			ret.append(iter.next().toString()+"\n");
		}
		return ret.toString();
	}	
	public String toHtmlTableString(){
		StringBuffer ret=new StringBuffer();
		ret.append("<table>");
		ret.append("<tr><td>"+WorkInstruction.toStringNames().replace("\t", "</td><td>")+"</td></tr>");
		Iterator<WorkInstruction> iter=iterator();
		while(iter.hasNext()){
			ret.append("<tr><td>"+iter.next().toString().replace("\t", "</td><td>")+"</td></tr>");
		}
		return ret.toString()+"</table>";
	}	

	
	
	public boolean equals(WorkList otherWorkList){
		if( otherWorkList != null){
			return this.convertWorkListToJson().equals(otherWorkList.convertWorkListToJson());
		} else  return false;
	}

	/**
	   *  Create array of arrays in JSON format from WorkList Java object
	   * @param worklist
	   * @return
	   */
		public String convertWorkListToJson() {
		    String result = ""; 
		    for (int index = 0; index < this.size() ; index++) 
		    {
		    	WorkInstruction wi = this.get(index);
		    	String row =  //"[\"<img width='30px' height='30px' src='Images/" + wi.getIcon() + ".ico'></img>"+ 
		    			"[\"" + wi.getIcon() + ";" + wi.getSequence()  + "\",\"" + wi.getCheCarry() + "\",\"" + wi.getKind() + "\",\"" + wi.getEquipmentId() + "\",\"" + wi.getMoveFrom() + "\",\"" + wi.getMoveTo() + "\",\""+ wi.getClass_() + "\",\"" + wi.getLength()+ "\",\"" + wi.getWeight() + "\",\"" + wi.getBondedDest() + "\",\""+ wi.getWarning() + "\",\"" + wi.getMoveTime() + "\"]";  
		        result +=  row + (index==(this.size()-1)? "" : ",");
		    }
			return  "[" +  result  + "]";
		}
		public String fakeConvertWorkListIoJson(){
			return  "[[\"<img width='30px' height='30px' src='Images/greenDotTop.ico'> 1</img>\",\"null\",\"LOAD\",\"SEGU2286994\",\"F027B.1\",\"470402\",\"2DRS\",\"20'\",\"null\",\"null\",\"null\",\"27/04/2016 13:12\"],[\"<img width='30px' height='30px' src='Images/greenDotBottom.ico'> 2</img>\",\"null\",\"LOAD\",\"CLHU3341682\",\"F133C.4\",\"450402\",\"2DRS\",\"20'\",\"null\",\"null\",\"null\",\"27/04/2016 13:12\"],[\"<img width='30px' height='30px' src='Images/greenDotTop.ico'> 3</img>\",\"null\",\"LOAD\",\"SEGU2280907\",\"F027C.4\",\"470202\",\"2DRS\",\"20'\",\"null\",\"null\",\"null\",\"27/04/2016 13:15\"],[\"<img width='30px' height='30px' src='Images/greenDotBottom.ico'> 4</img>\",\"null\",\"LOAD\",\"CMAU0516748\",\"F133C.3\",\"450202\",\"2DRS\",\"20'\",\"null\",\"null\",\"null\",\"27/04/2016 13:15\"],[\"<img width='30px' height='30px' src='Images/greenDotTop.ico'> 5</img>\",\"null\",\"LOAD\",\"ECMU2054620\",\"F027C.3\",\"470002\",\"2DRS\",\"20'\",\"null\",\"null\",\"null\",\"27/04/2016 13:18\"],[\"<img width='30px' height='30px' src='Images/greenDotBottom.ico'> 6</img>\",\"null\",\"LOAD\",\"TGHU1785169\",\"F133C.2\",\"450002\",\"2DRS\",\"20'\",\"null\",\"null\",\"null\",\"27/04/2016 13:18\"],[\"<img width='30px' height='30px' src='Images/greenDotTop.ico'> 7</img>\",\"null\",\"LOAD\",\"CMAU1860779\",\"F027C.2\",\"470102\",\"2DRS\",\"20'\",\"null\",\"null\",\"null\",\"27/04/2016 13:21\"],[\"<img width='30px' height='30px' src='Images/greenDotBottom.ico'> 8</img>\",\"null\",\"LOAD\",\"ECMU1619933\",\"F133C.1\",\"450102\",\"2DRS\",\"20'\",\"null\",\"null\",\"null\",\"27/04/2016 13:21\"],[\"<img width='30px' height='30px' src='Images/greenDotTop.ico'> 9</img>\",\"null\",\"LOAD\",\"CMAU1633559\",\"F027C.1\",\"470302\",\"2DRS\",\"20'\",\"null\",\"null\",\"null\",\"27/04/2016 13:24\"],[\"<img width='30px' height='30px' src='Images/greenDotBottom.ico'> 10</img>\",\"null\",\"LOAD\",\"TGCU0044836\",\"F133D.4\",\"450302\",\"2DRS\",\"20'\",\"null\",\"null\",\"null\",\"27/04/2016 13:24\"],[\"<img width='30px' height='30px' src='Images/greenDotTop.ico'> 11</img>\",\"null\",\"LOAD\",\"CMAU0427511\",\"F027D.5\",\"470404\",\"2DRS\",\"20'\",\"null\",\"null\",\"null\",\"27/04/2016 13:27\"],[\"<img width='30px' height='30px' src='Images/greenDotBottom.ico'> 12</img>\",\"null\",\"LOAD\",\"CMAU2123924\",\"F133D.3\",\"450404\",\"2DRS\",\"20'\",\"null\",\"null\",\"null\",\"27/04/2016 13:27\"],[\"<img width='30px' height='30px' src='Images/greenDotTop.ico'> 13</img>\",\"null\",\"LOAD\",\"CMAU0783235\",\"F027D.4\",\"470204\",\"2DRS\",\"20'\",\"null\",\"null\",\"null\",\"27/04/2016 13:30\"],[\"<img width='30px' height='30px' src='Images/greenDotBottom.ico'> 14</img>\",\"null\",\"LOAD\",\"TEMU3525934\",\"F133D.2\",\"450204\",\"2DRS\",\"20'\",\"null\",\"null\",\"null\",\"27/04/2016 13:30\"],[\"<img width='30px' height='30px' src='Images/greenDotTop.ico'> 15</img>\",\"null\",\"LOAD\",\"CMAU1306134\",\"F027D.3\",\"470004\",\"2DRS\",\"20'\",\"null\",\"null\",\"null\",\"27/04/2016 13:33\"],[\"<img width='30px' height='30px' src='Images/greenDotBottom.ico'> 16</img>\",\"null\",\"LOAD\",\"GLDU9863003\",\"F133D.1\",\"450004\",\"2DRS\",\"20'\",\"null\",\"null\",\"null\",\"27/04/2016 13:33\"],[\"<img width='30px' height='30px' src='Images/greenDotTop.ico'> 17</img>\",\"null\",\"LOAD\",\"TEMU2403356\",\"F027D.2\",\"470104\",\"2DRS\",\"20'\",\"null\",\"null\",\"null\",\"28/04/2016 14:29\"],[\"<img width='30px' height='30px' src='Images/greenDotBottom.ico'> 18</img>\",\"null\",\"LOAD\",\"CMAU0289074\",\"F133E.4\",\"450104\",\"2DRS\",\"20'\",\"null\",\"null\",\"null\",\"27/04/2016 13:36\"],[\"<img width='30px' height='30px' src='Images/greenDotTop.ico'> 19</img>\",\"null\",\"LOAD\",\"TGHU2991225\",\"F027D.1\",\"470304\",\"2DRS\",\"20'\",\"null\",\"null\",\"null\",\"27/04/2016 13:39\"],[\"<img width='30px' height='30px' src='Images/greenDotBottom.ico'> 20</img>\",\"null\",\"LOAD\",\"GESU3004663\",\"F133E.3\",\"450304\",\"2DRS\",\"20'\",\"null\",\"null\",\"null\",\"27/04/2016 13:39\"],[\"<img width='30px' height='30px' src='Images/greenDotTop.ico'> 21</img>\",\"null\",\"LOAD\",\"CMAU0325778\",\"F027E.5\",\"470406\",\"2DRS\",\"20'\",\"null\",\"null\",\"null\",\"28/04/2016 14:35\"],[\"<img width='30px' height='30px' src='Images/greenDotBottom.ico'> 22</img>\",\"null\",\"LOAD\",\"CMAU0580578\",\"F133E.2\",\"450406\",\"2DRS\",\"20'\",\"null\",\"null\",\"null\",\"28/04/2016 14:35\"],[\"<img width='30px' height='30px' src='Images/greenDotTop.ico'> 23</img>\",\"null\",\"LOAD\",\"ECMU1235964\",\"F027E.4\",\"470206\",\"2DRS\",\"20'\",\"null\",\"null\",\"null\",\"28/04/2016 14:38\"],[\"<img width='30px' height='30px' src='Images/greenDotBottom.ico'> 24</img>\",\"null\",\"LOAD\",\"CMAU0409740\",\"F133E.1\",\"450206\",\"2DRS\",\"20'\",\"null\",\"null\",\"null\",\"28/04/2016 14:38\"],[\"<img width='30px' height='30px' src='Images/greenDotTop.ico'> 25</img>\",\"null\",\"LOAD\",\"CMAU1140552\",\"F027E.3\",\"470006\",\"2DRS\",\"20'\",\"null\",\"null\",\"null\",\"28/04/2016 14:41\"],[\"<img width='30px' height='30px' src='Images/greenDotBottom.ico'> 26</img>\",\"null\",\"LOAD\",\"CMAU1671219\",\"F133F.4\",\"450006\",\"2DRS\",\"20'\",\"null\",\"null\",\"null\",\"28/04/2016 14:41\"],[\"<img width='30px' height='30px' src='Images/greenDotTop.ico'> 27</img>\",\"null\",\"LOAD\",\"CMAU1125265\",\"F027E.2\",\"470106\",\"2DRS\",\"20'\",\"null\",\"null\",\"null\",\"28/04/2016 14:44\"],[\"<img width='30px' height='30px' src='Images/greenDotBottom.ico'> 28</img>\",\"null\",\"LOAD\",\"ECMU2038126\",\"F133F.3\",\"450106\",\"2DRS\",\"20'\",\"null\",\"null\",\"null\",\"28/04/2016 14:44\"],[\"<img width='30px' height='30px' src='Images/greenDotTop.ico'> 29</img>\",\"null\",\"LOAD\",\"CMAU1135767\",\"F027E.1\",\"470306\",\"2DRS\",\"20'\",\"null\",\"null\",\"null\",\"28/04/2016 14:47\"],[\"<img width='30px' height='30px' src='Images/greenDotBottom.ico'> 30</img>\",\"null\",\"LOAD\",\"TCLU7381550\",\"F133F.2\",\"450306\",\"2DRS\",\"20'\",\"null\",\"null\",\"null\",\"28/04/2016 14:47\"]]";
		}
		

}
