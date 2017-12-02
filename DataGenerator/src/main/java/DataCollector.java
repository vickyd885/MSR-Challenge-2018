
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;

public class DataCollector{

  private JSONObject parent;
  private String fileName;
  private int entryCount;

  public DataCollector(String fileName){
    this.parent = new JSONObject();
    this.fileName = fileName;
    this.entryCount = 0;
  }

  public void handleNewEntry(
    String timeStamp,
    String eventType
  ){
    this.parent.put(
      ++this.entryCount,
      addNewEntry(timeStamp, eventType)
    );

    System.out.println("Added new entry!");
  }

  public JSONObject addNewEntry(
    String timeStamp,
    String eventType
  ){
    JSONObject newEntry = new JSONObject();

    newEntry.put("time_stamp", timeStamp);
    newEntry.put("event_type", eventType);

    JSONArray specificData = new JSONArray();

    specificData.add("test");

    newEntry.put("specific_data", specificData);

    return newEntry;
  }












  public void flushData(){
    JSONWriter.writeToJSONFile(this.parent, this.fileName);
  }


}
