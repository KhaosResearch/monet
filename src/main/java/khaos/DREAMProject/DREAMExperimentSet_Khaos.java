package khaos.DREAMProject;
/**
 * @author Fernando Moreno Jabato <fmjabato@yahoo.es>
 */

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import khaos.InformationNotFoundException;


/**
 * This class open a *-trajectories.tsv files given for DREAM3 
 * Challenge 4 (http://wiki.c2b2.columbia.edu/dream/index.php?title=D3c4)
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
public class DREAMExperimentSet_Khaos extends DREAMExperimentSet {
	//CONSTRUCTORS
	/**
	 * This constructor instantiate an object of class D4_TimeseriesTSV but without specify the file with all the information. You must specify it using setFile() method.
	 */
	public DREAMExperimentSet_Khaos(){
		this(null);
	}
	
	/**
	 * This constructor instantiate an object of class D4_TimeseriesTSV.
	 * @param tsv is the file with all the information.
	 */
	public DREAMExperimentSet_Khaos(File tsv){
		super(tsv);
	}
	
	
	//METHODS
	/**
	 * This method create the file at the desktop or home directory (windows or other OS respectively)
	 * @return the file created.
	 */
	public File convertToKhaosData(){
		File f;
		
		String s = fich.getName();
		
		int index = s.indexOf("_timese"); //_timeseries.tsv
		
		if(index>=0)
			s = s.substring(0, index) + ".txt";
		else
			s = s.substring(0, (s.length()-4)) + ".txt";
		
		if(System.getProperty("os.name").contains("Windows")) //Only done for Windows
			f = new File(System.getProperty("user.home")+"\\Desktop\\" + s);
		else
			f = new File(System.getProperty("user.home") + System.getProperty("file.separator") + s);
		return this.convertToKhaosData(f);
	}
	
	
	/**
	 * This method saves all the info of this file in DCU %GeneratedData.txt files
	 * @param outputFile is the file where will be saved the info
	 * @return the file where the info was saved (out)
	 * @throws InformationNotFoundException
	 */
	public File convertToKhaosData(File out) throws InformationNotFoundException{
		try {	
			//Load info
			this.load();
		
			//Prepare to write
			FileWriter fw = new FileWriter(out);
			
			fw.write(this.getNumGenes() + " " + this.getNumExperiments() + "\n"); //first line
			
			//Write experiments
			for(int i = 0; i<this.getNumExperiments(); ++i){
				//Write header
				fw.write(numTLapses + "\n");
				fw.write("0");
				for(int j = 0; j<(numTLapses-1);++j)
					fw.write(" "+timeLapse);
				fw.write("\n");
				
				double[][] exp = experiments.get(i);
				
				//Write matrix info
				for(int row=0; row < numTLapses; ++row){
					for( int col = 0; col < this.getNumGenes(); ++col)
						fw.write(exp[row][col] + " "); //DCU %generatedData.txt files has " " at the end of the row
					if(row!=(numTLapses-1)) //Don't add a \n at the end of the file
						fw.write("\n");
				}//for
				if(i != (this.getNumExperiments()-1))
					fw.write("\n");
			}//for
			
			//END
			fw.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
			return null;
		}
		
		//Any problem
		return out;
	}
} //END CLASS
