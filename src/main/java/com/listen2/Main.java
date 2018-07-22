package com.listen2;


import com.listen2.components.RootCtrl;
import com.listen2.utils.IniFileManger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Main extends Application{

  private TrayIcon trayIcon;
  private RootCtrl rootController;
  @Override
  public void start(Stage primaryStage) throws IOException {
    enableTray(primaryStage);

    primaryStage.initStyle(StageStyle.UNDECORATED);
    rootController = new RootCtrl();
    Scene scene = new Scene(rootController.getRootNode(),960,720);
    primaryStage.getIcons().add(new Image(
            Main.class.getResourceAsStream("/assets/logo.png")));
    scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
    primaryStage.setScene(scene);
    primaryStage.show();
  }


  private void enableTray(final Stage stage) {
    PopupMenu popupMenu = new PopupMenu();
    java.awt.MenuItem openItem = new java.awt.MenuItem("show");
    java.awt.MenuItem hideItem = new java.awt.MenuItem("miniMize");
    java.awt.MenuItem quitItem = new java.awt.MenuItem("quit");
    openItem.addActionListener(e->Platform.runLater(()->stage.show()));
    hideItem.addActionListener(e->Platform.runLater(()->stage.hide()));
    quitItem.addActionListener(e->Platform.runLater(()->{
      // save state here
      IniFileManger.createIniFile();
      IniFileManger.saveMyPlaylist(rootController.myListTabCtrl.getObservableMyList());
      SystemTray.getSystemTray().remove(trayIcon);
      Platform.exit();
    }));

    popupMenu.add(openItem);
    popupMenu.add(hideItem);
    popupMenu.add(quitItem);

    MouseListener mouseListener = new MouseListener() {
      public void mouseReleased(MouseEvent e) {
      }
      public void mousePressed(MouseEvent e) {
      }
      public void mouseExited(MouseEvent e) {
      }
      public void mouseEntered(MouseEvent e) {
      }
      public void mouseClicked(MouseEvent e) {
        Platform.setImplicitExit(false); //多次使用显示和隐藏设置false
         if (stage.isShowing()) {
            Platform.runLater(()->stage.hide());
          }else{
            Platform.runLater(()->stage.show());
          }
      }
    };
    SystemTray tray = SystemTray.getSystemTray();
    try {

      BufferedImage image = ImageIO.read(Main.class.getResourceAsStream("/assets/tray.png"));
      trayIcon = new TrayIcon(image,"listen 2",popupMenu);
      tray.add(trayIcon);
      trayIcon.addMouseListener(mouseListener);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}