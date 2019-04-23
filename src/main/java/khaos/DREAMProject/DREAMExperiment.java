package khaos.DREAMProject;

/**
 * @author Fernando Moreno Jabato <fmjabato@yahoo.es>
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

import khaos.IllegalFileFormatException;
import khaos.InformationNotFoundException;


/**
 * This class is used to open %knockdowns.tsv, %knockouts.tsv or %multifactorial.tsv 
 * files given for DREAM4 Challenge 2 (http://wiki.c2b2.columbia.edu/dream/index.php?title=D4c2)
 * and allow utilities to load DREAM3 files, managed by khaos.DREAMProject.DREAM3.DREAM3Experiment class.
 * This class saves all this info in this class' fields.
 *  
 * To do this you've to create the instance specifying a file or using setFile() and, after, 
 * use load() method to load all the info. 
 */

public class DREAMExperiment extends TSVFiles{
	//FIELDS
	protected double[][] experiment;
	
	//COSNTRUCTORS
	public DREAMExperiment(){
		this(null);
	}
	
	public DREAMExperiment(File tsv){
		super(tsv);
		
		experiment = null;
	}
	
	//METHODS
	/**
	 * @param index of the experiment wanted
	 * @return the experiment required
	 * @throws InformationNotFoundException
	 * @throws IllegalArgumentException
	 */
	public double[] getExperiment(int index) throws InformationNotFoundException, IllegalArgumentException{
		if(index < 0 || (genes != null & index >= genes.length)) throw new IllegalArgumentException("Index out of bounds");
		return this.getExperiments()[index];
	}
	
	
	/**
	 * @return the experiment values in an array of doubles
	 * @throws InformationNotFoundException
	 */
	public double[][] getExperiments() throws InformationNotFoundException{
		if(experiment == null | experiment.length <= 0)
			throw new InformationNotFoundException("Info not loaded yet");
		return experiment;
	}
	
	
	public boolean load() throws IllegalFileFormatException{
		try{
			RandomAccessFile reader = new RandomAccessFile(fich, "r");
			
			String line = reader.readLine();
				
				//Check
				if(line == null) throw new IllegalFileFormatException("Empty file");
			
			String[] splitted = line.split("\t");
			
				//Check
				if(splitted.length <= 0) throw new IllegalFileFormatException("Empty header");
			
			//Check if file is, or not, a DREAM3 heterozygous or nullmutants file
			boolean isDREAM3TSVFile = splitted[0].equals("\"strain\"");
			
			if(isDREAM3TSVFile){
				genes = new String[splitted.length-1];
				for(int i=1; i<splitted.length;++i)
					genes[i-1] = splitted[i];
				
				reader.readLine(); //PlusExperiment obviated
			}else
				genes = splitted;
			
			//Replace "G*" -> G*
			for(String s: genes)
				s.replace("\"", "");
			
			//Start read experiment
			experiment = new double[genes.length][genes.length];
			int actualLine = 0;
			
			while((line = reader.readLine())!=null){
				//Check
				if(line.isEmpty()) throw new IllegalFileFormatException("Empty lines at experiment");
				
				splitted = line.split("\t");
				
					//Check
					if(splitted.length != (isDREAM3TSVFile? genes.length+1 : genes.length)) throw new IllegalFileFormatException("Experiment dimension erroneus");
				
				for(int i = (isDREAM3TSVFile? 1:0); i<genes.length; ++i)
					experiment[actualLine][isDREAM3TSVFile? i-1 : i] = Double.valueOf(splitted[i]);
				
				actualLine++;
			}
			
			//Check
			if(actualLine != genes.length) throw new IllegalFileFormatException("Experiment dimension erroenous");
			
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
				throw new IllegalFileFormatException("Ilegal format: "+e.getClass()+" -> "+e.getMessage());
		}
		
		//Everything it's ok
		return true;
	}
	
}
