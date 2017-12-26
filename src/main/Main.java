package main;

import javafx.application.Application;
import javafx.stage.Stage;
import model.Project;
import org.opencv.core.Core;
import stages.start.StartController;
import view.windows.stage.MyStage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class Main extends Application {

    static { System.loadLibrary(Core.NATIVE_LIBRARY_NAME);}


    public static ArrayList<Project> PROJECTS = new ArrayList<>();
    public static Preferences PREFS = Preferences.userNodeForPackage(Main.class);

    public static void stopALL(){
    	for(Project project: PROJECTS){
    		project.close();
		}
	}

    public static final String INITIAL_DIR = "INITIAL_DIR";
	public static final String LAST_PROJECT = "LAST_PROJECT";

    public static void main(String[] args) {
	    launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
		new MyStage(StartController.START_FXML).animatingShow();
    }

    @Override
	public void stop() throws Exception{
		System.out.println("STOP");
		stopALL();
	}
    public static void addProjectToRecent(String path){
    	String p = "p";
    	int i = 0;
    	for(i = 0; PREFS.get(p + i, null) != null; ++i);
    	PREFS.put(p + i, path);
	}

	public static void removeProjectFromRecent(String text) {
		try {
			for(String s : PREFS.keys())
				if(PREFS.get(s, "").equals(text)){
					PREFS.remove(s);
					return;
				}
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}
}
