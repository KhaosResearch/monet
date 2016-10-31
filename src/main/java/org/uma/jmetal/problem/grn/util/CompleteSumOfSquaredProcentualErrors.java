package org.uma.jmetal.problem.grn.util;

import java.util.ArrayList;

public class CompleteSumOfSquaredProcentualErrors extends CompleteGeneModelEvaluator {

    /* evaluate corresponds to SquaredProcentualErrors-> Objective 1*/
	@Override
	protected double evaluate(ArrayList<ArrayList<ArrayList<Double>>> simulatedData) {
		double result=0.0;
        double experimental_data = 0.0;

		for(int k=0;k<this.mad.getTimeSeriesCount();k++)
		{
                ArrayList<ArrayList<Double>> difference = MathUtils.AddMatrix(simulatedData.get(k), MathUtils.MultiplyMatrix(this.mad.getMad().get(k), -1));
                //System.out.println("TS : " + k);
                //System.out.println("GENES : " + mad.getGeneCount() + " timespans " + mad.getTimeSpans().get(k).size());
                //for (int i = 0; i < mad.getGeneCount(); i++) {
                int i = 0;
                while ((i<mad.getGeneCount()) && !Double.isNaN(result)){
                    //System.out.println("GENE : " + i + " TS : " + k);
                    for (int j = 0; j < mad.getTimeSpans().get(k).size(); j++) {
                        //experimental_data = this.mad.getMad().get(k).get(i).get(j);
                        // checks that no experimental data from time series is 0 in order to avoid zero division
                        //if (experimental_data == 0.0)
                        //    experimental_data = 1e-05;
                        if ((this.mad.getMad().get(k).get(i).get(j) != 0.0) && (difference.get(i).get(j) != 0.0)) {
                            result = result + Math.pow((difference.get(i).get(j) / this.mad.getMad().get(k).get(i).get(j)), 2);
                            //System.out.println("RESULTS : " + result + " DIFF : " + difference.get(i).get(j) + " GETMAD " + this.mad.getMad().get(k).get(i).get(j) + " SOL " + simulatedData.get(k).get(i).get(j));
                            //System.out.println("MAD " + this.mad.getMad().get(k).get(i).get(j) + "  SIM " + simulatedData.get(k).get(i).get(j));
                            //System.out.print(simulatedData.get(k).get(i).get(j) + " ");
                        }
                    }
                    //System.out.print("\n");
                    i++;
                }
                result = result / (mad.getTimeSpans().get(k).size() );
	    }

        //System.out.println("GLOBAL RESULTS : " + result);

        return result;
	}



}
