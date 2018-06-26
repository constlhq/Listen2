package com.listen2.components;


import com.listen2.components.tabs.about.AbouTab;
import com.listen2.components.tabs.mylist.MyListTab;
import com.listen2.components.tabs.playlist.PlayListTab;
import com.listen2.components.tabs.search.SearchTab;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class AppMain extends Application{
  @Override
  public void start(Stage primaryStage) throws IOException {
    primaryStage.initStyle(StageStyle.UNDECORATED);


    VBox rootNode = new VBox();
    MainTabPane tabPane = new MainTabPane(new PlayListTab().getTab(),new SearchTab().getTab(),new MyListTab().getTab(),new AbouTab().getTab());
    PlayerController playerController = new PlayerController();
    HBox hBox = playerController.getPlayerBox();


    rootNode.getChildren().addAll(new FormTitleBar().getParentHBox(),tabPane.getJfxTabPane(),hBox);
    Scene scene = new Scene(rootNode,960,720);
    scene.getStylesheets().add(getClass().getResource("/css/test.css").toExternalForm());
    primaryStage.setScene(scene);
    primaryStage.show();


  }
}
