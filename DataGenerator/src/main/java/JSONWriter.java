import java.io.FileWriter;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class JSONWriter{

  public static void writeToJSONFile(JSONObject jsonObject, String fileName){
    try {
      FileWriter fileWriter = new FileWriter("output/" + fileName + ".json");
      fileWriter.write(jsonObject.toJSONString());
      fileWriter.flush();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
