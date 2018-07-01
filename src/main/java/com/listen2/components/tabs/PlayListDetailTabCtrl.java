package com.listen2.components.tabs;

import com.listen2.components.HomeTabPaneCtrl;
import com.listen2.components.PlayerCtrl;
import com.listen2.models.PlayList;
import com.listen2.models.Track;
import com.listen2.providers.IProvider;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.web.PopupFeatures;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Callback;


public class PlayListDetailTabCtrl {
  private IProvider provider;
  private PlayList playList;
  private PlayerCtrl playerCtrl;
  private HomeTabPaneCtrl homeTabPaneCtrl;
  private Tab tab;
  private GridPane gridPane;
  private VBox metaVBox;
  private TableView<Track> trackTableView;
  private ImageView coverImageView;
  private Label playListNameLable;
  private ObservableList<Track> trackObservableList;
  private Button playListBtn,addAll2QueuqBtn,saveListBtn,openLinkBtn;


  public PlayListDetailTabCtrl(String title,PlayList playList, PlayerCtrl playerCtrl,IProvider provider,HomeTabPaneCtrl homeTabPaneCtrl) {
    this.playList = playList;
    this.playerCtrl = playerCtrl;
    this.provider = provider;
    this.homeTabPaneCtrl = homeTabPaneCtrl;
    gridPane = new GridPane();
    metaVBox = new VBox();
    trackTableView = new TableView<>();
    trackObservableList = FXCollections.observableArrayList(playList.tracks);
    coverImageView = new ImageView(new Image(playList.playListMeta.cover_img_url));
    playListNameLable = new Label(playList.playListMeta.title);
    playListBtn = new Button("",new ImageView(new Image(PlayListDetailTabCtrl.class.getResource("/assets/play.png").toString())));
    addAll2QueuqBtn = new Button("",new ImageView(new Image(PlayListDetailTabCtrl.class.getResource("/assets/add.png").toString())));
    saveListBtn = new Button("",new ImageView(new Image(PlayListDetailTabCtrl.class.getResource("/assets/folder.png").toString())));
    openLinkBtn = new Button("",new ImageView(new Image(PlayListDetailTabCtrl.class.getResource("/assets/link.png").toString())));
    tab  = new Tab(title);
    init();
  }

  private void init(){
    tab.setContent(gridPane);
    gridPane.add(metaVBox,0,0);
    gridPane.add(trackTableView,1,0);
    HBox btnGroup =  new HBox();
    btnGroup.setAlignment(Pos.CENTER);
    playListBtn.getStyleClass().add("icon-btn");
    addAll2QueuqBtn.getStyleClass().add("icon-btn");
    saveListBtn.getStyleClass().add("icon-btn");
    openLinkBtn.getStyleClass().add("icon-btn");
    playListNameLable.getStyleClass().add("playlist-name-lable");

    metaVBox.setAlignment(Pos.TOP_CENTER);
    VBox.setMargin(coverImageView,new Insets(25,5,5,5));
    btnGroup.getChildren().addAll(playListBtn,addAll2QueuqBtn,saveListBtn,openLinkBtn);
    metaVBox.getChildren().addAll(coverImageView,playListNameLable,btnGroup);
    coverImageView.setFitHeight(224);
    coverImageView.setFitWidth(224);

    trackTableView.setPrefSize(960-224,620);
    buildTable();
    trackTableView.setItems(trackObservableList);
    addListeners();
    tab.setOnClosed(e->{
      int tabCount =  homeTabPaneCtrl.getContainerTabPane().getTabs().size();
      homeTabPaneCtrl.getContainerTabPane().getSelectionModel().select(tabCount==4?0:tabCount-1);
    });

  }

  private void addListeners(){
    playListBtn.setOnMouseClicked(e->{
      playerCtrl.playPlayList(playList);
    });
    addAll2QueuqBtn.setOnMouseClicked(e->{
      playList.tracks.forEach(track ->playerCtrl.addTrack(track));
    });
    saveListBtn.setOnMouseClicked(e->{
      System.out.println("saveall");
    });
    openLinkBtn.setOnMouseClicked(e->{
      Tab tempTab =  new Tab("原始链接");
      tempTab.setClosable(true);
      WebView browser = new WebView();
      final WebEngine webEngine = browser.getEngine();
      webEngine.setCreatePopupHandler((param->webEngine));
      webEngine.load(playList.playListMeta.source_url);
      tempTab.setContent(browser);
      homeTabPaneCtrl.addTab(tempTab);
    });
  }

