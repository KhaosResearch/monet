package org.uma.jmetal.problem.grn;

/*import wsi.ra.math.RNG;*/

/*import eva2.server.go.PopulationInterface;
import eva2.server.go.individuals.AbstractEAIndividual;
import eva2.server.go.individuals.GAIndividualDoubleData;
import eva2.server.go.operators.mutation.MutateESStandard;
import eva2.server.go.operators.mutation.dcu.TominagaSkeletalisingMutator;
import eva2.server.go.populations.Population;
import eva2.server.go.problems.AbstractOptimizationProblem;
import eva2.server.go.strategies.InterfaceOptimizer;
import eva2.server.go.individuals.*;
import eva2.server.go.operators.crossover.*;
*/

import org.uma.jmetal.problem.grn.util.CompleteSumOfSquaredProcentualErrors;

import java.util.ArrayList;
import java.util.List;

public class Tominaga99GrnFromMad extends GenericMultipleGeneGrnFromMad {

	protected double skeletalisingThreshold;
	
	public double getSkeletalisingThreshold() {
		return skeletalisingThreshold;
	}

	public void setSkeletalisingThreshold(double skeletalisingThreshold) {
		this.skeletalisingThreshold = skeletalisingThreshold;
	}

	public Tominaga99GrnFromMad() {
		this.skeletalisingThreshold=0.01;
    setNumberOfVariables(1860);
    setNumberOfObjectives(2);
    setNumberOfConstraints(0);

    List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables()) ;
    List<Double> upperLimit = new ArrayList<>(getNumberOfVariables()) ;

    for (int i = 0; i < getNumberOfVariables(); i++) {
      lowerLimit.add(-1.0);
      upperLimit.add(1.0);
    }

    setLowerLimit(lowerLimit);
    setUpperLimit(upperLimit);
	}

	public Tominaga99GrnFromMad(Tominaga99GrnFromMad instance) {
		super(instance);
		this.skeletalisingThreshold = instance.skeletalisingThreshold;
		setNumberOfVariables(1860);
		setNumberOfObjectives(2);
		setNumberOfConstraints(0);

		List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables()) ;
		List<Double> upperLimit = new ArrayList<>(getNumberOfVariables()) ;

		for (int i = 0; i < getNumberOfVariables(); i++) {
			lowerLimit.add(-1.0);
			upperLimit.add(1.0);
		}

		setLowerLimit(lowerLimit);
		setUpperLimit(upperLimit);
	}
	
	@Override
	public String getName() {
		return "Tominaga99FromMA GRN inference";
	}


	@Override
	public void initProblem() {
		super.initProblem();
		this.parameterRange = new double[this.geneParameterCount*this.getGeneCount()][2];
		for (int i = 0; i < this.getGeneCount(); i++) {
			for (int j = 0; j < this.geneParameterCount; j++) {
				if (j <  this.geneParameterCount-2) {
                    /*if ((j > (this.geneParameterCount-2)/2) && (i==j)){
                        this.parameterRange[j + i * this.geneParameterCount][0] = 0;
                        this.parameterRange[j + i * this.geneParameterCount][1] = this.kineticOrdersRange[1];
                    }else {
                        if (i==j){
                            this.parameterRange[j + i * this.geneParameterCount][0] = 0;
                            this.parameterRange[j + i * this.geneParameterCount][1] = 0;
                        }*/
                        this.parameterRange[j + i * this.geneParameterCount][0] = this.kineticOrdersRange[0];
                        this.parameterRange[j + i * this.geneParameterCount][1] = this.kineticOrdersRange[1];
                    //}
				} else {
					this.parameterRange [ j+i*this.geneParameterCount][0] = this.rateConstantsRange[0];
					this.parameterRange [ j+i*this.geneParameterCount][1] = this.rateConstantsRange[1];
				}
			}
		}
		
		this.evaluator= new CompleteSumOfSquaredProcentualErrors();
		this.evaluator.setMad(mad);
		/*((ESIndividualDoubleData) this.m_Template).SetDoubleRange(this.parameterRange);*/
		
		/*this.TominagaSkeletalisingMutator(this.skeletalisingThreshold);*/
		
		/*((ESIndividualDoubleData) this.m_Template).setMutationOperator(mutator);*/
	}



	public String globalInfo() {
		return "Find continuous model (S-System or Linear) parameters for GRN from microarray timeseries data with simple GA approach and skeletalising mutation";
	}


	public boolean isMultiObjective() {
		return false;
	}


	public Object clone() {
		Tominaga99GrnFromMad result = new Tominaga99GrnFromMad(this);

		return result;
	}

    /*
	@Override
	public String getStringRepresentationForProblem(InterfaceOptimizer arg0) {
		
		return null;
	}
	*/
	
	/*TODO
	@Override
	public void initPopulation(Population population) {
		
		super.initPopulation(population);
		double[] solution= {-0.5,0,2,0,-1,2,-0.5,0,0,0,0,-1,-0.5,0,0,0,0,2,-0.5,-1,0,0,0,2,-0.5};
		((ESIndividualDoubleData) population.get(0)).SetDoubleGenotype(solution);
	}

	@Override
	public void initPopulation(Population population) {
		
		super.initPopulation(population);
	double[] solution= {0, 0, 1, 0, -0.1, 2, 0, 0, 0, 0, 15, 10,
			2, 0, 0, 0, 0, 0, 2, 0, 0, 0, 10, 10,
			0, -0.1, 0, 0, 0, 0, -0.1, 2, 0, 0, 10, 10,
			0, 2, 0, 0, -1, 0, 0, 0, 2, 0, 8, 10,
			0, 0, 0, 2, 0, 0, 0, 0, 0, 2, 10, 10};
	((ESIndividualDoubleData) population.get(0)).SetDoubleGenotype(solution);
	}*/
}
