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
  public PlayerCtrl playerCtrl;
  public PlayListTabCtrl playListTabCtrl;
  private SearchTabCtrl searchTabCtrl;
  public MyListTabCtrl myListTabCtrl;
  private AbouTabCtrl abouTabCtrl;

  public RootCtrl() {
    rootNode = new VBox();
    FormTitleBarCtrl = new FormTitleBarCtrl();
    homeTabPaneCtrl = new HomeTabPaneCtrl();
    playerCtrl = new PlayerCtrl(homeTabPaneCtrl);
    playListTabCtrl = new PlayListTabCtrl(playerCtrl,homeTabPaneCtrl);
    searchTabCtrl = new SearchTabCtrl(playerCtrl,homeTabPaneCtrl);
    myListTabCtrl = new MyListTabCtrl(playerCtrl,homeTabPaneCtrl);
    abouTabCtrl = new AbouTabCtrl();
    homeTabPaneCtrl.setPlayListTabCtrl(playListTabCtrl);
    homeTabPaneCtrl.setSearchTabCtrl(searchTabCtrl);
    homeTabPaneCtrl.setMyListTabCtrl(myListTabCtrl);
    homeTabPaneCtrl.setAbouTabCtrl(abouTabCtrl);

    init();
  }
  private void init(){
    homeTabPaneCtrl.getContainerTabPane().setPrefHeight(720- playerCtrl.getPlayerBox().getPrefHeight() - FormTitleBarCtrl.getContainerHBox().getPrefHeight());
    rootNode.getChildren().addAll(FormTitleBarCtrl.getContainerHBox(),homeTabPaneCtrl.getContainerTabPane(),playerCtrl.getPlayerBox());
  }

  public VBox getRootNode() {
    return rootNode;
  }

}
