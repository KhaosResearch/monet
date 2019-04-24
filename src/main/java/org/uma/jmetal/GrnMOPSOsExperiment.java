package org.uma.jmetal;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.qualityindicator.impl.*;
import org.uma.jmetal.qualityindicator.impl.hypervolume.PISAHypervolume;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.experiment.Experiment;
import org.uma.jmetal.util.experiment.ExperimentBuilder;
import org.uma.jmetal.util.experiment.component.*;
import org.uma.jmetal.util.experiment.util.ExperimentAlgorithm;
import org.uma.jmetal.util.experiment.util.ExperimentProblem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GrnMOPSOsExperiment
{
  private static final int INDEPENDENT_RUNS = 25;

  public static void main(String[] args)
          throws IOException
  {
    if (args.length != 2) {
      throw new JMetalException("Needed arguments: experimentBaseDirectory referenceFrontDirectory");
    }
    String experimentBaseDirectory = args[0];
    String referenceFrontDirectory = args[1];

    List<ExperimentProblem<DoubleSolution>> problemList = new ArrayList();
    /*problemList.add(new ExperimentProblem(new P("10-Ecoli1", 60)));
    problemList.add(new ExperimentProblem(new P("10-Ecoli2", 60)));
    problemList.add(new ExperimentProblem(new P("10-Yeast1", 60)));
    problemList.add(new ExperimentProblem(new P("10-Yeast2", 60)));
    problemList.add(new ExperimentProblem(new P("10-Yeast3", 60)));
    problemList.add(new ExperimentProblem(new P("10-1", 60)));
    problemList.add(new ExperimentProblem(new P("10-2", 60)));
    problemList.add(new ExperimentProblem(new P("10-3", 60)));
    problemList.add(new ExperimentProblem(new P("10-4", 60)));
    problemList.add(new ExperimentProblem(new P("10-5", 60)));*/
    problemList.add(new ExperimentProblem(new P("100-Ecoli1", 60)));
    problemList.add(new ExperimentProblem(new P("100-Ecoli2", 60)));
    problemList.add(new ExperimentProblem(new P("100-Yeast1", 60)));
    problemList.add(new ExperimentProblem(new P("100-Yeast2", 60)));
    problemList.add(new ExperimentProblem(new P("100-Yeast3", 60)));
    problemList.add(new ExperimentProblem(new P("100-1", 60)));
    problemList.add(new ExperimentProblem(new P("100-2", 60)));
    problemList.add(new ExperimentProblem(new P("100-1", 60)));
    problemList.add(new ExperimentProblem(new P("100-4", 60)));
    problemList.add(new ExperimentProblem(new P("100-5", 60)));

    List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithmList = configureAlgorithmList(problemList);

    Experiment<DoubleSolution, List<DoubleSolution>> experiment = new ExperimentBuilder("results")
            .setAlgorithmList(algorithmList).setProblemList(problemList)
            .setExperimentBaseDirectory(experimentBaseDirectory)
            .setOutputParetoFrontFileName("FUN")
            .setOutputParetoSetFileName("VAR")
            .setReferenceFrontDirectory(referenceFrontDirectory)
            .setIndicatorList(Arrays.asList(
                    new GenericIndicator[] {
                            new Epsilon(),
                            new Spread(),
                            new GenerationalDistance(),
                            new PISAHypervolume(),
                            new InvertedGenerationalDistance(),
                            new InvertedGenerationalDistancePlus()
                    })).setIndependentRuns(25)
            .setNumberOfCores(8).build();

    new GenerateReferenceParetoFront(experiment).run();
    //new GenerateReferenceParetoSetAndFrontFromDoubleSolutions(experiment).run();
    new ComputeQualityIndicators(experiment).run();
    new GenerateLatexTablesWithStatistics(experiment).run();
    new GenerateWilcoxonTestTablesWithR(experiment).run();
    new GenerateFriedmanTestTables(experiment).run();

    new GenerateBoxplotsWithR(experiment).setRows(3).setColumns(4).run();
  }

  static List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> configureAlgorithmList(List<ExperimentProblem<DoubleSolution>> problemList)
  {
    List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithms = new ArrayList();
    for (int run = 0; run < 25; run++)
    {
      for (int i = 0; i < problemList.size(); i++)
      {
        Algorithm<List<DoubleSolution>> algorithm = new NSGAIIBuilder(((ExperimentProblem)problemList.get(i)).getProblem(), new SBXCrossover(1.0D, 5.0D), new PolynomialMutation(1.0D / ((ExperimentProblem)problemList.get(i)).getProblem().getNumberOfVariables(), 10.0D)).setMaxEvaluations(25000).setPopulationSize(100).build();
        algorithms.add(new ExperimentAlgorithm(algorithm, "MOPSO", ((ExperimentProblem)problemList.get(i)).getProblem().getName()));
      }
      for (int i = 0; i < problemList.size(); i++)
      {
        Algorithm<List<DoubleSolution>> algorithm = new NSGAIIBuilder(((ExperimentProblem)problemList.get(i)).getProblem(), new SBXCrossover(1.0D, 20.0D), new PolynomialMutation(1.0D / ((ExperimentProblem)problemList.get(i)).getProblem().getNumberOfVariables(), 20.0D)).setMaxEvaluations(25000).setPopulationSize(100).build();
        algorithms.add(new ExperimentAlgorithm(algorithm, "MOPSOHv", ((ExperimentProblem)problemList.get(i)).getProblem().getName()));
      }
      for (int i = 0; i < problemList.size(); i++)
      {
        Algorithm<List<DoubleSolution>> algorithm = new NSGAIIBuilder(((ExperimentProblem)problemList.get(i)).getProblem(), new SBXCrossover(1.0D, 40.0D), new PolynomialMutation(1.0D / ((ExperimentProblem)problemList.get(i)).getProblem().getNumberOfVariables(), 40.0D)).setMaxEvaluations(25000).setPopulationSize(100).build();
        algorithms.add(new ExperimentAlgorithm(algorithm, "SMPSO", ((ExperimentProblem)problemList.get(i)).getProblem().getName()));
      }
      for (int i = 0; i < problemList.size(); i++)
      {
        Algorithm<List<DoubleSolution>> algorithm = new NSGAIIBuilder(((ExperimentProblem)problemList.get(i)).getProblem(), new SBXCrossover(1.0D, 80.0D), new PolynomialMutation(1.0D / ((ExperimentProblem)problemList.get(i)).getProblem().getNumberOfVariables(), 80.0D)).setMaxEvaluations(25000).setPopulationSize(100).build();
        algorithms.add(new ExperimentAlgorithm(algorithm, "OMOPSO", ((ExperimentProblem)problemList.get(i)).getProblem().getName()));
      }
      for (int i = 0; i < problemList.size(); i++)
      {
        Algorithm<List<DoubleSolution>> algorithm = new NSGAIIBuilder(((ExperimentProblem)problemList.get(i)).getProblem(), new SBXCrossover(1.0D, 80.0D), new PolynomialMutation(1.0D / ((ExperimentProblem)problemList.get(i)).getProblem().getNumberOfVariables(), 80.0D)).setMaxEvaluations(25000).setPopulationSize(100).build();
        algorithms.add(new ExperimentAlgorithm(algorithm, "DMOPSO", ((ExperimentProblem)problemList.get(i)).getProblem().getName()));
      }
      for (int i = 0; i < problemList.size(); i++)
      {
        Algorithm<List<DoubleSolution>> algorithm = new NSGAIIBuilder(((ExperimentProblem)problemList.get(i)).getProblem(), new SBXCrossover(1.0D, 80.0D), new PolynomialMutation(1.0D / ((ExperimentProblem)problemList.get(i)).getProblem().getNumberOfVariables(), 80.0D)).setMaxEvaluations(25000).setPopulationSize(100).build();
        algorithms.add(new ExperimentAlgorithm(algorithm, "VEPSO", ((ExperimentProblem)problemList.get(i)).getProblem().getName()));
      }
    }
    return algorithms;
  }

  private static class P
          extends AbstractDoubleProblem
  {
    public P(String name, int variables)
    {
      setName(name);
      setNumberOfConstraints(0);
      setNumberOfObjectives(2);
      setNumberOfVariables(variables);

      List<Double> upperLimit = new ArrayList(getNumberOfVariables());
      List<Double> lowerLimit = new ArrayList(getNumberOfVariables());
      for (int i = 0; i < getNumberOfVariables(); i++)
      {
        lowerLimit.add(Double.valueOf(0.0D));
        upperLimit.add(Double.valueOf(1.0D));
      }
      setLowerLimit(lowerLimit);
      setUpperLimit(upperLimit);
    }

    public void evaluate(DoubleSolution solution) {}

    public DoubleSolution createSolution()
    {
      return null;
    }
  }
}
