package com.listen2.components.tabs;

import javafx.scene.control.Tab;
import javafx.scene.web.PopupFeatures;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Callback;

public class AbouTabCtrl{
  private Tab tab;


  public AbouTabCtrl() {
    tab = new Tab("关于");
    init();
  }

  private void init(){
    tab.setId("about-tab");
    tab.setClosable(false);
    WebView browser = new WebView();

    final WebEngine webEngine = browser.getEngine();
    webEngine.load(AbouTabCtrl.class.getResource("/about.html").toString());
    tab.setContent(browser);
//    homeTabPaneCtrl.addTab(tempTab);
//    homeTabPaneCtrl.getContainerTabPane().getSelectionModel().selectLast();
  }

  public Tab getTab() {
    return tab;
  }
}
