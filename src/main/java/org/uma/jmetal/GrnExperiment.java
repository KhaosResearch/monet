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

package org.uma.jmetal;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.gde3.GDE3Builder;
import org.uma.jmetal.algorithm.multiobjective.moead.MOEADBuilder;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.algorithm.multiobjective.smpso.SMPSOBuilder;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.qualityindicator.impl.*;
import org.uma.jmetal.qualityindicator.impl.hypervolume.PISAHypervolume;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.experiment.Experiment;
import org.uma.jmetal.util.experiment.ExperimentBuilder;
import org.uma.jmetal.util.experiment.component.*;
import org.uma.jmetal.util.experiment.util.TaggedAlgorithm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Example of experimental study based on solving the ZDT problems with four versions of NSGA-II, each
 * of them applying a different crossover probability (from 0.7 to 1.0).
 *
 * This experiment assumes that the reference Pareto front are known, so the names of files containing
 * them and the directory where they are located must be specified.
 *
 * Six quality indicators are used for performance assessment.
 *
 * The steps to carry out the experiment are:
 * 1. Configure the experiment
 * 2. Execute the algorithms
 * 3. Compute the quality indicators
 * 4. Generate Latex tables reporting means and medians
 * 5. Generate Latex tables with the result of applying the Wilcoxon Rank Sum Test
 * 6. Generate Latex tables with the ranking obtained by applying the Friedman test
 * 7. Generate R scripts to obtain boxplots
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class GrnExperiment {
  private static final int INDEPENDENT_RUNS = 25 ;

  public static void main(String[] args) throws IOException {
    if (args.length != 2) {
      throw new JMetalException("Needed arguments: experimentBaseDirectory referenceFrontDirectory") ;
    }
    String experimentBaseDirectory = args[0] ;
    String referenceFrontDirectory = args[1] ;

    List<Problem<DoubleSolution>> problemList = Arrays.<Problem<DoubleSolution>>asList(
       new P("0Noise", 60),
        new P("1Noise", 60),
        new P("2Noise", 60),
        new P("5Noise", 60),
        new P("10Noise", 60),

            new P("Ecoli1", 60),
            new P("Ecoli2", 60),
            new P("Yeast1", 60),
            new P("Yeast2", 60),
            new P("Yeast3", 60)
    ) ;
    //    new P("BB11001"), new P("BB11002")) ;

    List<TaggedAlgorithm<List<DoubleSolution>>> algorithmList = configureAlgorithmList(problemList, INDEPENDENT_RUNS) ;

    Experiment<DoubleSolution, List<DoubleSolution>> experiment =
        new ExperimentBuilder<DoubleSolution, List<DoubleSolution>>("bioinf2016")
            .setAlgorithmList(algorithmList)
            .setProblemList(problemList)
            .setExperimentBaseDirectory(experimentBaseDirectory)
            .setOutputParetoFrontFileName("FUN")
            .setOutputParetoSetFileName("VAR")
            .setReferenceFrontDirectory(referenceFrontDirectory)
            .setIndicatorList(Arrays.asList(
                new Epsilon<DoubleSolution>(), new Spread<DoubleSolution>(), new GenerationalDistance<DoubleSolution>(),
                new PISAHypervolume<DoubleSolution>(),
                new InvertedGenerationalDistance<DoubleSolution>(),
                new InvertedGenerationalDistancePlus<DoubleSolution>())
            )
            .setIndependentRuns(INDEPENDENT_RUNS)
            .setNumberOfCores(8)
            .build();

    //new ExecuteAlgorithms<>(experiment).run();
    new GenerateReferenceParetoFront(experiment).run();
    new GenerateReferenceParetoSetAndFrontFromDoubleSolutions(experiment).run();
    new ComputeQualityIndicators<>(experiment).run() ;
    new GenerateLatexTablesWithStatistics(experiment).run() ;
    new GenerateWilcoxonTestTablesWithR<>(experiment).run() ;
    new GenerateFriedmanTestTables<>(experiment).run();
    //new GenerateBoxplotsWithR<>(experiment).setRows(3).setColumns(2).setDisplayNotch().run();
      new GenerateBoxplotsWithR<>(experiment).setRows(3).setColumns(4).run();
  }

  /**
   * The algorithm list is composed of pairs {@link org.uma.jmetal.algorithm.Algorithm} + {@link org.uma.jmetal.problem.Problem} which form part of a
   * {@link org.uma.jmetal.util.experiment.util.TaggedAlgorithm}, which is a decorator for class {@link org.uma.jmetal.algorithm.Algorithm}. The {@link org.uma.jmetal.util.experiment.util.TaggedAlgorithm}
   * has an optional tag component, that can be set as it is shown in this example, where four variants of a
   * same algorithm are defined.
   *
   * @param problemList
   * @return
   */
  static List<TaggedAlgorithm<List<DoubleSolution>>> configureAlgorithmList(List<Problem<DoubleSolution>> problemList,
                                                                            int independentRuns) {
    List<TaggedAlgorithm<List<DoubleSolution>>> algorithms = new ArrayList<>();
    for (int run = 0; run < independentRuns; run++) {

     for (Problem<DoubleSolution> problem : problemList) {
        Algorithm<List<DoubleSolution>> algorithm = new NSGAIIBuilder<DoubleSolution>(problem, new SBXCrossover(1.0, 20.0),
            new PolynomialMutation(1.0 / problem.getNumberOfVariables(), 20.0))
            .build();
        algorithms.add(new TaggedAlgorithm<List<DoubleSolution>>(algorithm, "NSGAII", problem, run));
      }

      for (Problem<DoubleSolution> problem : problemList) {
        Algorithm<List<DoubleSolution>> algorithm = new NSGAIIBuilder<DoubleSolution>(problem, new SBXCrossover(1.0, 20.0),
            new PolynomialMutation(1.0 / problem.getNumberOfVariables(), 20.0))
            .build();
        algorithms.add(new TaggedAlgorithm<List<DoubleSolution>>(algorithm, "MOCell", problem, run));

      }

     /* for (Problem<DoubleSolution> problem : problemList) {
        Algorithm<List<DoubleSolution>> algorithm = new NSGAIIBuilder<DoubleSolution>(problem, new SBXCrossover(1.0, 20.0),
            new PolynomialMutation(1.0 / problem.getNumberOfVariables(), 20.0))
            .build();
        algorithms.add(new TaggedAlgorithm<List<DoubleSolution>>(algorithm, "SMPSO", problem, run));
      }

      for (Problem<DoubleSolution> problem : problemList) {
        Algorithm<List<DoubleSolution>> algorithm = new NSGAIIBuilder<DoubleSolution>(problem, new SBXCrossover(1.0, 20.0),
            new PolynomialMutation(1.0 / problem.getNumberOfVariables(), 20.0))
            .build();
        algorithms.add(new TaggedAlgorithm<List<DoubleSolution>>(algorithm, "GDE3", problem, run));
      }*/

      for (Problem<DoubleSolution> problem : problemList) {
        Algorithm<List<DoubleSolution>> algorithm = new NSGAIIBuilder<DoubleSolution>(problem, new SBXCrossover(1.0, 20.0),
            new PolynomialMutation(1.0 / problem.getNumberOfVariables(), 20.0))
            .build();
        algorithms.add(new TaggedAlgorithm<List<DoubleSolution>>(algorithm, "MOEADSTM", problem, run));
      }
    }
    return algorithms;
  }

  private static class P extends AbstractDoubleProblem {
    public P(String name, int variables) {
      setName(name);
      setNumberOfConstraints(0);
      setNumberOfObjectives(2);
      setNumberOfVariables(variables);

      List<Double> upperLimit = new ArrayList<>(getNumberOfVariables()) ;
      List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables()) ;

      for (int i = 0; i < getNumberOfVariables(); i++) {
        lowerLimit.add(0.0);
        upperLimit.add(1.0);
      }

      setLowerLimit(lowerLimit);
      setUpperLimit(upperLimit);
    }

    @Override
    public void evaluate(DoubleSolution solution) {

    }

    @Override
    public DoubleSolution createSolution() {
      return null;
    }
  }
}
