/*
* Author: Michelle Becerra
* Date: 9/21/16
* Foundations of Artificial Intelligence Homework 1 Search algorithms for car navigation
* Professor Dr. Itti
*/
import java.util.*;

public class Node implements Comparable<Node>{
	
	String state;
    String parent; 
    int pathCost;
    int totalPathCost;
    int timestamp;

	public Node(){
		state = null;
		pathCost = 0;
		totalPathCost = 0;
		timestamp = 0;
		parent = "";
		
	}
	public Node(Node n){
		this.state = new String(n.state);
		this.parent = new String(n.parent);
		this.pathCost = new Integer(n.pathCost);
		this.totalPathCost = new Integer(n.totalPathCost);
		this.timestamp = new Integer(n.timestamp);
	}
	public void setState(String st){
		state = st;
	}
	public String getState(){
		return state;
	}
	public void setParent(String paren){
		parent = paren;
	}
	public String getParent(){
		return parent;
	}
	public void setPathCost(int cost){
		pathCost = cost;
	}
	public int getPathCost(){
		return pathCost;
	}
	public void setTotalPathCost(int total){
		totalPathCost = total;
	}
	public int getTotalPathCost(){
		return totalPathCost;
	}
	public int getTimeStamp(){
		return timestamp;
	}
	public void setTimeStamp(int time){
		timestamp = time;
	}

	public int compareTo(Node n){
		int objectCost = this.totalPathCost;
		int otherCost = n.totalPathCost;
		if( objectCost != otherCost){
			return objectCost - otherCost;
		}
		else{

			return this.timestamp - n.timestamp;
		}

		//return this.totalPathCost > n.totalPathCost ? 1 : (this.totalPathCost < n.totalPathCost ? -1: 0);
	}

	public boolean equals(Object o){
		return (o instanceof Node && ((Node)o).state.equals(this.state));
	}
	public int hashCode(){
		return state.length();
	}

	public String toString(){

		return "Parent:" + parent + " " + "State:" + state + " " + "PathCost:" + pathCost;
	}
}