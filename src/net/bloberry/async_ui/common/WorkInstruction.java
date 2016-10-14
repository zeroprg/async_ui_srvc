package net.bloberry.async_ui.common;

import net.bloberry.async_ui.dpe.utils.SafeHtml;

public class WorkInstruction {
	private String sequence;
	private String cheCarry;
	private String kind;
	private String equipmentId;
	private String moveFrom;
	private String moveTo;
	private String class_;
	private String length;
	private String weight;
	private String bondedDest;
	private String warning;
	private String moveTime;
	private String moveStage;
	private String twinWith;
	private boolean confirmed;
	private boolean locked;
	private boolean definite;
	
	public WorkInstruction() {
		// TODO Auto-generated constructor stub
	}

	public String getSequence() {
		return SafeHtml.htmlEscape(sequence, false);
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	public String getCheCarry() {
		return SafeHtml.htmlEscape(cheCarry, false); 
	}

	public void setCheCarry(String cheCarry) {
		this.cheCarry = cheCarry;
	}

	public String getKind() {
		return SafeHtml.htmlEscape(kind, false); 
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public String getEquipmentId() {
		return SafeHtml.htmlEscape(equipmentId, false); 
	}

	public void setEquipmentId(String equipmentId) {
		this.equipmentId = equipmentId;
	}

	public String getMoveFrom() {
		return SafeHtml.htmlEscape(moveFrom, false);  
	}

	public void setMoveFrom(String moveFrom) {
		this.moveFrom = moveFrom;
	}

	public String getMoveTo() {
		return SafeHtml.htmlEscape(moveTo, false);  
	}

	public void setMoveTo(String moveTo) {
		this.moveTo = moveTo;
	}

	public String getClass_() {
		return SafeHtml.htmlEscape(class_, false); 
	}

	public void setClass_(String class_) {
		this.class_ = class_;
	}

	public String getLength() {
		return SafeHtml.htmlEscape(length, false); 
	}

	public void setLength(String length) {
		this.length = length;
	}

	public String getWeight() {
		return SafeHtml.htmlEscape(weight, false); 
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	public String getBondedDest() {
		return SafeHtml.htmlEscape(bondedDest, false); 
	}

	public void setBondedDest(String bondedDest) {
		this.bondedDest = bondedDest;
	}

	public String getWarning() {
		return SafeHtml.htmlEscape(warning, false); 
	}

	public void setWarning(String warning) {
		this.warning = warning;
	}

	public String getMoveTime() {
		return SafeHtml.htmlEscape(moveTime, false); 
	}

	public void setMoveTime(String moveTime) {
		this.moveTime = moveTime;
	}
	
	public String getIcon() {
		StringBuffer ret=new StringBuffer();
		if("COMPLETE".equals(moveStage)){
			return null;
		}else if("PLANNED".equals(moveStage)){
			if(confirmed){
				if(locked){
					ret.append("blue");
				}else{
					ret.append("black");
				}
			}else{
				if(locked){
					ret.append("red");
				}else{
					ret.append("green");
				}
				
			}
		}else if("CARRY_READY".equals(moveStage)){
			ret.append("yellow");
		}else{
			ret.append("orange");
		}
		if(definite){
			ret.append("Dot");
		}else{
			ret.append("Diamond");
		}
		if("NEXT".equals(twinWith)){
			ret.append("Top");
		}else if("PREV".equals(twinWith)){
			ret.append("Bottom");
		}
		return ret.toString();
	}

	public String getMoveStage() {
		return SafeHtml.htmlEscape(moveStage, false);  
	}

	public void setMoveStage(String moveStage) {
		this.moveStage = moveStage;
	}

	public String getTwinWith() {
		return SafeHtml.htmlEscape(twinWith, false); 
	}

	public void setTwinWith(String twinWith) {
		this.twinWith = twinWith;
	}

	public boolean isConfirmed() {
		return confirmed;
	}

	public void setConfirmed(boolean confirmed) {
		this.confirmed = confirmed;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public boolean isDefinite() {
		return definite;
	}

	public void setDefinite(boolean definite) {
		this.definite = definite;
	}

	public String toString(){
		String ret="";
		ret=ret+getIcon()+"\t";
		ret=ret+sequence+"\t";
		ret=ret+cheCarry+"\t";
		ret=ret+kind+"\t";
		ret=ret+equipmentId+"\t";
		ret=ret+moveFrom+"\t";
		ret=ret+moveTo+"\t";
		ret=ret+class_+"\t";
		ret=ret+length+"\t";
		ret=ret+weight+"\t";
		ret=ret+bondedDest+"\t";
		ret=ret+warning+"\t";
		ret=ret+moveTime;
		return ret;
	}
	public static String toStringNames(){
		String ret="";
		ret=ret+"Icon"+"\t";
		ret=ret+"Sequence"+"\t";
		ret=ret+"CheCarry"+"\t";
		ret=ret+"Kind"+"\t";
		ret=ret+"EquipmentId"+"\t";
		ret=ret+"MoveFrom"+"\t";
		ret=ret+"MoveTo"+"\t";
		ret=ret+"Class"+"\t";
		ret=ret+"Length"+"\t";
		ret=ret+"Weight"+"\t";
		ret=ret+"BondedDest"+"\t";
		ret=ret+"Warning"+"\t";
		ret=ret+"MoveTime";
		
		return ret;
	}
}
