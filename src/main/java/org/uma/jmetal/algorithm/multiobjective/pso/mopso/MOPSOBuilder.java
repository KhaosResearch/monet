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

package org.uma.jmetal.algorithm.multiobjective.pso.mopso;

import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.AlgorithmBuilder;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.PseudoRandomGenerator;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class MOPSOBuilder implements AlgorithmBuilder<MOPSO> {
  private DoubleProblem problem;

  private double c1;
  private double c2;
  private double weight;
  private double changeVelocity1;
  private double changeVelocity2;
  private double ratioOfArchiveSolutionsForGlobalLeaderSelection;
  private double ratioOfArchiveSolutionsForLocalLeaderSelection ;

  private int swarmSize;
  private int maxIterations;

  protected int archiveSize;

  protected MutationOperator<DoubleSolution> mutationOperator;

  private BoundedArchive<DoubleSolution> archive;

  private SolutionListEvaluator<DoubleSolution> evaluator;

  public MOPSOBuilder(DoubleProblem problem, BoundedArchive<DoubleSolution> archive) {
    this.problem = problem;
    this.archive = archive ;

    swarmSize = 100;
    maxIterations = 250;

    this.c1 = 1.0;
    this.c2 = 1.5;
    this.weight = 0.4;
    this.changeVelocity1 = -1;
    this.changeVelocity2 = -1;

    ratioOfArchiveSolutionsForGlobalLeaderSelection = 2.0 ;
    ratioOfArchiveSolutionsForLocalLeaderSelection = 98.0 ;

    mutationOperator = new PolynomialMutation(1.0/problem.getNumberOfVariables(), 20.0) ;
    evaluator = new SequentialSolutionListEvaluator<DoubleSolution>() ;
  }

  /* Getters */
  public int getSwarmSize() {
    return swarmSize;
  }

  public int getMaxIterations() {
    return maxIterations;
  }

  public MutationOperator<DoubleSolution> getMutation() {
    return mutationOperator;
  }

  public double getChangeVelocity1() {
    return changeVelocity1;
  }

  public double getChangeVelocity2() {
    return changeVelocity2;
  }

  /* Setters */
  public MOPSOBuilder setSwarmSize(int swarmSize) {
    this.swarmSize = swarmSize;

    return this;
  }

  public MOPSOBuilder setMaxIterations(int maxIterations) {
    this.maxIterations = maxIterations;

    return this;
  }

  public MOPSOBuilder setMutation(MutationOperator<DoubleSolution> mutation) {
    mutationOperator = mutation;

    return this;
  }

  public MOPSOBuilder setC1(double c1) {
    this.c1 = c1;

    return this;
  }


  public MOPSOBuilder setC2(double c2) {
    this.c2 = c2;

    return this;
  }

  public MOPSOBuilder setWeight(double weight) {
    this.weight = weight;

    return this;
  }

  public MOPSOBuilder setChangeVelocity1(double changeVelocity1) {
    this.changeVelocity1 = changeVelocity1;

    return this;
  }

  public MOPSOBuilder setChangeVelocity2(double changeVelocity2) {
    this.changeVelocity2 = changeVelocity2;

    return this;
  }

  public MOPSOBuilder setArchiveSize(int size) {
    this.archiveSize = size;

    return this;
  }

  public MOPSOBuilder setRandomGenerator(PseudoRandomGenerator randomGenerator) {
    JMetalRandom.getInstance().setRandomGenerator(randomGenerator);

    return this;
  }

  public MOPSOBuilder setSolutionListEvaluator(SolutionListEvaluator<DoubleSolution> evaluator) {
    this.evaluator = evaluator ;

    return this ;
  }

  public MOPSOBuilder setRatioOfArchiveSolutionsForGlobalLeaderSelection(double ratio) {
    this.ratioOfArchiveSolutionsForGlobalLeaderSelection = ratio ;

    return this ;
  }

  public MOPSOBuilder setRatioOfArchiveSolutionsForLocalLeaderSelection(double ratio) {
    this.ratioOfArchiveSolutionsForLocalLeaderSelection = ratio ;

    return this ;
  }

  public MOPSO build() {
    return new MOPSO(problem, swarmSize, archive, mutationOperator, maxIterations, c1, c2,
            weight, changeVelocity1, changeVelocity2,
            evaluator);
  }

  /*
   * Getters
   */

  public DoubleProblem getProblem() {
    return problem;
  }

  public int getArchiveSize() {
    return archiveSize;
  }

  public MutationOperator<DoubleSolution> getMutationOperator() {
    return mutationOperator;
  }

  public BoundedArchive<DoubleSolution> getLeaders() {
    return archive;
  }

  public SolutionListEvaluator<DoubleSolution> getEvaluator() {
    return evaluator;
  }
}



