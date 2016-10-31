package org.uma.jmetal.problem.grn.util;

import java.util.ArrayList;

//import eva2.server.go.individuals.InterfaceDataTypeDouble;

public class SingleGeneAICEvaluator extends SingleGeneModelEvaluator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5477498113314088613L;

	private int geneParameterCount;
	public SingleGeneAICEvaluator(int geneParameterCount) {
		this.geneParameterCount=geneParameterCount;
	}

	@Override
	protected double evaluate(ArrayList<ArrayList<Double>> simulatedData,
			int gene) {
		
		double AIC=0;
		double errorSum= 0;
		int timePoints=0;
		for(int k=0;k<this.mad.getTimeSeriesCount();k++)
		{
		ArrayList<Double> difference = MathUtils.AddVector(
				simulatedData.get(k), MathUtils.MultiplyVector(mad.getMad().get(k).get(gene), -1));

		timePoints+=this.mad.getExperimentCount().get(k);
		for(int i=0;i<difference.size();i++)
			errorSum+=Math.pow(difference.get(i), 2);
		}
		
		double variance=errorSum/timePoints;
		
		double likelihood= -errorSum/(2*variance)-timePoints/2*Math.log(2*Math.PI*variance);
		
		
		AIC=-2*likelihood+2*this.geneParameterCount;
		
		return AIC;
	}

}
