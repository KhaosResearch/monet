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

import org.uma.jmetal.algorithm.AbstractParticleSwarmOptimization;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.solutionattribute.impl.GenericSolutionAttribute;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * This class implements the MOPSO algorithm described in:
 * Handling Multiple Objectives with Particle Swarm Optimization. IEEE TECV 2004
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class MOPSO extends AbstractParticleSwarmOptimization<DoubleSolution, List<DoubleSolution>> {
  private DoubleProblem problem;

  private double c1;
  private double c2;
  private double weight;
  private double changeVelocity1;
  private double changeVelocity2;

  private int swarmSize;
  private int maxIterations;
  private int iterations;

  private GenericSolutionAttribute<DoubleSolution, DoubleSolution> localBest;
  private double[][] speed;

  private JMetalRandom randomGenerator;

  private BoundedArchive<DoubleSolution> archive;
  private Comparator<DoubleSolution> dominanceComparator;

  private MutationOperator<DoubleSolution> mutation;

  private SolutionListEvaluator<DoubleSolution> evaluator;

  /**
   * Constructor
   */
  public MOPSO(DoubleProblem problem, int swarmSize, BoundedArchive<DoubleSolution> archive,
               MutationOperator<DoubleSolution> mutationOperator, int maxIterations,
               double c1, double c2, double weight, double changeVelocity1, double changeVelocity2,
               SolutionListEvaluator<DoubleSolution> evaluator) {
    this.problem = problem;
    this.swarmSize = swarmSize;
    this.archive = archive;
    this.mutation = mutationOperator;
    this.maxIterations = maxIterations;

    this.c1 = c1;
    this.c2 = c2;
    this.weight = weight;
    this.changeVelocity1 = changeVelocity1;
    this.changeVelocity2 = changeVelocity2;

    randomGenerator = JMetalRandom.getInstance();
    this.evaluator = evaluator;

    dominanceComparator = new DominanceComparator<DoubleSolution>();
    localBest = new GenericSolutionAttribute<DoubleSolution, DoubleSolution>() ;
    speed = new double[swarmSize][problem.getNumberOfVariables()];

  }

  protected void updateLeadersDensityEstimator() {
    archive.computeDensityEstimator();
  }

  @Override protected void initProgress() {
    iterations = 1;
    updateLeadersDensityEstimator();
  }

  @Override protected void updateProgress() {
    iterations += 1;
    updateLeadersDensityEstimator();
  }

  @Override protected boolean isStoppingConditionReached() {
    return iterations >= maxIterations;
  }

  @Override protected List<DoubleSolution> createInitialSwarm() {
    List<DoubleSolution> swarm = new ArrayList<>(swarmSize);

    DoubleSolution newSolution;
    for (int i = 0; i < swarmSize; i++) {
      newSolution = problem.createSolution();
      swarm.add(newSolution);
    }

    return swarm;
  }

  @Override protected List<DoubleSolution> evaluateSwarm(List<DoubleSolution> swarm) {
    swarm = evaluator.evaluate(swarm, problem);

    return swarm;
  }

  @Override protected void initializeGlobalBest(List<DoubleSolution> swarm) {
    for (DoubleSolution particle : swarm) {
      archive.add(particle);
    }
  }

  @Override protected void initializeVelocity(List<DoubleSolution> swarm) {
    for (int i = 0; i < swarm.size(); i++) {
      for (int j = 0; j < problem.getNumberOfVariables(); j++) {
        speed[i][j] = 0.0;
      }
    }
  }

  @Override protected void initializeParticleBest(List<DoubleSolution> swarm) {
    for (DoubleSolution particle : swarm) {
      localBest.setAttribute(particle, (DoubleSolution) particle.copy());
    }
  }

  @Override protected void updateVelocity(List<DoubleSolution> swarm) {
    double r1, r2 ;
    DoubleSolution bestGlobal;

    for (int i = 0; i < swarm.size(); i++) {
      DoubleSolution particle = (DoubleSolution) swarm.get(i).copy();
      DoubleSolution bestParticle = (DoubleSolution) localBest.getAttribute(swarm.get(i)).copy();

      bestGlobal = selectGlobalBest();

      r1 = randomGenerator.nextDouble();
      r2 = randomGenerator.nextDouble();

      for (int var = 0; var < particle.getNumberOfVariables(); var++) {
        speed[i][var] =  weight * speed[i][var] +
                c1 * r1 * (bestParticle.getVariableValue(var) - particle.getVariableValue(var)) +
                c2 * r2 * (bestGlobal.getVariableValue(var) - particle.getVariableValue(var));
      }
    }
  }

  @Override protected void updatePosition(List<DoubleSolution> swarm) {
    for (int i = 0; i < swarmSize; i++) {
      DoubleSolution particle = swarm.get(i);
      for (int j = 0; j < particle.getNumberOfVariables(); j++) {
        particle.setVariableValue(j, particle.getVariableValue(j) + speed[i][j]);

        if (particle.getVariableValue(j) < problem.getLowerBound(j)) {
          particle.setVariableValue(j, problem.getLowerBound(j));
          speed[i][j] = speed[i][j] * changeVelocity1;
        }
        if (particle.getVariableValue(j) > problem.getUpperBound(j)) {
          particle.setVariableValue(j, problem.getUpperBound(j));
          speed[i][j] = speed[i][j] * changeVelocity2;
        }
      }
    }
  }

  @Override protected void perturbation(List<DoubleSolution> swarm) {
    for (int i = 0; i < swarm.size(); i++) {
      if ((i % 6) == 0) {
        mutation.execute(swarm.get(i));
      }
    }
  }

  @Override protected void updateGlobalBest(List<DoubleSolution> swarm) {
    for (DoubleSolution particle : swarm) {
      archive.add((DoubleSolution)particle.copy());
    }
  }

  @Override protected void updateParticleBest(List<DoubleSolution> swarm) {
    for (int i = 0; i < swarm.size(); i++) {
      int flag = dominanceComparator.compare(swarm.get(i), localBest.getAttribute(swarm.get(i)));
      if (flag != 1) {
        DoubleSolution particle = (DoubleSolution) swarm.get(i).copy();
        localBest.setAttribute(swarm.get(i), particle);
      }
    }
  }

  @Override public List<DoubleSolution> getResult() {
    return archive.getSolutionList();
  }

  protected DoubleSolution selectGlobalBest() {
    DoubleSolution one, two;
    DoubleSolution bestGlobal;
    int pos1 = randomGenerator.nextInt(0, archive.getSolutionList().size() - 1);
    int pos2 = randomGenerator.nextInt(0, archive.getSolutionList().size() - 1);
    one = archive.getSolutionList().get(pos1);
    two = archive.getSolutionList().get(pos2);

    if (archive.getComparator().compare(one, two) < 1) {
      bestGlobal = (DoubleSolution) one.copy();
    } else {
      bestGlobal = (DoubleSolution) two.copy();
    }

    return bestGlobal;
  }

  @Override public String getName() {
    return "MOPSO" ;
  }

  @Override public String getDescription() {
    return "Original MOPSO algorithm" ;
  }
}
