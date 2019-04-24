package org.uma.jmetal.problem.grn;

/**
 * @author Jose Manuel Garcia Nieto, Fernando Moreno Jabato
 */

import org.uma.jmetal.problem.grn.util.*;
import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class is used to instantiate multi-gene problems. It could be used to multi-objective problems or single-objective problems
 */
public abstract class GenericMultipleGeneGrnFromMad extends AbstractDoubleProblem {

	//FIELDS
	protected MicroarrayData mad;
	/*protected AbstractEAIndividual overallBest;*/
	protected int geneParameterCount;
	protected ModelEvaluator evaluator;
	//protected String madFileName = null;
	protected String madFileName = "../SS5GeneratedData0Noise.txt";

	protected int modelType=0; // 0 S-System ; 1 Linear System

	protected double[][] parameterRange;
	protected double[] rateConstantsRange= {0,1};//{0,10}; //// (IRMA) {0,20}
	protected double[] kineticOrdersRange= {0,1};//{0,1};  //{-3,3};  //// (IRMA) {0,2}

	//CONSTRUCTORS
	/**
	 * Instantiate a GenericMultipleGeneGrnFromMad object using default values:
	 *	numberOfVariables_   = 1860;
	 *  numberOfObjectives_  = 2;
	 *  numberOfConstraints_ = 0;
	 *  solutionType_ = RealSolutionType;
	 */
	public GenericMultipleGeneGrnFromMad(){
	}

	/**
	 * Instantiate a GenericMultipleGeneGrnFromMad object
	 * @param args: [0]->numberOfVariables_; [1]->numberOfObjectives_; [2]->numberOfConstraints_
	 */
	public GenericMultipleGeneGrnFromMad(int[] args){
		if(args != null && args.length < 3) throw new IllegalArgumentException("Illegar arguments (int[])");

		setNumberOfVariables(args == null? 1860 : args[0]);
		setNumberOfObjectives(args == null? 2 : args[1]);
		setNumberOfConstraints(args == null? 0 : args[2]);

		List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables()) ;
		List<Double> upperLimit = new ArrayList<>(getNumberOfVariables()) ;

		for (int i = 0; i < getNumberOfVariables(); i++) {
			lowerLimit.add(-1.0);
			upperLimit.add(+1.0);
		}

