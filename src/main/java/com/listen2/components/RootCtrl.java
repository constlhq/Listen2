package com.listen2.components;

import com.listen2.components.tabs.AbouTabCtrl;
import com.listen2.components.tabs.MyListTabCtrl;
import com.listen2.components.tabs.PlayListTabCtrl;
import com.listen2.components.tabs.SearchTabCtrl;
import javafx.scene.layout.VBox;

public class RootCtrl {
  private VBox rootNode;
  private FormTitleBarCtrl FormTitleBarCtrl;
  private HomeTabPaneCtrl homeTabPaneCtrl;
  private PlayerCtrl PlayerCtrl;
  private PlayListTabCtrl PlayListTabCtrl;
  private SearchTabCtrl SearchTabCtrl;
  private MyListTabCtrl MyListTabCtrl;
  private AbouTabCtrl AbouTabCtrl;

  public RootCtrl() {
    rootNode = new VBox();
    FormTitleBarCtrl = new FormTitleBarCtrl();
    homeTabPaneCtrl = new HomeTabPaneCtrl();
    PlayerCtrl = new PlayerCtrl(homeTabPaneCtrl);
    PlayListTabCtrl = new PlayListTabCtrl(PlayerCtrl,homeTabPaneCtrl);
    SearchTabCtrl = new SearchTabCtrl(PlayerCtrl,homeTabPaneCtrl);
    MyListTabCtrl = new MyListTabCtrl();
    AbouTabCtrl = new AbouTabCtrl();
    homeTabPaneCtrl.setPlayListTabCtrl(PlayListTabCtrl);
    homeTabPaneCtrl.setSearchTabCtrl(SearchTabCtrl);
    homeTabPaneCtrl.setMyListTabCtrl(MyListTabCtrl);
    homeTabPaneCtrl.setAbouTabCtrl(AbouTabCtrl);

    init();
  }
  private void init(){
    homeTabPaneCtrl.getContainerTabPane().setPrefHeight(720- PlayerCtrl.getPlayerBox().getPrefHeight() - FormTitleBarCtrl.getContainerHBox().getPrefHeight());
    rootNode.getChildren().addAll(FormTitleBarCtrl.getContainerHBox(),homeTabPaneCtrl.getContainerTabPane(),PlayerCtrl.getPlayerBox());
  }

  public VBox getRootNode() {
    return rootNode;
  }

}
