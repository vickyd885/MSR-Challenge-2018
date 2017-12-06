/*
* FileWriter writes the json object to file
* the hardcoded file location is "/output"
*/

import java.io.FileWriter;
import java.io.File;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class JSONWriter{

  public static void writeToJSONFile(JSONObject jsonObject, String fileName){
    try {
      File f = new File("output/" + fileName.split("/")[0]);
      if(!f.exists()){
        f.mkdirs();
      }
      FileWriter fileWriter = new FileWriter("output/" + fileName + ".json");
      fileWriter.write(jsonObject.toJSONString());
      fileWriter.flush();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
