package GenNetWeaver;

/**
 * @author Fernando Moreno Jabato <fmjabato@yahoo.com>
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;
import java.util.Vector;


/**
 *  This class load and display the information saved on the VAR.txt files generated by JMetal algorithms.
 *  This class had been configured to load VAR files generated for microarrays problems and should have 
 *  the following format:
 *  	*Understanding that the microarray has N genes in the experiment the format of each line of VARFile is:
 *  	
 *  	line: [<FirstSet (N values)> <SecondSet (N values)> <AdjustValue (2 values)>] x N
 *  It means that each line contains a full square matrix of NxN with each line of this matrix separated by 2
 *  values that are the constant rates. All elements on each line are separated by spaces (" "). According to 
 *  this format, each line of the file should have number_element = N*(2N+2)
 *  
 *  To use this class you should specify, least, the VAR file and, optionally, the number of genes used on the
 *  experiment. If you don't specify the number of genes used it will be calculated automatically using the
 *  number_element function given before. 
 *  WARNING: be careful if you're going to specify the number of genes. Be sure that the number is correct.
 *  
 *  After configured the class you have to call the load() method to load the information. If you try to display
 *  the information before it's loaded an exception will be thrown.
 */

/**
 *  This class saves 2 fields with the following information:
 *  	Vector<double[][]> results1: it saves each matrix of the first set contained on each line of the file. The size
 *  		of each matrix are [numGenesExperiment] x [numGenesExperiment]
 *  Note: equal for results2.
 *  	Vector<double[][]> cRates: saves the 2 constant rate values specified after each matrix line. The size
 *  		of each matrix are [numGenesExperiment] x [2]
 *  NOTE: the cRates are the same for each matrix of results2 and results1 at the same index
 */
public class VARFile {
	//STATIC FIELDS
	public static final int FirstSet = -1;
	public static final int SecondSet = 1;
	public static final int BothSets = 0;
	
	//FIELDS
	private File file;
	
	private Vector<double[][]> resultsFirstSet;
	private Vector<double[][]> resultsSecondtSet;
	private Vector<double[][]> cRates;
	
	private int numGenesExperiment;
	
	//CONSTRUCTORS
	/**
	 * Instantiate an object of VARFile class without specify the File that should be used and the number of genes used on the experiment.
	 */
	public VARFile(){
		this(null);
	}
	
	/**
	 * Instantiate an object of VARFile using the file given to load the information and not specifying the number of genes used on the experiment.
	 * @param tsv is the file that will be used to load the information.
	 */
	public VARFile(File tsv){
		this(tsv,-1);
	}
	
	/**
	 * Instantiate an object of the class VARFile using the file given and the number of genes specified.
	 * @param tsv is the file that will be used to load the information.
	 * @param numGenesExp number of genes used on the experiment.
	 */
	public VARFile(File tsv, int numGenesExp){
		file = tsv;
		resultsFirstSet = new Vector<double[][]>();
		resultsSecondtSet = new Vector<double[][]>();
		cRates = new Vector<double[][]>();
		numGenesExperiment = numGenesExp;
	}
	
	
	//METHODS
	
	public boolean checkFormat(){
		try{
			FileReader fr = new FileReader(file);
			Scanner sc = new Scanner(fr);
			
			//Take first line
			String[] splited = sc.nextLine().split(" ");
			
			//Prepare check condition (
			int numGenesAux=0;
			if(numGenesExperiment > 0)
				numGenesAux = numGenesExperiment;
			else //Calculate numGenes used
				for(boolean found=false; numGenesAux < splited.length/2 && !found ;++numGenesAux)
					if((Math.pow(numGenesAux,2)+numGenesAux) == splited.length/2){
						found = true;
						numGenesAux--; //Correct error
					}
			
			//Check
			double check = Math.pow(numGenesAux,2)+numGenesAux;
			
			if(splited.length/2 != check){
				sc.close();
				return false;
			}//else
			
			while(sc.hasNextLine()){
				splited = sc.nextLine().split(" ");
				if(splited.length/2 != check){
					sc.close();
					return false;
				}else
					for(int i=0; i<splited.length;++i)
						Double.valueOf(splited[i]);
			}
			
			//End
			sc.close();
		}catch(FileNotFoundException fnfe){
			fnfe.printStackTrace();
			return false;
		}catch(NumberFormatException nfe){
			nfe.printStackTrace();
			return false;
		}catch(IndexOutOfBoundsException ioobe){
			ioobe.printStackTrace();
			return false;
		}
		
		//Everything's OK
		return true;
	}
	
	
	/**
	 * @param index of the constant rate values wanted.
	 * @return the matrix with the result wanted.
	 * @throws IllegalArgumentException
	 * @throws InformationNotFoundException
	 */
	public double[][] getConstantRate(int index) throws IllegalArgumentException, InformationNotFoundException {
		if(cRates.isEmpty()) throw new InformationNotFoundException("This information isn't already loaded. Please use load() method");
		if(index >= cRates.size() || index < 0) throw new IllegalArgumentException("This index is not related to a c.rate array value (index out of Bounds)");
		return cRates.get(index);
	}


