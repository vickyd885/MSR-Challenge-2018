import json
import matplotlib.pyplot as plt
import numpy as np
import os
from scipy.stats import kendalltau
from scipy.stats import spearmanr
from scipy.stats import pearsonr


size = []
fail = []
success = []
lim1 = 6
lim2 = 240
lim3 = 591
lim4 = 942

def getFreq(data, name):
	#print (name.split('\\')[-1].split('.')[0])
	s1 = 0
	f1 = 0
	s2 = 0
	f2 = 0
	s = 0
	n_s = 0
	for key in data:

		event  = data[key]["event_type"]

		if event == "BuildEvent":

			d = data[key]["specific_data"]["ListOfBuilds"]
			for k in d:
				if data[key]["specific_data"]["ListOfBuilds"][k]["Successful"]==True:
					s1 = s1 + 1
				else:
					f1 = f1 + 1

		elif event == 'TestRunEvent':
			d = data[key]["specific_data"]["ListOfTests"]
			for k in d:
				if data[key]["specific_data"]["ListOfTests"][k]["Result"]=="Success":
					s2 = s2 + 1
				else:
					f2 = f2 + 1

		elif event == "EditEvent":

			d = data[key]["specific_data"]["SizeOfChanges"]
				
			#change the limits small: between lim1-im2, med: lim2-lim3, large: lim3=lim4
			if d>=lim3 and d<lim4:
				s = s + 1
			else:
				n_s = n_s + 1

		#use f1 for build event
		#use f2 for test event
	if (f2) > 0:
		#user.append(name.split('\\')[-1].split('.')[0])
		percentage = s/(s+n_s) if (s+n_s) > 0 else 0
		#print (percentage)
		size.append(percentage*100)
		fail.append(f2)

	return;

def loop_through_files():
    for subdir, dirs, files in os.walk('output'):
        for file in files:
            #print (os.path.join(subdir, file))
            data = json.load(open(os.path.join(subdir, file)))
            getFreq(data, os.path.join(subdir, file))   
    return;


def drawGraph(failures, sz, szName):

	# print(large)
	# print(failures)
	#print (np.correlate(failures, large))
	print (kendalltau(failures, sz))
	print (spearmanr(failures, sz))
	print (pearsonr(failures, sz))

	s = np.pi * (1)**2
	plt.scatter(sz, failures, s, c='m', alpha=0.5)
	plt.xlabel(szName +" changes percentage")
	plt.ylabel("Total number of test and build failures")
	plt.plot(np.unique(sz), np.poly1d(np.polyfit(sz, failures, 1))(np.unique(sz)))
	plt.show()

	return;



loop_through_files()
#change szName to size (small/medium/large)
szName = "large"
drawGraph(fail, size, szName)







