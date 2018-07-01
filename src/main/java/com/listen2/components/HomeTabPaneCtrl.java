package com.listen2.components;



import com.listen2.components.tabs.AbouTabCtrl;
import com.listen2.components.tabs.MyListTabCtrl;
import com.listen2.components.tabs.PlayListTabCtrl;
import com.listen2.components.tabs.SearchTabCtrl;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public  class HomeTabPaneCtrl {

  private TabPane tabPane;

  private PlayListTabCtrl PlayListTabCtrl;
  private MyListTabCtrl MyListTabCtrl;
  private SearchTabCtrl SearchTabCtrl;
  private AbouTabCtrl AbouTabCtrl;

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
    this.PlayListTabCtrl = PlayListTabCtrl;
    tabPane.getTabs().add(PlayListTabCtrl.getTab());
  }
  public void setSearchTabCtrl(SearchTabCtrl SearchTabCtrl) {
    this.SearchTabCtrl = SearchTabCtrl;
    tabPane.getTabs().add(SearchTabCtrl.getTab());
  }

  public void setMyListTabCtrl(MyListTabCtrl MyListTabCtrl) {
    this.MyListTabCtrl = MyListTabCtrl;
    tabPane.getTabs().add(MyListTabCtrl.getTab());
  }

  public void setAbouTabCtrl(AbouTabCtrl AbouTabCtrl) {
    this.AbouTabCtrl = AbouTabCtrl;
    tabPane.getTabs().add(AbouTabCtrl.getTab());
  }

  public TabPane getContainerTabPane() {
    return tabPane;
  }

  public void addTab(Tab newTab){
    tabPane.getTabs().add(newTab);
    System.out.println(tabPane.getTabs().size());
  }
}
