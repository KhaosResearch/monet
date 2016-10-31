package org.uma.jmetal.problem.grn.util;

import java.io.Serializable;
import java.util.ArrayList;

public abstract class SingleGeneModelEvaluator extends ModelEvaluator implements Serializable {

	public  double evaluate(SingleGeneGrnContinuousModel model)
	{
		double result;
		ArrayList<ArrayList<Double>> simulatedData = new ArrayList<ArrayList<Double>>();

		for(int k=0;k<this.mad.getTimeSeriesCount();k++)
		{//simulate each time series

			simulatedData.add(new ArrayList<Double>());
			double startValue = this.mad.getMad().get(k).get(model.getGene()).get(0);
			simulatedData.get(k).add(startValue);

			double currentTime=0;

			ArrayList<Double> currentTimeSpans=this.mad.getTimeSpans().get(k);
			for (int i = 1; i < currentTimeSpans.size(); i++) {
				startValue = model.getValueAfterTimeSpan(startValue,
						currentTimeSpans.get(i), currentTime,k);

				simulatedData.get(k).add(startValue);
				currentTime+=currentTimeSpans.get(i);
			}

			}
		
		
		result= evaluate(simulatedData,model.getGene());
		return result;
	}

	protected abstract double evaluate(ArrayList<ArrayList<Double>> simulatedData, int gene);
}
