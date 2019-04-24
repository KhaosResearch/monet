package org.uma.jmetal.algorithm.multiobjective.pso.vepso;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.singleobjective.particleswarmoptimization.ConstrictionBasedPSO;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

public class VEPSO
        implements Algorithm<List<DoubleSolution>>
{
  private DoubleProblem problem;
  private Archive<DoubleSolution> archive;
  private int swarmSize;
  private int maxIterations;
  private List<ConstrictionBasedPSO> psoIslandList;
  private List<List<DoubleSolution>> ListOfIslandSwarms;
  private double weight;
  private JMetalRandom randomGenerator = JMetalRandom.getInstance();

  public VEPSO(DoubleProblem problem, int swarmSize, int maxIterations, Archive<DoubleSolution> archive)
  {
    this.problem = problem;
    this.swarmSize = swarmSize;
    this.maxIterations = maxIterations;
    this.archive = archive;

    this.weight = (1.0D / (2.0D * Math.log(2.0D)));

    this.psoIslandList = new ArrayList();
    for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
      this.psoIslandList.add(new ConstrictionBasedPSO(problem, i, swarmSize, maxIterations / problem
              .getNumberOfObjectives(), new SequentialSolutionListEvaluator()));
    }
  }

  public void run()
  {
    this.ListOfIslandSwarms = createInitialSwarm();
    this.ListOfIslandSwarms = evaluateSwarm(this.ListOfIslandSwarms);
    initializeVelocity(this.ListOfIslandSwarms);
    initializeParticlesMemory(this.ListOfIslandSwarms);
    initializeLeaders(this.ListOfIslandSwarms);
    initProgress();
    while (!isStoppingConditionReached())
    {
      updateVelocity(this.ListOfIslandSwarms);
      updatePosition(this.ListOfIslandSwarms);
      perturbation(this.ListOfIslandSwarms);
      this.ListOfIslandSwarms = evaluateSwarm(this.ListOfIslandSwarms);
      updateLeaders(this.ListOfIslandSwarms);
      updateParticlesMemory(this.ListOfIslandSwarms);
      updateProgress();
    }
  }

  protected void initProgress()
  {
    for (ConstrictionBasedPSO pso : this.psoIslandList) {
      pso.initProgress();
    }
  }

  protected void updateProgress()
  {
    for (ConstrictionBasedPSO pso : this.psoIslandList) {
      pso.updateProgress();
    }
  }

  protected boolean isStoppingConditionReached()
  {
    return ((ConstrictionBasedPSO)this.psoIslandList.get(0)).isStoppingConditionReached();
  }

  protected List<List<DoubleSolution>> createInitialSwarm()
  {
    this.ListOfIslandSwarms = new ArrayList(this.problem.getNumberOfObjectives());
    for (ConstrictionBasedPSO pso : this.psoIslandList) {
      this.ListOfIslandSwarms.add(pso.createInitialSwarm());
    }
    return this.ListOfIslandSwarms;
  }

  protected List<List<DoubleSolution>> evaluateSwarm(List<List<DoubleSolution>> swarmList)
  {
    List<List<DoubleSolution>> swarms = new ArrayList(this.problem.getNumberOfObjectives());
    for (int i = 0; i < this.psoIslandList.size(); i++)
    {
      swarms.add(i, ((ConstrictionBasedPSO)this.psoIslandList.get(i)).evaluateSwarm((List)swarmList.get(i)));
      updateArchive((List)swarmList.get(i));
    }
    return swarms;
  }

  protected void initializeLeaders(List<List<DoubleSolution>> swarmList)
  {
    for (int i = 0; i < this.psoIslandList.size(); i++) {
      ((ConstrictionBasedPSO)this.psoIslandList.get(i)).initializeGlobalBest((List)swarmList.get(i));
    }
  }

  protected void initializeParticlesMemory(List<List<DoubleSolution>> swarmList)
  {
    for (int i = 0; i < this.psoIslandList.size(); i++)
    {
      ((ConstrictionBasedPSO)this.psoIslandList.get(i)).initializeParticleBest((List)swarmList.get(i));
      ((ConstrictionBasedPSO)this.psoIslandList.get(i)).setSwarm((List)swarmList.get(i));
    }
  }

  protected void initializeVelocity(List<List<DoubleSolution>> swarmList)
  {
    for (int i = 0; i < this.psoIslandList.size(); i++) {
      ((ConstrictionBasedPSO)this.psoIslandList.get(i)).initializeVelocity((List)swarmList.get(i));
    }
  }

  protected void updateVelocity(List<List<DoubleSolution>> swarmList)
  {
    updateSwarmVelocity(swarmList);
  }

  protected void updatePosition(List<List<DoubleSolution>> swarmList)
  {
    for (int i = 0; i < this.psoIslandList.size(); i++) {
      ((ConstrictionBasedPSO)this.psoIslandList.get(i)).updatePosition((List)swarmList.get(i));
    }
  }

  protected void perturbation(List<List<DoubleSolution>> swarmList) {}

  protected void updateLeaders(List<List<DoubleSolution>> swarmList)
  {
    for (int i = 0; i < this.psoIslandList.size(); i++) {
      ((ConstrictionBasedPSO)this.psoIslandList.get(i)).updateGlobalBest((List)swarmList.get(i));
    }
  }

  protected void updateParticlesMemory(List<List<DoubleSolution>> swarmList)
  {
    for (int i = 0; i < this.psoIslandList.size(); i++) {
      ((ConstrictionBasedPSO)this.psoIslandList.get(i)).updateParticleBest((List)swarmList.get(i));
    }
  }

  protected void updateArchive(List<DoubleSolution> swarm)
  {
    for (DoubleSolution solution : swarm) {
      this.archive.add((DoubleSolution)solution.copy());
    }
  }

  public List<DoubleSolution> getResult()
  {
    return this.archive.getSolutionList();
  }

  private void updateSwarmVelocity(List<List<DoubleSolution>> swarmList)
  {
    for (ConstrictionBasedPSO pso : this.psoIslandList) {
      for (int i = 0; i < this.swarmSize; i++)
      {
        DoubleSolution particle = (DoubleSolution)pso.getSwarm().get(i);

        double r1 = this.randomGenerator.nextDouble(0.0D, 1.0D);
        double r2 = this.randomGenerator.nextDouble(0.0D, 1.0D);

        double c1 = 2.05D;
        double c2 = 2.05D;
        double constrictionFactor = 0.729D;

        int globalBestSwarmId = JMetalRandom.getInstance().nextInt(0, 1);

        DoubleSolution localBest = pso.getLocalBest()[i];

        DoubleSolution globalBest = ((ConstrictionBasedPSO)this.psoIslandList.get(globalBestSwarmId)).getResult();
        for (int var = 0; var < particle.getNumberOfVariables(); var++) {
          pso.getSwarmSpeedMatrix()[i][var] = (constrictionFactor * (this.weight * pso.getSwarmSpeedMatrix()[i][var] + c1 * r1 * (((Double)localBest.getVariableValue(var)).doubleValue() - ((Double)particle.getVariableValue(var)).doubleValue()) + c2 * r2 * (((Double)globalBest.getVariableValue(var)).doubleValue() - ((Double)particle.getVariableValue(var)).doubleValue())));
        }
      }
    }
  }

  public String getName()
  {
    return "VEPSO";
  }

  public String getDescription()
  {
    return "VEPSO";
  }
}
