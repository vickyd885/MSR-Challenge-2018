import os
import json
import numpy as np
import plotly
import plotly.graph_objs as go

rootdir = '/Users/jasminelu/Desktop/ToolsCW/SizeClassification/output'

numberOfChanges = []
sizeOfChanges = []

numberOfChangesLog = []
sizeOfChangesLog = []

def loop_through_files():
    for subdir, dirs, files in os.walk(rootdir):
        for file in files:
            if file.lower().endswith(('.json')):
                json_data = open(os.path.join(subdir, file)).read()
                data = json.loads(json_data)
                extract_edit_event_data(data)

def extract_edit_event_data(obj):
    for item in obj.values():
        if item['event_type'] == 'EditEvent':
            if item['specific_data']['NumberOfChanges'] > 0 and item['specific_data']['SizeOfChanges'] > 0:
                numberOfChanges.append(item['specific_data']['NumberOfChanges'])
                sizeOfChanges.append(item['specific_data']['SizeOfChanges'])
                numberOfChangesLog.append(np.log(item['specific_data']['NumberOfChanges']))
                sizeOfChangesLog.append(np.log(item['specific_data']['SizeOfChanges']))

def print_size_classification(array):
    print len(sizeOfChanges)
    q0, q25, q50, q75, q100 = np.percentile(array, [0, 25, 50, 75, 100])
    iqr = q75 - q25

    print "q0: ", q0
    print "q25: ", q25
    print "q50: ", q50
    print "q75: ", q75
    print "q100: ", q100
    print "x-small: ", q0, "-", q25
    print "small: ", q25, "-", q75
    print "medium: ", q75, "-", (iqr*1.5)+q75
    print "large: ", (iqr*1.5)+q75, "-", (iqr*3)+q75
    print "x-large: >", (iqr*3)+q75

def plot_scatter():
    trace = go.Scatter(
        x = numberOfChangesLog,
        y = sizeOfChangesLog,
        mode = 'markers'
    )

    plot_data = [trace]
    layout = go.Layout(
        xaxis=dict(
            title='Number of Changes',
        ),
        yaxis=dict(
            title='Size of Changes',
        )
    )

    plot(plot_data, layout, 'scatter')

def plot_histogram_number_of_changes():
    trace = go.Histogram(
        x = numberOfChangesLog
    )

    plot_data = [trace]
    layout = go.Layout(
        xaxis=dict(
            title='Number of Changes (Natural Log)',
        ),
        yaxis=dict(
            title='Frequency',
        )
    )

    plot(plot_data, layout, 'hist-num-changes')

def plot_histogram_size_of_changes():
    trace = go.Histogram(
        x = sizeOfChangesLog
    )

    plot_data = [trace]
    layout = go.Layout(
        xaxis=dict(
            title='Size of Changes (Natural Log)',
        ),
        yaxis=dict(
            title='Frequency',
        )
    )

    plot(plot_data, layout, 'hist-size_changes')

def plot(data, layout, filename):
    fig = go.Figure(data=data, layout=layout)
    plotly.offline.plot(fig, filename=filename)

def box_plot_size_of_changes():
    trace = go.Box(
        x = sizeOfChangesLog,
        boxpoints = False
    )

    plot_data = [trace]

    layout = go.Layout(
        font=dict(family='Times', size=16)
    )

    plot(plot_data, layout, 'box-plot-size')

def box_plot_number_of_changes():
    trace = go.Box(
        x = numberOfChangesLog,
        boxpoints = False
    )

    plot_data = [trace]

    layout = go.Layout(
        font=dict(family='Times', size=16)
    )

    plot(plot_data, layout, 'box-plot-number')


loop_through_files()
print_size_classification(sizeOfChanges)
print_size_classification(numberOfChanges)

box_plot_size_of_changes()
plot_scatter()
plot_histogram_size_of_changes()
plot_histogram_number_of_changes()
box_plot_number_of_changes()
