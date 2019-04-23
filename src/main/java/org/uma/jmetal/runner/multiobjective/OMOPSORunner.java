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

package org.uma.jmetal.runner.multiobjective;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.pso.omopso.OMOPSOBuilder;
import org.uma.jmetal.operator.impl.mutation.NonUniformMutation;
import org.uma.jmetal.operator.impl.mutation.UniformMutation;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.ProblemUtils;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

import java.util.List;

/**
 * Class for configuring and running the OMOPSO algorithm
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */

public class OMOPSORunner extends AbstractAlgorithmRunner {
  /**
   * @param args Command line arguments.
   * @throws SecurityException Invoking command: java org.uma.jmetal.runner.multiobjective.OMOPSORunner
   *                           problemName [referenceFront]
   */
  public static void main(String[] args) throws Exception {
    DoubleProblem problem;
    Algorithm<List<DoubleSolution>> algorithm;

    String referenceParetoFront = "";

    String variant = "epsilonArchive" ;

    String problemName;
    if (args.length == 1) {
      problemName = args[0];
    } else if (args.length == 2) {
      problemName = args[0];
      referenceParetoFront = args[1];
      variant = "epsilonArchive";
    } else if (args.length == 3) {
      problemName = args[0];
      referenceParetoFront = args[1];
      variant = args[2];
    } else {
      problemName = "org.uma.jmetal.problem.multiobjective.dtlz.DTLZ7";
      referenceParetoFront = "/pareto_fronts/DTLZ1.3D.pf";
    }

    problem = (DoubleProblem) ProblemUtils.<DoubleSolution>loadProblem(problemName);

    double mutationProbability = 1.0 / problem.getNumberOfVariables();

    double epsilon = 0.0075;

    BoundedArchive<DoubleSolution> archive = new CrowdingDistanceArchive<>(100);
    //new HypervolumeArchive<DoubleSolution>(100, new PISAHypervolume<DoubleSolution>()) ;

    //variant = "epsilonArchive" ;
    //variant = "crowdingArchive" ;

    algorithm = new OMOPSOBuilder(
            problem,
            epsilon,
            new SequentialSolutionListEvaluator<DoubleSolution>(),
            archive,
            variant)
            .setMaxIterations(250)
            .setSwarmSize(100)
            .setArchiveSize(100)
            .setUniformMutation(new UniformMutation(mutationProbability, 0.5))
            .setNonUniformMutation(new NonUniformMutation(mutationProbability, 0.5, 250))
            .build();

    AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
            .execute();

    List<DoubleSolution> population = algorithm.getResult();
    long computingTime = algorithmRunner.getComputingTime();

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");

    JMetalLogger.logger.info("Final population size: " + population.size());
    printFinalSolutionSet(population);
    if (!referenceParetoFront.equals("")) {
      printQualityIndicators(population, referenceParetoFront);
    }

    /*
    SelectSubListOfSolutions<DoubleSolution> selector =
            new SelectSubListOfSolutions<>(problem.getNumberOfObjectives()) ;
    List<DoubleSolution> list2 = selector.selectSubList(population, 100) ;
*/
    /*
    HypervolumeArchive<DoubleSolution> archive1 = new HypervolumeArchive<>(100, new PISAHypervolume<DoubleSolution>());
    CrowdingDistanceArchive<DoubleSolution> archive2 = new CrowdingDistanceArchive<>(100);
    for (DoubleSolution solution : population) {
      archive1.add(solution);
      archive2.add(solution);
    }
    new SolutionListOutput(archive1.getSolutionList())
            .setSeparator("\t")
            .setVarFileOutputContext(new DefaultFileOutputContext("VAR2.tsv"))
            .setFunFileOutputContext(new DefaultFileOutputContext("FUN2.tsv"))
            .print();

    new SolutionListOutput(archive2.getSolutionList())
            .setSeparator("\t")
            .setVarFileOutputContext(new DefaultFileOutputContext("VAR3.tsv"))
            .setFunFileOutputContext(new DefaultFileOutputContext("FUN3.tsv"))
            .print();
    */
  }
}
