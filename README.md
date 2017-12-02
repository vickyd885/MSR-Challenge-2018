# Tools & Env CW

In an attempt to answer the most important question of all time, **"Do large changes to source code have a negative impact on software engineering?"**, here is the code base to make use of the Mining Repository API.

## DataGenerator

This module filters for this set of data from the Events Dataset.

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

### Installation and use

The first part requires use to filter data from the massive zip file. Code for this can be found in the `DataGenerator` folder.

We use ant to build the project and make use of an uberjar with all the required files.

The only file you should need to change (for now) is,
`DataGenerator/src/main/java/Filter.java`.

You need to update the _directory path_ inside the Filter.java folder to point to your massive zip folder.

**TODO:** update this to use args

To run, simply call `ant` in `/DataGenerator`.

For every user, the program will dump the associated data file in `DataGenerator/output/$userCount.json`

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
The intention is that you can use the `event_type` to access the keys in `specific_data`
as its dependent on it.

## To Do List

- Statistcal analysis
- Write Report


[Kave Events Specification]: http://www.kave.cc/feedbag/event-generation
