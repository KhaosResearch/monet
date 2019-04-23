package khaos;

/**
 * @author Fernando Moreno Jabato <fmjabato@yahoo.es>
 */

public class IllegalFileFormatException extends RuntimeException {
	public IllegalFileFormatException(){
		super();
	}
	public IllegalFileFormatException(String s){
		super(s);
	}
}