  public void buildTable(){

    TableColumn<Track, String> songCol = new TableColumn<>("曲目");
    songCol.setEditable(true);
    songCol.setCellValueFactory(new PropertyValueFactory("titleProperty"));
    songCol.setPrefWidth(trackTableView.getPrefWidth() *0.35);
    
    TableColumn<Track, String> singerCol = new TableColumn<>("歌手");
    singerCol.setCellValueFactory(new PropertyValueFactory("artistProperty"));
    singerCol.setPrefWidth(trackTableView.getPrefWidth() *0.2);

    TableColumn<Track, String> albumCol = new TableColumn<>("专辑");
    albumCol.setCellValueFactory(new PropertyValueFactory("albumProperty"));
    albumCol.setPrefWidth(trackTableView.getPrefWidth() *0.2);

    TableColumn actionCol = new TableColumn();
    actionCol.setCellValueFactory(new PropertyValueFactory<>("Dummy"));
    actionCol.setPrefWidth(trackTableView.getPrefWidth() *0.15);


    Callback<TableColumn<Track, String>, TableCell<Track, String>> songCellFactory =
            new Callback<TableColumn<Track, String>, TableCell<Track, String>>() {
              @Override
              public TableCell call(final TableColumn<Track, String> param) {
                final TableCell<Track, String> cell = new TableCell<Track, String>() {
                  @Override
                  public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : getString());
                    setAlignment(Pos.CENTER_LEFT);
                    setGraphic(null);
                  }
                  private String getString() {
                    return getItem() == null ? "" : getItem().toString();
                  }
                };
                cell.addEventFilter(MouseEvent.MOUSE_CLICKED, event ->{
                    if (event.getEventType() ==MouseEvent.MOUSE_CLICKED) {
                      int selindex = trackTableView.getSelectionModel().getSelectedIndex();
                      if (selindex!=-1){
                        Track choosenTrack =  trackObservableList.get(selindex);
                        playerCtrl.addTrackThenPlay(choosenTrack);
                      }
                    }
                  });
                return cell;
              }
            };

    Callback<TableColumn<Track, String>, TableCell<Track, String>> singerCellFactory =
            new Callback<TableColumn<Track, String>, TableCell<Track, String>>() {
              @Override
              public TableCell call(final TableColumn<Track, String> param) {
                final TableCell<Track, String> cell = new TableCell<Track, String>() {
                  @Override
                  public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : getString());
                    setAlignment(Pos.CENTER_LEFT);
                    setGraphic(null);
                  }
                  private String getString() {
                    return getItem() == null ? "" : getItem().toString();
                  }
                };
                cell.addEventFilter(MouseEvent.MOUSE_CLICKED, event ->{
                    if (event.getEventType() ==MouseEvent.MOUSE_CLICKED) {
                      Track choosenTrack = trackObservableList.get(trackTableView.getSelectionModel().getSelectedIndex());

                    }
                  });

                return cell;
              }

            };

    Callback<TableColumn<Track, String>, TableCell<Track, String>> albumCellFactory =
            new Callback<TableColumn<Track, String>, TableCell<Track, String>>() {
              @Override
              public TableCell call(final TableColumn<Track, String> param) {
                final TableCell<Track, String> cell = new TableCell<Track, String>() {
                  @Override
                  public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : getString());
                    setAlignment(Pos.CENTER_LEFT);
                    setGraphic(null);
                  }
                  private String getString() {
                    return getItem() == null ? "" : getItem().toString();
                  }
                };
                cell.addEventFilter(MouseEvent.MOUSE_CLICKED, event ->{
                    if (event.getEventType() ==MouseEvent.MOUSE_CLICKED) {
                      Track choosenTrack = trackObservableList.get(trackTableView.getSelectionModel().getSelectedIndex());
                      PlayList playList = provider.album(choosenTrack.album_id);
                      PlayListDetailTabCtrl playListDetailTabCtrl = new PlayListDetailTabCtrl("专辑详情",playList,playerCtrl,provider,homeTabPaneCtrl);
                      Tab playListDetailTab = playListDetailTabCtrl.getTab();
                      homeTabPaneCtrl.addTab(playListDetailTab);
                      homeTabPaneCtrl.getContainerTabPane().getSelectionModel().select(playListDetailTabCtrl.getTab());
                      homeTabPaneCtrl.getContainerTabPane().getSelectionModel().selectLast();
                    }
                  });
                return cell;
              }

            };

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
                        playerCtrl.addTrackThenPlay(trackObservableList.get(index));

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


    songCol.setCellFactory(songCellFactory);
    singerCol.setCellFactory(singerCellFactory);
    albumCol.setCellFactory(albumCellFactory);
    actionCol.setCellFactory(actionCellFactory);


    trackTableView.getColumns().setAll(songCol,singerCol,albumCol,actionCol);
  }

  public Tab getTab() {
    return tab;
  }
}
