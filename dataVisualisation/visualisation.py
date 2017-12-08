import json
import matplotlib.pyplot as plt
from matplotlib.ticker import MaxNLocator
from collections import namedtuple
import numpy as np
import matplotlib.ticker as mticker

def buildEventGraph(data):
	success = []
	fail = []

	for key in data:
		if data[key]["event_type"] == "BuildEvent":
			#print (data[key]["specific_data"]["build_data"])
			d = data[key]["specific_data"]["build_data"]
			s = 0
			f = 0
			for k in d:
				#print (data[key]["specific_data"]["build_data"][k]["Successful"])
				if data[key]["specific_data"]["build_data"][k]["Successful"]==True:
					s = s + 1
				else:
					f = f + 1

			#print ("------------------------------------")
			success.append(s)
			fail.append(f)

	fig, ax = plt.subplots()
	index = np.arange(len(success))
	bar_width = 0.35
	opacity = 0.4
	error_config = {'ecolor': '0.3'}
	rects1 = ax.bar(index, success, bar_width,
                alpha=opacity, color='b',
                error_kw=error_config,
                label='Success')

	rects2 = ax.bar(index + bar_width, fail, bar_width,
                alpha=opacity, color='r',
                error_kw=error_config,
                label='Failure')

	ax.set_xlabel('Build Event')
	ax.set_ylabel('Frequency')
	ax.set_title('Frequency of failures/successes in each build event')
	ax.set_xticks(index + bar_width / 2)
	ax.set_xticklabels(())
	ax.legend()
	fig.tight_layout()
	plt.show()
	return;


def testrunEventGraph(data):
	success = []
	fail = []
	ignored = []
	error = []

	for key in data:
		if data[key]["event_type"] == "TestRunEvent":
			#print (data[key]["specific_data"]["TestRunEvent"])
			d = data[key]["specific_data"]["Tests"]
			s = 0
			f = 0
			e = 0
			i = 0
			for k in d:
				#print (data[key]["specific_data"]["Tests"][k]["Result"])
				r = data[key]["specific_data"]["Tests"][k]["Result"]
				if r == "Success":
					s = s + 1
				elif r == "Failed":
					f = f + 1
				elif r == "Error":
					e = e + 1
				else:
					i = i + 1

			#print ("------------------------------------")
			success.append(s)
			fail.append(f)
			ignored.append(i)
			error.append(e)

	fig, ax = plt.subplots()
	index = np.arange(len(success))
	bar_width = 0.2
	opacity = 0.4
	error_config = {'ecolor': '0.3'}
	rects1 = ax.bar(index, success, bar_width,
                alpha=opacity, color='b',
                error_kw=error_config,
                label='Success')
	rects2 = ax.bar(index + bar_width, fail, bar_width,
                alpha=opacity, color='r',
                error_kw=error_config,
                label='Failure')
	rects3 = ax.bar(index + bar_width, error, bar_width,
                alpha=opacity, color='g',
                error_kw=error_config,
                label='Error')
	rects4 = ax.bar(index + bar_width, ignored, bar_width,
                alpha=opacity, color='y',
                error_kw=error_config,
                label='Ignored')

	ax.set_xlabel('TestRun Event')
	ax.set_ylabel('Frequency')
	ax.set_title('Frequency of failures/successes in each TestRun event')
	ax.set_xticks(index + bar_width / 2)
	ax.set_xticklabels(())
	ax.legend()
	fig.tight_layout()
	plt.show()
	return;


def editOutcomeGraph(time, outcome, size, s, o):
	fig, ax1 = plt.subplots()
	ax2 = ax1.twinx()
	ax1.set_title('Frequency of ' + o + ' and ' + s + ' edits over time')
	a1 = ax1.plot(time, size, color='b', label='Edits')
	ax1.set_xlabel('time')
	ax1.set_ylabel('Frequency of ' + o + ' edits')
	a2 = ax2.plot(time, outcome, color='r', label=o)
	ax2.set_ylabel('Frequency of ' + s)
	#ax1.set_xticks(time[0::5])
	#ax2.set_xticks(time[0::5])
	a = a1 + a2
	labs = [l.get_label() for l in a]
	ax1.legend(a, labs, loc=0)
	plt.show()
	return;



def buildEditRelation(data):
	time = []
	success = []
	fail = []
	large = []
	small = []
	start = 1
	#change this to define a small/large edit
	changeSizeLim = 5000

	for key in data:
		event  = data[key]["event_type"]
		if event == "BuildEvent" or event == "EditEvent":

			t = data[key]["time_stamp"].split('T')[0]

			if start == 1 or time[-1] != t:
				start = 0
				time.append(t)
				success.append(0)
				fail.append(0)
				small.append(0)
				large.append(0)
			

			if event == "BuildEvent":
				s = 0
				f = 0
				d = data[key]["specific_data"]["build_data"]
				for k in d:
					if data[key]["specific_data"]["build_data"][k]["Successful"]==True:
						s = s + 1
					else:
						f = f + 1

				success[-1] = success[-1] + s
				fail[-1] = fail[-1] + f	

			else:
				d = data[key]["specific_data"]["SizeOfChanges"]

				if d < changeSizeLim:
					small[-1] = small[-1] + 1
				else:
					large[-1] = large[-1] + 1

	return time, fail, success, large, small;


data = json.load(open('1.json'))
testrunEventGraph(data)
buildEventGraph(data)
time, fail, success, large, small = buildEditRelation(data)
editOutcomeGraph(time, fail, large, 'large', 'failures')
editOutcomeGraph(time, success, large, 'large', 'successes')
editOutcomeGraph(time, fail, small, 'small', 'failures')
editOutcomeGraph(time, success, small, 'small', 'successes')

