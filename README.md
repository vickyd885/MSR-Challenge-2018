# Tools & Env CW

In an attempt to answer the most important question of all time, **"Do large changes to source code have a negative impact on software engineering?"**, here is the code base to make use of the Mining Repository API.

## Data Filtering

First we need to filter events from the Events Dataset, keeping those we are relevant to answering the question.

To find out more about what each event is, go to [Kave Events Specification]

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
3. DebuggerEvent
  + DebuggerMode
  + Reason
  + Action
4. IDEStateEvent
  + State
5. ErrorEvent
  + Content
  + StackTrace
6. TestRunEvent
  + WasAborted
  + ListOfTests
    + Duration
    + Result
7. VersionControlEvent
  + Action

### Installation and use

The first part requires use to filter data from the massive zip file. Code for this can be found in the `DataFilter` folder.

We use **ant** to build the project and make use of an uberjar with all the required files.

Download and unzip the [Events dataset] into the root of the repo.

```shell
cd DataFilter
ant -Dargs9=$path_to_Events_Dataset
```

Results will be dumped into `DataFilter/output/_user_name_/*.json`

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

## To Do List

- Statistcal analysis
- Write Report


[Kave Events Specification]: http://www.kave.cc/feedbag/event-generation

[Events dataset]: http://www.kave.cc/datasets
