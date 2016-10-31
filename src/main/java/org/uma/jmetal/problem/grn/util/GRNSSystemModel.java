package org.uma.jmetal.problem.grn.util;

import java.util.ArrayList;

public class GRNSSystemModel extends GRNContinuousModel {

	protected ArrayList<ArrayList<Double>> parameters = null;
	
	public GRNSSystemModel(ArrayList<ArrayList<Double>> parameters) {
		super(parameters.size());
		this.parameters=parameters;

	}

	// parameters for each gene i: gij,hij,alpha1,betai

	@Override
	protected ArrayList<Double> getGeneExpressionChanges(ArrayList<Double> currentGeneValues) {
		ArrayList<Double> result = new ArrayList<Double>();
		for (int i = 0; i < this.geneCount; i++) {
			double degradation = 1;
			double synthesis = 1;
			ArrayList<Double> geneParameters = this.parameters.get(i);
			for (int j = 0; j < this.geneCount; j++) {
                if (geneParameters.get(j)!=0.0)
				    synthesis *= Math.pow(currentGeneValues.get(j), geneParameters.get(j));
                    //System.out.println("SYN ("+j+"): "+  currentGeneValues.get(j)+ " " +  geneParameters.get(j)+  " SYS acum : " +synthesis);
                if (geneParameters.get(j + this.geneCount)!=0.0)
				    degradation *= Math.pow(currentGeneValues.get(j), geneParameters.get(j + this.geneCount));
                int pos = j + this.geneCount;
                //System.out.println("SYN ("+j+"): "+  geneParameters.get(j)+ " DEG ("+pos+"): "+  geneParameters.get(j + this.geneCount) + " SYS acum : " +synthesis+ " DEG acum "+degradation);
                //System.out.println("GEN ("+j+"): "+ currentGeneValues.get(j)+ " DEG ("+pos+"): "+  currentGeneValues.get(j) + " SYS acum : " +synthesis+ " DEG acum "+degradation);
			}

			result.add(synthesis * geneParameters.get(2 * this.geneCount) - degradation * geneParameters.get(2 * this.geneCount + 1)); // systhesis*alpha + degradation*beta
            //System.out.println("SYSN ("+i+"): " +synthesis+ " DEGR " +degradation+ " ALPHA " +geneParameters.get(2 * this.geneCount)+ " BETA " + geneParameters.get(2 * this.geneCount + 1));
		}
		return result;
	}


}
