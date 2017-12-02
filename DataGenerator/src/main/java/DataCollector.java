
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Set;

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
    String eventType,
    HashMap<String, Object> specificData
  ){
    this.parent.put(
      ++this.entryCount,
      addNewEntry(timeStamp, eventType, specificData)
    );

    System.out.println("Added new entry!");
  }

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












  public void flushData(){
    JSONWriter.writeToJSONFile(this.parent, this.fileName);
  }


}
