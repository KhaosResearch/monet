//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
// 
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package org.uma.jmetal.algorithm.multiobjective.pso.omopso.util;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.comparator.ConstraintViolationComparator;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.comparator.impl.OverallConstraintViolationComparator;

/**
 * This class implements a solution comparator taking into account the violation constraints and
 * an optional epsilon value (i.e, implements an epsilon dominance comparator)
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class EpsilonDominanceComparator<S extends Solution<?>> extends DominanceComparator<S> {
  private ConstraintViolationComparator<S> constraintViolationComparator;
  private double epsilon = 0.0 ;

  /** Constructor */
  public EpsilonDominanceComparator() {
    this(new OverallConstraintViolationComparator<S>(), 0.0) ;
  }

  /** Constructor */
  public EpsilonDominanceComparator(double epsilon) {
    this(new OverallConstraintViolationComparator<S>(), epsilon) ;
  }

  /** Constructor */
  public EpsilonDominanceComparator(ConstraintViolationComparator<S> constraintComparator) {
    this(constraintComparator, 0.0) ;
  }

  /** Constructor */
  public EpsilonDominanceComparator(ConstraintViolationComparator<S> constraintComparator, double epsilon) {
    constraintViolationComparator = constraintComparator ;
    this.epsilon = epsilon ;
  }

  /**
   * Compares two solutions.
   *
   * @param solution1 Object representing the first <code>Solution</code>.
   * @param solution2 Object representing the second <code>Solution</code>.
   * @return -1, or 0, or 1 if solution1 dominates solution2, both are
   * non-dominated, or solution1  is dominated by solution2, respectively.
   */
  @Override
  public int compare(S solution1, S solution2) {
    if (solution1 == null) {
      throw new JMetalException("Solution1 is null") ;
    } else if (solution2 == null) {
      throw new JMetalException("Solution2 is null") ;
    } else if (solution1.getNumberOfObjectives() != solution2.getNumberOfObjectives()) {
      throw new JMetalException("Cannot compare because solution1 has " +
          solution1.getNumberOfObjectives()+ " objectives and solution2 has " +
          solution2.getNumberOfObjectives()) ;
    }
    int result ;
    result = constraintViolationComparator.compare(solution1, solution2) ;
    if (result == 0) {
      result = dominanceTest(solution1, solution2) ;
    }

    return result ;
  }

  private int dominanceTest(Solution<?> solution1, Solution<?> solution2) {
    boolean bestIsOne = false ;
    boolean bestIsTwo = false ;
    for (int i = 0; i < solution1.getNumberOfObjectives(); i++) {
      double value1 = Math.floor(solution1.getObjective(i) / epsilon);
      double value2 = Math.floor(solution2.getObjective(i) / epsilon);
      if (value1 < value2) {
        bestIsOne = true;

        if (bestIsTwo) {
          return 0;
        }
      } else if (value2 < value1) {
        bestIsTwo = true;

        if (bestIsOne) {
          return 0 ;
        }
      }
    }
    if (!bestIsOne && !bestIsTwo) {
      double dist1 = 0.0;
      double dist2 = 0.0;

      for (int i = 0; i < solution1.getNumberOfObjectives(); i++) {
        double index1 = Math.floor(solution1.getObjective(i) / epsilon);
        double index2 = Math.floor(solution2.getObjective(i) / epsilon);

        dist1 += Math.pow(solution1.getObjective(i) - index1 * epsilon,
                2.0);
        dist2 += Math.pow(solution2.getObjective(i) - index2 * epsilon,
                2.0);
      }

      if (dist1 < dist2) {
        return -1;
      } else {
        return 1;
      }
    } else if (bestIsTwo) {
      return 1;
    } else {
      return -1;
    }
 /*
    int bestIsOne = 0 ;
    int bestIsTwo = 0 ;
    int result ;
    for (int i = 0; i < solution1.getNumberOfObjectives(); i++) {
      double value1 = solution1.getObjective(i);
      double value2 = solution2.getObjective(i);
      if (value1 != value2) {
        if (value1 / (1.0 + epsilon) < value2) {
          bestIsOne = 1;
        }
        if (value2 / (1.0 + epsilon) < value1) {
          bestIsTwo = 1;
        }
      }
    }
    if (bestIsOne > bestIsTwo) {
      result = -1;
    } else if (bestIsTwo > bestIsOne) {
      result = 1;
    } else {
      result = 0;
    }
    return result ;
  */
    /*
    int result ;
    boolean solution1Dominates = false ;
    boolean solution2Dominates = false ;

    int flag;
    double value1, value2;
    for (int i = 0; i < solution1.getNumberOfObjectives(); i++) {
      value1 = solution1.getObjective(i);
      value2 = solution2.getObjective(i);
      if (value1 / (1 + epsilon) < value2) {
        flag = -1;
      //} else if (value1 / (1 + epsilon) > value2) {
      } else if (value2 / (1 + epsilon) < value1) {
        flag = 1;
      } else {
        flag = 0;
      }

      if (flag == -1) {
        solution1Dominates = true ;
      }

      if (flag == 1) {
        solution2Dominates = true ;
      }
    }

    if (solution1Dominates == solution2Dominates) {
      // non-dominated solutions
      result = 0;
    } else if (solution1Dominates) {
      // solution1 dominates
      result = -1;
    } else {
      // solution2 dominates
      result = 1;
    }
    return result ;
    */
  }
}
