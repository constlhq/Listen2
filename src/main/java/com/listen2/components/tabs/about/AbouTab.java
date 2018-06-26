package com.listen2.components.tabs.about;

import javafx.scene.control.Tab;

public class AbouTab{
  private Tab tab;


  public AbouTab() {
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
