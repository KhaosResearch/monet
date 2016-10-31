package org.uma.jmetal.problem.grn.util;

import java.util.ArrayList;

public class SingleGeneSimplifiedSSModel extends SingleGeneGrnContinuousModel {

	public SingleGeneSimplifiedSSModel(MicroarrayData mad, int gene,
			ArrayList<Double> parameters) {
		super(mad, gene, parameters);
		
	}

	@Override
	protected double getGeneExpressionChange(ArrayList<Double> currentGeneValues) {
		
		double degradation = 1;
		double synthesis = 1;
		
		for (int j = 0; j < this.mad.getGeneCount(); j++) 
			synthesis *= Math.pow(currentGeneValues.get(j), this.parameters
					.get(j));
		
			degradation *= Math.pow(currentGeneValues.get(this.getGene()),
					this.parameters.get(this.mad.getGeneCount()));
		

		return synthesis * parameters.get(this.mad.getGeneCount()+1)
				- degradation * parameters.get(this.mad.getGeneCount() + 2);
	
	}

}
