import os
import json
import plotly
import plotly.graph_objs as go

rootdir = '/Users/jasminelu/Desktop/ToolsCW/SizeClassification/output'

x_small = 0
small = 0
medium = 0
large = 0
x_large = 0

def loop_through_files():
    for subdir, dirs, files in os.walk(rootdir):
        for file in files:
            if file.lower().endswith(('.json')):
                json_data = open(os.path.join(subdir, file)).read()
                data = json.loads(json_data)
                enumerate_size_of_changes(data)

    print "x_small: ", x_small
    print "small: ", small
    print "medium: ", medium
    print "large: ", large
    print "x_large: ", x_large

def enumerate_size_of_changes(obj):
    global x_small, small, medium, large, x_large
    for item in obj.values():
        if item['event_type'] == 'EditEvent':
            if item['specific_data']['SizeOfChanges'] < 6:
                x_small += 1
            elif item['specific_data']['SizeOfChanges'] >= 6 and item['specific_data']['SizeOfChanges'] < 240:
                small += 1
            elif item['specific_data']['SizeOfChanges'] >= 240 and item['specific_data']['SizeOfChanges'] < 591:
                medium += 1
            elif item['specific_data']['SizeOfChanges'] >= 591 and item['specific_data']['SizeOfChanges'] < 942:
                large += 1
            elif item['specific_data']['SizeOfChanges'] >= 942:
                x_large += 1

def plot_pie_chart():
    labels = ['Extra-small','Small','Medium','Large', 'Extra-large']
    values = [x_small,small,medium,large,x_large]
    trace = go.Pie(labels=labels, values=values)
    plotly.offline.plot([trace], filename='pie-chart-sizes')

loop_through_files()
plot_pie_chart()
