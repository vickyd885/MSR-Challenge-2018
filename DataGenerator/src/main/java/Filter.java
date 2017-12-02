
import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import com.google.common.collect.Lists;

// Events we are interested in
import cc.kave.commons.model.events.visualstudio.*;
import cc.kave.commons.model.events.testrunevents.*;
import cc.kave.commons.model.events.ErrorEvent;


import cc.kave.commons.model.events.IIDEEvent;
import cc.kave.commons.model.events.versioncontrolevents.VersionControlEvent;
import cc.kave.commons.model.events.versioncontrolevents.VersionControlAction;
import cc.kave.commons.model.events.versioncontrolevents.VersionControlActionType;
import cc.kave.commons.utils.json.JsonUtils;
import cc.recommenders.io.ReadingArchive;




public class Filter{

  private static final String DIR_USERDATA = "/Users/VickyD/Desktop/Year4/ToolsAndEnvironments/ToolsCW/Events-170301/";

  private static int limit = 0;


  private static int userCount = 0;

  public static void main(String[] args) {

    //readPlainEvents();

    // List<String> users = findAllUsers();
    //
    // //for(String x : users) System.out.println(x);
    //
    // System.out.println("Running");

    readPlainEvents();
  }

  /**
  * 1: Find all users in the dataset.
  */
  public static List<String> findAllUsers() {
  // This step is straight forward, as events are grouped by user. Each
  // .zip file in the dataset corresponds to one user.

    List<String> zips = Lists.newLinkedList();
    for (File f : FileUtils.listFiles(new File(DIR_USERDATA), new String[] { "zip" }, true)) {
      zips.add(f.getAbsolutePath());
      break;
    }
    return zips;
  }

  // /**
	//  * 2: Reading events
	//  */
	// public static void readAllEvents() {
	// 	// each .zip file corresponds to a user
	// 	List<String> userZips = findAllUsers();
  //
	// 	for (String user : userZips) {
	// 		// you can use our helper to open a file...
	// 		ReadingArchive ra = new ReadingArchive(new File(user));
	// 		// ...iterate over it...
	// 		while (ra.hasNext()) {
	// 			// ... and desrialize the IDE event.
	// 			IIDEEvent e = ra.getNext(IIDEEvent.class);
	// 			// afterwards, you can process it as a Java object
	// 			//process(e);
  //
  //
	// 		}
	// 		ra.close();
	// 	}
	// }

	/**
	 * 3: Reading the plain JSON representation
	 */
	public static void readPlainEvents() {
		// the example is basically the same as before, but...
		List<String> userZips = findAllUsers();

		for (String user : userZips) {

      DataCollector dc = new DataCollector(Integer.toString(++userCount));
			ReadingArchive ra = new ReadingArchive(new File(user));
			while (ra.hasNext()) {
				// ... sometimes it is easier to just read the JSON...
				String json = ra.getNextPlain();
				// .. and call the deserializer yourself.
				IIDEEvent e = JsonUtils.fromJson(json, IIDEEvent.class);

        // Process the event
        process(e, dc);

				// Not all event bindings are very stable already, reading the
				// JSON helps debugging possible bugs in the bindings

        if(limit > 500) break;

			}
			ra.close();
      dc.flushData();
		}
	}

	/**
	 * 4: Processing events
	 */
	private static void process(IIDEEvent event, DataCollector dc) {

    String timeStamp = event.getTriggeredAt().toString();
    String eventType = null;
    HashMap<String, Object> specificData = new HashMap<String, Object>();

		if (event instanceof EditEvent) {
			EditEvent ee = (EditEvent) event;
      eventType = "EditEvent";
      specificData.put("NumberOfChanges", ee.NumberOfChanges);
      specificData.put("SizeOfChanges", ee.SizeOfChanges);
		} else if(event instanceof BuildEvent) {

      //System.out.println("Looking at BE");
      BuildEvent be = (BuildEvent) event;
      eventType = "BuildEvent";
      specificData.put("Scope", be.Scope);
      specificData.put("Action", be.Action);

      List<BuildTarget> buildTargets = be.Targets;
      HashMap<Integer, Object> buildData = new HashMap<Integer, Object>();

      int buildTargetCount = 0;
      for(BuildTarget bt : buildTargets){
        HashMap<String, Object> localBTMap = new HashMap<String, Object>();
        localBTMap.put("StartedAt", bt.StartedAt.toString());
        localBTMap.put("Duration", bt.Duration.toString());
        localBTMap.put("Successful", bt.Successful);

        buildData.put(++buildTargetCount, localBTMap);
      }

      specificData.put("build_data", buildData);

    } else if(event instanceof DebuggerEvent){
      DebuggerEvent de = (DebuggerEvent) event;
      eventType = "DebuggerEvent";

      specificData.put("DebuggerMode", de.Mode.toString());
      specificData.put("Reason", de.Reason);
      specificData.put("Action", de.Action);

    } else if(event instanceof IDEStateEvent){
      IDEStateEvent idse = (IDEStateEvent) event;

      eventType = "LifeCyclePhase";
      specificData.put("state", idse.IDELifecyclePhase.toString());

    } else if(event instanceof ErrorEvent){
      ErrorEvent ee = (ErrorEvent)event;

      eventType = "ErrorEvent";
      specificData.put("Content", ee.Content);
      specificData.put("StackTrace", ee.StackTrace);

    } else if(event instanceof TestRunEvent){
      TestRunEvent tre = (TestRunEvent) event;

      eventType = "TestRunEvent";
      specificData.put("WasAborted", tre.WasAborted);

      HashMap<Integer, Object> testResults = new HashMap<Integer, Object>();

      Set<TestCaseResult> tests = tre.Tests;

      int testCount = 0;
      for(TestCaseResult tcr : tests){
        HashMap<String, Object> localTestResult = new HashMap<String, Object>();
        localTestResult.put("Duration", tcr.Duration.toString());
        localTestResult.put("Result", tcr.Result.toString());
        testResults.put(++testCount, localTestResult);
      }
      specificData.put("Tests", testResults);
    } else{
      // Looking at an undesired event
		}

    if(eventType != null){
      dc.handleNewEntry(timeStamp, eventType, specificData);
      limit++;
    }


	}






}
