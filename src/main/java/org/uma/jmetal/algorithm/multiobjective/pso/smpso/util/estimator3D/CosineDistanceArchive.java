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

package org.uma.jmetal.algorithm.multiobjective.pso.smpso.util.estimator3D;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.archive.impl.AbstractBoundedArchive;
import org.uma.jmetal.util.point.Point;
import org.uma.jmetal.util.solutionattribute.DensityEstimator;

import java.util.Comparator;

/**
 * Created by Antonio J. Nebro on 24/09/14.
 */
public class CosineDistanceArchive<S extends Solution<?>> extends AbstractBoundedArchive<S> {
  private Comparator<S> comparator;
  private DensityEstimator<S> densityEstimator;

  public CosineDistanceArchive(int maxSize, Point referencePoint) {
    super(maxSize);
    comparator = new CosineDistanceComparator<>() ;
    densityEstimator = new CosineDistanceEstimatorB<S>(referencePoint) ;
  }

  @Override
  public void prune() {
    if (getSolutionList().size() > getMaxSize()) {
      computeDensityEstimator();
      S worst = new SolutionListUtils().findWorstSolution(getSolutionList(), comparator) ;
      getSolutionList().remove(worst);
    }
  }

  @Override
  public Comparator<S> getComparator() {
    return comparator;
  }

  @Override
  public void computeDensityEstimator() {
    densityEstimator.computeDensityEstimator(getSolutionList());
  }

  @Override
  public void sortByDensityEstimator() {

  }
}
