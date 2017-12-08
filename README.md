# Mining Software Repositories Challenge 2018 

This repository supplements our report for Tools and Environment Coursework 2. (This is **not** an actual submission for the MSR 2018 Mining Challenge.) 

Research question: **"Do large changes to source code have a negative impact on software engineering?"*

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

## Results & Conclusions 


[Kave Events Specification]: http://www.kave.cc/feedbag/event-generation

[Events dataset]: http://www.kave.cc/datasets
