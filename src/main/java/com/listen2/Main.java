package com.listen2;


import com.listen2.components.RootCtrl;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.IOException;

public class Main extends Application{
  @Override
  public void start(Stage primaryStage) throws IOException {
    primaryStage.initStyle(StageStyle.UNDECORATED);

    RootCtrl rootController = new RootCtrl();

    Scene scene = new Scene(rootController.getRootNode(),960,720);
    scene.getStylesheets().add(getClass().getResource("/css/test.css").toExternalForm());
    primaryStage.setScene(scene);
    primaryStage.show();

  }
}
