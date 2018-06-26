package com.listen2.components.tabs.mylist;

import javafx.scene.control.Tab;

public class MyListTab {
  private Tab tab;

  public MyListTab() {
    this.tab =new Tab("我的歌单");
    init();
  }

  private void init(){
    tab.setId("mylist-tab");
    tab.setClosable(false);
  }

  public Tab getTab() {
    return tab;
  }
}
