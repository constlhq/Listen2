package com.listen2.components.tabs;

import javafx.scene.control.Tab;

public class AbouTabCtrl{
  private Tab tab;


  public AbouTabCtrl() {
    tab = new Tab("关于");
    init();
  }

  private void init(){
    tab.setId("about-tab");
    tab.setClosable(false);
  }

  public Tab getTab() {
    return tab;
  }
}
