package org.uma.jmetal.problem.grn.util;

import org.uma.jmetal.problem.grn.util.ra.math.RNG;

import java.util.ArrayList;

public class DataTools {
	

	public MicroarrayData normaliseMad(MicroarrayData data)
	{// limit the values to interval [0,1] keeping the distribution
		for(int i=0;i<data.getTimeSeriesCount();i++)
		{
			for(int j=0;j<data.getGeneCount();j++)
			{
				double max=MathUtils.Max(data.getMad().get(i).get(j));
				double min=MathUtils.Min(data.getMad().get(i).get(j));
				double range= max-min;
				//shift data so that min-5%*range is on 0
				for(int k=0;k<data.getExperimentCount().get(i);k++)
				{
					data.getMad().get(i).get(j).set(k,data.getMad().get(i).get(j).get(k)-min+range*0.05);
				}
				//consider range to be 10% wider than seen in the data
				range*=1.1;
				//divide each element by range to limit values in interval [0,1]
				
				for(int k=0;k<data.getExperimentCount().get(i);k++)
				{
					data.getMad().get(i).get(j).set(k,data.getMad().get(i).get(j).get(k)/range);
				}
			}
				
		}
		return data;
	}
	
	public MicroarrayData normaliseMadNoTrans(MicroarrayData data)//for positive data
	{// limit the values to interval [0,1] keeping the distribution
		for(int i=0;i<data.getTimeSeriesCount();i++)
		{
			for(int j=0;j<data.getGeneCount();j++)
			{
				double max=MathUtils.Max(data.getMad().get(i).get(j));
				double min=MathUtils.Min(data.getMad().get(i).get(j));
				
				//consider max to be 10% wider than seen in the data
				max*=1.1;
				//divide each element by range to limit values in interval [0,1]
				
				for(int k=0;k<data.getExperimentCount().get(i);k++)
				{
					data.getMad().get(i).get(j).set(k,data.getMad().get(i).get(j).get(k)/max);
				}
			}
				
		}
		return data;
	}
	public MicroarrayData scaleMad(MicroarrayData data, double factor)
	{
		for(int i=0;i<data.getTimeSeriesCount();i++)
		{
			for(int j=0;j<data.getGeneCount();j++)
			{
				
				
				
				for(int k=0;k<data.getExperimentCount().get(i);k++)
				{
					data.getMad().get(i).get(j).set(k,data.getMad().get(i).get(j).get(k)*factor);
				}
			}
				
		}
		return data;
	}
	
	public MicroarrayData translateMad(MicroarrayData data, double factor)
	{
		for(int i=0;i<data.getTimeSeriesCount();i++)
		{
			for(int j=0;j<data.getGeneCount();j++)
			{
				
				
				
				for(int k=0;k<data.getExperimentCount().get(i);k++)
				{
					data.getMad().get(i).get(j).set(k,data.getMad().get(i).get(j).get(k)+factor);
				}
			}
				
		}
		return data;
	}
	
	public MicroarrayData addNoise(MicroarrayData data, double noiseRate)
	{
		ArrayList<Double> noise= new ArrayList<Double>();// max noise value for each gene
		MicroarrayData result= (MicroarrayData) data.clone();
		
		int timePoints=0;
		for(int j=0;j<result.getTimeSeriesCount();j++)
		{
			timePoints+=result.getExperimentCount().get(j);
		}
		
		for(int i=0;i<result.getGeneCount();i++)
		{//compute mean for current gene
			
			double mean=0;
			
			for(int j=0;j<result.getTimeSeriesCount();j++)
			{
				for(int k=0;k<result.getExperimentCount().get(j);k++)
				{
					mean+=result.getMad().get(j).get(i).get(k);
				}
			}
			mean/=timePoints;
			
			
			noise.add(mean*noiseRate);
		}
		
		for(int i=0;i<result.getTimeSeriesCount();i++)
		{
			for(int j=0;j<result.getGeneCount();j++)
			{
				for(int k=0;k<result.getExperimentCount().get(i);k++)
				{
					result.getMad().get(i).get(j).set(k, result.getMad().get(i).get(j).get(k)+ RNG.gaussianDouble(noise.get(j)));
				}
			}
		}
		return result;
	}
	
