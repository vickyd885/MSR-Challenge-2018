# Mining Software Repositories Challenge 2018 

This repository supplements our report for Tools and Environment Coursework 2. (This is **not** an actual submission for the MSR 2018 Mining Challenge.) 

Research question: **Do large changes to source code have a negative impact on software projects?*

## Overview

There are two stages of importance. 
First, we need to filter events from the Events Dataset, keeping those that are relevant to answering the question. 
We then perform statistical analysis on the filtered dataset. You can find our results and conclusions at the end of this README.

## Data Filtering

To find out more about what each event is, go to [Kave Events Specification]

Here are the events we are interested in and the following data we capture from the filtering stage.

1. EditEvent
  + NumberOfChanges
  + SizeOfChanges
2. BuildEvent
  + Scope
  + Action
  + ListOfBuilds
    + StartedAt
    + Duration
    + Successful
3. TestRunEvent
  + WasAborted
  + ListOfTests
    + Duration
    + Result

### Installation and usage

The first part requires us to filter data from the raw dataset.

Code for this can be found in the `DataFilter` folder.

We use **ant** to build the project and make use of an uberjar with all the required files.

Download and unzip the [Events dataset] into the root of the repo.

```shell
cd DataFilter
ant -Darg0=$absolute_path_to_Events_Dataset
```

Results will be saved to `DataFilter/output/_user_name_/*.json`

The output json format looks like this:

```json
{
  "n": [
    {
      "time_stamp": "..",
      "event_type" : "..",
      "specific_data": {
      }
    }
  ]
}
```

The intention is that you can use the `event_type` to access the keys in `specific_data` as its dependent on it. (Exact info is listed above ^)

## Statistical Analysis

This part of the project is split into two parts: `SizeClassification` and `dataVisualisation`, with corrseponding folders.

The first part categorises te size of changes into extra-small to extra-large changes. The second part contains the code for analysing the impact of large code changes on software development.

Before running the code, make sure the data (generated from `DataFilter`) is in a folder named `output` and in both folders.

### SizeClassification

Files: `size_classification.py`, `graph_plot.py`, `size_distribution_pie`

`size_classification.py` filters through EditEvents and uses a five-number summary to categorise each of the sizes from extra-small to extra-large.

`graph_plot.py` produces various histograms and box plot showing the distribution of all changes in relation to the size of each change.

`size_distribution_pie` uses the size categories and produces a pie chart showing the percentage of each size out of all edit events.

To run:

```
python size_classification.py
python graph_plot.py
python size_distribution_pie.py
```

### dataVisualisation

Navigate to the data visualisation folder

```shell
cd dataVisualisation
```

To generate the graphs on the data for a single user, run 

```
python visualisation.py
```

To generate the graphs for the entire dataset, run graph.py

```
python graph.py
```

## Results & Conclusions 

![alt text](https://github.com/vickyd885/ToolsCW/blob/master/dataVisualisation/graph1.jpg "Description goes here")

![alt text](https://github.com/vickyd885/ToolsCW/blob/master/dataVisualisation/graph2.jpg "Description goes here")

![alt text](https://github.com/vickyd885/ToolsCW/blob/master/dataVisualisation/graph3.jpg "Description goes here")

![alt text](https://github.com/vickyd885/ToolsCW/blob/master/dataVisualisation/graph4.jpg "Description goes here")

![alt text](https://github.com/vickyd885/ToolsCW/blob/master/dataVisualisation/graph5.jpg "Description goes here")

![alt text](https://github.com/vickyd885/ToolsCW/blob/master/dataVisualisation/graph6.jpg "Description goes here")

[Kave Events Specification]: http://www.kave.cc/feedbag/event-generation

[Events dataset]: http://www.kave.cc/datasets
