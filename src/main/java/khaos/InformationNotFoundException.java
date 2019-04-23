package khaos;

/**
 * @author Fernando Moreno Jabato
 */

public class InformationNotFoundException extends RuntimeException {
	//FIELDS
	private static final long serialVersionUID = -352389381252261412L;
	
	//CONSTRUCTORS
	public InformationNotFoundException(){
		super();
	}
	
	public InformationNotFoundException(String s){
		super(s);
	}
}
