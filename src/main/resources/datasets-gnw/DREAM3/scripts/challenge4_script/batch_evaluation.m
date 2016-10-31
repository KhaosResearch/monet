function [score, ...
	P_AUPR_all, ...
	P_AUROC_all, ...
	P_AUPR_overall, ...
	P_AUROC_overall, ...
	AUPR_all, ...
	AUROC_all] = batch_evaluation(sub_challenge_number)
%
% This function computes prediction accuracy 
% metrics and summary statistics, including an overall
% score for the five networks 
% (Ecoli1, Ecoli2, Yeast1, Yeast2, Yeast3).
%
% Usage: [score, ...
%	P_AUPR_overall, ...
%	P_AUROC_overall, ...
%	P_AUPR_all, ...
%	P_AUROC_all, ...
%	AUPR_all, ...
%	AUROC_all] = batch_evaluation(sub_challenge_number)
%
% INPUTS:
% 	sub_challenge_number=
% 		1 for InSilicoSize10 
%	 	2 for InSilicoSize50
%	 	3 for InSilicoSize100
%
% OUTPUTS:
%	score:		0.5 * -log(P_AUPR_overall * P_AUROC_overall)
%	P_AUPR_overall: geometric mean of the 5 AUPR p-values
%	P_AUROC_overall:geometric mean of the 5 AUROC p-values
%	P_AUPR_all:	5 AUPR p-values
%	P_AUROC_all:	5 AUROC p-values
%	AUPR_all:	5 AUPR values
%	AUROC_all:	5 AUROC values

% Gustavo A. Stolovitzky, Ph.D.
% Adj. Assoc Prof of Biomed Informatics, Columbia Univ
% Mngr, Func Genomics & Sys Biology, IBM  Research
% P.O.Box 218 					Office :  (914) 945-1292
% Yorktown Heights, NY 10598 	Fax     :  (914) 945-4217
% http://www.research.ibm.com/people/g/gustavo
% http://domino.research.ibm.com/comm/research_projects.nsf/pages/fungen.index.html 
%
% Robert Prill, Ph.D.
% Postdoctoral Researcher
% Computational Biology Center, IBM Research
% P.O.Box 218
% Yorktown Heights, NY 10598 	
% Office :  914-945-1377
% http://domino.research.ibm.com/researchpeople/rjprill.index.html
%

%% use scientific notation for display
format short e

%% %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%% PATHS

%% where the gold standard files are located
GOLDPATH = 'gold_standards/';

%% where the probability densities for ONE TEAM are located
PDFPATH = 'probability_densities/';

%% where the predictions are located
TESTPATH = 'predictions/';

%% TYPES of sub-challenges (i.e., network size)
TYPES = {'InSilicoSize10','InSilicoSize50','InSilicoSize100'};
TYPE = TYPES{sub_challenge_number};

%% %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

files = directory_list(TESTPATH);
got = sum(cell2mat(regexp(files,TYPE))>0);
expecting = 5;
if got~=expecting
	error('WRONG NUMBER OF PREDICTION FILES');
end

%% for each file
for fi = 1:length(files)

	file = files{fi};
	disp(file)
	
	testfile = [TESTPATH file];
	
	%% use helpers to find corresponding data files
	goldfile = [GOLDPATH find_goldfile(file)];
	pdffile = [PDFPATH find_pdffile(file)];

	%% metrics
	[AUC AUROC P_AUC P_AUROC] = ...
		DREAM3_Challenge4_Evaluation(testfile, goldfile, pdffile)

	%% remember
	AUPR_all(fi) = AUC;
	AUROC_all(fi) = AUROC;
	P_AUPR_all(fi) = P_AUC;
	P_AUROC_all(fi) = P_AUROC;

end

%% %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

disp('--------------------------------------------------')
disp('SUMMARY')

%% geometric mean
N = length(AUPR_all);
P_AUPR_overall  = exp(sum(log(P_AUPR_all))/N);
P_AUROC_overall = exp(sum(log(P_AUROC_all))/N);

%% SCORE
score = sum(-log10([P_AUPR_overall P_AUROC_overall]))/2;


%% %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%% HELPER FUNCTIONS

