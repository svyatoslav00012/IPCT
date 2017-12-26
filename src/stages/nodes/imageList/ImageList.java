package stages.nodes.imageList;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import model.Project;
import stages.nodes.imageList.imagePane.ImagePane;
import stages.projectStage.NewController;
import view.windows.stage.MyStage;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class ImageList extends AnchorPane {

	static class Select{
		public static final String ALL = "all";
		public static final String ANNOTATED = "annotated";
		public static final String NOT_ANNOTATED = "not annotated";
		public static final String COPIES = "copies";
		public static final String ORIGINALS = "originals";
	}

	private HBox buttonBox;
	private ComboBox selectTypeBox;
	private Button deleteSelectedButton;

	private ScrollPane scrollPane;
	private GridPane gridPane;

	private File file;

	public void initNodes(){
		deleteSelectedButton = new Button();
		selectTypeBox = new ComboBox();
		buttonBox = new HBox(50, selectTypeBox, deleteSelectedButton);

		gridPane = new GridPane();
		scrollPane = new ScrollPane(gridPane);

		getChildren().addAll(buttonBox, scrollPane);
	}

	public void initAnchors(){
		AnchorPane.setLeftAnchor(buttonBox, 0.0);
		AnchorPane.setTopAnchor(buttonBox, 0.0);
		AnchorPane.setRightAnchor(buttonBox, 0.0);

		AnchorPane.setTopAnchor(scrollPane, 50.0);
		AnchorPane.setRightAnchor(scrollPane, 0.0);
		AnchorPane.setBottomAnchor(scrollPane, 0.0);
		AnchorPane.setLeftAnchor(scrollPane, 0.0);
	}

	public void initSizes(){
		deleteSelectedButton.setPrefSize(40, 40);
		selectTypeBox.setPrefSize(70, 40);
		scrollPane.setFitToWidth(true);
	}

	public void initStyles(){
		selectTypeBox.getStyleClass().add("selectTypeBox");
		deleteSelectedButton.getStyleClass().add("deleteSelectedButton");
		scrollPane.getStyleClass().add("imageListScrollPane");
	}

	public void initContent(){
		selectTypeBox.getItems().add(new Label(Select.ALL));
	}

	public void initBehaviour(){
		selectTypeBox.setOnAction(sel -> onSelect());
		deleteSelectedButton.setOnAction(del -> delete());
		heightProperty().addListener(observable -> onResize());
		widthProperty().addListener(observable -> onResize());
		gridPane.setOnDragOver(this::onDragOver);
		gridPane.setOnDragDropped(this::onDragDropped);
	}

	public void init(){
		initNodes();
		initAnchors();
		initSizes();
		initStyles();
		initContent();
		initBehaviour();
	}

	public void linkTo(File file){
		if(file == null){
			System.err.println("ImageList.linkTo() File is null");
			return;
		}
		if(!file.exists()){
			System.err.println("ImageList.linkTo() File doesn't exist");
			return;
		}
		this.file = file;
		if(Project.isInfoFile(file)){
			readInfo();
		}
	}

	public void onDragOver(DragEvent dragEvent) {
		if (dragEvent.getDragboard().hasFiles()
				&& dragEvent.getDragboard().getFiles().get(0) != null
				&& dragEvent.getDragboard().getFiles().get(0).isFile()
				&& dragEvent.getDragboard().getFiles().get(0).exists()
				&& !new Image(dragEvent.getDragboard().getFiles().get(0).toURI().toString()).isError()
				) {
			dragEvent.acceptTransferModes(TransferMode.ANY);
		}
	}

	public void onDragDropped(DragEvent dragEvent) {
		getProject().addPositives((ArrayList<File>)dragEvent.getDragboard().getFiles());
	}

	int colIndex = 0;
	int rowIndex = 0;

	public void readInfo(){
		if(file == null || !file.exists())
			return;
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String line;
			while((line = reader.readLine()) != null){
				ImagePane imagePane = new ImagePane(line, getProject(),true);
				GridPane.setColumnIndex(imagePane, colIndex);
				GridPane.setRowIndex(imagePane, rowIndex);
				gridPane.getChildren().add(imagePane);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void onResize() {

	}

	private void delete() {

	}

	private void onSelect() {

	}

	public ImageList(){
		init();
	}

	public Project getProject(){
		return ((NewController)((MyStage)getScene().getWindow()).getLoader().getController()).getProject();
	}

}
