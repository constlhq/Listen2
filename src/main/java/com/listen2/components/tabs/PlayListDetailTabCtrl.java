package com.listen2.components.tabs;

import com.listen2.components.HomeTabPaneCtrl;
import com.listen2.components.PlayerCtrl;
import com.listen2.models.PlayList;
import com.listen2.models.Track;
import com.listen2.providers.IProvider;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Callback;


public abstract class PlayListDetailTabCtrl {
  protected IProvider provider;
  protected PlayList playList;
  protected PlayerCtrl playerCtrl;
  protected HomeTabPaneCtrl homeTabPaneCtrl;
  protected Tab tab;
  protected GridPane gridPane;
  protected VBox metaVBox;
  protected TableView<Track> trackTableView;
  protected ImageView coverImageView;
  protected Label playListNameLable;
  protected ObservableList<Track> trackObservableList;
  protected Button playListBtn,addAll2QueuqBtn,functionBtn3,functionBtn4;


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

    tab  = new Tab(title);
    //init();
  }

  protected void init(){
    tab.setContent(gridPane);
    gridPane.add(metaVBox,0,0);
    gridPane.add(trackTableView,1,0);
    HBox btnGroup =  new HBox();
    btnGroup.setAlignment(Pos.CENTER);
    playListBtn.getStyleClass().add("icon-btn");
    addAll2QueuqBtn.getStyleClass().add("icon-btn");

    playListNameLable.getStyleClass().add("playlist-name-lable");

    metaVBox.setAlignment(Pos.TOP_CENTER);
    VBox.setMargin(coverImageView,new Insets(25,5,5,5));

    btnGroup.getChildren().addAll(playListBtn,addAll2QueuqBtn);
    metaVBox.getChildren().addAll(coverImageView,playListNameLable,btnGroup);
    coverImageView.setFitHeight(224);
    coverImageView.setFitWidth(224);

    trackTableView.setPrefSize(960-224,620);
    trackTableView.setItems(trackObservableList);
    tab.setOnClosed(e->{
      int tabCount =  homeTabPaneCtrl.getContainerTabPane().getTabs().size();
      homeTabPaneCtrl.getContainerTabPane().getSelectionModel().select(tabCount==4?0:tabCount-1);
    });

  }

  protected void addListeners(){
    playListBtn.setOnMouseClicked(e->{
      playerCtrl.playPlayList(playList);
    });
    addAll2QueuqBtn.setOnMouseClicked(e->{
      playList.tracks.forEach(track ->playerCtrl.addTrack(track));
    });
  }

  public  void buildTable() {
    TableColumn<Track, String> songCol = new TableColumn<>("曲目");
    songCol.setEditable(true);
    songCol.setCellValueFactory(new PropertyValueFactory("titleProperty"));
    songCol.setPrefWidth(trackTableView.getPrefWidth() * 0.35);

    TableColumn<Track, String> singerCol = new TableColumn<>("歌手");
    singerCol.setCellValueFactory(new PropertyValueFactory("artistProperty"));
    singerCol.setPrefWidth(trackTableView.getPrefWidth() * 0.2);

    TableColumn<Track, String> albumCol = new TableColumn<>("专辑");
    albumCol.setCellValueFactory(new PropertyValueFactory("albumProperty"));
    albumCol.setPrefWidth(trackTableView.getPrefWidth() * 0.2);


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

                  protected String getString() {
                    return getItem() == null ? "" : getItem().toString();
                  }
                };
                cell.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
                  if (event.getEventType() == MouseEvent.MOUSE_CLICKED) {
                    int selindex = trackTableView.getSelectionModel().getSelectedIndex();
                    if (selindex != -1) {
                      Track choosenTrack = trackObservableList.get(selindex);
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

                  protected String getString() {
                    return getItem() == null ? "" : getItem().toString();
                  }
                };
                cell.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
                  if (event.getEventType() == MouseEvent.MOUSE_CLICKED) {
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

                  protected String getString() {
                    return getItem() == null ? "" : getItem().toString();
                  }
                };
                cell.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
                  if (event.getEventType() == MouseEvent.MOUSE_CLICKED) {
                    Track choosenTrack = trackObservableList.get(trackTableView.getSelectionModel().getSelectedIndex());
                    PlayList playList = provider.album(choosenTrack.album_id);
                    PlayListDetailTabCtrl playListDetailTabCtrl = new MyListDetailTabCtrl("专辑详情", playList, playerCtrl, provider, homeTabPaneCtrl);
                    Tab playListDetailTab = playListDetailTabCtrl.getTab();
                    homeTabPaneCtrl.addTab(playListDetailTab);
                    homeTabPaneCtrl.getContainerTabPane().getSelectionModel().select(playListDetailTabCtrl.getTab());
                    homeTabPaneCtrl.getContainerTabPane().getSelectionModel().selectLast();
                  }
                });
                return cell;
              }

            };


    songCol.setCellFactory(songCellFactory);
    singerCol.setCellFactory(singerCellFactory);
    albumCol.setCellFactory(albumCellFactory);


    trackTableView.getColumns().add(0, songCol);
    trackTableView.getColumns().add(1, singerCol);
    trackTableView.getColumns().add(2, albumCol);

  }
  public Tab getTab() {
    return tab;
  }
}
