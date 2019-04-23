package khaos.DREAMProject.DREAM3.Solutions;

/**
 * @author Fernando Moreno Jabato <fmjabato@yahoo.es>
 */

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import khaos.InformationNotFoundException;
import khaos.Solutions.VARFile;

/**
 *  This class are the same than khaos.Solutions.VARFile.java but implements a method that
 *  transform the VARFiles information to a correct format for DREAM3 solutions.
 */
public class D3_ResultJMetal extends VARFile{
	public D3_ResultJMetal(){
		super();
	}
	public D3_ResultJMetal(File tsv){
		super(tsv);
	}
	public D3_ResultJMetal(File tsv,int numGenes){
		super(tsv,numGenes);
	}
	
	//METHODS
	/**
	 * This method write a result saved on a VARFile in an specified file with the DREAM3 result format.
	 * @param out file where will be wrote the result.
	 * @param set set should be focus. Could be "<0" -> set1, ">0" -> set2 or "=0" -> Best of each value for both sets.
	 * @param resultIndex result wanted to be saved.
	 * @param threshold that should be exceeded for save the value.
	 * @return the file where the result had been saved.
	 * @throws InformationNotFoundException
	 */
	public File VARFile_DREAMFormat(File out, int set, int resultIndex, double threshold) throws InformationNotFoundException{
		if(this.toString().contains("FILE NOT LOADED YET")) throw new InformationNotFoundException("Information not loaded yet");
		
		if(set == 0)
			try{
				this.getResults(-1);
				this.getResults(1);
			}catch(InformationNotFoundException infe){
				throw new InformationNotFoundException();
			}
		
		
		double[][] result;
		double[][] result2 = null;
		
		if(set!=0)
			result = this.getResult(set, resultIndex);
		else{
			result = this.getResult(-1, resultIndex);
			result2 = this.getResult(1, resultIndex);
		}
		
		HashMap<String,Double> mapAux = new HashMap<String, Double>();
		
		//Put info on map
		for(int i=0; i<result.length; ++i)
			for(int j=0; j<result[0].length; ++j)
				if(set!=0){
					if(i!=j && result[i][j] > threshold) //Self regulation isn't allowed
						mapAux.put("G"+i+"\t"+"G"+j,new Double(result[i][j]));
				}else{
					if(i!=j && result[i][j] > threshold){
						// If result[i][j] is over threshold and result2[i][j] > result[i][j] => result2[i][j] is over threshold
						Double v = new Double(result[i][j]>result2[i][j]? result[i][j] : result2[i][j]);
						mapAux.put("G"+i+"\t"+"G"+j,v);
					}else if(i!=j && result2[i][j] > threshold)
						mapAux.put("G"+i+"\t"+"G"+j,new Double(result2[i][j]));
				}
					
		//Write the info
		
		TreeMap<String,Double>map = SortByValue(mapAux);
		
		try{
			FileWriter fw = new FileWriter(out);
			
			
			Iterator<String> it = map.keySet().iterator();
			Iterator<Double> it2 = map.values().iterator();
			while(it.hasNext()){
				//String key = it.next();
				fw.write(it.next()+"\t"+it2.next()+"\n");
			}
			
			fw.close();
		}catch(IOException ioe){
			ioe.printStackTrace();
		}
		
		return out;
	}
	
	
			public static TreeMap<String, Double> SortByValue (HashMap<String, Double> map) {
				ComparatorDoubles cd =  new ComparatorDoubles(map);
				TreeMap<String,Double> sortedMap = new TreeMap<String,Double>(cd);
				sortedMap.putAll(map);
				return sortedMap;
				}
	
}


class ComparatorDoubles implements Comparator<String>{
	//FIELDS
	private Map<String,Double> map;
	
	//COSNTRUCTOR
	public ComparatorDoubles(Map<String,Double> mp){
		this.map = mp;
	}
	
	//METHODS
	@Override
	public int compare(String o1, String o2) {
		if(map.get(o1).compareTo(map.get(o2))>=0) return -1;
		else return 1;
	}
}
