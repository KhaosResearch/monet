package khaos.DREAMProject.DREAM4;

/**
 * @author Fernando Moreno Jabato <fmjabato@yahoo.es>
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import khaos.IllegalFileFormatException;
import khaos.InformationNotFoundException;
import khaos.DREAMProject.TSVFiles;


/**
 * This class open a %wildtype.tsv files given for DREAM4 
 * Challenge 2 (http://wiki.c2b2.columbia.edu/dream/index.php?title=D4c2)
 * and save all this info in this class' fields.
 *  
 * To do this you've to create the instance specifying a file or using setFile() and, after, 
 * use load() method to load all the info. 
 */
public class D4_WildtypeTSV extends TSVFiles{
	//FIELDS
	private double[] wildtype;
	
	//CONSTRUCTORS
	/**
	 * This constructor instantiate an object of class D4_WildtypeTSV but without specify the file with all the information. You must specify it using setFile() method.
	 */
	public D4_WildtypeTSV(){
		this(null);
	}
	
	
	/**
	 * This constructor instantiate an object of class D4_WildtypesTSV
	 * @param tsv is the file with all the information.
	 */
	public D4_WildtypeTSV(File tsv){
		super(tsv);
		
		wildtype = null;
	}
		
		
	//METHODS
	/**
	 * @return the steady state transcription rate of the given gene or -1 if this gene isn't on this file.
	 * @throws InformationNotFoundException
	 */
	public double getSteadyState(String gene) throws InformationNotFoundException{
		if(genes == null || wildtype == null) throw new InformationNotFoundException("This information isn't already loaded. Please use load() method");
		int index = -1;
			for(int i=0; i<genes.length; ++i)
				if(genes[i].equals(gene))
					index = i; 
		return index < 0? -1 : wildtype[index];
	}
	
	
	/**
	 * @return a map with all pairs of <Gen,SteadyState>
	 * @throws InformationNotFoundException
	 */
	public Map<String,Double> getGene_SteadyStates() throws InformationNotFoundException{
		if(genes == null || wildtype == null) throw new InformationNotFoundException("This information isn't already loaded. Please use load() method");
		Map<String,Double> map = new TreeMap<String,Double>();
		for(int i=0; i<genes.length; ++i)
			map.put(genes[i], new Double(wildtype[i]));
		
		return map;
	}
	
	
	/**
	 * @return an array of floats with all steady states. This values are related and ordered equals than the String[] that returns getGenes() method.
	 * @throws InformationNotFoundException
	 */
	public double[] getSteadyStates() throws InformationNotFoundException{
		if(wildtype == null) throw new InformationNotFoundException("This information isn't already loaded. Please use load() method");
		return wildtype;
	}
	
	
	/**
	 * This method load the info saved at the knock%.tsv file.
	 * @return true if load action finish without errors. It never returns false, instead of it throws a Exception.
	 * @throws NullPointerException, FileNotFoundException
	 */
	public boolean load() throws FileNotFoundException, NullPointerException, IllegalFileFormatException {
		try{
			//Open file
			FileReader fr = new FileReader(fich);
			Scanner sc = new Scanner(fr);
	
			//Check
			if(!sc.hasNextLine()) throw new IllegalFileFormatException("Empty file");
			
			//Take header
			String line = sc.nextLine(); //header
			genes = line.split("\t");
			for(String s: genes)
				s.replaceAll("\"", "");
			
			//Check
			if(genes.length <= 0) throw new IllegalFileFormatException("Empty header");
			if(!sc.hasNextLine()) throw new IllegalFileFormatException("Wildtype information not found");
			//Take steady state info
			line = sc.nextLine(); //Wildtype
			String[] splitted = line.split("\t");
			
			//Check
			if(splitted.length != genes.length) throw new IllegalFileFormatException("Header and wildtype have different dimensions");
			
			wildtype = new double[genes.length];
			
			for(int i = 0; i<genes.length; i++)
				wildtype[i] = Float.valueOf(splitted[i]);
			
			//Close
			sc.close();
			fr.close();
		}catch(Exception e){
			genes = null;
			wildtype = null;
			
			if(e instanceof FileNotFoundException){
				e.printStackTrace();
				return false;
			}//else

			fich = null;
			
			if(e instanceof IllegalFileFormatException)
				throw new IllegalFileFormatException(e.getMessage());
			else
				throw new IllegalFileFormatException("Erroneous format");
		}
		
		return true;
	}
}
