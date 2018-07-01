package com.listen2.components;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;


public class FormTitleBarCtrl {
  private HBox containerHBox;
  private double xOffset = 0;
  private double yOffset = 0;
  private ImageView closeIcon, maxSizeICon,minSizeIcon;

  private void  init(){
    containerHBox.setId("window-title-bar");
    createElements();
    initListeners();
  }

  private void createElements(){

    closeIcon =  new ImageView(new Image(FormTitleBarCtrl.class.getResource("/assets/close.png").toString()));
    closeIcon.setId("window-close-btn");
    closeIcon.setFitHeight(16);
    closeIcon.setFitWidth(16);
    closeIcon.setCursor(Cursor.HAND);
    HBox.setMargin(closeIcon,new Insets(2,0,2,10));

    maxSizeICon =  new ImageView(new Image(FormTitleBarCtrl.class.getResource("/assets/max.png").toString()));
    maxSizeICon.setId("window-max-btn");
    maxSizeICon.setFitHeight(16);
    maxSizeICon.setFitWidth(16);
    maxSizeICon.setCursor(Cursor.HAND);
    maxSizeICon.setDisable(true);
    HBox.setMargin(maxSizeICon,new Insets(2,0,2,5));

    minSizeIcon =  new ImageView(new Image(FormTitleBarCtrl.class.getResource("/assets/min.png").toString()));
    minSizeIcon.setId("window-min-btn");
    minSizeIcon.setFitHeight(16);
    minSizeIcon.setFitWidth(16);
    minSizeIcon.setCursor(Cursor.HAND);
    HBox.setMargin(minSizeIcon,new Insets(2,0,2,5));

    containerHBox.getChildren().addAll(closeIcon,maxSizeICon,minSizeIcon);
  }

  private void initListeners() {
    closeIcon.setOnMouseClicked((event -> Platform.exit()));
    minSizeIcon.setOnMouseClicked(event -> ((Stage)minSizeIcon.getScene().getWindow()).setIconified(true));
    maxSizeICon.setOnMouseClicked(event -> {
       Stage mainstage =  (Stage)minSizeIcon.getScene().getWindow();
       mainstage.setFullScreen(!mainstage.isFullScreen());
    });

    containerHBox.setOnMousePressed((MouseEvent event) -> {
      xOffset = event.getSceneX();
      yOffset = event.getSceneY();
    });
    containerHBox.setOnMouseDragged((MouseEvent event) -> {
      Stage primaryStage = (Stage)containerHBox.getScene().getWindow();
      primaryStage.setX(event.getScreenX() - xOffset);
      primaryStage.setY(event.getScreenY() - yOffset);
    });

  }


  public FormTitleBarCtrl() {
    containerHBox = new HBox();
    init();
  }

  public HBox getContainerHBox() {
    return containerHBox;
  }
}
