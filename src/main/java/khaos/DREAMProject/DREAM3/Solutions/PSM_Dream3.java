package khaos.DREAMProject.DREAM3.Solutions;

/**
 * @author Fernando Moreno Jabato <fmjabato@yahoo.es>
 */

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import khaos.InformationNotFoundException;
import khaos.Solutions.PredictionSetManager;
import khaos.tools.Graphics.Edge;
import edu.uci.ics.jung.visualization.VisualizationImageServer;


public class PSM_Dream3 extends PredictionSetManager {
	//CONSTANTS
	public static final int DREAM3Format = 0;
	public static final int StatisticFormat = 1; 
	
	//FIELDS
	private Vector<Map<String,Double>> predictions;
	private Vector<Force> resumePrediction;
	
	//CONSTRUCTORS
	public PSM_Dream3(String[] predictionFiles){
		super(predictionFiles);
		predictions = new Vector<Map<String,Double>>();
		resumePrediction = new Vector<Force>();
	}
	
	public PSM_Dream3(Vector<String> predictionFiles){
		super(predictionFiles);
		predictions = new Vector<Map<String,Double>>();
		resumePrediction = new Vector<Force>();
	}
	
	//METHODS
	@Override
	public boolean checkFormat(){
		for(Iterator<String> i = files.iterator(); i.hasNext();)
			if(!checkFormat(i.next()))
				return false;
		return true;
	}
	
	@Override
	public boolean checkFormat(String file){
		File f = new File(file);
		if(!f.exists()) return false; //Check if it's an existing file
		
		try{
			FileReader fr = new FileReader(f);
			Scanner sc = new Scanner(f);
			
			if(!sc.hasNextLine()){
				fr.close();
				sc.close();
				
				return false; //Empty file
			}
			
			String line;
			String[] elements;
			
			while(sc.hasNextLine()){
				line = sc.nextLine();
				elements = line.split("\t");
				
				if(elements.length != 3){
					fr.close();
					sc.close();
					
					return false; //Format is: "ID1 \t ID2 \t Strength" (without spaces)
				}
				if(!isNumber(elements[2])){
					fr.close();
					sc.close();
					
					return false;
				}
			}
			
			fr.close();
			sc.close();
		}catch(IOException ioe){
			ioe.printStackTrace();
			
			return false;
		}
		//Everything's OK
		return true;
	}
	
	/**
	 * Take the prediction set information and create an accumulative prediction.
	 * @throws InformationNotFoundException.
	 */
	public void createResumePrediction(){
		if(predictions.isEmpty()) throw new InformationNotFoundException("Information not loaded yet");
		
		Map<String,Vector<Double>> forces = new TreeMap<String,Vector<Double>>(); //Resume of predictions
		
		Map<String,Double> prediction;
		String key;
			
		//Take all values
		for(Iterator<Map<String,Double>> i = predictions.iterator(); i.hasNext();){
			prediction = i.next();
			for(Iterator<String> keys = prediction.keySet().iterator(); keys.hasNext();){
				key = keys.next();
				if(forces.containsKey(key)) //Include the new value
					forces.get(key).add(prediction.get(key));
				else{ //Add the new register and include the value
					Vector<Double> newVector = new Vector<Double>();
					newVector.add(prediction.get(key));
					
					forces.put(key, newVector);
				}
			}
		}//
			
		//Save the resume
		for(Iterator<String> keys = forces.keySet().iterator(); keys.hasNext();){
			key = keys.next(); //Force identifier
			int frequency = forces.get(key).size();
			double meanForce=0;
			
			for(Iterator<Double> f = forces.get(key).iterator(); f.hasNext();)
				meanForce += f.next();
			
			meanForce = meanForce/frequency;
			
			resumePrediction.add(new Force(key.split("_")[0],key.split("_")[1],meanForce,frequency));
		}

	}
	
