package stages.start.newProject;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import main.Main;
import model.Project;
import stages.projectStage.NewController;
import view.windows.alert.AlertController;
import view.windows.notifications.NotificationsController;
import view.windows.stage.MyStage;

import java.io.File;
import java.io.IOException;

public class NewProjectController {
	public static final String FXML = "/stages/start/newProject/newProjectView.fxml";

	public TextField nameField;
	public TextField locatonField;

	public void initialize() {
		locatonField.setText(Main.PREFS.get(Main.INITIAL_DIR, "") + File.separatorChar + nameField.getText());
	}

	public static void createNewProject() {
		MyStage create = new MyStage(FXML);
		create.initModality(Modality.APPLICATION_MODAL);
		create.animatingShow();
	}

	public void setLocation(ActionEvent actionEvent) {
		File choice = null;
		DirectoryChooser chooseDir = new DirectoryChooser();
		chooseDir.setTitle("Choose directory project");
		chooseDir.setInitialDirectory(new File(Main.PREFS.get(Main.INITIAL_DIR, System.getProperty("user.dir"))));
		choice = chooseDir.showDialog(nameField.getScene().getWindow());
		if (choice != null) {
			locatonField.setText(choice.getAbsolutePath() + File.separatorChar + nameField.getText());
			Main.PREFS.put(Main.INITIAL_DIR, choice.getAbsolutePath());
		}

	}

	public void create(ActionEvent actionEvent) {
		File newProject = new File(locatonField.getText());
		if (!new File(getLocation()).exists()) {
			AlertController.showWarning("Such directory doesn't exists");
			return;
		}
		if (newProject.exists() && !AlertController.showConfirm("Such directory already exists, do you want to use it?")) {
			return;
		}
		Project project = new Project(newProject);
		project.create();
		((MyStage)nameField.getScene().getWindow()).animatingClose();
		Main.START_STAGE.animatingClose();
		NotificationsController.showComplete("Project successfully created");
		NewController.openProject(project);
	}

	public void nameChanged(KeyEvent keyEvent) {
		locatonField.setText(
				getLocation() + nameField.getText()
		);
	}

	private String getLocation() {
		return locatonField.getText().substring(
				0,
				locatonField.getText().lastIndexOf(File.separatorChar) + 1
		);
	}
}
