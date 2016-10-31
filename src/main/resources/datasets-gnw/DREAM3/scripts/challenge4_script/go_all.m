%
% This script demonstrates how to call the function 
% batch_evaluation() which computes prediction accuracy 
% metrics and summary statistics, including an overall
% score for the five networks 
% (Ecoli1, Ecoli2, Yeast1, Yeast2, Yeast3).
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

%diary('C:\Users\Jose-Manuel\Software\jmetalmaven\diary.txt')
diary on

clear all

%% pick a number:
%% 	1 for InSilicoSize10 
%%  2 for InSilicoSize50
%%  3 for InSilicoSize100
sub_challenge_number = 1;

[score, ...
	P_AUPR_all, ...
	P_AUROC_all, ...
	P_AUPR_overall, ...
	P_AUROC_overall, ...
	AUPR_all, ...
	AUROC_all] = batch_evaluation(sub_challenge_number)

diary off

exit
