package org.uma.jmetal.problem.grn.util;

import java.util.ArrayList;

public abstract class GRNContinuousModel {

	
	protected int geneCount;
	protected double integrationStep = 1.0;//0.025; //5.0;//0.0025; //h

	public GRNContinuousModel(int geneCount) {
		
		this.geneCount = geneCount;
	}

	/**
	 * Applies runge kutta method with integration step 0.025
	 */
	public ArrayList<Double> getValuesAfterTimeSpan(ArrayList<Double> startValues, Double timeSpan) {
		ArrayList<Double> result = new ArrayList<Double>();
		for (int i = 0; i < this.geneCount; i++)
			result.add(startValues.get(i));

		double doubleIterations = timeSpan / integrationStep;
		int iterations = (int) doubleIterations;
		double lastStep = timeSpan - (iterations * integrationStep);

        //System.out.println("ITs: "+iterations);
		for (int i = 0; i < iterations; i++) {
			result = this.getNewGeneValues(result, this.integrationStep);
            //System.out.println("it ("+i+"): "+result);
		}

		result = this.getNewGeneValues(result, lastStep);

		return result;

	}

    /* Wang05 Collocation method to integrate the solution */
    public ArrayList<Double> getCollocationValuesAfterTimeSpan(ArrayList<Double> currentGeneValues, ArrayList<Double> previousGeneValues, Double timeSpan) {
        /* X(j) = X(j-1) + 0.5*timeSpan*{f[X(j)]+f[X(j-1)])} */
        timeSpan=0.05;
        return MathUtils.EliminateNegatives(MathUtils.AddVector(previousGeneValues,
                MathUtils.MultiplyVector(MathUtils.AddVector(this.getGeneExpressionChanges(currentGeneValues),
                        this.getGeneExpressionChanges(previousGeneValues)),
                        0.5*timeSpan)));
    }



    /* Runge-Kutta method to integrate the solution */
	private ArrayList<Double> getNewGeneValues(ArrayList<Double> currentGeneValues, double integrationStep) {
		ArrayList<Double> k1, k2, k3, k4;
		k1 = this.getGeneExpressionChanges(currentGeneValues);
		k2 = this.getGeneExpressionChanges(MathUtils
				.EliminateNegatives(MathUtils.AddVector(currentGeneValues,
						MathUtils.MultiplyVector(k1, integrationStep
								/ (double) 2.0))));
		k3 = this.getGeneExpressionChanges(MathUtils
				.EliminateNegatives(MathUtils.AddVector(currentGeneValues,
						MathUtils.MultiplyVector(k2, integrationStep
								/ (double) 2.0))));
		k4 = this.getGeneExpressionChanges(MathUtils
				.EliminateNegatives(MathUtils.AddVector(currentGeneValues,
						MathUtils.MultiplyVector(k3, integrationStep))));

		ArrayList<Double> result = MathUtils.AddVector(currentGeneValues,
				MathUtils.MultiplyVector(MathUtils.AddVector(k1,
						MathUtils.AddVector(MathUtils.MultiplyVector(k2, 2),
								MathUtils.AddVector(MathUtils.MultiplyVector(
										k3, 2), k4))), integrationStep / 6));

		return MathUtils.EliminateNegatives(result);
	}


	protected abstract ArrayList<Double> getGeneExpressionChanges(ArrayList<Double> currentGeneValues);

}
