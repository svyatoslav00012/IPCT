package stages.projectStage;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import model.Project;
import stages.nodes.imageList.ImageList;
import view.nodes.wcbPane.WCBTypes;
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
	private AnchorPane createSamplesWindow;
	@FXML
	private AnchorPane trainCascadeWindow;
	@FXML
	private ImageList positiveImList;
	@FXML
	private ImageList negativeImList;

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
		MyStage projectStage = new MyStage(FXML, WCBTypes.ICONIFIED, WCBTypes.MXMIZE, WCBTypes.CLOSE);
		//projectStage.setOnHidden(h -> project.close());
		((NewController)projectStage.getLoader().getController()).initProject(project);
		projectStage.animatingShow();
	}

	public Project getProject() {
		return curProject;
	}
}
