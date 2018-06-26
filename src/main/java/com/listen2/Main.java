package com.listen2;


import com.listen2.models.Sound;
import com.listen2.models.Track;
import com.listen2.providers.QQ;
import com.listen2.providers.Xiami;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;


import java.io.IOException;

public class Main extends Application{
//  @Override
//  public void init(){
//    AquaFx.style();
//  }
  @Override
  public void start(Stage primaryStage) throws IOException{
    primaryStage.initStyle(StageStyle.UNDECORATED);
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxmls/HomeWindow.fxml"));

    StackPane rootNode =loader.load();
    Scene scene = new Scene(rootNode,800,600);
    primaryStage.setScene(scene);
    primaryStage.show();

  }
}
