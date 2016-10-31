package org.uma.jmetal.problem.grn.util;

import java.util.ArrayList;

public class SingleGeneGrnLinearModel extends SingleGeneGrnContinuousModel {

	public SingleGeneGrnLinearModel(MicroarrayData mad, int gene,
			ArrayList<Double> parameters) {
		super(mad, gene, parameters);
	}

	@Override
	protected double getGeneExpressionChange(ArrayList<Double> currentGeneValues) {
		double geneChange = 0;
		
		for (int j = 0; j < this.mad.getGeneCount(); j++) {
			geneChange += this.parameters.get(j) * currentGeneValues.get(j);
		}
		return geneChange;
	}

	

}
