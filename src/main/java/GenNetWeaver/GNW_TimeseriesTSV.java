package GenNetWeaver;

/**
 * @author Fernando Moreno Jabato
 */

import java.io.*;
import java.util.Scanner;
import java.util.Vector;


/**
 * This class open a %timeseries.tsv file generated by GeneNetWeaver (GNW) and save
 * all this info in this class' fields. 
 * To do this you've to create the instance specifying a file or using setFile() and, after, 
 * use load() method to load all the info. 
 * 
 * This class includes a method to generate a file with the format used on DCU problems:
 * 		line 1: number of genes(n) and of time series (T)
 *		line 2: number of time points for first time series (T1)
 *		line 3: T1 numbers indicating time spans between consecutive time points (first is 0)
 *		line 4: n numbers indicating gene expression levels for the first time point in first time series
 *		line 5: n numbers indicating gene expression levels for the second time point in first time series
 *		...
 *		line T1+3: n numbers indicating gene expression levels for the T1-th time point in first time series
 *		repeat lines 2 to T1+3 for all time series
 * To generate this file use convertToDCUData() method after load specifying, or not (generate it on desktop)
 *  , the output file. 
 */
public class GNW_TimeseriesTSV {
	//FIELDS
	private File fich;
	private int numGenes, numTLapses, numExperiments;
	private float timeLapse;
	private Vector<float[][]> experiment;
	
	
	//CONSTRUCTORS
	public GNW_TimeseriesTSV(){
		this(null);
	}
	
	public GNW_TimeseriesTSV(File f){
		fich = f;
		
		numGenes = 0;
		numTLapses = 0;
		numExperiments = 0;
		
		timeLapse = 0;
		
		experiment = new Vector<float[][]>();
	}
	
	
	//METHODS
	
	/**
	 * This method create the file at the desktop or home directory (windows or other OS respectively)
	 */
	public File convertToDCUData(){
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
		return this.convertToDCUData(f);
	}
	
	
	/**
	 * This method saves all the info of fich in DCU %GeneratedData.txt files
	 * @param outputFile is the file where will be saved the info
	 */
	public File convertToDCUData(File out){
		//Load info
		this.load();
		
		//Prepare to write
		try {
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
				
				float[][] exp = experiment.get(i);
				
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
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		//Any problem
		return out;
	}
	
	
	/**
	 * @return true if a File is selected
	 */
	public boolean fileSelected(){
		return fich == null? false : true;
	}
	
	
	/**
	 * @return selected file's name
	 */
	public String getFileName(){
		return fich.getName();
	}
	
	
	/**
	 * This method load the info saved at the timeseries.tsv file.
	 * @return true if load action finish without errors
	 */
	public boolean load(){
		if(fich == null)
			return false;
		
		//Take info
		this.GenesAndExperemients();
		
		//Start load action
		try {
			//Each experiment will be saved here
			float[][] exp = new float[numTLapses][numGenes];
			
			//Open file
			FileReader fr = new FileReader(fich);
			Scanner sc = new Scanner(fr);

			Scanner aux = new Scanner("");
			
			//Clean header and empty line
			sc.nextLine(); //header
			sc.nextLine(); //empty line
			
			
			//Start to read
			int i = 0;
			
			while(sc.hasNextLine()){
				String s = sc.nextLine();
				
				if(s.isEmpty()){
					//save experiment
					experiment.addElement(exp);
					//reset matrix
					exp = new float[numTLapses][numGenes];
					i = 0;
				}else{
					//read line
					aux = new Scanner(s);
					aux.useDelimiter("\t");
					
					aux.next(); //obviate time column;
					
					for(int j = 0; aux.hasNext();j++)
						exp[i][j] = Float.valueOf(aux.next().replace(",", "."));
					++i;
				}
				aux.close();
			}//while

			//Load last experiment
			experiment.addElement(exp);
			
			//END
			sc.close();
			
			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}

		//Load ended correctly
		return true;
	}//
	
	
	/**
	 * Load file info on numGenes, numTLapses, numExperiments and timeLapse fields
	 */
	private void GenesAndExperemients(){
		int[] result = {-1,-1,-1};
		float tlaps = -1;
		
		try {
			//Open file
			FileReader fr = new FileReader(fich);
			Scanner sc = new Scanner(fr);
			
			//Number of tabs on first line (header) = number of genes
			result[0] = 0;
			
			Scanner aux = new Scanner(sc.nextLine());
			aux.useDelimiter("\t");
			
			while(aux.hasNext()){
				result[0]++;
				aux.next();
			}
			result[0]--;
			
			//Count number of experiments
			result[1] = 0;
			
			while(sc.hasNextLine()){
				if(sc.nextLine().equals(""))
					result[1]++;
			}
			
			//Restart scanner and take lapse info
			sc = new Scanner(new FileReader(fich));

			sc.nextLine(); //header
			sc.nextLine(); //empty line
			
			result[2] = 0;
			
			float t0=-1,t1=-1;
			
			for(int i=0;i<2;++i){	
				aux = new Scanner(sc.nextLine());
				aux.useDelimiter("\t");
				if(i==0)
					t0 = aux.nextInt();
				else
					t1 = aux.nextInt();
				result[2]++;
			}
			
			tlaps = (t0<0 ||t1<0)? -1 : (t1 - t0)/1000;
			
			aux.close();
			
			while(!sc.nextLine().equals(""))
				result[2]++;
				
			sc.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		//save info
		numGenes = result[0];
		numExperiments = result[1];
		numTLapses = result[2];
		
		timeLapse = tlaps;
	}
	
	/**
	 * @param f = new file
	 */
	public void setFile(File f){
		fich = f;
	}
	
	
	public String toString(){
		return "File: " + fich.getName() + "\n" +
				"Number of genes: " + numGenes + " || Number of experiments: " + numExperiments + "\n" +
				"Time lapse: " + timeLapse + " || Number of lapses: " + numTLapses;
	}
}//END CLASS
