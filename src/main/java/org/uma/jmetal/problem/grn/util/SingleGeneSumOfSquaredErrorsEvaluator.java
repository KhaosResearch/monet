package org.uma.jmetal.problem.grn.util;

import java.util.ArrayList;

public class SingleGeneSumOfSquaredErrorsEvaluator extends
		SingleGeneModelEvaluator {

	public SingleGeneSumOfSquaredErrorsEvaluator() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected double evaluate(ArrayList<ArrayList<Double>> simulatedData,
			int gene) {
		double result=0;
		for(int i=0;i<this.mad.getTimeSeriesCount();i++)
		{
			ArrayList<Double> difference = MathUtils.AddVector(
					simulatedData.get(i), MathUtils.MultiplyVector(mad.getMad().get(i).get(gene), -1));


			for (int j = 0; j < mad.getTimeSpans().get(i).size(); j++) {
				result += Math.pow(difference.get(j)
						, 2);

			}
		}
		return result;

	}

}