	public MicroarrayData filterGenes(MicroarrayData data, double variabilityThreshold)
	{
		MicroarrayData result=(MicroarrayData) data.clone();
		
		
		ArrayList<Integer> eliminatedGenes= new ArrayList<Integer>();
		
		int timePoints=0;
		for(int j=0;j<result.getTimeSeriesCount();j++)
		{
			timePoints+=result.getExperimentCount().get(j);
		}
		
		for(int i=0;i<result.getGeneCount();i++)
		{//compute coeff of  variation : stDev/mean
			double stdDev=0;
			double mean=0;
			
			for(int j=0;j<result.getTimeSeriesCount();j++)
			{
				for(int k=0;k<result.getExperimentCount().get(j);k++)
				{
					mean+=result.getMad().get(j).get(i).get(k);
				}
			}
			mean/=timePoints;
			
			
			for(int j=0;j<result.getTimeSeriesCount();j++)
			{
				for(int k=0;k<result.getExperimentCount().get(j);k++)
				{
					stdDev+=Math.pow(result.getMad().get(j).get(i).get(k)-mean,2);
				}
			}
			stdDev/=timePoints;
			//if stdDev/mean <threshold, add gene to deleted list
			if(stdDev/mean<variabilityThreshold)
				eliminatedGenes.add(i);
			
		}
		
		//eliminate all genes that fall below the variability threshold
		for(int i=0;i<eliminatedGenes.size();i++)
			result.eliminateGene(eliminatedGenes.get(i)-i);
		
		return result;
	}
	
	public static void main(String[] args)
	{
		//pramila 17 genes
		//read data
		DataTools processor=new DataTools();
		
		MicroarrayData mad= new MicroarrayData("PramilaS17.txt");
		mad.initMadFromFile();
		
		
		mad=processor.normaliseMad(mad);
		
		
		mad.writeToFile("PramilaS17Norm.txt");
		
		mad= new MicroarrayData("PramilaS17.txt");
		mad.initMadFromFile();
		
		double minS=Double.MAX_VALUE;
		for(int i=0;i<mad.getTimeSeriesCount();i++)
		{
			for(int j=0;j<mad.getGeneCount();j++)
			{
				double geneMin= MathUtils.Min(mad.getMad().get(i).get(j));
				if(minS>geneMin)
					minS=geneMin;
			}
		}
		mad=processor.translateMad(mad,-minS*1.1);
		
		 double factorS=0;
			for(int i=0;i<mad.getTimeSeriesCount();i++)
			{
				for(int j=0;j<mad.getGeneCount();j++)
				{
					double geneMax= MathUtils.Max(mad.getMad().get(i).get(j));
					if(factorS<geneMax)
						factorS=geneMax;
				}
			}
			factorS*=1.1;
		mad=processor.scaleMad(mad, 1/factorS);
		mad.writeToFile("PramilaS17Scaled.txt");
		
		//read data
		 mad= new MicroarrayData("PramilaL17.txt");
		mad.initMadFromFile();
		
		
		
		mad=processor.normaliseMad(mad);
		
		
		mad.writeToFile("PramilaL17Norm.txt");
		
		mad= new MicroarrayData("PramilaL17.txt");
		mad.initMadFromFile();
		
		double minL=Double.MAX_VALUE;
		for(int i=0;i<mad.getTimeSeriesCount();i++)
		{
			for(int j=0;j<mad.getGeneCount();j++)
			{
				double geneMin= MathUtils.Min(mad.getMad().get(i).get(j));
				if(minL>geneMin)
					minL=geneMin;
			}
		}
		mad=processor.translateMad(mad,-minL*1.1);
		
		double  factorL=0;
			for(int i=0;i<mad.getTimeSeriesCount();i++)
			{
				for(int j=0;j<mad.getGeneCount();j++)
				{
					double geneMax= MathUtils.Max(mad.getMad().get(i).get(j));
					if(factorL<geneMax)
						factorL=geneMax;
				}
			}
			factorL*=1.1;
		mad=processor.scaleMad(mad, 1/factorL);
		mad.writeToFile("PramilaL17Scaled.txt");
		
		//read data pramila 9 genes
		 mad= new MicroarrayData("PramilaS9.txt");
		mad.initMadFromFile();
		
		
		
		mad=processor.normaliseMad(mad);
		
		
		mad.writeToFile("PramilaS9Norm.txt");
		
		mad= new MicroarrayData("PramilaS9.txt");
		mad.initMadFromFile();
		
		
		mad=processor.translateMad(mad,-minS*1.1);
		
		 
		mad=processor.scaleMad(mad, 1/factorS);
		mad.writeToFile("PramilaS9Scaled.txt");
		
		//read data
		 mad= new MicroarrayData("PramilaL9.txt");
		mad.initMadFromFile();
		
		
		
		mad=processor.normaliseMad(mad);
		
		
		mad.writeToFile("PramilaL9Norm.txt");
		
		mad= new MicroarrayData("PramilaL9.txt");
		mad.initMadFromFile();
		
		mad=processor.translateMad(mad,-minL*1.1);
		
		 
		mad=processor.scaleMad(mad, 1/factorL);
		mad.writeToFile("PramilaL9Scaled.txt");
		
		

		
	}
	
	
}
