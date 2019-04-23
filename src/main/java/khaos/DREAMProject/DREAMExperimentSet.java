package khaos.DREAMProject;

/**
 * @author Fernando Moreno Jabato <fmjabato@yahoo.es>
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.util.Vector;

import khaos.IllegalFileFormatException;
import khaos.InformationNotFoundException;

/*
 * This class is used to open *-trajectories.tsv files given for DREAM3 
 * Challenge 4 (http://wiki.c2b2.columbia.edu/dream/index.php?title=D3c4)
 * and  %timeseries.tsv files given for DREAM4 
 * Challenge 2 (http://wiki.c2b2.columbia.edu/dream/index.php?title=D4c2)
 * and save all this info in this class' fields.
 *  
 *  To use it you've to create the instance specifying a file or using setFile() and, after, 
 *  use load() method to load all the info.
 * 
 *  *-trajectories.tsv files saves information of the timeseries with the transcript ratio 
 *  for a few experiments.
 *  
 *  This class includes a method to generate a file with the format used on DCU problems:
 * 		line 1: number of genes(n) and of time series (T)
 *		line 2: number of time points for first time series (T1)
 *		line 3: T1 numbers indicating time spans between consecutive time points (first is 0)
 *		line 4: n numbers indicating gene expression levels for the first time point in first time series
 *		line 5: n numbers indicating gene expression levels for the second time point in first time series
 *		...
 *		line T1+3: n numbers indicating gene expression levels for the T1-th time point in first time series
 *		repeat lines 2 to T1+3 for all time series
 * To generate this file use convertToKhaosData() method after load specifying, or not (generate it on desktop)
 *  , the output file. 
 */

public class DREAMExperimentSet extends TSVFiles{
	//FIELDS
	protected Vector<double[][]> experiments;
	protected int numTLapses;
	protected float timeLapse;
	
	//COSNTRUCTORS
	public DREAMExperimentSet(){
		this(null);
	}
		
	public DREAMExperimentSet(File tsv){
		super(tsv);
		
		experiments = new Vector<double[][]>();
		numTLapses = -1;
		timeLapse = -1;
	}
	
	//METHODS
	/**
	 * @return the experiment related to the index given
	 * @throws InformationNotFoundException, IllegalArgumentException
	 */
	public double[][] getExperiment(int index) throws InformationNotFoundException,IllegalArgumentException {
		if(experiments.isEmpty()) throw new InformationNotFoundException("This information isn't already loaded. Please use load() method");
		if(index >= experiments.size() || index < 0) throw new IllegalArgumentException("This index is not related to an experiment (index out of Bounds)");
		return experiments.get(index);
	}
	
	
	/**
	 * @return a vector with all the experiments included on this file
	 * @throws InformationNotFoundException
	 */
	public Vector<double[][]> getExperiments() throws InformationNotFoundException {
		if(experiments.isEmpty()) throw new InformationNotFoundException("This information isn't already loaded. Please use load() method");
		return experiments;
	}
	
