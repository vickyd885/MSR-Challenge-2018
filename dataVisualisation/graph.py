import json
import matplotlib.pyplot as plt
import numpy as np
import os

from scipy.stats import kendalltau
from scipy.stats import spearmanr
from scipy.stats import pearsonr

lim1 = 6
lim2 = 240
lim3 = 591
lim4 = 942

build_fail_array = []
test_fail_array = []

small_percentage_test = []
medium_percentage_test = []
large_percentage_test = []

small_percentage_build = []
medium_percentage_build = []
large_percentage_build = []

def loop_through_files():
    for subdir, dirs, files in os.walk('output'):
        for file in files:
            #print (os.path.join(subdir, file))
            data = json.load(open(os.path.join(subdir, file)))
            getFreq(data, os.path.join(subdir, file))
    return;

def getFreq(data, name):
	#user.append(name.split('\\')[-1].split('.')[0])
	build_failures = 0
	test_failures = 0
	total_changes = 0
	small_changes = 0
	medium_changes = 0
	large_changes = 0

	for key in data:
		event  = data[key]["event_type"]

		if event == "BuildEvent":
			d = data[key]["specific_data"]["ListOfBuilds"]
			for k in d:
				if data[key]["specific_data"]["ListOfBuilds"][k]["Successful"] == False:
					build_failures += 1

		elif event == 'TestRunEvent':
			d = data[key]["specific_data"]["ListOfTests"]
			for k in d:
				if data[key]["specific_data"]["ListOfTests"][k]["Result"] == "Failed":
					test_failures += 1

		elif event == "EditEvent":
			total_changes += 1

			d = data[key]["specific_data"]["SizeOfChanges"]
			#change the limits small: between lim1-im2, med: lim2-lim3, large: lim3=lim4
			if d >= lim1 and d < lim2:
				small_changes += 1
			elif d >= lim2 and d < lim3:
				medium_changes += 1
			elif d >= lim3 and d < lim4:
				large_changes += 1

	if test_failures > 0:
		small_percentage_test.append((float(small_changes)/total_changes)*100)
		medium_percentage_test.append((float(medium_changes)/total_changes)*100)
		large_percentage_test.append((float(large_changes)/total_changes)*100)
		test_fail_array.append(test_failures)

	if build_failures > 0:
		small_percentage_build.append((float(small_changes)/total_changes)*100)
		medium_percentage_build.append((float(medium_changes)/total_changes)*100)
		large_percentage_build.append((float(large_changes)/total_changes)*100)
		build_fail_array.append(build_failures)


def drawGraph(failures, sz, x_label, y_label):
	print (kendalltau(failures, sz))
	print (spearmanr(failures, sz))
	print (pearsonr(failures, sz))

	s = np.pi * (1)**2
	plt.scatter(sz, failures, s, c='m', alpha=0.5)
	plt.xlabel(x_label)
	plt.ylabel(y_label)
	plt.plot(np.unique(sz), np.poly1d(np.polyfit(sz, failures, 1))(np.unique(sz)))
	plt.show()

	return;

def drawAllGraphs():
	# small changes vs test fails
	drawGraph(test_fail_array, small_percentage_test, "Percentage of changes that are small", "Total number of test failures")

	# medium changes vs test fails
	drawGraph(test_fail_array, medium_percentage_test, "Percentage of changes that are medium", "Total number of test failures")

	# large changes vs test fails
	drawGraph(test_fail_array, large_percentage_test, "Percentage of changes that are large", "Total number of test failures")

	# large changes vs build fails
	drawGraph(build_fail_array, small_percentage_build, "Percentage of changes that are small", "Total number of test failures")

	# large changes vs build fails
	drawGraph(build_fail_array, medium_percentage_build, "Percentage of changes that are medium", "Total number of test failures")

	# large changes vs build fails
	drawGraph(build_fail_array, large_percentage_build, "Percentage of changes that are large", "Total number of test failures")


loop_through_files()
drawAllGraphs()
