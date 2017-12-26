package stages.start;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import main.Main;
import stages.start.newProject.NewProjectController;
import view.windows.stage.MyStage;

public class StartController {
	public static final String START_FXML = "/stages/start/startView.fxml";
	private final String LOGO = "/images/logo.png";

	@FXML
	private ImageView logoView;
	@FXML
	private VBox recentProjects;

	public void initialize(){
		initRecent();
		logoView.setImage(new Image(LOGO));
	}

	private void initRecent() {
		String proj = "p";
		String curPath = null;
		for (int i = 0; (curPath = Main.PREFS.get(proj + i, null)) != null; ++i){
			recentProjects.getChildren().add(new RecentTab(curPath));
		}
	}

	public void createNew(ActionEvent actionEvent) {
		NewProjectController.createNewProject();
	}
}
