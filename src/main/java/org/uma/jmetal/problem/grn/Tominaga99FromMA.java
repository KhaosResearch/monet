package org.uma.jmetal.problem.grn;

import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jose-Manuel on 17/10/2014.
 */
public class Tominaga99FromMA extends AbstractDoubleProblem {
  protected GenericMultipleGeneGrnFromMad grn_problem;

  private double tominagaThreshold = 0.01; // default in Tominaga's paper 99. To be performed every 5 generations

  public GenericMultipleGeneGrnFromMad getGRNProblem() {
    return grn_problem;
  }

  /**
   * Constructor.
   * Creates a default instance of problem ZDT1 (30 decision variables)
   */
  public Tominaga99FromMA() throws ClassNotFoundException {
    this(60, 2, "Ecoli1"); // 12 parametros (5+1+5+1) * 5 genes -> Tominaga
    //this(solutionType, 1860); // 62 parametros (30+1+30+1) * 30 genes -> Tominaga
    //this(solutionType, 220); // 22 parametros (10+1+10+1) * 10 genes -> Tominaga
    //this(solutionType,10200); //  102 parametros (50+1+50+1) * 100 genes -> Tominaga
    //this(solutionType, 220); // 22 parametros (10+1+10+1) * 10 genes -> Tominaga
    //GenericMultipleGeneGrnFromMad grn_problem = new Tominaga99GrnFromMad(solutionType);
  } // ZDT1

  public Tominaga99FromMA(int numberOfObjectives) throws ClassNotFoundException {
    this(60, numberOfObjectives, "Ecoli1"); // 12 parametros (5+1+5+1) * 5 genes -> Tominaga
    //this(solutionType, 1860); // 62 parametros (30+1+30+1) * 30 genes -> Tominaga
    //this(solutionType, 220); // 22 parametros (10+1+10+1) * 10 genes -> Tominaga
    //this(solutionType,10200); //  102 parametros (50+1+50+1) * 100 genes -> Tominaga
    //this(solutionType, 220); // 22 parametros (10+1+10+1) * 10 genes -> Tominaga
    //GenericMultipleGeneGrnFromMad grn_problem = new Tominaga99GrnFromMad(solutionType);
  }

  public Tominaga99FromMA(int numberOfObjectives, String matrixFileName) throws ClassNotFoundException {
    this(60, numberOfObjectives, matrixFileName); // 12 parametros (5+1+5+1) * 5 genes -> Tominaga
    //this(solutionType, 1860); // 62 parametros (30+1+30+1) * 30 genes -> Tominaga
    //this(solutionType, 220); // 22 parametros (10+1+10+1) * 10 genes -> Tominaga
    //this(solutionType,10200); //  102 parametros (50+1+50+1) * 100 genes -> Tominaga
    //this(solutionType, 220); // 22 parametros (10+1+10+1) * 10 genes -> Tominaga
    //GenericMultipleGeneGrnFromMad grn_problem = new Tominaga99GrnFromMad(solutionType);
  }

  /**
   * Creates a new instance of problem ZDT1.
   *
   * @param numberOfVariables Number of variables.
   */
  public Tominaga99FromMA(Integer numberOfVariables, int numberOfObjectives, String matrixFileName) {
    double[][] paramsRange_;
    setNumberOfObjectives(numberOfObjectives);
    setNumberOfConstraints(0);
    setNumberOfVariables(numberOfVariables);
    setName("Tominaga99");

    grn_problem = new Tominaga99GrnFromMad();
    grn_problem.setMadFileName(matrixFileName);
    grn_problem.initProblem();

    paramsRange_ = grn_problem.getParameterRange();
    setNumberOfVariables(paramsRange_.length);
    System.out.println("VARs:" + getNumberOfVariables() + " MA File " + matrixFileName);

    List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables()) ;
    List<Double> upperLimit = new ArrayList<>(getNumberOfVariables()) ;

    for (int i = 0; i < getNumberOfVariables(); i++) {
      lowerLimit.add(paramsRange_[i][0]);
      upperLimit.add(paramsRange_[i][1]);
    }

    setLowerLimit(lowerLimit);
    setUpperLimit(upperLimit);
  }

  /**
   * Evaluates a solution.
   *
   * @param solution The solution to evaluate.
   */
  public void evaluate(DoubleSolution solution) {
    this.tominagaSkeletizing(tominagaThreshold, solution); //0.01
    grn_problem.evaluate(solution);
  }
  public void tominagaSkeletizing(double parameterThreshold, DoubleSolution solution) {
    tominagaThreshold = parameterThreshold;
    for (int var = 0; var < solution.getNumberOfVariables(); var++)
      if (Math.abs(solution.getVariableValue(var)) < tominagaThreshold) // tominagaThreshold
        solution.setVariableValue(var, 0.0);
  }
}
