package org.uma.jmetal.algorithm.multiobjective.pso.smpso.util;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.ObjectiveComparator;
import org.uma.jmetal.util.distance.impl.CosineDistanceBetweenSolutionsInObjectiveSpace;
import org.uma.jmetal.util.solutionattribute.DensityEstimator;
import org.uma.jmetal.util.solutionattribute.impl.GenericSolutionAttribute;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by ajnebro on 9/2/16.
 */
public class CosineDistanceEstimator2<S extends Solution<?>>
    extends GenericSolutionAttribute<S, Double> implements DensityEstimator<S> {

  /**
   * Assigns crowding distances to all solutions in a <code>SolutionSet</code>.
   *
   * @param solutionList The <code>SolutionSet</code>.
   * @throws org.uma.jmetal.util.JMetalException
   */

  @Override
  public void computeDensityEstimator(List<S> solutionList) {
    int size = solutionList.size();

    if (size == 0) {
      return;
    }

    if (size == 1) {
      solutionList.get(0).setAttribute(getAttributeID(), Double.MIN_VALUE);
      return;
    }

    int numberOfObjectives = solutionList.get(0).getNumberOfObjectives() ;

    if (size == numberOfObjectives) {
      for (S solution : solutionList) {
        solution.setAttribute(getAttributeID(), Double.MIN_VALUE);
      }

      return;
    }

    // OJO: Funciona para dos objetivos!!!!!


    S idealPoint = (S)solutionList.get(0).copy() ;
    for (int i = 0; i < idealPoint.getNumberOfObjectives(); i++) {
      idealPoint.setObjective(i,  1.0e+30);
    }

    List<S> front = new ArrayList<>(size);
    for (S solution : solutionList) {
      front.add(solution);
      idealPoint = updateIdealPoint(solution, idealPoint) ;
    }

    for (int i = 0; i < size; i++) {
      front.get(i).setAttribute(getAttributeID(), Double.MAX_VALUE);
    }

    CosineDistanceBetweenSolutionsInObjectiveSpace<S> distance ;
    distance = new CosineDistanceBetweenSolutionsInObjectiveSpace<S>(idealPoint) ;

    for (int i = 0; i < numberOfObjectives; i++) {
      Collections.sort(front, new ObjectiveComparator<S>(i));
      front.get(0).setAttribute(getAttributeID(), Double.MIN_VALUE);
    }

    for (int i = 0; i < front.size(); i++) {
      if ((Double)front.get(i).getAttribute(getAttributeID()) != Double.MIN_VALUE) {
        double maxValue = 0.0;
        for (int j = 0; j < front.size(); j++) {
          if (i != j) {
            double sumCosines = distance.getDistance(front.get(i), front.get(j));
            if (sumCosines > maxValue) {
              maxValue = sumCosines;
            }
          }
        }
        front.get(i).setAttribute(getAttributeID(), maxValue);
      }
    }
/*
    for (int i = 1; i < (front.size() - 1); i++) {
      double d1 = distance.getDistance(front.get(i-1), front.get(i)) ;
      double d2 = distance.getDistance(front.get(i), front.get(i+1)) ;
      double sumCosines =  d1 + d2;
      front.get(i).setAttribute(getAttributeID(), sumCosines);
    }
*/
    //Collections.sort(front, new CosineDistanceComparator<S>()) ;
  }

  public Object getAttributeID() {
    return this.getClass() ;
  }

  protected S updateIdealPoint(S individual, S idealPoint) {
    S point = (S)idealPoint.copy() ;

    for (int n = 0; n < individual.getNumberOfObjectives(); n++) {
      if (individual.getObjective(n) < point.getObjective(n)) {
        point.setObjective(n, individual.getObjective(n));
      }
    }

    return point ;
  }
}
