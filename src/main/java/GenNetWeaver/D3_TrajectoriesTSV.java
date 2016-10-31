package GenNetWeaver;
/**
 * @author Fernando Moreno Jabato <fmjabato@yahoo.es>
 */

import java.io.*;
import java.util.Scanner;
import java.util.Vector;


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
public class D3_TrajectoriesTSV extends D3_TSVFiles{
	//FIELDS
		private int numGenes, numTLapses, numExperiments;
		private float timeLapse;
		private Vector<double[][]> experiments;
		
		
		//CONSTRUCTORS
		/**
		 * This constructor instantiate an object of class D4_TimeseriesTSV but without specify the file with all the information. You must specify it using setFile() method.
		 */
		public D3_TrajectoriesTSV(){
			this(null);
		}
		
		/**
		 * This constructor instantiate an object of class D4_TimeseriesTSV.
		 * @param tsv is the file with all the information.
		 */
		public D3_TrajectoriesTSV(File tsv){
			fich = tsv;
			
			numGenes = -1;
			numTLapses = -1;
			numExperiments = -1;
			
			timeLapse = -1;
			
			genes = new String[0];
			
			experiments = new Vector<double[][]>();
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
				int[] aux = this.countNumberOfExperimentsAndTLapses();
				
				//Number of lines should be =number of genes for each experiment
				for(int k=0; k<aux[0];++k){
					for(int i=0;i<aux[1];i++){
						s = sc.nextLine();
						splited = s.split("\t");
						for(int j = 1; j<numGenesAux+1;++j)
							Float.valueOf(splited[j]);
					}
					sc.nextLine();
				}
				
				
				
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
		 */
		public File convertToKhaosData(File out){
			try {	
				//Load info
				this.load();
			
				//Prepare to write
				FileWriter fw = new FileWriter(out);
				
				fw.write(numGenes + " " + numExperiments + "\n"); //first line
				
				//Write experiments
				for(int i = 0; i<numExperiments; ++i){
					//Write header
					fw.write(numTLapses + "\n");
					fw.write("0");
					for(int j = 0; j<(numTLapses-1);++j)
						fw.write(" "+timeLapse);
					fw.write("\n");
					
					double[][] exp = experiments.get(i);
					
					//Write matrix info
					for(int row=0; row < numTLapses; ++row){
						for( int col = 0; col < numGenes; ++col)
							fw.write(exp[row][col] + " "); //DCU %generatedData.txt files has " " at the end of the row
						if(row!=(numTLapses-1)) //Don't add a \n at the end of the file
							fw.write("\n");
					}//for
					if(i != (numExperiments-1))
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
		
		
		/**
		 * @return an array monodimensional where [0]=numExperimentes and [1]=numTimeLapses
		 */
		private int[] countNumberOfExperimentsAndTLapses(){
			int numExp = 0;
			int tLapses = 0;
			try{	
				FileReader fr = new FileReader(fich);
				Scanner sc = new Scanner(fr);
				
				sc.nextLine(); //Clean header
				
				boolean condition = true;
				//Count number of experiments
				while(sc.hasNextLine()){
					String s = sc.nextLine();
					if(s.isEmpty()){
						numExp++;
						condition=false;
					}else if(condition)
						tLapses++;
				}
				
				sc.close();
			}catch(FileNotFoundException fnfe){
				return null;
			}
			
			int[] result = new int[2];
			result[0] = numExp;
			result[1] = tLapses;
			
			return result;
		}
		
		
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
			if(numExperiments < 0) throw new InformationNotFoundException("This information isn't already loaded. Please use load() method");
			return numExperiments;
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
		 * This method load the info saved at the timeseries.tsv file.
		 * @return true if load action finish without errors. It never returns false, instead of it throws a Exception.
		 * @throws NullPointerException, FileNotFoundException
		 */
		public boolean load() throws NullPointerException{
			if(fich == null)
				throw new NullPointerException("File pointer is null");

			if(!checkFormat())
				throw new IllegalFileFormatException("Illegal format of file" + fich.getName());
			
			//Take info
			try{	
				FileReader fr = new FileReader(fich);
				Scanner sc = new Scanner(fr);
				
				//Take header
				String s = sc.nextLine();
				String[] splited = s.split("\t");
				
				genes = new String[splited.length-1];
				numGenes = splited.length-1;
				for(int i=1;i<splited.length;++i)
					genes[i-1] = splited[i];
				
				//Take times
				this.takeTime();
				
				//Take experiments
				double[][] exp = new double[numTLapses][numGenes];
				int line = 0;
				while(sc.hasNextLine()){
					s = sc.nextLine();
					if(s.isEmpty()){
						experiments.add(exp);
						exp = new double[numTLapses][numGenes];
						line = 0;
					}else{
						splited = s.split("\t");
						for(int col = 0; col<numGenes;++col)
							exp[line][col] = Double.valueOf(splited[col+1]);
						++line;
					}
				}
				
				//Last experiment
				experiments.add(exp);
				
				numExperiments = experiments.size();
				
				sc.close();
			}catch(FileNotFoundException fnfe){
				return false;
			}
			
			
			//Load ended correctly
			return true;
		}//
		
		
		private void takeTime(){
			try{	
				FileReader fr = new FileReader(fich);
				Scanner sc = new Scanner(fr);
				
				sc.nextLine(); //header
				
				String[] tLapse = new String[2];
				boolean condition=true;
				numTLapses = 0;
				
				while(sc.hasNextLine() && condition){
					String s = sc.nextLine();
					if(s.isEmpty())
						condition = false;
					else{
						String[] splited = s.split("\t");
						
						if(tLapse[0]==null) tLapse[0] = splited[0];
						else if(tLapse[1]==null) tLapse[1] = splited[0];
						
						numTLapses++;
					}
				}
				
				timeLapse = Float.valueOf(tLapse[1]) - Float.valueOf(tLapse[0]);
				
				sc.close();
			}catch(FileNotFoundException fnfe){
				fnfe.printStackTrace();
			}
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
					String s2 = "Number of genes: " + this.getNumGenes() + " || Number of experiments: " + this.getNumExperiments() + "\n" +
						"Time lapse: " + this.getTimeLapse() + " || Number of lapses: " + this.getNumTimeLapses();
					extra = s2;
				}catch(Exception e){}
			}catch(Exception e){}	
				
			return s + extra;
		}
} //END CLASS
