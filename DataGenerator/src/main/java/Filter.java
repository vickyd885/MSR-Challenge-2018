
import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import com.google.common.collect.Lists;

import cc.kave.commons.model.events.visualstudio.EditEvent;
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

        if(limit > 10) break;
        
			}
			ra.close();
      dc.flushData();
		}
	}

	/**
	 * 4: Processing events
	 */
	private static void process(IIDEEvent event, DataCollector dc) {
		// once you have access to the instantiated event you can dispatch the
		// type. As the events are not nested, we did not implement the visitor
		// pattern, but resorted to instanceof checks.
    //System.out.println(event);

    String timeStamp = event.getTriggeredAt().toString();
    String eventType = null;

		if (event instanceof EditEvent) {
			// if the correct type is identified, you can cast it...
			EditEvent ee = (EditEvent) event;

      eventType = "EditEvent";


			// ...and access the special context for this kind of event
			//System.out.println(ee);

      // System.out.println("NumberOfChanges: " + ee.NumberOfChanges);
      // System.out.println("SizeOfChanges: " + ee.SizeOfChanges);
      // System.out.println();
		} else if( event instanceof VersionControlEvent) {
      //
      // VersionControlEvent vce = (VersionControlEvent) event;
      //
      // System.out.println("VCE! ");
      // System.out.println();
      //
      // List<VersionControlAction> listOfActions = vce.Actions;
      //
      // System.out.println("List of actions for that VCE...");
      // for(VersionControlAction action : listOfActions){
      //
      //   System.out.println(action);
      //   System.out.print("Type " + action.ActionType);
      //
      // }

    }else{
			// there a many different event types to process, it is recommended
			// that you browse the package to see all types and consult the
			// website for the documentation of the semantics of each event...
		}

    if(eventType != null){
      dc.handleNewEntry(timeStamp, eventType);
      limit++;
    }


	}




}
