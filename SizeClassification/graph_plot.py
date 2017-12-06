import os
import json
import numpy as np
import plotly
import plotly.graph_objs as go

rootdir = '/Users/jasminelu/Desktop/ToolsCW/SizeClassification/output'

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
                numberOfChangesLog.append(np.log(item['specific_data']['NumberOfChanges']))
                sizeOfChangesLog.append(np.log(item['specific_data']['SizeOfChanges']))

def plot_scatter():
    trace = go.Scatter(
        x = numberOfChangesLog,
        y = sizeOfChangesLog,
        mode = 'markers'
    )

    plot_data = [trace]
    layout = go.Layout(
        xaxis=dict(
            title='Number of Changes (Natural Log)',
        ),
        yaxis=dict(
            title='Size of Changes (Natural Log)',
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
        xaxis=dict(
            title='Size of Changes (Natural Log)',
        )
    )

    plot(plot_data, layout, 'box-plot-size')

def box_plot_number_of_changes():
    trace = go.Box(
        x = numberOfChangesLog,
        boxpoints = False
    )

    plot_data = [trace]

    layout = go.Layout(
        xaxis=dict(
            title='Number of Changes (Natural Log)',
        ),
    )

    plot(plot_data, layout, 'box-plot-number')


loop_through_files()

box_plot_size_of_changes()
plot_scatter()
plot_histogram_size_of_changes()
plot_histogram_number_of_changes()
box_plot_number_of_changes()
