package stages.nodes.imageList;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
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
import javafx.util.Duration;
import model.Project;
import stages.nodes.imageList.imagePane.ImagePane;
import stages.projectStage.NewController;
import view.windows.stage.MyStage;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;

public class ImageList extends AnchorPane {

	static class Select {
		public static final String ALL = "all";
		public static final String NOTHING = "nothing";
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

	public void initNodes() {
		deleteSelectedButton = new Button();
		selectTypeBox = new ComboBox();
		buttonBox = new HBox(50, selectTypeBox, deleteSelectedButton);

		gridPane = new GridPane();
		scrollPane = new ScrollPane(gridPane);

		getChildren().addAll(buttonBox, scrollPane);
	}

	public ArrayList<File> getAnnotated() {
		ArrayList<File> annotated = new ArrayList<>();
		for(Node n : gridPane.getChildren()){
			ImagePane p = (ImagePane)n;
			if(p.isAnnotated())
				annotated.add(p.getFile());
		}
		return annotated;
	}

	public void initAnchors() {
		AnchorPane.setLeftAnchor(buttonBox, 0.0);
		AnchorPane.setTopAnchor(buttonBox, 0.0);
		AnchorPane.setRightAnchor(buttonBox, 0.0);

		AnchorPane.setTopAnchor(scrollPane, 50.0);
		AnchorPane.setRightAnchor(scrollPane, 0.0);
		AnchorPane.setBottomAnchor(scrollPane, 0.0);
		AnchorPane.setLeftAnchor(scrollPane, 0.0);
	}

	public void initSizes() {
		deleteSelectedButton.setPrefSize(40, 40);
		selectTypeBox.setPrefSize(70, 40);
		scrollPane.setFitToWidth(true);
		gridPane.setPrefHeight(300);
	}

	public void initStyles() {
		selectTypeBox.getStyleClass().add("selectTypeBox");
		deleteSelectedButton.getStyleClass().add("deleteSelectedButton");
		scrollPane.getStyleClass().add("imageListScrollPane");
	}

	public void initContent() {
		selectTypeBox.getItems().add(new Label(Select.ALL));
	}

	public ObservableList<Label> getSelectTypes() {
		Label all = new Label(Select.ALL);
		all.setOnMouseClicked(cl -> select(Select.ALL));

		Label nothing = new Label(Select.NOTHING);
		nothing.setOnMouseClicked(cl -> select(Select.NOTHING));

		Label annot = new Label(Select.ANNOTATED);
		annot.setOnMouseClicked(cl -> select(Select.ANNOTATED));

		Label notAnn = new Label(Select.NOT_ANNOTATED);
		notAnn.setOnMouseClicked(cl -> select(Select.NOT_ANNOTATED));

		ObservableList<Label> types = FXCollections.observableArrayList();
		types.addAll(all, nothing);
		if (Project.isInfoFile(file))
			types.addAll(annot, notAnn);
		return types;
	}

	public void select(String type) {
		for (Object p : gridPane.getChildren()) {
			ImagePane pane = (ImagePane) p;
			if (type.equals(Select.ALL)
					|| type.equals(Select.ANNOTATED) && pane.isAnnotated()
					|| type.equals(Select.NOT_ANNOTATED) && !pane.isAnnotated())
				pane.select(true);
			else if (type.equals(Select.NOTHING))
				pane.select(false);
		}
	}

	public void initBehaviour() {
		selectTypeBox.setOnAction(sel -> onSelect());
		deleteSelectedButton.setOnAction(del -> delete());
		heightProperty().addListener(observable -> onResize());
		widthProperty().addListener(observable -> onResize());
		gridPane.setOnDragOver(this::onDragOver);
		gridPane.setOnDragDropped(this::onDragDropped);
		gridPane.setHgap(10.0);
		gridPane.setVgap(10.0);
	}

	public void init() {
		initNodes();
		initAnchors();
		initSizes();
		initStyles();
		initContent();
		initBehaviour();
	}

