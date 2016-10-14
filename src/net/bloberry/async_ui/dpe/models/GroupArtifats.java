package net.bloberry.async_ui.dpe.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import net.bloberry.async_ui.common.Message;

public class GroupArtifats {
	public static final int MAXIMUM_MESSAGES_INQUEUE = 10;
	private Queue<Message> msgQueue;
	List<String> users;
	String name;
	int activeMembers = 0;
	
	public GroupArtifats(String group)
	{   this.name = group;
		this.users = new ArrayList<String>();
		this.msgQueue = new ArrayBlockingQueue<Message>(MAXIMUM_MESSAGES_INQUEUE);
	}
	public Queue<Message> getMsgQueue() {
		return msgQueue;
	}
	public void setMsgQueue(Queue<Message> msgQueue) {
		this.msgQueue = msgQueue;
	}
	public List<String> getUsers() {
		return users;
	}
	public void setUsers(List<String> users) {
		this.users = users;
	}
	public int getActiveMembers() {
		return activeMembers;
	}
	public void setActiveMembers(int activeMembers) {
		this.activeMembers = activeMembers;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void addActiveMember(String newMember){
		if( !this.getUsers().contains(newMember))
		{
			this.getUsers().add(newMember);
			activeMembers++;
		}
	}
	public void removeActiveMember(String oldMember)
	{
		if( this.getUsers().contains(oldMember))
		{
			this.getUsers().remove(oldMember);			
			activeMembers--;
		}
	}
	
}
