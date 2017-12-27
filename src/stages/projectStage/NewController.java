package stages.projectStage;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import main.Main;
import model.Project;
import model.helpers.ImageProcessor;
import model.helpers.OpenCVUtils;
import stages.nodes.imageList.ImageList;
import stages.nodes.imageList.imagePane.ImagePane;
import view.nodes.wcbPane.WCBTypes;
import view.windows.alert.AlertController;
import view.windows.notifications.NotificationsController;
import view.windows.stage.MyStage;

import java.io.File;
import java.util.ArrayList;

public class NewController {

	public static final String FXML = "/stages/projectStage/newView.fxml";

	private Project curProject;

	@FXML
	private Label showPrepLabel;
	@FXML
	private Label showCreateSamplesLabel;
	@FXML
	private Label showTrainLabel;

	@FXML
	private StackPane stackPane;

	@FXML
	private AnchorPane imagePreparationWindow;

	@FXML
	private ImageList positiveImList;
	@FXML
	private ImageList negativeImList;

	@FXML
	private AnchorPane createSamplesWindow;

	@FXML
	private TextField crSampHeightField;
	@FXML
	private TextField crSampWidthField;

	@FXML
	private AnchorPane trainCascadeWindow;


	public void initialize(){
		showPrepWindow();
	}

	public void initProject(Project project){
		this.curProject = project;
		System.out.println(curProject.getInfo().getAbsolutePath());
		positiveImList.linkTo(curProject.getInfo());
		negativeImList.linkTo(curProject.getBg());
	}

	public void showPrepWindow(){
		imagePreparationWindow.toFront();
		selectLabel(showPrepLabel, showCreateSamplesLabel, showTrainLabel);
	}

	public void showCreateSamplesWindow(){
		createSamplesWindow.toFront();
		selectLabel(showCreateSamplesLabel, showPrepLabel, showTrainLabel);
	}

	public void showTrainCascadeWindow(){
		trainCascadeWindow.toFront();
		selectLabel(showTrainLabel, showPrepLabel, showCreateSamplesLabel);
	}

	private void selectLabel(Label selected, Label unselected1, Label unselected2){
		selected.getStyleClass().remove("unselected");
		selected.getStyleClass().add("selected");

		unselected1.getStyleClass().remove("selected");
		if(!unselected1.getStyleClass().contains("unselected"))
			unselected1.getStyleClass().add("unselected");
		unselected2.getStyleClass().remove("selected");
		if(!unselected2.getStyleClass().contains("unselected"))
			unselected2.getStyleClass().add("unselected");
	}

	public Project getProject() {
		return curProject;
	}

	public void annotate(ActionEvent actionEvent) {
		ArrayList<ImagePane> images =  positiveImList.getSelected();
		if(images.isEmpty()){
			AlertController.showWarning("You didn't choose anything");
			return;
		}
		if(!AlertController.showConfirm("Annotate choosen images?"))
			return;
		curProject.annotate(positiveImList.getSelectedFiles());
		System.out.println("update");
		positiveImList.update(positiveImList.readFile());
	}

	public void duplicate(ActionEvent actionEvent) {

	}

	public void createSamples(){
		ArrayList<File> annotated = positiveImList.getAnnotated();
		if(annotated.isEmpty()){
			AlertController.showWarning("You haven't got annotated images");
			return;
		}
		curProject.createSamples(annotated, getCreateSamplesWidth(), getCreateSamplesHeight());
	}

	/**
	 * static methods
	 */


	public static void openProject(String path){
		openProject(new File(path));
	}

	public static void openProject(File dir){
		openProject(new Project(dir));
	}

	public static void openProject(Project project){
		Main.PREFS.put(Main.LAST_PROJECT, project.getLocation().getAbsolutePath());
		MyStage projectStage = new MyStage(FXML, WCBTypes.ICONIFIED, WCBTypes.MXMIZE, WCBTypes.CLOSE);
		//projectStage.setOnHidden(h -> project.close());
		((NewController)projectStage.getLoader().getController()).initProject(project);
		projectStage.setTitleText(project.getName());
		projectStage.animatingShow();
	}

	public int getCreateSamplesWidth(){
		return crSampWidthField.getText().isEmpty() ? 30 : Integer.parseInt(crSampWidthField.getText());
	}

	public int getCreateSamplesHeight(){
		return crSampHeightField.getText().isEmpty() ? 30 : Integer.parseInt(crSampHeightField.getText());
	}
}
