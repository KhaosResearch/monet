
package org.uma.jmetal.algorithm.multiobjective.pso.smpso.util.estimator3D;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.ObjectiveComparator;
import org.uma.jmetal.util.distance.impl.CosineDistanceBetweenSolutionsInObjectiveSpace;
import org.uma.jmetal.util.point.Point;
import org.uma.jmetal.util.solutionattribute.DensityEstimator;
import org.uma.jmetal.util.solutionattribute.impl.GenericSolutionAttribute;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by ajnebro on 9/2/16.
 */
public class CosineDistanceEstimator<S extends Solution<?>>
    extends GenericSolutionAttribute<S, Double> implements DensityEstimator<S> {

  private Point referencePoint ;

  public CosineDistanceEstimator() {
  }

  public CosineDistanceEstimator(Point referencePoint) {
    this.referencePoint = referencePoint ;
  }

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
      solutionList.get(0).setAttribute(getAttributeID(), 0.0);
      return;
    }

    int numberOfObjectives = solutionList.get(0).getNumberOfObjectives() ;

    if (size == numberOfObjectives) {
      for (S solution : solutionList) {
        solution.setAttribute(getAttributeID(), 0.0);
      }

      return;
    }

    // OJO: Funciona para dos objetivos!!!!!

    List<S> front = new ArrayList<>(size);
    for (S solution : solutionList) {
      front.add(solution);
      referencePoint.update(solution.getObjectives()) ;
    }

    for (int i = 0; i < numberOfObjectives; i++) {
      Collections.sort(front, new ObjectiveComparator<S>(i));
      front.get(0).setAttribute(getAttributeID(), Double.MIN_VALUE);
    }

    //Collections.sort(front, new ObjectiveComparator<S>(0));

    for (int i = 0; i < size; i++) {
      front.get(i).setAttribute(getAttributeID(), 0.0);
    }

    CosineDistanceBetweenSolutionsInObjectiveSpace<S> distance ;
    distance = new CosineDistanceBetweenSolutionsInObjectiveSpace<S>((S)referencePoint) ;

    for (int i = 1; i < (front.size() - 1); i++) {
      double d1 = distance.getDistance(front.get(i-1), front.get(i)) ;
      double d2 = distance.getDistance(front.get(i), front.get(i+1)) ;
      double sumCosines =  d1 + d2;
      front.get(i).setAttribute(getAttributeID(), sumCosines);
    }
  }

  public Object getAttributeID() {
    return this.getClass() ;
  }
}
