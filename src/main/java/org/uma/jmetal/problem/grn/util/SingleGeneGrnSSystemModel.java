package org.uma.jmetal.problem.grn.util;

import java.util.ArrayList;

public class SingleGeneGrnSSystemModel extends SingleGeneGrnContinuousModel {

	public SingleGeneGrnSSystemModel(MicroarrayData mad, int gene,
			ArrayList<Double> parameters) {
		super(mad, gene, parameters);
		
	}
	
	@Override
	protected double getGeneExpressionChange(ArrayList<Double> currentGeneValues) {
	
			double degradation = 1;
			double synthesis = 1;
			
			for (int j = 0; j < this.mad.getGeneCount(); j++) {
				synthesis *= Math.pow(currentGeneValues.get(j), this.parameters
						.get(j));
				degradation *= Math.pow(currentGeneValues.get(j),
						this.parameters.get(j + this.mad.getGeneCount()));
			}

			return synthesis * parameters.get(2 * this.mad.getGeneCount())
					- degradation * parameters.get(2 * this.mad.getGeneCount() + 1);
		
	}

	

}
