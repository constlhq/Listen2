package com.listen2.components;

import com.jfoenix.controls.JFXTabPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class MainTabPane {

  private JFXTabPane jfxTabPane;

  private Tab playListTab,searchTab,myListTab,aboutTab;

  private void init(){
    jfxTabPane.setId("main-tab-pane");
    jfxTabPane.getTabs().addAll(playListTab,searchTab,myListTab,aboutTab);

    jfxTabPane.setMinHeight(300);
  }


  public MainTabPane(Tab playListTab, Tab searchTab, Tab myListTab, Tab aboutTab) {
    jfxTabPane = new JFXTabPane();
    this.playListTab = playListTab;
    this.searchTab = searchTab;
    this.myListTab = myListTab;
    this.aboutTab = aboutTab;
    init();
  }

  public JFXTabPane getJfxTabPane() {
    return jfxTabPane;
  }
}
