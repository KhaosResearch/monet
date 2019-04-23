package org.uma.jmetal.algorithm.multiobjective.pso.omopso.util;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.point.impl.IdealPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ajnebro on 18/1/17.
 */
public class SelectSubListOfSolutions<S extends Solution<?>> {
  private int numberOfObjectives ;
  private IdealPoint idealPoint;

  public SelectSubListOfSolutions(int numberOfObjectives) {
    this.numberOfObjectives = numberOfObjectives ;
    idealPoint = new IdealPoint(numberOfObjectives) ;
  }

  private void updateIdealPoint(List<S> list) {
    for (S solution : list) {
      idealPoint.update(solution.getObjectives());
    }
  }

  public List<S> selectSubList(List<S> list, int size) {
    List<S> resultList = new ArrayList<>(size);
    updateIdealPoint(list);

    if (numberOfObjectives == 2) { // subcase 1
      double[][] internLambda = new double[size][2];
      for (int i = 0; i < size; i++) {
        double a = 1.0 * i / (size - 1);
        internLambda[i][0] = a;
        internLambda[i][1] = 1 - a;
      } // for

      // we have now the weights, now select the best solution for each of them
      for (int i = 0; i < size; i++) {
        S currentBest = list.get(0);
        double value = fitnessFunction(currentBest, internLambda[i]);
        for (int j = 1; j < size; j++) {
          double aux = fitnessFunction(list.get(j), internLambda[i]); // we are looking the best for the weight i
          if (aux < value) { // solution in position j is better!
            value = aux;
            currentBest = list.get(j);
          }
        }
        resultList.add((S) currentBest.copy());
      }

    }
    return resultList;
  }

  private double fitnessFunction(S solution, double[] lambda) {
    double fitness;

      double maxFun = -1.0e+30;

      for (int n = 0; n < numberOfObjectives; n++) {
        double diff = Math.abs(solution.getObjective(n) - idealPoint.getValue(n));

        double feval;
        if (lambda[n] == 0) {
          feval = 0.0001 * diff;
        } else {
          feval = diff * lambda[n];
        }
        if (feval > maxFun) {
          maxFun = feval;
        }
      }

      fitness = maxFun;
    return fitness;
  }
}
