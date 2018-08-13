package com.listen2.components.tabs;

import com.listen2.components.HomeTabPaneCtrl;
import com.listen2.components.PlayerCtrl;
import com.listen2.models.PlayList;
import com.listen2.models.Track;
import com.listen2.providers.IProvider;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Callback;

public class RecommendedPlayListDetailTabCtrl extends PlayListDetailTabCtrl{

  public RecommendedPlayListDetailTabCtrl(String title, PlayList playList, PlayerCtrl playerCtrl, IProvider provider, HomeTabPaneCtrl homeTabPaneCtrl){
    super(title,playList, playerCtrl,provider,homeTabPaneCtrl);
    functionBtn3 = new Button("",new ImageView(new Image(PlayListDetailTabCtrl.class.getResource("/assets/folder.png").toString())));
    functionBtn4 = new Button("",new ImageView(new Image(PlayListDetailTabCtrl.class.getResource("/assets/link.png").toString())));
    init();
  }

  @Override
  protected void init(){
    super.init();
    functionBtn3.getStyleClass().add("icon-btn");
    functionBtn4.getStyleClass().add("icon-btn");
    ((HBox)metaVBox.getChildren().get(2)).getChildren().addAll(functionBtn3,functionBtn4);
    addListeners();
    buildTable();
  }

  @Override
  protected void addListeners() {
    super.addListeners();
    // save this playlist as myplaylist
    functionBtn3.setOnMouseClicked(e->{});
    // goto original link
    functionBtn4.setOnMouseClicked(e->{});
  }

  @Override
  public void buildTable() {
    super.buildTable();
    TableColumn actionCol = new TableColumn();
    actionCol.setCellValueFactory(new PropertyValueFactory<>("Dummy"));
    actionCol.setPrefWidth(trackTableView.getPrefWidth() *0.15);

    Callback<TableColumn<Track, String>, TableCell<Track, String>> actionCellFactory =
            new Callback<TableColumn<Track, String>, TableCell<Track, String>>() {
              @Override
              public TableCell call(final TableColumn<Track, String> param) {
                final TableCell<Track, String> cell = new TableCell<Track, String>() {
                  ImageView addImgv = new ImageView(new Image(PlayerCtrl.class.getResource("/assets/add.png").toString()));
                  Button addBtn = new Button("",addImgv);
                  ImageView saveImgv = new ImageView(new Image(PlayerCtrl.class.getResource("/assets/folder.png").toString()));
                  Button saveBtn = new Button("",saveImgv);
                  ImageView linkImgv = new ImageView(new Image(PlayerCtrl.class.getResource("/assets/link.png").toString()));
                  Button linkBtn = new Button("",linkImgv);
                  HBox hBox = new HBox();


                  @Override
                  public void updateItem(String item, boolean empty) {
                    addImgv.setFitHeight(16);
                    saveImgv.setFitHeight(16);
                    linkImgv.setFitHeight(16);

                    addImgv.setFitWidth(16);
                    saveImgv.setFitWidth(16);
                    linkImgv.setFitWidth(16);

                    addBtn.getStyleClass().add("icon-btn");
                    saveBtn.getStyleClass().add("icon-btn");
                    linkBtn.getStyleClass().add("icon-btn");


                    super.updateItem(item, empty);
                    if (empty) {
                      setGraphic(null);
                      setText(null);
                    } else {
                      if(hBox.getChildren().isEmpty()){

                        hBox.getChildren().addAll(addBtn,saveBtn,linkBtn);
                      }

                      addBtn.setOnMouseClicked(event -> {
                        int index = getIndex();
                        playerCtrl.addTrack(trackObservableList.get(index));

                      });

                      saveBtn.setOnMouseClicked(e->{
                        System.out.println("save song to my list");
                      });

                      linkBtn.setOnMouseClicked(e->{
                        Tab tempTab =  new Tab("原始链接");
                        tempTab.setClosable(true);
                        WebView browser = new WebView();

                        final WebEngine webEngine = browser.getEngine();
                        webEngine.setCreatePopupHandler((param->webEngine));
                        webEngine.load(trackObservableList.get(getIndex()).source_url);
                        tempTab.setContent(browser);
                        homeTabPaneCtrl.addTab(tempTab);
                      });
                      setGraphic(hBox);
                      setText(null);
                    }
                  }
                };

                return cell;
              }
            };

    actionCol.setCellFactory(actionCellFactory);
    trackTableView.getColumns().add(3,actionCol);
  }
}