	/**
	 * @return the vector with all the constant rate values obtained.
	 * @throws InformationNotFoundException
	 */
	public Vector<double[][]> getConstantRates()throws InformationNotFoundException {
		if(cRates.isEmpty()) throw new InformationNotFoundException("This information isn't already loaded. Please use load() method");
		return cRates;
	}


	/**
	 * @return the number of genes included on the experiment used to generate this results.
	 * @throws InformationNotFoundException
	 */
	public int getNumberOfGenes() throws InformationNotFoundException {
		if(numGenesExperiment < 0) throw new InformationNotFoundException("This information isn't already loaded. Please use load() method");
		return numGenesExperiment;
	}


	/**
	 * This method doesn't care about what set had been loaded because both have the same length.
	 * @return the number of results obtained.
	 * @throws InformationNotFoundException
	 */
	public int getNumberOfResults() throws InformationNotFoundException {
		//We use cRates because it's common between both sets.
		if(cRates.isEmpty()) throw new InformationNotFoundException("This information isn't already loaded. Please use load() method");
		return cRates.size();
	}


	/**
	 * @param set is the result set wanted. Use static variables of this VARFile class. (conjunto gs o hs strengths)
	 * @param index of the result wanted. (solución que quedamos convertir)
	 * @return the matrix with the result wanted.
	 * @throws IllegalArgumentException
	 * @throws InformationNotFoundException
	 */
	public double[][] getResult(int set,int index) throws IllegalArgumentException, InformationNotFoundException {
		if(set == 0) throw new IllegalArgumentException("Set value isn't correct");

		Vector<double[][]> results = set<0? resultsFirstSet:resultsSecondtSet;

		if(results.isEmpty()) throw new InformationNotFoundException("This information isn't already loaded. Please use load() method");
		if(index >= results.size() || index < 0) throw new IllegalArgumentException("This index is not related to a result (index out of Bounds)");
		return results.get(index);
	}


