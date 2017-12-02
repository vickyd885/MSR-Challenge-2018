/*
* DataCollector holds the json object, and is responsible for conversions
* between java types to json types.
* DataCollector does not invoke the writing to json mechanism, and should be
* done by the user of DataCollector.
*/

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Set;

// time library
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Period;

public class DataCollector{

  private JSONObject parent; // Reference to main body
  private String fileName; // filename that wile be flushed
  private int entryCount; // reference to latest entry in json

  private DateTime start;

  public DataCollector(String fileName){
    this.parent = new JSONObject();
    this.fileName = fileName;
    this.entryCount = 0;
    this.start = new DateTime();
  }

  // Adds a number : [...] to help with order
  public void handleNewEntry(
    String timeStamp,
    String eventType,
    HashMap<String, Object> specificData
  ){
    this.parent.put(
      ++this.entryCount,
      addNewEntry(timeStamp, eventType, specificData)
    );

    System.out.println("Added new entry!");
  }

  // Creates the actual json object
  // We deal with the hashmap here
  public JSONObject addNewEntry(
    String timeStamp,
    String eventType,
    HashMap<String, Object> specificDataMap
  ){
    JSONObject newEntry = new JSONObject();

    newEntry.put("time_stamp", timeStamp);
    newEntry.put("event_type", eventType);

    JSONObject specificDataObj = new JSONObject();

    Set<String> keySet = specificDataMap.keySet();

    for(String key : keySet){
      specificDataObj.put(key, specificDataMap.get(key));
    }

    newEntry.put("specific_data", specificDataObj);

    return newEntry;
  }


  public int getNumOfEvents(){
    return this.entryCount;
  }
  // Write all the json data to the file
  public void flushData(){
    JSONWriter.writeToJSONFile(this.parent, this.fileName);
  }

  // Prints amount of time passed since DataCollector was initialised
  public void printTimeInfo(){
    DateTime now = new DateTime();
    Interval interval = new Interval(this.start, now);
    Period period = interval.toPeriod();
    System.out.println("Time taken for user " + this.fileName +
      " was ... " + period.getSeconds() +
      " (seconds)!");
  }

}
