package org.uma.jmetal.problem.grn.util;

import java.util.ArrayList;

public class GRNLinearModel extends GRNContinuousModel {

	protected ArrayList<ArrayList<Double>> parameters = null;
	public GRNLinearModel(ArrayList<ArrayList<Double>> parameters) {
		super(parameters.size());
		this.parameters=parameters;
	}

	@Override
	protected ArrayList<Double> getGeneExpressionChanges(
			ArrayList<Double> currentGeneValues) {
		ArrayList<Double> result = new ArrayList<Double>();
		for (int i = 0; i < this.geneCount; i++) {
			double geneChange = 0;
			ArrayList<Double> geneParameters = this.parameters.get(i);
			for (int j = 0; j < this.geneCount; j++) {
				geneChange += geneParameters.get(j) * currentGeneValues.get(j);
			}

			result.add(geneChange);
		}
		return result;
	}

}
