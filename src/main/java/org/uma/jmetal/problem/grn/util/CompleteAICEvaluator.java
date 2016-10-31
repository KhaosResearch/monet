package org.uma.jmetal.problem.grn.util;

import java.util.ArrayList;

public class CompleteAICEvaluator extends CompleteGeneModelEvaluator {

	private int geneParameterCount;

	public CompleteAICEvaluator(int geneParameterCount) {
		this.geneParameterCount= geneParameterCount;
	}

	@Override
	protected double evaluate(
			ArrayList<ArrayList<ArrayList<Double>>> simulatedData) {
		double errorSum=0;
		int totalTimePoints=0;
		for(int k=0;k<this.mad.getTimeSeriesCount();k++)
		{
			totalTimePoints+=this.mad.getExperimentCount().get(k);
		ArrayList<ArrayList<Double>> difference = MathUtils.AddMatrix(
				simulatedData.get(k), MathUtils.MultiplyMatrix(this.mad.getMad().get(k), -1));
		
		for (int i = 0; i < mad.getGeneCount(); i++) {
			for (int j = 0; j < mad.getTimeSpans().get(k).size(); j++) {
				errorSum += Math.pow(difference.get(i).get(j), 2);
			}
		}
		
	}
		double AIC=0;
		
		double variance=errorSum/totalTimePoints;

		double likelihood= -errorSum/(2*variance)-totalTimePoints/2*Math.log(2*Math.PI*variance);


		AIC=-2*likelihood+2*this.geneParameterCount;



		if (Double.isInfinite(AIC) || Double.isNaN(AIC))
			AIC = Double.MAX_VALUE / 2;

		return AIC;
	}

}
