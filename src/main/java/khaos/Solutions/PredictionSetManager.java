package khaos.Solutions;

import java.util.Iterator;
/**
 * @author Fernando Moreno Jabato <fmjabato@yahoo.es>
 */
import java.util.Vector;

/**
 *
 */
public abstract class PredictionSetManager {
	//FIELDS
	protected Vector<String> files;
	
	//CONSTRUCTORS
	public PredictionSetManager(){
		files = new Vector<String>();
	}
	
	public PredictionSetManager(String[] predictionFiles){
		files = new Vector<String>();
		for(String s: predictionFiles)
			files.add(s);
	}
	
	public PredictionSetManager(Vector<String> predictionFiles){
		files = predictionFiles;
	}
	
	//METHODS
	/**
	 * This method is used to add new files to the prediction set.
	 * @param file that will be added.
	 */
	public void addFile(String file){
		files.add(file);
	}
	
	/**
	 * This method is used to check the format of the whole prediction set.
	 * @return true if all files have a correct format and false in the other case.
	 */
	public abstract boolean checkFormat();
	
	/**
	 * This method is used to check the format of a prediction file.
	 * @param file is the file that will be checked.
	 * @return true if the file has a correct format and false in the other case.
	 */
	public abstract boolean checkFormat(String file);
	
	/**
	 * This method is used to delete the file of the prediction set with the index given.
	 */
	public void deleteFile(int index){
		files.remove(index);
	}
	
	/**
	 * This method is used to obtain a file of the prediction set.
	 * @param index of the file wanted.
	 * @return the file's path.
	 */
	public String getFile(int index){
		return files.elementAt(index);
	}
	
	/**
	 * This method is used to get the prediction set.
	 * @return an array of strings with all the file paths.
	 */
	public String[] getFiles(){
		String[] fs = new String[files.size()];
		
		Iterator<String> i = files.iterator();
		for(int j=0; i.hasNext();j++)
			fs[j] = i.next();
		
		return fs;
	}
	
	/**
	 * This method is used to find the index of a file given in the prediction set.
	 * @param s is the file that will be seek.
	 * @return the file index on the prediction set or a negative number if the file isn't in the prediction set.
	 */
	public int getFileIndex(String s){
		int index=-1;
		for(int i=0;i<files.size();++i)
			if(s.equals(files.elementAt(i)))
				index = i;
		return index;
	}
	
	/**
	 * This method is used to now the size of the prediction set.
	 * @return the number of files on the prediction set.
	 */
	public int getNumPredictions(){
		return files.size();
	}
	
	/**
	 * This method is used to check if a file given is in the current prediction set.
	 * @param file that will be checked
	 * @return true if it's in the file set or false in other case.
	 */
	public boolean isAPredictionSetFile(String file){
		return files.contains(file);
	}
	
	/**
	 * This method is used to load the info saved on the prediction set.
	 */
	public abstract void load();	
}
