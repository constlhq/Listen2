package com.listen2.components;



import com.listen2.components.tabs.AbouTabCtrl;
import com.listen2.components.tabs.MyListTabCtrl;
import com.listen2.components.tabs.PlayListTabCtrl;
import com.listen2.components.tabs.SearchTabCtrl;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.StackPane;

public  class HomeTabPaneCtrl {

  private TabPane tabPane;

  private PlayListTabCtrl playListTabCtrl;
  private MyListTabCtrl myListTabCtrl;
  private SearchTabCtrl searchTabCtrl;
  private AbouTabCtrl abouTabCtrl;

  public TabPane getTabPane() {
    return tabPane;
  }

  public PlayListTabCtrl getPlayListTabCtrl() {
    return playListTabCtrl;
  }

  public MyListTabCtrl getMyListTabCtrl() {
    return myListTabCtrl;
  }

  public SearchTabCtrl getSearchTabCtrl() {
    return searchTabCtrl;
  }

  public AbouTabCtrl getAbouTabCtrl() {
    return abouTabCtrl;
  }

  private void init(){
    tabPane.setId("main-tab-pane");
    setTebHeight(540 );

  }

  public void  setTebHeight(double height){
    tabPane.setPrefHeight(height);
  }


  public HomeTabPaneCtrl() {
    tabPane = new TabPane();
    init();
  }

  public void setPlayListTabCtrl(PlayListTabCtrl PlayListTabCtrl) {
    this.playListTabCtrl = PlayListTabCtrl;
    tabPane.getTabs().add(PlayListTabCtrl.getTab());
  }
  public void setSearchTabCtrl(SearchTabCtrl SearchTabCtrl) {
    this.searchTabCtrl = SearchTabCtrl;
    tabPane.getTabs().add(SearchTabCtrl.getTab());
  }

  public void setMyListTabCtrl(MyListTabCtrl MyListTabCtrl) {
    this.myListTabCtrl = MyListTabCtrl;
    tabPane.getTabs().add(MyListTabCtrl.getTab());
  }

  public void setAbouTabCtrl(AbouTabCtrl AbouTabCtrl) {
    this.abouTabCtrl = AbouTabCtrl;
    tabPane.getTabs().add(AbouTabCtrl.getTab());
  }

  public TabPane getContainerTabPane() {
    return tabPane;
  }
  public void addTab(Tab newTab){
    tabPane.getTabs().add(newTab);
  }




}