	/**
	 * This method is used to draw a graph of an specified prediction of the prediction set.
	 * @param prediction index of the prediction set.
	 * @return a jung.visualization.VisualizationImageServer that is a type of JPanel.
	 * @throws IndexOutOfBoundsException InformationNotFoundException
	 */
	public VisualizationImageServer drawPrediction(int prediction){
		if(predictions.isEmpty()) throw new InformationNotFoundException("Information not loaded yet");
		
		Map<String,Double> p = predictions.elementAt(prediction);
		
		String s;
		Double d;
		Vector<Edge> edges = new Vector<Edge>();
		
		for(Iterator<String> i=p.keySet().iterator(); i.hasNext();){
			s = i.next();
			d = p.get(s);
			
			edges.add(new Edge(d.toString(),s.split("_")[0],s.split("_")[1]));
		}
		
		D3_GraphicDrawer d3gd = new D3_GraphicDrawer(edges);
		
		return d3gd.draw();
	}
	
	/**
	 * This method is used to draw a graph of the resume prediction generated.
	 * @return a jung.visualization.VisualizationImageServer that is a type of JPanel.
	 * @throws InformationNotFoundException
	 */
	public VisualizationImageServer drawResumePrediction(){
		D3_GraphicDrawer d3gd = new D3_GraphicDrawer(this.getResumePrediction());
		
		return d3gd.draw();
	}
	
	/**
	 * This method is used to obtain an specific prediction.
	 * @param index of the prediction wanted.
	 * @return a map that contains the prediction.
	 * @throws InformationNotFoundException
	 */
	public Map<String,Double> getPrediction(int index){
		if(predictions.isEmpty()) throw new InformationNotFoundException("Information not loaded yet");
		return predictions.elementAt(index);
	}
	
	/**
	 * This method is used to obtain the prediction set loaded.
	 * @return a vector of maps that contains each prediction.
	 */
	public Vector<Map<String,Double>> getPredictions(){
		if(predictions.isEmpty()) throw new InformationNotFoundException("Information not loaded yet");
		return predictions;
	}
	
	/**
	 * Thus method is used to obtain a vector with an accumulative prediction made using the prediction set.
	 * @return a vector that contains the forces of the accumulative prediction.
	 * @throws InformationNotFoundException.
	 */
	public Vector<Edge> getResumePrediction(){
		if(resumePrediction.isEmpty()) throw new InformationNotFoundException("Predicition isn't created. Use createResumePrediction()");
		
		Vector<Edge> edges = new Vector<Edge>();
		
		for(Iterator<Force> f=resumePrediction.iterator();f.hasNext();){
			edges.add((Edge) f.next());
		}
		
		return edges;
	}
	
	/**
	 * This method is used to know what files of the prediction set have a wrong format.
	 * @return an array with the index of the wrong files.
	 */
	public int[] getWrongFormatFiles(){
		Vector<Integer> v = new Vector<Integer>();
		for(int i=0; i<files.size(); ++i)
			if(!checkFormat(files.elementAt(i)))
				v.add(new Integer(i));
		
		int[] wrong = new int[v.size()];
		for(int i=0; i<v.size(); ++i)
			wrong[i] = v.elementAt(i);
		
		return wrong;
	}
	
	/**
	 * @param s string to be checked
	 * @return true if the string given correspond to a number. Return false in the other case.
	 */
	public static boolean isNumber(String s){
		try{
			Double d = new Double(s);
		}catch(NumberFormatException nfe){
			return false;
		}
		return true;
	}
	
	@Override
	public void load(){
		Map<String,Double> map;
		File file;
		String s;
		String[] elements;
		
		for(int i=0; i<files.size(); ++i){
			map = new TreeMap<String,Double>();
			file = new File(files.elementAt(i));
			
			try{
				FileReader fr = new FileReader(file);
				Scanner sc = new Scanner(fr);
				
				while(sc.hasNext()){
					s = sc.nextLine();
					elements = s.split("\t");
					
					map.put(elements[0]+"_"+elements[1],new Double(elements[2]));
				}
				
				fr.close();
				sc.close();
				
				predictions.add(map);
			}catch(IOException ioe){
				System.err.println("AT FILE "+i);
				ioe.printStackTrace();
			}
		}
	}
	
