package GenNetWeaver;

/**
 * @author Fernando Moreno Jabato <fmjabato@yahoo.es>
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

/**
 * This class open a *-heterozygous.tsv files given for DREAM3 
 * Challenge 4 (http://wiki.c2b2.columbia.edu/dream/index.php?title=D3c4)
 * and save all this info in this class' fields.
 *  
 *  To use it you've to create the instance specifying a file or using setFile() and, after, 
 *  use load() method to load all the info.
 * 
 *  *-heterozygous.tsv files saves information of a knock-down simulation. It means that
 *  for simulate the experiment of the file N they decrease the transcript ratio of the gene
 *  N to a half. It 
 *  REMEMBER: this class saves this matrix including a special experiment where all genes
 */

public class D3_HeterozygousTSV extends D3_TSVFiles {
	//FIELDS
		/* Remember that experiment format has the following format:
		 * 	Matrix of dimension [numGenes]x[numGenes]
		 *  Line 1: knock-down to gene 1 experiment information
		 *  ...
		 *  Line N: knock-down to gene N experiment information
		 */
	double[][] experiment;
	double[] plusExperiment; //Every gene suffered the knock-down
	int numGenes;
	
	//CONSTRUCTORS
	/**
	 * This method instantiate an object of D3_HeterozygousTSV class unspecifying the file that should be used.
	 */
	public D3_HeterozygousTSV(){
		this(null);
	}
	
	
	/**
	 * This method instantiate an object of D3_HeterozygousTSV class using the tsv file to load the information
	 * @param tsv: file where load the info
	 */
	public D3_HeterozygousTSV(File tsv){
		fich = tsv;
		genes = null;
		
		experiment = null;
		plusExperiment = null;
		numGenes = -1;
	}
	
	
	//METHODS
	/**
	 * This method check the format of the file given to load the information is correct.
	 * @return true if the format is right and false in the other case.
	 */
	public boolean checkFormat(){
		boolean format = false;
		
		try{
			FileReader fr = new FileReader(fich);
			Scanner sc = new Scanner(fr);
			
			//Take header
			String s = sc.nextLine();
			String[] splited = s.split("\t");
			
			int numGenesAux = splited.length - 1; //Auxiliar number of genes
			
			//Number of lines should be =number of genes + 1
			for(int i=0;i<numGenesAux+1;i++){
				s = sc.nextLine();
				splited = s.split("\t");
				for(int j = 1; j<numGenesAux+1;++j)
					Float.valueOf(splited[j]);
			}
			
			if(sc.hasNextLine()) //If has next line format are incorrect
				format = true;
			
			sc.close();
		}catch(FileNotFoundException fnfe){
			fnfe.printStackTrace();
			return false;
		}catch(IndexOutOfBoundsException ioobe){ //Not enough lines
			ioobe.printStackTrace();
			return false;
		}catch(NumberFormatException nfe){ //It's not a float
			nfe.printStackTrace();
			return false;
		}
		
		//Everything's OK
		return !format;
	}
	
	
	/**
	 * @return the experiment values in an array of floats
	 * @throws InformationNotFoundException
	 */
	public double[][] getExperiment() throws InformationNotFoundException{
		if(experiment == null)
			throw new InformationNotFoundException("Info not loaded yet");
		return experiment;
	}
	
	
	/**
	 * @return the number of genes in this experiment
	 * @throws InformationNotFoundException
	 */
	public int getNumGenes() throws InformationNotFoundException{
		if(numGenes<0)
			throw new InformationNotFoundException("Info not loaded yet");
		return numGenes;
	}
	
	
	/**
	 * @return the plus experiment values in an array of floats
	 * @throws InformationNotFoundException
	 */
	public double[] getPlusExperiment() throws InformationNotFoundException{
		if(plusExperiment == null)
			throw new InformationNotFoundException("Info not loaded yet");
		return plusExperiment;
	}
	
	
	/**
	 * This method load the info saved at the timeseries.tsv file.
	 * @return true if load action finish without errors. It never returns false, instead of it throws a Exception.
	 * @throws NullPointerException, IllegalFileFormatException
	 */
	public boolean load() throws NullPointerException,IllegalFileFormatException{
		if(fich == null)
			throw new NullPointerException("File pointer is null");
		
		if(!checkFormat())
			throw new IllegalFileFormatException("Illegal format of file" + fich.getName());
		
		try{
			FileReader fr = new FileReader(fich);
			Scanner sc = new Scanner(fr);
			
			//Take header
			String s = sc.nextLine();
			String[] splited = s.split("\t");
			
			numGenes = splited.length-1; //Number of genes
			
			//Save genes names
			genes = new String[numGenes];
			for(int i = 1; i<numGenes+1;++i)
				genes[i-1] = splited[i];
			
			//Take info
			experiment = new double[numGenes][numGenes];
			plusExperiment = new double[numGenes];
			
			s = sc.nextLine();
			splited = s.split("\t");
			for(int i=0;i<numGenes;++i)
				plusExperiment[i] = Double.valueOf(splited[i+1]);
			
			for(int i=1;i<numGenes+1;++i){
				s = sc.nextLine();
				splited = s.split("\t");
				for(int j=1; j<numGenes+1;++j)
					experiment[i-1][j-1] = Double.valueOf(splited[j]);
			}
			
			sc.close();
		}catch(FileNotFoundException fnfe){
			fnfe.printStackTrace();
			return false;
		}
		
		//Load process finished without errors
		return true;
	}
	
	
	/**
	 * @return a description of this object
	 */
	public String toString(){
		String s = "FILE NOT GIVEN YET";
		String extra = "";
		try{
			s = "File: " + fich.getName() + "\n";
			
			String s3 = "FILE NOT LOADED YET";
			extra = s3;
			
			try{
				String s2 = "Number of genes: " + this.getNumGenes();
				extra = s2;
			}catch(Exception e){}
		}catch(Exception e){}	
			
		return s + extra;
	}
	
}//END CLASS
