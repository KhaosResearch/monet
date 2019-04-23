package khaos.tools.Graphics;

/**
 * @author Fernando Moreno Jabato <fmjabato@yahoo.es>
 */

public class Edge {
	//FIELDS
	private String label;
	private String source, target;
	
	//CONSTRUCTORS
	public Edge(String edge, String origin, String end){
		label = edge;
		source = origin;
		target = end;
	}
	
	//METHODS
	public boolean contains(String node){
		return (node.equals(source) || node.equals(target));
	}
	
	
	public String getLabel(){
		return label;
	}
	
	
	public String getSource(){
		return source;
	}
	
	
	public String getTarget(){
		return target;
	}
	
//	/**
//	 * Replace the value of the name
//	 * @param n new value to be stored
//	 */
//	public void setName(String n){
//		label = n;
//	}
//	
//	/**
//	 * Replace the value of the source
//	 * @param n new value to be stored
//	 */
//	public void setSource(String n){
//		source = n;
//	}
//	
//	/**
//	 * Replace the value of the target
//	 * @param n new value to be stored
//	 */
//	public void setTarget(String n){
//		target = n;
//	}
}//END CLASS
