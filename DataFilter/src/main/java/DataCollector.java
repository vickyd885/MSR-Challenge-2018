/*
* DataCollector holds the json object, and is responsible for conversions
* between java types to json types.
* DataCollector does not invoke the writing to json mechanism, and should be
* done by the user of DataCollector.
*/

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.TreeMap;
import java.util.Set;

// java time library - this is what kave uses
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;


// joda time library
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Period;

public class DataCollector{

  private JSONObject parent; // Reference to main body
  private String fileName; // filename that wile be flushed
  private int entryCount; // reference to latest entry in json
  private HashMap<String, Integer> eventTypes; //keep occurrences of event types found
  private DateTime start;
  private static int objectsFound = 0; // amount of JSONS found in this run, static as this needs to be shared across all instances
  private TreeMap<ZonedDateTime, JSONObject> tmap;

  private DateTimeFormatter dformat = DateTimeFormatter.ofPattern("HHmm:ss, dd MMM yyyy");

  public DataCollector(String fileName){
    this.parent = new JSONObject();
    this.fileName = fileName;
    this.entryCount = 0;
    this.start = new DateTime();
    this.tmap = new TreeMap<ZonedDateTime, JSONObject>();
    this.eventTypes = new HashMap<String, Integer>();
  }

  // Adds a number : [...] to help with order
  public void handleNewEntry(
    String timeStamp,
    String eventType,
    HashMap<String, Object> specificData
  ){
    ZonedDateTime zdt = ZonedDateTime.parse(timeStamp);

    this.tmap.put(
      zdt,
      addNewEntry(timeStamp, eventType, specificData)
    );

    ++this.entryCount;

    //System.out.println("Added new entry!");
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
    
    if(eventTypes.containsKey(eventType)==false){ //This event type is new, so initialise the value to 1
    	eventTypes.put(eventType, 1);
    }else{
    	eventTypes.put(eventType, eventTypes.get(eventType)+1);
    }
    
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
    populateJSONBody();
    JSONWriter.writeToJSONFile(this.parent, this.fileName);
  }

  // Prints amount of time passed since DataCollector was initialised
  public void printTimeInfo(){
    DateTime now = new DateTime();
    Interval interval = new Interval(this.start, now);
    Period period = interval.toPeriod();
    objectsFound++;
    System.out.println("Event types found for user " + this.fileName + ": " + eventTypes);
    System.out.println("Time taken for user " + this.fileName +
      " was " + period.getSeconds() +
      " seconds");
    System.out.println("Objects found so far: " + objectsFound);
  }

  public void showAllKeysInTM(){
    Set<ZonedDateTime> dateSet = this.tmap.keySet();
    for(ZonedDateTime zdt : dateSet){
      System.out.println(zdt.format(dformat));
    }
  }

  private void populateJSONBody(){
    Set<ZonedDateTime> dateSet = this.tmap.keySet();
    int i = 0;
    for(ZonedDateTime zdt : dateSet){
      this.parent.put(
        ++i,
        this.tmap.get(zdt)
      );
    }
  }

}
