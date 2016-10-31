package org.uma.jmetal.problem.grn.util;

import java.util.ArrayList;

public class CompleteSumOfSquaredErrorsEvaluator extends CompleteGeneModelEvaluator {

	@Override
	protected double evaluate(ArrayList<ArrayList<ArrayList<Double>>> simulatedData) {
		
		double result=0;
		for(int k=0;k<this.mad.getTimeSeriesCount();k++)
		{
		ArrayList<ArrayList<Double>> difference = MathUtils.AddMatrix(simulatedData.get(k), MathUtils.MultiplyMatrix(this.mad.getMad().get(k), -1));
		
		for (int i = 0; i < mad.getGeneCount(); i++) {
			for (int j = 0; j < mad.getTimeSpans().get(k).size(); j++) {
				result += Math.pow(difference.get(i).get(j), 2);
			}
		}
		
	}
		return result;

}
}
