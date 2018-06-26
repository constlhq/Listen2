package com.listen2.components;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;


public class FormTitleBar {
  private HBox parentHBox;
  private double xOffset = 0;
  private double yOffset = 0;
  private ImageView closeIcon, maxSizeICon,minSizeIcon;

  private void  init(){
    parentHBox.setId("window-title-bar");
    createElements();
    initListeners();

  }

  private void createElements(){

    closeIcon =  new ImageView(new Image(FormTitleBar.class.getResource("/assets/close.png").toString()));
    closeIcon.setId("window-close-btn");
    closeIcon.setFitHeight(16);
    closeIcon.setFitWidth(16);
    closeIcon.setCursor(Cursor.HAND);
    HBox.setMargin(closeIcon,new Insets(2,0,2,10));

    maxSizeICon =  new ImageView(new Image(FormTitleBar.class.getResource("/assets/max.png").toString()));
    maxSizeICon.setId("window-max-btn");
    maxSizeICon.setFitHeight(16);
    maxSizeICon.setFitWidth(16);
    maxSizeICon.setCursor(Cursor.HAND);
    HBox.setMargin(maxSizeICon,new Insets(2,0,2,5));

    minSizeIcon =  new ImageView(new Image(FormTitleBar.class.getResource("/assets/min.png").toString()));
    minSizeIcon.setId("window-min-btn");
    minSizeIcon.setFitHeight(16);
    minSizeIcon.setFitWidth(16);
    minSizeIcon.setCursor(Cursor.HAND);
    HBox.setMargin(minSizeIcon,new Insets(2,0,2,5));

    parentHBox.getChildren().addAll(closeIcon,maxSizeICon,minSizeIcon);
  }

  private void initListeners() {
    closeIcon.setOnMouseClicked((event -> Platform.exit()));
    minSizeIcon.setOnMouseClicked(event -> ((Stage)minSizeIcon.getScene().getWindow()).setIconified(true));
    maxSizeICon.setOnMouseClicked(event -> {
       Stage mainstage =  (Stage)minSizeIcon.getScene().getWindow();
       mainstage.setFullScreen(!mainstage.isFullScreen());
    });

    parentHBox.setOnMousePressed((MouseEvent event) -> {
      xOffset = event.getSceneX();
      yOffset = event.getSceneY();
    });
    parentHBox.setOnMouseDragged((MouseEvent event) -> {
      Stage primaryStage = (Stage)parentHBox.getScene().getWindow();
      primaryStage.setX(event.getScreenX() - xOffset);
      primaryStage.setY(event.getScreenY() - yOffset);
    });

  }


  public FormTitleBar() {
    parentHBox = new HBox();
    init();
  }

  public HBox getParentHBox() {
    return parentHBox;
  }
}
