# MONET: A Multi-objective Cellular Genetic Algorithm for the Inference of Gene Regulatory Networks
MONET is a software project aimed at solving the precise inference of Gene Regulatory Networks (GRNs) by using multi-objective metaheuristics. It is based on the jMetal multi-objective framework, which is extended with an new multi-objective approach for GRNs, and encoding of solutions to tune parameters in S-System model. 

Currently it contains an implementation of the MOCell algorithm configured with a SBX crossover, polynomial mutation operators, and two objectives to optimize: MSE and Topology Regularization (TR). Additional classes for S-System modeling and time-series management are included in the util sub-folder of problem contents. 
This folder contains a series of classes adapted and integrated from the additional software package provided in (Sirbu et al., 2010)

## Summary of features
MONET containts the following features:
* The algorithm is instantiated through a MOCEllRunner class, although other runners can be employed to use other well-known multi-objective algorithms: NSGAII and SPEA2.
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

## Bibliography

Akutsu, T., Kuhara, S., Maruyama, O., and Miyano, S. (2003). Identification of genetic networks by strategic gene disruptions and gene overexpressions under a boolean model. Theoretical Computer Science, 298(1), 235 – 251.

Durillo, J. J. and Nebro, A. J. (2011). jmetal: A java framework for multi-objective optimization. Advances in Engineering Software, 42, 760–771.

Friedman, N., Linial, M., Nachman, I., and er, D. P. (2004). Using bayesian networks to analyze expression data. Journal of Computational Biology, 7(3-4).

Hlavacek, W. S. and Savageau, M. A. (1996). Rules for coupled expression of regulator and effector genes in inducible circuits. Journal of Molecular Biology, 255(1), 121 – 139.

Huynh-Thu, V. A. and Sanguinetti, G. (2015). Combining tree-based and dynamical systems for the inference of gene regulatory networks. Bioinformatics, 31 (10): 1614-1622.. 

Kikuchi, S., Tominaga, D., Arita, M., Takahashi, K., and Tomita, M. (2003). Dynamic modeling of genetic networks using genetic algorithm and s-system. Bioinformatics, 19(5), 643–650.

Kimura, S., Ide, K., Kashihara, A., Kano, M., Hatakeyama, M., Masui, R., Nakagawa, N., Yokoyama, S., Kuramitsu, S., and Konagaya, A. (2005). Inference of s-system models of genetic networks using a cooperative coevolutionary algorithm. Bioinformatics, 21(7), 1154–1163.

Lee, W.-P. and Hsiao, Y.-T. (2012). Inferring gene regulatory networks using a hybrid gaâŁ“pso approach with numerical constraints and network decomposition. Information Sciences, 188, 80 – 99.

Liu, P.-K. and Wang, F.-S. (2008). Inference of biochemical network models in ssystem using multiobjective optimization approach. Bioinformatics, 24(8), 1085– 1092.

Nebro, A. J., Durillo, J. J., Luna, F., Dorronsoro, B., and Alba, E. (2007). Design Issues in a Multiobjective Cellular Genetic Algorithm, pages 126–140. Springer Berlin Heidelberg, Berlin, Heidelberg.

Nebro, A. J., Durillo, J. J., Garcia-Nieto, J., Coello Coello, C. A., Luna, F., and Alba, E. (2009). SMPSO: A new PSO-based metaheuristic for multi-objective optimization. In IEEE Symposium on Computational Intelligence in Multi-Criteria Decision-Making, pages 66–73.

Nebro, A. J., Durillo, J. J., and Vergne, M. (2015). Redesigning the jmetal multiobjective optimization framework. In Genetic and Evolutionary Computation Conference (GECCO 2015) Companion, pages 1093–1100.

Noman, N. and Iba, H. (2007). Inferring gene regulatory networks using differential evolution with local search heuristics. IEEE/ACM Transactions on Computational Biology and Bioinformatics, 4(4), 634–647.

Palafox, L., Noman, N., and Iba, H. (2013). Reverse engineering of gene regulatory networks using dissipative particle swarm optimization. Evolutionary Computation, IEEE Transactions on, 17(4), 577–587.

Prill, R. J., Marbach, D., Saez-Rodriguez, J., Sorger, P. K., Alexopoulos, L. G., Xue, X., Clarke, N. D., Altan-Bonnet, G., and Stolovitzky, G. (2010). Towards a rigorous assessment of systems biology models: The dream3 challenges. PLoS ONE, 5(2), 1–18.

Savageau, M. (2010). Biochemical Systems Analysis: A Study of Function and Design in Molecular Biology. Addison-Wesley Educational Publishers Inc.

Sirbu, A., Ruskin, H. J., and Crane, M. (2010). Comparison of evolutionary algorithms in gene regulatory network model inference. BMC Bioinformatics, 11:59(1).

Spieth, C., Streichert, F., Supper, J., Speer, N., and Zell, A. (2005a). Feedback memetic algorithms for modeling gene regulatory networks. In Computational Intelligence in Bioinformatics and Computational Biology, 2005. CIBCB ’05. Proceedings of the 2005 IEEE Symposium on, pages 1–7.

Spieth, C., Streichert, F., Speer, N., and Zell, A. (2005b). Multi-objective model optimization for inferring gene regulatory networks. In C. Coello Coello, A. Hernández Aguirre, and E. Zitzler, editors, Evolutionary Multi-Criterion Optimization, volume 3410 of Lecture Notes in Computer Science, pages 607–620. Springer Berlin Heidelberg.

Tominaga, D., Koga, N., and Okamoto, M. (2000). Efficient numerical optimization algorithm based on genetic algorithm for inverse problem. In Proceedings of the 2Nd Annual Conference on Genetic and Evolutionary Computation, GECCO’00, pages 251–258, San Francisco, CA, USA. Morgan Kaufmann Publishers Inc.

Tsai, K.-Y. andWang, F.-S. (2005). Evolutionary optimization with data collocation for reverse engineering of biological networks. Bioinformatics, 21(7), 1180–1188. Voit, E. O. (2000). Computational Analysis of Biochemical Systems. A Practical Guide for Biochemists and Molecular Biologists. Cambridge University Press.

Zitzler, E., Knowles, J., and Thiele, L. (2008). Quality Assessment of Pareto Set Approximations, pages 373–404. Springer Berlin Heidelberg, Berlin, Heidelberg.