	/**
	 * @return the vector with all the results obtained.
	 * @param set is the result set wanted. Use static variables of this VARFile class.
	 * @throws InformationNotFoundException
	 */
	public Vector<double[][]> getResults(int set)throws InformationNotFoundException {
		if(set == 0) throw new IllegalArgumentException("Set value isn't correct");
		
		Vector<double[][]> results = set<0? resultsFirstSet:resultsSecondtSet;
		
		if(results.isEmpty()) throw new InformationNotFoundException("This information isn't already loaded. Please use load() method");
		return results;
	}
	
	
	/**
	 * Load the information saved on the file given if it has a valid format.
	 * @return true if the load action finished correctly.
	 * @throws NullPointerException
	 */
	public boolean load(int set) throws NullPointerException{
		if(file == null)
			throw new NullPointerException("File pointer is null");
		
		if(!this.checkFormat())
			throw new IllegalFileFormatException("Incorrect format on file " + file.getName());
		
		try{
			FileReader fr = new FileReader(file);
			Scanner sc = new Scanner(fr);
			
			String[] splited = sc.nextLine().split(" ");
			
			//Prepare numGenes
			if(numGenesExperiment <= 0){
				numGenesExperiment = 0;
				for(boolean found=false;numGenesExperiment < splited.length/2 && !found;++numGenesExperiment)
					if((Math.pow(numGenesExperiment,2)+numGenesExperiment) == splited.length/2){
						found = true;
						numGenesExperiment--; //Correct error
					}
			}
			
			boolean set1 = set<=0? true:false; 
			boolean set2 = set>=0? true:false;
			
			double[][] result1 = null;
			double[][] result2 = null;
			
			if(set1) result1 = new double[numGenesExperiment][numGenesExperiment];
			if(set2) result2 = new double[numGenesExperiment][numGenesExperiment];
			
			double[][] adjust = new double[numGenesExperiment][2];
			
			
			
			//Take&Save first line(matrix)
			for(int N=0, line=0; N<(2*numGenesExperiment*(numGenesExperiment+1)); N+=(2*numGenesExperiment+2), line++){
				//If we want first line
				if(set1){
					for(int pos = N, col=0 ; pos<(N+numGenesExperiment) ; ++pos, ++col)
						result1[line][col] = Double.valueOf(splited[pos]);
						
				}else if(set2){ //We want second set
					for(int pos = N+numGenesExperiment, col=0 ; pos<(N+2*numGenesExperiment) ; ++pos, ++col)
						result2[line][col] = Double.valueOf(splited[pos]);
				}//end else
				
				//Take adjust values
				adjust[line][0] = Double.valueOf(splited[N+2*numGenesExperiment]);
				adjust[line][1] = Double.valueOf(splited[N+2*numGenesExperiment+1]);
			}//
			
			if(set1){
				resultsFirstSet.add(result1);
				result1 = new double[numGenesExperiment][numGenesExperiment];
			}
			if(set2){
				resultsSecondtSet.add(result2);
				result2 = new double[numGenesExperiment][numGenesExperiment];
			}
			cRates.add(adjust);
			
			adjust = new double[numGenesExperiment][2];
			
			//Take the rest	
			while(sc.hasNextLine()){
				splited = sc.nextLine().split(" ");
				
				for(int N=0, line=0; N<(2*numGenesExperiment*(numGenesExperiment+1)); N+=(2*numGenesExperiment+2), line++){
					//If we want first line
					if(set1){
						for(int pos = N, col=0 ; pos<(N+numGenesExperiment) ; ++pos, ++col)
							result1[line][col] = Double.valueOf(splited[pos]);
							
					}else if(set2){ //We want second set
						for(int pos = N+numGenesExperiment, col=0 ; pos<(N+2*numGenesExperiment) ; ++pos, ++col)
							result2[line][col] = Double.valueOf(splited[pos]);
					}//end else
					
					//Take adjust values
					adjust[line][0] = Double.valueOf(splited[N+2*numGenesExperiment]);
					adjust[line][1] = Double.valueOf(splited[N+2*numGenesExperiment+1]);
				}//
				
				if(set1){
					resultsFirstSet.add(result1);
					result1 = new double[numGenesExperiment][numGenesExperiment];
				}
				if(set2){
					resultsSecondtSet.add(result2);
					result2 = new double[numGenesExperiment][numGenesExperiment];
				}
				cRates.add(adjust);
				
				adjust = new double[numGenesExperiment][2];
				
			}
			
			sc.close();
		}catch(FileNotFoundException fnfe){
			fnfe.printStackTrace();
		}
		
		return true;
	}
	
	
	/**
	 * @param genes are the number of genes that will be used to configurate the load action.
	 */
	public void setNumGenes(int genes){
		numGenesExperiment = genes;
	}
	
	
	/**
	 * @return a description of this object
	 */
	public String toString(){
		String s = "FILE NOT GIVEN YET";
		String extra = "";
		try{
			s = "File: " + file.getName() + "\n";
			
			String s3 = "FILE NOT LOADED YET";
			extra = s3;
			
			try{
				String s2 = "Number of genes used on the experiment: " + this.getNumberOfGenes() + "\n"+
						"Number of results generated: " + this.getNumberOfResults();  
				extra = s2;
			}catch(Exception e){}
		}catch(Exception e){}	
			
		return s + extra;
	}
}//END CLASS
