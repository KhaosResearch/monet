package khaos.DREAMProject.DREAM3;

/**
 * @author Fernando Moreno Jabato <fmjabato@yahoo.es>
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

import khaos.IllegalFileFormatException;
import khaos.InformationNotFoundException;
import khaos.DREAMProject.DREAMExperiment;

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

public class DREAM3Experiment extends DREAMExperiment {
	//FIELDS
		/* Remember that experiment format has the following format:
		 * 	Matrix of dimension [numGenes]x[numGenes]
		 *  Line 1: knock-down to gene 1 experiment information
		 *  ...
		 *  Line N: knock-down to gene N experiment information
		 */
	private double[] plusExperiment; //Every gene suffered the knock-down
	
	//CONSTRUCTORS
	/**
	 * This method instantiate an object of D3_HeterozygousTSV class unspecifying the file that should be used.
	 */
	public DREAM3Experiment(){
		this(null);
	}
	
	
	/**
	 * This method instantiate an object of D3_HeterozygousTSV class using the tsv file to load the information
	 * @param tsv: file where load the info
	 */
	public DREAM3Experiment(File tsv){
		super(tsv);
		
		plusExperiment = null;
	}
	
	
	//METHODS
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
	public boolean load() throws IllegalFileFormatException {
		super.load(); //It can throw IllegalFileFormatException
		
		try{
			RandomAccessFile reader = new RandomAccessFile(fich, "r");
			
			//Correct file format has been checked by super.load()
			reader.readLine();
			
			String[] splitted = reader.readLine().split("\t");
			
				//Check if it's a DREAM3 File
				if(!splitted[0].equals("\"wt\"")) throw new IllegalFileFormatException("Plus experiment couldn't be found. It's not a *wildtype.tsv file");
			
			plusExperiment = new double[genes.length];
			for(int i=1; i<=genes.length; ++i)
				plusExperiment[i-1] = Double.valueOf(splitted[i]);
			
			reader.close();
		}catch(Exception e){
			genes = null;
			experiment = null;
			
			if(e instanceof FileNotFoundException){
				e.printStackTrace();
				return false;
			}
			
			fich = null;
			
			if(e instanceof IllegalFileFormatException)
				throw new IllegalFileFormatException(e.getMessage());
			else
				throw new IllegalFileFormatException("Illegal plus experiment format");
		}
		
		return true;
	}
	
}//END CLASS
