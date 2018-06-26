package com.listen2.components.tabs.search;

import javafx.scene.control.Tab;

public class SearchTab {
  private Tab tab;

  public SearchTab() {
    tab  = new Tab("快速搜索");
    init();
  }

  private void init(){
    tab.setId("search-tab");
    tab.setClosable(false);
  }

  public Tab getTab() {
    return tab;
  }
}