        setLowerLimit(lowerLimit);
        setUpperLimit(upperLimit);
	}

	/**
	 * Create an object using an already instantiate GenericMultipleGeneGrnFromMad object.
	 * @param instance: it's the already instantiate object.
	 */
	public GenericMultipleGeneGrnFromMad(GenericMultipleGeneGrnFromMad instance) {
		this.mad=instance.mad;
		this.evaluator=instance.evaluator;
		this.geneParameterCount=instance.geneParameterCount;
		this.madFileName= instance.madFileName;
	}

	//METHODS
	@Override
	public abstract Object clone();

	/**
	 * Evaluate the solution given.
	 * @param solution: the solution that will be evaluated.
	 */
	@Override
	public void evaluate(DoubleSolution solution) {
		if(solution.getNumberOfObjectives() > 1)
			this.evaluateMultiObjective(solution);
		else
			this.evaluateSingleObjective(solution);
	}


	/**
	 * Evaluate a multi-objective solution given
	 * @param solution: the solution that will be evaluated.
	 */
	public void evaluateMultiObjective(DoubleSolution solution) {

		//create model using parameters encoded in individual
		//Variable[] decisionVariables  = solution.getDecisionVariables();
		double[] parametersArray = new double[getNumberOfVariables()] ;

		for (int i = 0 ; i < solution.getNumberOfVariables(); i++)
			parametersArray[i] = solution.getVariableValue(i) ;

		ArrayList<ArrayList<Double>> parameters = new ArrayList<ArrayList<Double>>();
		int geneParameterCount = parametersArray.length / this.getGeneCount();

		for (int i = 0; i < this.getGeneCount(); i++) {
			ArrayList<Double> geneParameters = new ArrayList<Double>();
			for (int j = 0; j < geneParameterCount; j++)
				geneParameters.add(parametersArray[i * geneParameterCount + j]);

			parameters.add(geneParameters);
		}
		GRNContinuousModel model;
		model = (this.modelType == 0) ? (new GRNSSystemModel(parameters))
						: (new GRNLinearModel(parameters));

		//evaluate model
		double[] fitness=new double[2]; //new double[2];
		fitness[0]=((CompleteGeneModelEvaluator) this.evaluator).evaluate(model);
		//fitness[1]=regularize(parameters);
		fitness[1]=regularizeNomanIba(parameters);
        System.out.println(fitness[0]+"  "+fitness[1]);

		if (Double.isInfinite(fitness[0]) || Double.isNaN(fitness[0]))
			fitness[0] = Double.MAX_VALUE / 2;
		solution.setObjective(0, fitness[0]);
		solution.setObjective(1, fitness[1]);
	}


	/**
	 * Evaluate a multi-objective solution given
	 * @param solution: the solution that will be evaluated.
	 */
	public void evaluateSingleObjective(DoubleSolution solution) {

		//create model using parameters encoded in individual
		double[] parametersArray = new double[solution.getNumberOfVariables()] ;

		for (int i = 0 ; i < solution.getNumberOfVariables(); i++)
			parametersArray[i] = solution.getVariableValue(i) ;

		ArrayList<ArrayList<Double>> parameters = new ArrayList<ArrayList<Double>>();
		int geneParameterCount = parametersArray.length / this.getGeneCount();

		for (int i = 0; i < this.getGeneCount(); i++) {
			ArrayList<Double> geneParameters = new ArrayList<Double>();
			for (int j = 0; j < geneParameterCount; j++)
				geneParameters.add(parametersArray[i * geneParameterCount + j]);

			parameters.add(geneParameters);
		}
		GRNContinuousModel model;
		model = (this.modelType == 0) ? (new GRNSSystemModel(parameters))
						: (new GRNLinearModel(parameters));

		//evaluate model
		double[] fitness=new double[1];
		fitness[0]=((CompleteGeneModelEvaluator) this.evaluator).evaluate(model);

		if (Double.isInfinite(fitness[0]) || Double.isNaN(fitness[0]))
			fitness[0] = Double.MAX_VALUE / 2;

		solution.setObjective(0, fitness[0]);
	}//evaluate


	/**
	 * @return the evaluator that is being used.
	 */
	public ModelEvaluator getEvaluator() {
		return evaluator;
	}


	/**
	 * @return the gene count of the microarray that is being used.
	 */
	public int getGeneCount(){
		return this.mad.getGeneCount();
	}


	/**
	 * @return the kinetic orders range that is being used.
	 */
	public double[] getKineticOrdersRange() {
		return kineticOrdersRange;
	}


	/**
	 * @return the File that is being used.
	 */
	public String getMadFileName() {
		return madFileName;
	}


	/**
	 * @return the model type that is being used.
	 */
	public int getModelType() {
		return modelType;
	}


	/**
	 * @return parameter range that is being used.
	 */
	public double[][] getParameterRange() {
		return parameterRange;
	}


	/**
	 * @return rate constants range that is being used.
	 */
	public double[] getRateConstantsRange() {
		return rateConstantsRange;
	}


	/**
	 * Evaluator and range must be initialized before this method is used. This method only
	 * works if individuals implement InterfaceDataTypeDouble
	 */
	public void initProblem() {
		/*if(madFileName == null){
			JFileChooser jfch = new JFileChooser();
			jfch.showOpenDialog(null);
			madFileName = jfch.getSelectedFile().getAbsolutePath();
		}*/

		this.mad = new MicroarrayData(this.madFileName);
		this.mad.initMadFromFile();

		this.geneParameterCount = (modelType==0)?((this.getGeneCount()+1)*2): ((modelType==1)?(this.getGeneCount()):(this.getGeneCount()+3));

		//this.numberOfVariables_  = this.geneParameterCount*this.getGeneCount();
        setNumberOfVariables(this.geneParameterCount*this.getGeneCount());
	}


	/* Equation 13 in paper TEC Palafox 2013*/
	protected double regularize(ArrayList<ArrayList<Double>> params) {
		double result = 0;

        for (int i = 0; i < mad.getGeneCount(); i++) {
            ArrayList<Double> geneParameters = new ArrayList<Double>();
            for (int j=0; j < params.get(0).size() - 2; j++) {
                geneParameters.add(params.get(i).get(j));
            }//for

		    for (int j = 0; j <mad.getGeneCount();  j++) {
                /* sum abs(gij+hij)*/
                result += Math.abs(geneParameters.get(j));
                if (i!=j)
                    result += Math.abs(geneParameters.get(mad.getGeneCount() + j));
            }// 2º for
		}

		if (result < 0.0001) // 0.001
			result=1.0;
		return result;
	}

	/* Equation 13 in paper TCBB Noman Iba 2007*/
	protected double regularizeNomanIba(ArrayList<ArrayList<Double>> params) {
		double result = 0;
		double penalty_term = 1.0; //
		//int cardinality_degree = (params.get(0).size() - 2); // I = (2N+2) - 2 - 4
		int cardinality_degree = 3; //mad.getGeneCount();

		//System.out.println("car " + cardinality_degree);

		for (int i = 0; i < mad.getGeneCount(); i++) {
            /* arreglar esto para no meter los constant rate *****/
			ArrayList<Double> geneParameters = new ArrayList<Double>();
			for (int j=0; j < params.get(0).size() - 2; j++) {
				geneParameters.add(Math.abs(params.get(i).get(j)));
			}//for
            /* sort abs(geneParameters) in ascensing order */
			Collections.sort(geneParameters);
			//for (int j=0; j<(2*mad.getGeneCount()-cardinality_degree); j++) {
            for (int j=0; j<geneParameters.size()-cardinality_degree; j++){
            //for (int j=cardinality_degree; 0<j; j--) {
                result += Math.abs(geneParameters.get(j));
			}// for
            //System.out.println("\n RESULT "+result);
		}

        if (result < 0.0000001) // 0.001
            return 1.0;
        else
		    return result*penalty_term;
	}

	/**
	 * @param evaluator: the evaluator that will be used.
	 */
	public void setEvaluator(ModelEvaluator evaluator) {
		this.evaluator = evaluator;
	}


	/**
	 * @param kineticOrdersRange: kinetic orders range that will be used. Must be [lower, higher]
	 */
	public void setKineticOrdersRange(double[] kineticOrdersRange) {
		this.kineticOrdersRange = kineticOrdersRange;
	}


	/**
	 */
	public void setMadFileName() {
		this.madFileName = madFileName;
	}

	public void setMadFileName(String madFileName ) {
		this.madFileName = madFileName;
	}


	/**
	 * @param modelType: model type that will be used.
	 */
	public void setModelType(int modelType) {
		this.modelType = modelType;
	}


	/**
	 * @param paramRange: the parameter range that will be used. Must be [lower, higher]
	 */
	public void setParameterRange(double[][] paramRange){
		this.parameterRange = paramRange;
	}


	/**
	 * @param rateConstantsRange: rate constants range that will be used. Must be [lower, higher]
	 */
	public void setRateConstantsRange(double[] rateConstantsRange) {
		this.rateConstantsRange = rateConstantsRange;
	}

}//END CLASS

