package stages.nodes.imageList.imagePane;

import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import model.Project;
import model.helpers.FileProcessor;

import java.io.File;

public class ImagePane extends AnchorPane {

	static class Status{
		public static final String ANNOTATED = "A";
		public static final String COPY = "C";
	}

	private VBox imageBox;
	private ImageView imageView;
	private Label statusLabel;
	private CheckBox checkBox;

	public void initNodes(){
		imageView = new ImageView();
		imageBox = new VBox(imageView);
		statusLabel = new Label();
		checkBox = new CheckBox();
		getChildren().addAll(imageBox, statusLabel, checkBox);
	}

	public void initAnchors(){
		AnchorPane.setTopAnchor(imageBox, 0.0);
		AnchorPane.setLeftAnchor(imageBox, 0.0);
		AnchorPane.setRightAnchor(imageBox, 0.0);
		AnchorPane.setBottomAnchor(imageBox, 0.0);

		AnchorPane.setLeftAnchor(statusLabel, 0.0);
		AnchorPane.setBottomAnchor(statusLabel, -10.0);

		AnchorPane.setRightAnchor(checkBox, 0.0);
		AnchorPane.setBottomAnchor(checkBox, 0.0);
	}

	public void initSize(){

		setPrefSize(150.0, 150.0);

		imageView.setFitHeight(150);
		imageView.setFitWidth(150);
		imageView.setPreserveRatio(true);
		imageBox.setAlignment(Pos.CENTER);

		checkBox.setPrefSize(50, 50);
	}

	public void initStyle(){
		imageView.getStyleClass().add("imageInPane");
		statusLabel.getStyleClass().add("statusLabelInPane");
		checkBox.getStyleClass().add("checkBoxInPane");
		getStylesheets().add("/stages/nodes/imageList/imagePane/imagePaneStyle.css");
	}

	public void init(){
		initNodes();
		initSize();
		initAnchors();
		initStyle();
	}

	public ImagePane(){
		init();
	}

	public ImagePane(Image image) {
		this();
		setImage(image);
	}

	public ImagePane(Image image, String status){
		this(image);
		setStatus(status);
	}

	public ImagePane(String line, Project project, boolean info){
		this();
		line = File.separatorChar + line + " ";
		String fileName = line.substring(0, line.indexOf(" "));
		System.out.println("IMAGE: " + project.getLocation().getAbsolutePath() + fileName);
		Image image = new Image(String.valueOf(new File(project.getLocation().getAbsolutePath() + fileName).toURI()));
		if(line.length() - fileName.length() > 3)
			statusLabel.setText(Status.ANNOTATED);
		setImage(image);
	}

	public void setImage(Image image){
		imageView.setImage(image);
	}

	public void setStatus(String status){
		if(status.equals(Status.ANNOTATED)){
			statusLabel.setText(status);
			statusLabel.setTextFill(Color.RED);
			return;
		}
		if(status.equals(Status.COPY)){
			statusLabel.setText(status);
			statusLabel.setTextFill(Color.BLUE);
			return;
		}
		System.err.println("Unknown status!");
	}

	public void setChoosen(boolean choosen){
		checkBox.setSelected(choosen);
	}
}
