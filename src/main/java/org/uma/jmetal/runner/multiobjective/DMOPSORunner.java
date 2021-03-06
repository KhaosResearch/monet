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
import org.uma.jmetal.algorithm.multiobjective.pso.dmopso.DMOPSO;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.problem.grn.Tominaga99FromMA;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.ProblemUtils;

import java.util.List;

/**
 * Class for configuring and running the DMOPSO algorithm
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */

public class DMOPSORunner extends AbstractAlgorithmRunner {
  /**
   * @param args Command line arguments.
   * @throws org.uma.jmetal.util.JMetalException
   * @throws java.io.IOException
   * @throws SecurityException
   * Invoking command:
  java DMOPSORunner problemName [referenceFront]
   */
  public static void main(String[] args) throws Exception {
    DoubleProblem problem;
    Algorithm<List<DoubleSolution>> algorithm;

    String referenceParetoFront = "" ;

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

    //problem = (DoubleProblem) ProblemUtils.<DoubleSolution> loadProblem(problemName);

    algorithm = new DMOPSO(problem,
            100, 250, 0.0, 1.0, 0.0, 1.0, 1.2, 2.0, 1.2, 2.0, 0.1, 0.5,
            -1.0, -1.0, "_TCHE",
            "/Users/ajnebro/Softw/jMetal/metaheuristicsBook/src/main/resources/MOEAD_Weights", 2) ;

    AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
        .execute();

    List<DoubleSolution> population = algorithm.getResult();
    long computingTime = algorithmRunner.getComputingTime();

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");

    printFinalSolutionSet(population);
    if (!referenceParetoFront.equals("")) {
      printQualityIndicators(population, referenceParetoFront) ;
    }
  }
}
