//  SMPSORunner.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//
//  Copyright (c) 2014 Antonio J. Nebro
//
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
import org.uma.jmetal.algorithm.multiobjective.pso.mopso.MOPSOBuilder;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.problem.grn.Tominaga99FromMA;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.ProblemUtils;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.pseudorandom.impl.MersenneTwisterGenerator;

import java.util.List;

/**
 * Class for configuring and running the SMPSO algorithm using an HypervolumeArchive, i.e, the
 * SMPSOhv algorithm described in: A.J Nebro, J.J. Durillo, C.A. Coello Coello. Analysis of Leader
 * Selection Strategies in a Multi-Objective Particle Swarm Optimizer. 2013 IEEE Congress on
 * Evolutionary Computation. June 2013 DOI: 10.1109/CEC.2013.6557955
 *
 * This is a variant using the WFG Hypervolume implementation
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class MOPSORunner {
  /**
   * @param args Command line arguments. The first (optional) argument specifies the problem to
   *             solve.
   * @throws SecurityException Invoking command: java SMPSOHvRunner problemName [referenceFront]
   */
  public static void main(String[] args) throws Exception {
    DoubleProblem problem;
    Algorithm<List<DoubleSolution>> algorithm;
    MutationOperator<DoubleSolution> mutation;

    String referenceParetoFront = "";

    String problemName ;
    if (args.length == 1) {
      problemName = args[0];
      System.out.println(problemName);
      problem = new Tominaga99FromMA(2,args[0]) ;
    } else if (args.length == 2) {
      problemName = args[0] ;
      System.out.println(problemName);
      referenceParetoFront = args[1] ;
      problem = new Tominaga99FromMA(2,args[0]) ;
    } else {
      problemName = "/datasets-gnw/DREAM3/InSilicoSize10/InSilicoSize10-Ecoli1-trajectories.txt";
      referenceParetoFront = "jmetal-problem/src/test/resources/pareto_fronts/Tominaga5SSGeneratedData.pf" ;
      problem = new Tominaga99FromMA(2) ;
    }


    //problem = (DoubleProblem) ProblemUtils.<DoubleSolution>loadProblem(problemName);

    double mutationProbability = 1.0 / problem.getNumberOfVariables();
    double mutationDistributionIndex = 20.0;
    mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    BoundedArchive<DoubleSolution> archive =
            new CrowdingDistanceArchive<>(100) ;

    algorithm = new MOPSOBuilder(problem, archive)
            .setMutation(mutation)
            .setMaxIterations(250)
            .setSwarmSize(100)
            .setArchiveSize(100)
            .setRandomGenerator(new MersenneTwisterGenerator())
            .setSolutionListEvaluator(new SequentialSolutionListEvaluator<DoubleSolution>())
            .build();

    AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
            .execute();

    List<DoubleSolution> population = algorithm.getResult();
    long computingTime = algorithmRunner.getComputingTime();

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");

    new SolutionListOutput(population)
            .setSeparator("\t")
            .setVarFileOutputContext(new DefaultFileOutputContext("VAR.tsv"))
            .setFunFileOutputContext(new DefaultFileOutputContext("FUN.tsv"))
            .print();
  }
}
