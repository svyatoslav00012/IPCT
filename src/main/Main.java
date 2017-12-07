package main;

import javafx.application.Application;
import javafx.stage.Stage;
import org.opencv.core.Core;
import view.windows.stage.MyStage;

public class Main extends Application {

    static { System.loadLibrary(Core.NATIVE_LIBRARY_NAME);}

    public static void main(String[] args) {
	    launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        new MyStage("/cascadeTraining/view/view.fxml").animatingShow();
    }
}
