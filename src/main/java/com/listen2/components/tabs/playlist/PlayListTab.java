package com.listen2.components.tabs.playlist;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class PlayListTab{
  private Tab tab;
  private TabPane playListSourceTabPane;


  public PlayListTab() {
    tab = new Tab("精选歌单");
    init();
  }

  private void init(){
    tab.setId("playlist-tab");
    tab.setClosable(false);
  }

  public Tab getTab() {
    return tab;
  }
}
