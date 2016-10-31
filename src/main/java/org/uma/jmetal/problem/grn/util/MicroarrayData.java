package org.uma.jmetal.problem.grn.util;

import java.io.*;
import java.util.ArrayList;

public class MicroarrayData implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7876270943042900250L;
	private int timeSeriesCount;
	private int geneCount;
	public int getGeneCount() {
		return geneCount;
	}

	public void setGeneCount(int geneCount) {
		this.geneCount = geneCount;
	}

	public ArrayList<Integer> getExperimentCount() {
		return experimentCount;
	}

	public void setExperimentCount(ArrayList<Integer> experimentCount) {
		this.experimentCount = experimentCount;
	}

	public ArrayList<ArrayList<Double>> getTimeSpans() {
		return timeSpans;
	}

	public void setTimeSpans(ArrayList<ArrayList<Double>> timeSpans) {
		this.timeSpans = timeSpans;
	}

	public ArrayList<ArrayList<ArrayList<Double>>> getMad() {
		return mad;
	}

	public void setMad(ArrayList<ArrayList<ArrayList<Double>>> mad) {
		this.mad = mad;
	}

	private ArrayList<Integer> experimentCount;
	private ArrayList<ArrayList<Double>> timeSpans;
	private ArrayList<ArrayList<ArrayList<Double>>> mad;

	private String madFileName;

	public void setMadFileName(String madFile) {
		this.madFileName = madFile;

	}

	public String getMadFileName() {
		return madFileName;
	}




	public MicroarrayData(ArrayList<ArrayList<Double>> timeSpans,
			ArrayList<ArrayList<ArrayList<Double>>> mad) {
		super();
		this.timeSpans = timeSpans;
		this.mad = mad;
		this.geneCount=mad.get(0).size();
		this.experimentCount= new ArrayList<Integer>();
		this.timeSeriesCount=mad.size();
		for(int i=0;i<timeSpans.size();i++)
			this.experimentCount.add(timeSpans.get(i).size());
	}

	public MicroarrayData(String madFileName) {
		this.madFileName=madFileName;

	}

	public void initMadFromFile() {
		try {
			InputStream in = getClass().getResourceAsStream(madFileName);
			InputStreamReader isr = new InputStreamReader(in);
			BufferedReader madFile = new BufferedReader(isr);

			//BufferedReader madFile = new BufferedReader(new FileReader(
			//		madFileName));
			String line = madFile.readLine();//line 1: gene count and number of timeSeries
			String[] splitLine = line.split(" ");
			int genes = Integer.parseInt(splitLine[0]);
			this.geneCount = genes;
			timeSeriesCount = Integer.parseInt(splitLine[1]);

			this.timeSpans = new ArrayList<ArrayList<Double>>();
			this.experimentCount= new ArrayList<Integer>();

			mad = new ArrayList<ArrayList<ArrayList<Double>>>();
			//next lines for each time series
			for(int i=0;i<timeSeriesCount;i++)
			{

				ArrayList<ArrayList<Double>> currentTimeSeries= new ArrayList<ArrayList<Double>>();
				line = madFile.readLine();//number of time points in current time series
				this.experimentCount.add(Integer.parseInt(line));

				this.timeSpans.add(new ArrayList<Double>());
				line = madFile.readLine();//time spans for current time series
				splitLine = line.split("\\s");
				for (int j = 0; j < experimentCount.get(i); j++) {// read time spans -first is
					// always 0
					this.timeSpans.get(i).add(Double.parseDouble(splitLine[j]));
				}


				for (int j = 0; j < genes; j++) {
					ArrayList<Double> gene = new ArrayList<Double>();
					currentTimeSeries.add(gene);
				}

				for (int j = 0; j < this.experimentCount.get(i); j++) {
					line = madFile.readLine();
					splitLine = line.split("\\s");
					for (int k = 0; k < genes; k++) {
						currentTimeSeries.get(k).add(Double.parseDouble(splitLine[k]));
					}

				}
				mad.add(currentTimeSeries);
			}
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void setTimeSeriesCount(int timeSeriesCount) {
		this.timeSeriesCount = timeSeriesCount;
	}

	public int getTimeSeriesCount() {
		return timeSeriesCount;
	}

	@Override
	protected Object clone() {

		return new MicroarrayData((ArrayList<ArrayList<Double>>)this.timeSpans.clone(),
				(ArrayList<ArrayList<ArrayList<Double>>>) this.mad.clone());
	}

	public void eliminateGene(int gene) {
		this.geneCount--;

		for(int i=0;i<this.timeSeriesCount;i++)
		{
			this.mad.get(i).remove(gene);

		}

	}

	public void writeToFile(String fileName)
	{
		try {
			BufferedWriter madFile = new BufferedWriter(new FileWriter(
					fileName));
			madFile.write(String.valueOf(this.geneCount)+" "+String.valueOf(this.timeSeriesCount));
			for(int i=0;i<this.timeSeriesCount;i++)
			{
				madFile.newLine();
				madFile.write(this.experimentCount.get(i).toString());
				madFile.newLine();
				int j;
				for(j=0;j<this.experimentCount.get(i)-1;j++)
				{
					madFile.write(this.timeSpans.get(i).get(j)+" ");
				}
				madFile.write(this.timeSpans.get(i).get(j).toString());

				for(j=0;j<this.experimentCount.get(i);j++)
				{
					madFile.newLine();
					int k;
					for(k=0;k<this.geneCount-1;k++)
					{
						madFile.write(this.mad.get(i).get(k).get(j)+" ");
					}
					madFile.write(this.mad.get(i).get(k).get(j).toString());
				}

			}
			madFile.close();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}



}
