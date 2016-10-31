# MONET: A Multi-objective Cellular Genetic Algorithm for the Inference of Gene Regulatory Networks
MONET is a software project aimed at solving the precise inference of Gene Regulatory Networks (GRNs) by using multi-objective metaheuristics. It is based on the jMetal multi-objective framework, which is extended with an new multi-objective approach for GRNs, and encoding of solutions to tune parameters in S-System model. 

Currently it contains an implementation of the MOCell algorithm configured with a SBX crossover, polynomial mutation operators, and two objectives to optimize: MSE and Topology Regularization (TR). 

## Summary of features
MONET containts the following features:
* The algorithm is instantiated through a MOCEllRunner class, although other runners can be employed to use other well-known multi-objective algorithms: NSGAII,  SPEA2, MOEAD, and SMPSO.
* The included datasets in "resources" folder are: Noisy time-series (Sirbu et al., 2010) in files "SS5GeneratedData(0-10)Noise.txt" and DREAM3 Challenge for GRNs.
* The "GeneNetWeaver" folder contains classess to covert solutions in variable files (VAR.tsv) to graphs representing the inferred networks.
* more features ....

## Requirements
To use jMetalMSA the following software packages are required:
* [Java SE Development Kit 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html?ssSourceSiteId=otnes)
* [Apache Maven](https://maven.apache.org/)
* [Git](https://git-scm.com/)

## Downloading and compiling
To download MONET just clone the Git repository hosted in GitHub:
```
git clone https://github.com/jMetal/monet.git
```
Once cloned, you can compile the software and generate a jar file with the following command:
```
mvn package
```
This sentence will generate a directory called `target` which will contain a file called `monet-1.0-SNAPSHOT-jar-with-dependencies.jar`

## Runing MOCell
To execute the algorithm to solve a problem in DREAM3, for example Ecoli1 size-10, just run this command:
````
java -cp target/monet-1.0-SNAPSHOT-jar-with-dependencies.jar  org.uma.jmetal.runner.multiobjective.MOCellRunnerGRN /datasets-gnw/DREAM3/InSilicoSize10/InSilicoSize10-Ecoli1-trajectories.txt
```

The output of the program are two files:
* `FUN.tsv`: contains the Pareto front approximation. For each solution, this file contains a line with the values of the two objectives: MSE TR.
* `VAR.tsv`: contains the Pareto set approximation. Each solution (in each line) is represented in S-System parameter's format: Kynetic orders(gene1), Rate constansts (gene1), Kynetic orders(gene2), Rate constansts (gene2), ...