	/**
	 * This method is used to create a file that contains a resume of the different forces and their mean in the prediction set.
	 * @param outputFile the path of the file where the resume will be written.
	 * @return true if the process finish without errors and false in other case.
	 * @throws InformationNotFoundException.
	 */
	public boolean printResumePrediction(String outputFile){
		return this.printResumePrediction(outputFile, StatisticFormat);
	}
	
	/**
	 * This method is used to create a file that contains an accumulative prediction using the format specified.
	 * @param outputFile the path of the file where the resume will be written.
	 * @param format of the file will be created.
	 * @return true if the process finish without errors and false in other case.
	 * @throws InformationNotFoundException.
	 */
	public boolean printResumePrediction(String outputFile,int format){
		if(predictions.isEmpty()) throw new InformationNotFoundException("Information not loaded yet");
		
		this.createResumePrediction();
		
		try{
			FileWriter fw = new FileWriter(new File(outputFile));

			//Header
			if(format != DREAM3Format){
				fw.write("Number of predictions:"+predictions.size()+"\n");
				fw.write("ForceSource\tForceTarget\tFrequency\tMean\n");
			}
			
			//Write the resume
			for(Iterator<Force> forces = resumePrediction.iterator(); forces.hasNext();){
				Force f = forces.next();
				if(format == DREAM3Format)
					fw.write(f.getSource()+"\t"+f.getTarget()+"\t"+
							f.getForce()+"\n");
				else
					fw.write(f.getSource()+"\t"+f.getTarget()+"\t"+
						f.getFrequency()+"\t"+f.getForce()+"\n");
					
			}
			
			fw.close();
		}catch(IOException ioe){
			ioe.printStackTrace();
			return false;
		}
		
		//Everything's OK
		return true;
	}

	
	
				public class Force extends Edge{
					//FIELD
					double meanForce;
					int frequency;
					//CONSTRUCTOR
					public Force(String origin,String end,double force,int freq){
						super(String.valueOf(force)+"/"+String.valueOf(freq),origin,end);
						meanForce = force;
						frequency = freq;
					}
					//METHODS
					public double getForce(){
						return meanForce;
					}
					public int getFrequency(){
						return frequency;
					}
				}//END INTErNAL CLASS
	
	
	
	public static void main(String[] args){
		Vector<String> files = new Vector<String>();
		
		/*for(int i=0;i<2;++i){
			files.add("C:\\Users\\Jose-Manuel\\Software\\jmetalmaven\\prediccionFalsa"+i+".txt");
		}*/
        files.add(args[0]);
        //files.add("C:\\Users\\Jose-Manuel\\Software\\jmetalmaven\\datasets-gnw\\DREAM3\\scripts\\challenge4_script\\predictions\\InSilicoSize10_Ecoli1.txt");

		PSM_Dream3 d3 = new PSM_Dream3(files);
		
		//System.out.println(d3.checkFormat("a"));
		
		d3.load();
		
		//d3.printResumePrediction("C:\\Users\\fmjab_000\\Desktop\\resume.txt");
		
		d3.createResumePrediction();
		

		//D3_GraphicDrawer gd = new D3_GraphicDrawer(d3.getResumePrediction());

		// SHOW IN A JFRAME
		
		JFrame frame = new JFrame();
		frame.setContentPane(new JScrollPane(d3.drawResumePrediction()));
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		//EXPORT AS IMAGE
		/*
		JPanel panel = gd.draw();
		BufferedImage img = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_RGB);
	    panel.print(img.getGraphics()); // or: panel.printAll(...);
	    try {
	        ImageIO.write(img, "jpg", new File("C:\\Users\\fmjab_000\\Desktop\\panel.jpg"));
	    }
	    catch (IOException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }
	    */
		
	}
}
