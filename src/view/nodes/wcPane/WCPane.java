package view.nodes.wcPane;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import view.nodes.wcbPane.WCBPane;
import view.nodes.wcbPane.WCBTypes;

import java.io.File;

public class WCPane extends AnchorPane{//window control pane

	private static final String STYLE_SHEET = "/view/nodes/wcPane/wcPaneStyle.css";


	class LNTPane extends HBox{ // Logo and title pane

		private ImageView logo;
		private Label title;

		public ImageView getLogo(){
			return logo;
		}

		public Label getTitle(){
			return title;
		}

		LNTPane(Image logo, String title, double size){
			super(10);
			this.setAlignment(Pos.CENTER);
			if(logo == null)logo = LOGO;
			this.logo = new ImageView(logo);
			this.logo.setFitHeight(size - 10);
			this.logo.setFitWidth(size - 10);
			HBox.setMargin(this.logo, new Insets(0, 0, 0, 5));
			this.title = new Label(title);
			setPrefHeight(size);
			getChildren().addAll(this.logo, this.title);
		}

		LNTPane(String title, double size){
			this(LOGO, title, size);
		}
	}

	public static final Image LOGO = new File("logo.png").exists() ? new Image("logo.png") : null;
	public static final String PROJECT_NAME = "Diet";

	private LNTPane lntPane;
	private WCBPane wcbPane;

	public LNTPane getLntPane() {
		return lntPane;
	}

	public WCBPane getWcbPane() {
		return wcbPane;
	}

	public WCPane(Image logo, String title, double height, WCBTypes... buttons){
		setId("wcPane");
		getStylesheets().add(STYLE_SHEET);
		lntPane = new LNTPane(logo, title, height);
		wcbPane = new WCBPane(height, 0, buttons);
		initNodesAnchors();
		getChildren().addAll(lntPane, wcbPane);
		AnchorPane.setTopAnchor(this, 0.0);
		AnchorPane.setLeftAnchor(this, 0.0);
		AnchorPane.setRightAnchor(this, 0.0);
	}

	public void setTitle(String title) {
		getLntPane().title.setText(title);
	}

	public WCPane(String title, double height, WCBTypes... buttons){
		this(LOGO, title, height, buttons);
	}

	public WCPane(double height, WCBTypes... buttons){
		this(LOGO, "", height, buttons);
	}

	public WCPane(WCBTypes... buttons){
		this(LOGO, "", 40, buttons);
	}

	public void initNodesAnchors(){
		AnchorPane.setTopAnchor(lntPane, 0.0);
		AnchorPane.setLeftAnchor(lntPane, 0.0);
		AnchorPane.setTopAnchor(wcbPane, 0.0);
		AnchorPane.setRightAnchor(wcbPane, 0.0);
	}

}
