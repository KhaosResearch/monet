package org.uma.jmetal.problem.grn.util;

import java.util.ArrayList;

public abstract class CompleteGeneModelEvaluator extends ModelEvaluator{

	
	public  double evaluate(GRNContinuousModel model)
	{
		double result ;
        boolean collocation = true;
		ArrayList<ArrayList<ArrayList<Double>>> simulatedData = new ArrayList<ArrayList<ArrayList<Double>>>();
		for(int k=0;k<this.mad.getTimeSeriesCount();k++)
		{
			simulatedData.add(new ArrayList<ArrayList<Double>>());
			for (int i = 0; i < this.mad.getGeneCount(); i++)
				simulatedData.get(k).add(new ArrayList<Double>());

			ArrayList<Double> startValues = new ArrayList<Double>();
            for (int i = 0; i < this.mad.getGeneCount(); i++) {
				double expressionValue = this.mad.getMad().get(k).get(i).get(0);
				startValues.add(expressionValue);
				simulatedData.get(k).get(i).add(expressionValue);
			}

            // for X(0) = X0, therefore it starts from 1

			for (int i = 1; i < this.mad.getTimeSpans().get(k).size(); i++) {
                if (collocation) {
                    ArrayList<Double> currentGeneValues = new ArrayList<Double>();
                    //ArrayList<Double> previousGeneValues = new ArrayList<Double>();
                    /* Wang05 :  currentGeneValues, previousGeneValues, timeSpan */
                    for (int j = 0; j < this.mad.getGeneCount(); j++) {
                        currentGeneValues.add(this.mad.getMad().get(k).get(j).get(i));
                        //previousGeneValues.add(mad.getMad().get(k).get(j).get(i - 1));
                    }
                    //startValues = model.getCollocationValuesAfterTimeSpan(currentGeneValues, previousGeneValues, this.mad.getTimeSpans().get(k).get(i));
                    startValues = model.getCollocationValuesAfterTimeSpan(currentGeneValues, startValues, this.mad.getTimeSpans().get(k).get(i));
                } else {
                    /* Runge Kutta */
                    startValues = model.getValuesAfterTimeSpan(startValues, this.mad.getTimeSpans().get(k).get(i));
                }/* end if */
                for (int j = 0; j < this.mad.getGeneCount(); j++) {
                    simulatedData.get(k).get(j).add(startValues.get(j));
                    //System.out.println("start values: " + startValues.get(j));
                }
            }/* end for */
		}
		result= this.evaluate(simulatedData);

        //System.out.println("RESULT: " + result);
		return result;
	}


    protected abstract double evaluate(ArrayList<ArrayList<ArrayList<Double>>> simulatedData);

}
