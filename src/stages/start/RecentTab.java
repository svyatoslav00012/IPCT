package stages.start;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import main.Main;
import stages.projectStage.NewController;
import view.windows.alert.AlertController;

import java.io.File;

public class RecentTab extends VBox {
	private Label name;
	private Label path;

	public RecentTab(String path){
		setPadding(new Insets(5, 5, 5, 5));
		getStyleClass().add("recentTab");
		this.path = new Label(path);
		this.path.getStyleClass().add("pathLabel");
		name = new Label(path.substring(path.lastIndexOf(File.separatorChar) + 1));
		name.getStyleClass().add("nameLabel");
		getChildren().addAll(this.name, this.path);
		setOnMouseClicked(onC -> openProject());
	}

	private void openProject() {
		if(!new File(path.getText()).exists()) {
			AlertController.showWarning("Such project doesn't exists anymore");
			getVBox().getChildren().remove(this);
			Main.removeProjectFromRecent(path.getText());
			return;
		}
		System.out.println("Open : " + path.getText());
		NewController.openProject(path.getText());
	}

	public VBox getVBox(){
		return (VBox)getParent();
	}
}