	public void linkTo(File file) {
		if (file == null) {
			System.err.println("ImageList.linkTo() File is null");
			return;
		}
		if (!file.exists()) {
			System.err.println("ImageList.linkTo() File doesn't exist");
			return;
		}
		this.file = file;
		if (Project.isInfoFile(file)) {
			update(readFile());
		}
		if (Project.isBgFile(file)) {
			update(readFile());
		}
		selectTypeBox.setItems(getSelectTypes());
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
		if (Project.isInfoFile(file))
			getProject().addPositives((ArrayList<File>) dragEvent.getDragboard().getFiles());
		else if (Project.isBgFile(file))
			getProject().addNegatives((ArrayList<File>) dragEvent.getDragboard().getFiles());
		new Timeline(
				new KeyFrame(
						Duration.millis(1500),
						f -> update(readFile())
				)
		).play();
	}

	int colIndex = 0;
	int rowIndex = 0;

	public ArrayList<ImagePane> readFile() {
		ArrayList<ImagePane> imagePanes = new ArrayList<>();
		if (file == null || !file.exists()) {
			return imagePanes;
		}
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String line;
			while ((line = reader.readLine()) != null) {
				ImagePane imagePane = new ImagePane(line, getProject(), true);
				imagePanes.add(imagePane);
			}
			ArrayList<ImagePane> newPanes = new ArrayList<>();
			if (Project.isInfoFile(file)) {
				ArrayList<File> files = new ArrayList<>(Arrays.asList(getProject().getPos().listFiles()));
				for (File f : files) {
					boolean contain = false;
					for (ImagePane p : imagePanes)
						if (p.getFile().getName().equals(f.getName())) {
							contain = true;
							break;
						}
					if (!contain) {
						newPanes.add(new ImagePane(f));
						System.out.println("add");
					}
				}
				imagePanes.addAll(newPanes);
			}
//			else if(Project.isBgFile(file)) {
//				ArrayList<File> files = new ArrayList<>(Arrays.asList(getProject().getNeg().listFiles()));
//				for (File f : files) {
//					boolean contain = false;
//					for (ImagePane p : imagePanes)
//						if (p.getFile().equals(f)) {
//							contain = true;
//							break;
//						}
//					if (!contain)
//						newPanes.add(new ImagePane(f));
//				}
//				imagePanes.addAll(newPanes);
//			}
			} catch(FileNotFoundException e){
				e.printStackTrace();
			} catch(IOException e){
				e.printStackTrace();
			}
			return imagePanes;
		}

	private void onResize() {
		update();
	}

	private void delete() {

	}

	private void onSelect() {

	}

	public ImageList() {
		init();
	}

	public Project getProject() {
		return ((NewController) ((MyStage) getScene().getWindow()).getLoader().getController()).getProject();
	}

	public ArrayList<ImagePane> getSelected(){
		ArrayList<ImagePane> imagePanes = new ArrayList<>();
		for(Object o : gridPane.getChildren()) {
			ImagePane p = (ImagePane)o;
			if (p.isSelected())
				imagePanes.add(p);
		}
		return imagePanes;
	}

	public ArrayList<File> getSelectedFiles() {
		ArrayList<File> selectedImages = new ArrayList<>();
		for(Node n : gridPane.getChildren()) {
			ImagePane p = (ImagePane) n;
			if (p.isSelected())
				selectedImages.add(p.getFile());
		}
		return selectedImages;
	}

	public void update() {
		ArrayList<ImagePane> imPanes = new ArrayList<>();
		for (int i = 0; i < gridPane.getChildren().size(); ++i)
			imPanes.add((ImagePane) gridPane.getChildren().get(i));
		update(imPanes);
	}

	public void update(ArrayList<ImagePane> imPanes) {
		gridPane.getChildren().clear();
		if (imPanes.isEmpty())
			return;
		System.out.println("Size: " + imPanes.size());
		System.out.println();
		int columns = (getWidth() == 0) ? 3 : (int)getWidth() / 160;
		int rows = imPanes.size() / ((columns == 0) ? 1 : columns) + 1;
		int p = 0;
		for (int i = 0; i < rows; ++i)
			for (int j = 0; j < columns; ++j) {
				GridPane.setRowIndex(imPanes.get(p), i);
				GridPane.setColumnIndex(imPanes.get(p), j);
				gridPane.getChildren().add(imPanes.get(p));
				if (++p >= imPanes.size())
					return;
			}
	}
}
