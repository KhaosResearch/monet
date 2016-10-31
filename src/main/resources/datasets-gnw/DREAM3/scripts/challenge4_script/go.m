%
% This script demonstrates how to call the function 
% DREAM3_Challenge4_Evaluation().
%
%
% Gustavo A. Stolovitzky, Ph.D.
% Adj. Assoc Prof of Biomed Informatics, Columbia Univ
% Mngr, Func Genomics & Sys Biology, IBM  Research
% P.O.Box 218 					Office :  (914) 945-1292
% Yorktown Heights, NY 10598 	Fax     :  (914) 945-4217
% http://www.research.ibm.com/people/g/gustavo
% http://domino.research.ibm.com/comm/research_projects.nsf/pages/fungen.index.html 
%
% Bernd Jagla, Ph.D.
% Assoc. Rsch. Scientist
% Joint Centers for Systems Biology
% Columbia University
% Irving Cancer Res Ctr
% 1130 St Nicholas Avenue 8th Floor
% United States
%
% Robert Prill, Ph.D.
% Postdoctoral Researcher
% Computational Biology Center, IBM Research
% P.O.Box 218
% Yorktown Heights, NY 10598 	
% Office :  914-945-1377
% http://domino.research.ibm.com/researchpeople/rjprill.index.html
%

clear all

%% predictions to be evaluated
testfile = 'predictions/InSilicoSize10_Ecoli1.txt';

%% the gold standard that corresponds to the testfile
goldfile = 'gold_standards/DREAM3GoldStandard_InSilicoSize10_Ecoli1.txt';

%% the (precomputed) probability density that corresponds to the testfile
pdffile = 'probability_densities/PDF_InSilicoSize10_Ecoli1.mat';

%% results
[AUC AUROC P_AUC P_AUROC] = DREAM3_Challenge4_Evaluation(testfile, goldfile, pdffile)
