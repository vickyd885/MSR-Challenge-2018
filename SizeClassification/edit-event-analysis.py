import os
import json
import numpy as np
import plotly
import plotly.graph_objs as go

rootdir = '/Users/jasminelu/Desktop/ToolsCW/DataAnalysis/output'

numberOfChanges = []
sizeOfChanges = []

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
            numberOfChanges.append(np.log(item['specific_data']['NumberOfChanges']))
            sizeOfChanges.append(item['specific_data']['SizeOfChanges'])

def plot_scatter():
    trace = go.Scatter(
        x = numberOfChanges,
        y = sizeOfChanges,
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
        x = numberOfChanges
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
        x = sizeOfChanges
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
    SizeOfChanges = go.Box(
        x = sizeOfChanges
    )

    plot_data = [SizeOfChanges]

    layout = go.Layout(
        font=dict(family='Times', size=16)
    )

    plot(plot_data, layout, 'box-plot-size')

def box_plot_number_of_changes():
    NumberOfChanges = go.Box(x=numberOfChanges)
    plot_data = [NumberOfChanges]
    plot(plot_data, {}, 'box-plot-number')


loop_through_files()
box_plot_size_of_changes()
# plot_scatter()
# plot_histogram_size_of_changes()
# plot_histogram_number_of_changes()
# box_plot_number_of_changes()