	/**
	 * @return number of experiments included on this file
	 * @throws InformationNotFoundException
	 */
	public int getNumExperiments() throws InformationNotFoundException {
		if(experiments.isEmpty()) throw new InformationNotFoundException("This information isn't already loaded. Please use load() method");
		return experiments.size();
	}
	
	
	/**
	 * @return number of time lapses used on this experiments
	 * @throws InformationNotFoundException
	 */
	public int getNumTimeLapses() throws InformationNotFoundException {
		if(numTLapses < 0) throw new InformationNotFoundException("This information isn't already loaded. Please use load() method");
		return numTLapses;
	}
	
	
	/**
	 * @return the time lapse used to take data on this experiments
	 * @throws InformationNotFoundException
	 */
	public float getTimeLapse() throws InformationNotFoundException {
		if(timeLapse < 0) throw new InformationNotFoundException("This information isn't already loaded. Please use load() method");
		return timeLapse;
	}
	
	
	/**
	 * This method load the info saved multi-experiments DREAMProject files.
	 * @return true if load action finish without errors. It only returns false in case that file couldn't be found.
	 * @throws NullPointerException, FileNotFoundException, IllegalFileFormatException
	 */
	public boolean load() throws NullPointerException, FileNotFoundException, IllegalFileFormatException{
		if(fich == null) throw new IllegalFileFormatException("File pointer is NULL");
		
		try{
			RandomAccessFile reader = new RandomAccessFile(fich, "r");
			
			//Take header
			String line = reader.readLine();
				
				//Check format
				if(line.isEmpty()) throw new IllegalFileFormatException("File doesn't contains header at first line");
				
			String[] splitted = line.split("\t");
			
				//Check format
				//It must have at least 2 elements (Time + G1)
				if(splitted.length < 2) throw new IllegalFileFormatException("Header doesn't contains information or isn't right");
			
			genes = new String[splitted.length-1]; //Number of genes ([0]="Time")
			
			//Save genes
			for(int i=1; i<splitted.length; ++i)
				genes[i-1] = splitted[i].replace("\"", "");
			
			//Obviate empty lines before first experiment
			long pointer = reader.getFilePointer();
			while((line=reader.readLine())!=null && line.isEmpty())
				pointer = reader.getFilePointer();
			
				//Check format
				//If line = null => End of file
				if(line == null) throw new IllegalFileFormatException("Erroneus format, file only contains header");
			
			//We are at the end of first line of 1st experiment (1st line is on "line" variable)
			int auxNumTLapses = 1;
			
			//First line
			float t0 = Float.valueOf(line.split("\t")[0]);
			float t1;
			//Second line
			line = reader.readLine();
				
				//Check format
				if((line == null | line.isEmpty()) && genes.length > 1) throw new IllegalFileFormatException("Incomplete 1st experiment");
				else if(genes.length == 1){
					t1 = t0;
				}else{
					auxNumTLapses++;
					t1 = Float.valueOf(line.split("\t")[0]);
				}
				
			//Take tLapse
			timeLapse = t1-t0;
			
			//Read to the end of 1st experiment
			while((line=reader.readLine())!=null && !line.isEmpty())
				auxNumTLapses++;
			
			//Now we start to read and save elements
			reader.seek(pointer); //Reset to first line of 1st experiment
			numTLapses = auxNumTLapses;
			
			double[][] experiment = new double[numTLapses][genes.length];
			
			//Read first line
			line = reader.readLine();
			splitted = line.split("\t");
			
			for(int i=1; i<=genes.length;++i)
				experiment[0][i-1] = Float.valueOf(splitted[i]);
			
			//Variables for tLapse checks
			float lastT = Float.valueOf(splitted[0]), actualT = lastT;
			
			//Read experiments
			int actualRow=1;
			while((line=reader.readLine())!=null){
				if(line.isEmpty()){
					//Check
					if(actualRow != numTLapses) throw new IllegalFileFormatException("Experiments has not same number of time lapses");
					
					experiments.add(experiment);
					experiment = new double[numTLapses][genes.length];
					actualRow = 0;
					lastT = 0;
					actualT = 0;
				}else{
					splitted = line.split("\t");
					
						//Check
						if(splitted.length != genes.length+1)
					
					for(int col = 0; col<genes.length;++col)
						experiment[actualRow][col] = Double.valueOf(splitted[col+1]);
					
					actualT = Float.valueOf(splitted[0]);
					
						//Check
						if((actualT-lastT) != timeLapse && actualRow != 0) throw new IllegalFileFormatException("Time lapses isn't constant");
						
					lastT = actualT;	
					++actualRow;
				}
			}
			
			//Save last experiment
			experiments.add(experiment);
			
			//Close streams
			reader.close();
			
		}catch(Exception e){
			if(e instanceof FileNotFoundException){
				e.printStackTrace();
				return false;
			}//else
			
			//Reset all
			experiments = new Vector<double[][]>();
			numTLapses = -1;
			timeLapse = -1;
			genes = null;
			fich = null; //Reject actual file
			
			if(e instanceof IllegalFileFormatException)
				throw new IllegalFileFormatException(e.getMessage());
			else
				throw new IllegalFileFormatException("File format isn't right: "+e.toString());
		}
		
		//Everything's OK
		return true;
	}

	@Override
	public String toString(){
		String s = super.toString();
		if(s.contains("FILE")) return s;
		//else
		String extra = "";
		try{
			extra = "Number of experiments: "+ this.getNumExperiments() + " || Number of time lapses: " + this.getNumTimeLapses() +
					 "\nTime lapse: " + this.getTimeLapse(); 
		}catch(Exception e){}
		
		return s+extra;
	}
}
