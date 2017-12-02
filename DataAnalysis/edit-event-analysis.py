import json
import numpy as np
import plotly
import plotly.graph_objs as go

json_data = open('1.json').read()
data = json.loads(json_data)

numberOfChanges = []
sizeOfChanges = []

def extract_edit_event_data(obj):
    for item in data.values():
        if item['event_type'] == 'EditEvent':
            numberOfChanges.append(np.log(item['specific_data']['NumberOfChanges']))
            sizeOfChanges.append(np.log(item['specific_data']['SizeOfChanges']))

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

extract_edit_event_data(data)
plot_scatter()
plot_histogram_size_of_changes()
plot_histogram_number_of_changes()