function filename = directory_list(there)
	filename = {};
	directory = dir(there);
	j = 1;
	for i = 1:size(directory,1)
		entry = directory(i).name;
		if isempty(regexp(entry, '^\.'))
			%% the first char is not a dot "."
	  		filename{j} = entry;
	  		j = j+1;
		end
	end
end

function goldfile = find_goldfile(file)
	[dreamteam dreamtype dreamnetwork] = challenge4_fileparts(file);
	goldfile = ['DREAM3GoldStandard_' dreamtype '_' dreamnetwork '.txt'];
end

function goldfile = find_pdffile(file)
	[dreamteam dreamtype dreamnetwork] = challenge4_fileparts(file);
	goldfile = ['PDF_' dreamtype '_' dreamnetwork '.mat'];
end

function [dreamteam dreamtype dreamnetwork] = challenge4_fileparts(file)
	[pathstr, name, ext] = fileparts(file);
	parts = strsplit('_', name);
	dreamnetwork = parts{end};
	dreamtype = parts{end-1};
	dreamteam = strjoin('_', parts{1:(end-2)}); %% allows _ in the team name
end

function parts = strsplit(splitstr, str, option)
	%STRSPLIT Split string into pieces.
	%
	%   STRSPLIT(SPLITSTR, STR, OPTION) splits the string STR at every occurrence
	%   of SPLITSTR and returns the result as a cell array of strings.  By default,
	%   SPLITSTR is not included in the output.
	%
	%   STRSPLIT(SPLITSTR, STR, OPTION) can be used to control how SPLITSTR is
	%   included in the output.  If OPTION is 'include', SPLITSTR will be included
	%   as a separate string.  If OPTION is 'append', SPLITSTR will be appended to
	%   each output string, as if the input string was split at the position right
	%   after the occurrence SPLITSTR.  If OPTION is 'omit', SPLITSTR will not be
	%   included in the output.
	%   Author:      Peter J. Acklam
	%   Time-stamp:  2003-10-13 11:09:44 +0200
	%   E-mail:      pjacklam@online.no
	%   URL:         http://home.online.no/~pjacklam
	nargsin = nargin;
	error(nargchk(2, 3, nargsin));
	if nargsin < 3
	   option = 'omit';
	else
	   option = lower(option);
	end
	splitlen = length(splitstr);
	parts = {};
	while 1
	   k = strfind(str, splitstr);
	   if length(k) == 0
	      parts{end+1} = str;
	      break
	   end
	   switch option
	      case 'include'
	         parts(end+1:end+2) = {str(1:k(1)-1), splitstr};
	      case 'append'
	         parts{end+1} = str(1 : k(1)+splitlen-1);
	      case 'omit'
	         parts{end+1} = str(1 : k(1)-1);
	      otherwise
	         error(['Invalid option string -- ', option]);
	   end
	   str = str(k(1)+splitlen : end);
	end
end

function str = strjoin(sep, varargin)
%STRJOIN Join strings in a cell array.
%
%   STRJOIN(SEP, STR1, STR2, ...) joins the separate strings STR1, STR2, ...
%   into a single string with fields separated by SEP, and returns that new
%   string.
%   Examples:
%
%     strjoin('-by-', '2', '3', '4')
%
%   returns '2-by-3-by-4'.
%
%     list = {'fee', 'fie', 'foe.m'};
%     strjoin('/', list{:}).
%
%   returns 'fee/fie/foe.m'.
%
%   This function is inspired by Perl' function join().
%   Author:      Peter J. Acklam
%   Time-stamp:  2003-10-13 11:13:55 +0200
%   E-mail:      pjacklam@online.no
%   URL:         http://home.online.no/~pjacklam
   % Check number of input arguments.
   error(nargchk(1, Inf, nargin));
   % Quick exit if output will be empty.
   if nargin == 1
      str = '';
      return
   end
   if isempty(sep)
      % special case: empty separator so use simple string concatenation
      str = [ varargin{:} ];
   else
      % varargin is a row vector, so fill second column with separator (using scalar
      % expansion) and concatenate but strip last separator
      varargin(2,:) = { sep };
      str = [ varargin{1:end-1} ];
   end
end

end
