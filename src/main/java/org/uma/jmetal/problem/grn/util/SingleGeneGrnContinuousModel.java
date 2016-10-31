package org.uma.jmetal.problem.grn.util;

import java.util.ArrayList;

public abstract class SingleGeneGrnContinuousModel {

	protected MicroarrayData mad;
	private int gene;
	public int getGene() {
		return gene;
	}

	public void setGene(int gene) {
		this.gene = gene;
	}

	protected ArrayList<Double> parameters;
	
	private double integrationStep=0.008;
	
	
	public SingleGeneGrnContinuousModel(MicroarrayData mad,
			int gene, ArrayList<Double> parameters) {
		super();
		this.mad = mad;
		this.gene = gene;
		this.parameters = parameters;
		
	}

	public ArrayList<Double> simulate(int timeSeries)
	{
		ArrayList<Double> result= new ArrayList<Double>();
		ArrayList<ArrayList<Double>> timeSeriesMad=this.mad.getMad().get(timeSeries);
		double initialValue= timeSeriesMad.get(this.gene).get(0);
		result.add(initialValue);
		double nextValue=initialValue;
		double currentTime=0;
		ArrayList<Double> timeSpans= this.mad.getTimeSpans().get(timeSeries);
		for(int i=1;i<timeSpans.size();i++)
		{
			 nextValue= this.getValueAfterTimeSpan(nextValue,timeSpans.get(i),currentTime,timeSeries);
			 currentTime+=timeSpans.get(i);
			 result.add(nextValue);
		}
		return result;
	}

	public double getValueAfterTimeSpan(double initialValue, double timeSpan, double currentTime, int timeCourse) 
	{
		double result = initialValue;
		

		double doubleIterations = timeSpan / integrationStep;
		int iterations = (int) doubleIterations;
		double lastStep = timeSpan - (iterations * integrationStep);

		for (int i = 0; i < iterations; i++) {

			result = this.getNewGeneValue(result, integrationStep, currentTime,timeCourse);
			currentTime+=integrationStep;
		}

		result = this.getNewGeneValue(result, lastStep, currentTime,timeCourse);

		return result;
		
	}
	
	private double getNewGeneValue(double initialValue, double step, double currentTime, int timeCourse) {
		double k1, k2, k3, k4;
		ArrayList<Double> currentGeneValues=this.getInterpolatedGeneValues(currentTime,timeCourse);
		currentGeneValues.set(this.gene, initialValue);
		k1 = this.getGeneExpressionChange(currentGeneValues);
		
		currentGeneValues=this.getInterpolatedGeneValues(currentTime+step/2,timeCourse);
		currentGeneValues.set(this.gene, initialValue+k1*step/2);
		
		k2 = this.getGeneExpressionChange(currentGeneValues);
		
		currentGeneValues=this.getInterpolatedGeneValues(currentTime+step/2,timeCourse);
		currentGeneValues.set(this.gene, initialValue+k2*step/2);
		
		k3 = this.getGeneExpressionChange(currentGeneValues);
		
		currentGeneValues=this.getInterpolatedGeneValues(currentTime+step,timeCourse);
		currentGeneValues.set(this.gene, initialValue+k3*step);
		
		k4 = this.getGeneExpressionChange(currentGeneValues);
		
		double result = initialValue+
				(k1+k2* 2+k3*2+ k4)* step / 6;

		return (result>0.00001)?result:0.00001;
	}

	private ArrayList<Double> getInterpolatedGeneValues(double currentTime, int timeCourse) {
		//linear Interpolation
		
		//find time interval in which current time is (find xi and xi+1)
		double xi=this.mad.getTimeSpans().get(timeCourse).get(0);
		double aux;
		int i=0;
		while((aux=xi+this.mad.getTimeSpans().get(timeCourse).get(i+1))<currentTime)
		{
			i++;
			xi=aux;
		}
		
		ArrayList<Double> result = new ArrayList<Double>();
		for(int j=0;j<this.mad.getGeneCount();j++)
		{
			ArrayList<Double> geneMad= this.mad.getMad().get(timeCourse).get(j);
			double geneValue=geneMad.get(i)+
				(geneMad.get(i+1)-geneMad.get(i))/this.mad.getTimeSpans().get(timeCourse).get(i+1)*(currentTime-xi);
			
			result.add(geneValue);
		}
		//quadratic spline interpolation
		
		//local linear regression
		return result;
	}

	protected abstract double getGeneExpressionChange(ArrayList<Double> currentGeneValues);
}
