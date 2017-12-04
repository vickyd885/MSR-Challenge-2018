/*
* Purpose of Filter is to filter out events based on what we're interested in.
* It calls DataCollector per every user, which is responsible for handling the
* JSON creation.
*/
import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Queue;
import java.util.Comparator;
import java.util.PriorityQueue;

import org.apache.commons.io.FileUtils;
import com.google.common.collect.Lists;

// Events we are interested in
import cc.kave.commons.model.events.visualstudio.*;
import cc.kave.commons.model.events.testrunevents.*;
import cc.kave.commons.model.events.ErrorEvent;
import cc.kave.commons.model.events.versioncontrolevents.VersionControlEvent;
import cc.kave.commons.model.events.versioncontrolevents.VersionControlAction;
import cc.kave.commons.model.events.versioncontrolevents.VersionControlActionType;


//Other KAVE imports
import cc.kave.commons.model.events.IIDEEvent;
import cc.kave.commons.utils.json.JsonUtils;
import cc.recommenders.io.ReadingArchive;

public class Filter{

  private static String DIR_USERDATA; // "/Users/VickyD/Desktop/Year4/ToolsAndEnvironments/ToolsCW/Events-170301/";

  // limit to stop adding events to. Useful for debugging
  private static int eventLimit = 500;
  // Number of users processed, used for setting the file name, e.g 1.json
  private static int userCount = 0;

  public static void main(String[] args) {
    if(args.length == 0){
      System.out.println("You forgot to specify the dataset path");
    }else if(args.length > 1){
      System.out.println("Too many arguments!");
    }

    DIR_USERDATA = args[0];

    readPlainEvents();
  }

  /**
  * Find all users in the dataset.
  */
  public static  List<String> findAllUsers() {
  // This step is straight forward, as events are grouped by user. Each
  // .zip file in the dataset corresponds to one user.

    List<String> zips = Lists.newLinkedList();
    for (File f : FileUtils.listFiles(new File(DIR_USERDATA), new String[] { "zip" }, true)) {
      zips.add(f.getAbsolutePath());
    }
    return zips;
  }


	/**
	 * Reading the plain JSON representation
	 */
	public static void readPlainEvents() {
		// the example is basically the same as before, but...
		List<String> userZips = findAllUsers();
    Queue<String> fileQueue = prepareReadQueue(userZips);

		while(!fileQueue.isEmpty()){

      //System.out.println(user);

      String user = fileQueue.poll();
      String folderName = createFilePath(user);

      if(folderName == null) continue;

      //System.out.println(folderName);

      DataCollector dc = new DataCollector(folderName);
			ReadingArchive ra = new ReadingArchive(new File(user));
			while (ra.hasNext()) {

				// ... sometimes it is easier to just read the JSON...
				String json = ra.getNextPlain();
				// .. and call the deserializer yourself.
				IIDEEvent e = JsonUtils.fromJson(json, IIDEEvent.class);

        // Process the event
        process(e, dc);

        // Uncomment to stop at a certain event count
        //if(dc.getNumOfEvents() > 10) break;

			}
			ra.close();
      dc.flushData();
      dc.printTimeInfo();
      //dc.showAllKeysInTM();
		}
	}

	/**
	 * 4: Processing events
   *
   * We process the event to check if it is a type we are interested in.
   * We also pass the DataCollector reference so it can add it to the json
   * per user.
   *
   * There is some HashMap<String, Object> magic happening
	 */
	private static void process(IIDEEvent event, DataCollector dc) {

    // Assume we're going to add the event, so we get the timestamp,
    // set up an eventType string and a data representation of what could
    // be added to it.
    //
    // In the end, if eventType is still null, then we don't add the event to
    // json.
    String timeStamp = event.getTriggeredAt().toString();

    String eventType = null;
    HashMap<String, Object> specificData = new HashMap<String, Object>();


    // For each event type, we have to manually add some keys and their assoc. values
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

      specificData.put("ListOfBuilds", buildData);

    } else if(event instanceof DebuggerEvent){
      DebuggerEvent de = (DebuggerEvent) event;
      eventType = "DebuggerEvent";

      specificData.put("DebuggerMode", de.Mode.toString());
      specificData.put("Reason", de.Reason);
      specificData.put("Action", de.Action);

    } else if(event instanceof IDEStateEvent){
      IDEStateEvent idse = (IDEStateEvent) event;

      eventType = "LifeCyclePhase";
      specificData.put("State", idse.IDELifecyclePhase.toString());

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

      //System.out.println("Number of test cases: " + testCount);
      specificData.put("ListOfTests", testResults);
    } else if(event instanceof VersionControlEvent){
      VersionControlEvent vce = (VersionControlEvent) event;

      //System.out.println(vce);
      List<VersionControlAction> listOfvca = vce.Actions;

      // System.out.println("List of VCA");
      for(VersionControlAction vca : listOfvca){
        eventType = "VersionControlEvent";
        timeStamp = vca.ExecutedAt.toString();
        specificData.put("Action", vca.ActionType.toString());
        dc.handleNewEntry(timeStamp, eventType, specificData);
      }

    } else{
      // Looking at an undesired event so do nothing
		}

    // if the eventType is not set, then we're not interested in the event,
    // and so it doesnt get added to the json
    // we don't add version control events because that's handled in else if case
    if(eventType != null && !eventType.equals("VersionControlEvent")){
      dc.handleNewEntry(timeStamp, eventType, specificData);
    }
	}


  public static String createFilePath(String user){
    String folderName = null;
    try{
      String[] fractions = user.split("/");
      // the containing folder is always 2 less
      folderName = fractions[fractions.length-2];
      folderName += "/";
      folderName += Integer.toString(++userCount);
    } catch(Exception e){
      System.out.println("Caught an exception for ... " + user);
    }
    System.out.println("Beginning extraction for: " + user);
    return folderName;
  }

  /*
  * Build a queue based on size of file, so processes can happen faster
  *
  */
  public static Queue<String> prepareReadQueue(List<String> files){
    Queue<String> fileQueue = new PriorityQueue<>(5,fileSizeComparator);

    for(String fileName : files){
      fileQueue.offer(fileName);
    }

    return fileQueue;
  }

  // Custom compartor to compare the file sizes
  public static Comparator<String> fileSizeComparator = new Comparator<String>(){
    @Override
    public int compare(String s1, String s2) {
            File f1 = new File(s1);
            File f2 = new File(s2);

            return (int) (f1.length() - f2.length());
        }
    };



}
