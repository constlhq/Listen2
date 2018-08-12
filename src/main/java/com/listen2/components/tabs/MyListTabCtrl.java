package com.listen2.components.tabs;


import com.listen2.components.HomeTabPaneCtrl;
import com.listen2.components.PlayerCtrl;
import com.listen2.models.PlayList;
import com.listen2.models.PlayListMeta;
import com.listen2.models.Track;
import com.listen2.providers.IProvider;
import com.listen2.providers.Netease;
import com.listen2.providers.QQ;
import com.listen2.providers.Xiami;
import com.listen2.utils.IniFileManger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Popup;
import javafx.stage.Window;
import javafx.util.Callback;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class MyListTabCtrl{

  private Tab tab;
  private VBox add2MyListPane;
  private VBox mylistTabVBox;
  private HBox mylistSourceHBox;
  private ScrollPane scrollPane;
  private FlowPane flowPane;
  private ToggleGroup sourceToggleGroup;
  private RadioButton neRadio,xmRadio,qqRadio,kwRadio,kgRadio;
  private IProvider currentProvider;
  private int curpage;
  private PlayerCtrl playerCtrl;
  private HomeTabPaneCtrl homeTabPaneCtrl;
  private Popup createListPopup;

  public ObservableList<PlayList> getObservableMyList() {
    return observableMyList;
  }

  private ObservableList<PlayList> observableMyList;
  ListView<PlayList> add2myListView;


  public MyListTabCtrl(PlayerCtrl playerCtrl, HomeTabPaneCtrl parentTabPaneCtrl) {
    this.playerCtrl = playerCtrl;
    homeTabPaneCtrl = parentTabPaneCtrl;
    tab = new Tab("我的歌单");
    flowPane = new FlowPane();
    scrollPane = new ScrollPane();
    mylistTabVBox = new VBox();
    add2MyListPane = new VBox();
    observableMyList = FXCollections.observableArrayList();
    add2myListView = new ListView<>(observableMyList);
    init();
  }

  private void init(){
    List<PlayList> myList =  IniFileManger.getMyPlaylist();
    if(myList!=null){
      observableMyList.addAll(myList);
    }
    tab.setId("mylist-tab");
    tab.setClosable(false);
    scrollPane.getStyleClass().add("pane-dark");
    scrollPane.setFitToWidth(true);
    scrollPane.setContent(flowPane);
    mylistTabVBox.getChildren().add(scrollPane);
    flowPane.setVgap(20);
    flowPane.setHgap(20);
    flowPane.setAlignment(Pos.TOP_LEFT);
    flowPane.setPadding(new Insets(10,0,10,10));
    flowPane.getChildren().addAll(getPlaylists());
    tab.setContent(mylistTabVBox);

    playerCtrl.getAdd2MyListRegion().setOnMouseClicked(
            e->{
              Track track = playerCtrl.playQueueController.currentTrack();
              Popup add2MyListPopup = playerCtrl.getAdd2MyListPopup();
              if(!add2MyListPopup.isShowing()){
                Window window = this.getTab().getTabPane().getScene().getWindow();
                add2MyListPopup.getContent().add(buildAdd2MyListContentPane(new ArrayList<Track>(){{add(track);}}));
                add2MyListPopup.show(playerCtrl.getAdd2MyListRegion(),window.getX()+window.getWidth()/2 - 160,window.getY()+window.getHeight()/2 - 145);
              }
            });
    playerCtrl.playQueueController.getSaveAll().setOnMouseClicked(e->{
      List<Track> tracks = playerCtrl.playQueueController.getObservableTrackList();
      Popup add2MyListPopup = playerCtrl.getAdd2MyListPopup();
      if(!add2MyListPopup.isShowing()){
        Window window = this.getTab().getTabPane().getScene().getWindow();
        add2MyListPopup.getContent().add(buildAdd2MyListContentPane(tracks));
        add2MyListPopup.show(playerCtrl.getAdd2MyListRegion(),window.getX()+window.getWidth()/2 - 160,window.getY()+window.getHeight()/2 - 145);
      }
    });
  }



  public StackPane buildAdd2MyListContentPane(List<Track> tracks){
    StackPane add2MyListStackPane = new StackPane();
    add2MyListStackPane.setPrefSize(350,350);
    add2MyListStackPane.getStyleClass().add("pane-deep-dark");
    // add to existed playlist
    VBox add2ExistedListVBox = new VBox(10);
    StackPane.setMargin(add2ExistedListVBox,new Insets(20,10,20,10));
    HBox btnContainer1 = new HBox(30);
    btnContainer1.setAlignment(Pos.CENTER);
    Button createNewPlayListButton = new Button("新建歌单");
    Button add2SelectedPlayListButton = new Button("确定");
    Button cancelAdd2MyListButton = new Button("取消");
    createNewPlayListButton.getStyleClass().add("transparent-btn");
    add2SelectedPlayListButton.getStyleClass().add("transparent-btn");
    cancelAdd2MyListButton.getStyleClass().add("transparent-btn");

    add2myListView.getStyleClass().add("add-2-mylist");

    //create a new playlist and add to it
    VBox add2CreateListVBox = new VBox(10);
    StackPane.setMargin(add2CreateListVBox,new Insets(20,10,20,10));
    HBox btnContainer2 = new HBox(30);
    btnContainer2.setAlignment(Pos.CENTER_RIGHT);
    TextField myListTitle = new TextField("新建歌单名");
    TextField myListCover = new TextField("歌单封面url(可选)");
    Button createNewPlayListAndAddBtn = new Button("新建并添加");
    Button cancelCreateNewPlayListBtn = new Button("取消");
    createNewPlayListAndAddBtn.getStyleClass().add("transparent-btn");
    cancelCreateNewPlayListBtn.getStyleClass().add("transparent-btn");



    btnContainer1.getChildren().addAll(createNewPlayListButton,add2SelectedPlayListButton,cancelAdd2MyListButton);
    add2ExistedListVBox.getChildren().addAll(add2myListView ,btnContainer1);

    cancelAdd2MyListButton.setOnMouseClicked(e->{
      playerCtrl.getAdd2MyListPopup().hide();
    });

    add2SelectedPlayListButton.setOnMouseClicked(e->{
      List<Track> trackList =  observableMyList.get(add2myListView.getSelectionModel().getSelectedIndex()).tracks;
      try{
        trackList.getClass().getMethod("addAll", Collection.class).invoke(trackList,tracks);
      }
      catch (NoSuchMethodException | IllegalAccessException |InvocationTargetException err){
        err.printStackTrace();
      }
      playerCtrl.getAdd2MyListPopup().hide();
    });

    createNewPlayListButton.setOnMouseClicked(e->{
      add2ExistedListVBox.setVisible(false);
      add2CreateListVBox.setVisible(true);
    });

    createNewPlayListAndAddBtn.setOnMouseClicked(e->{
      String cover  =  myListCover.getText();
      if(!cover.startsWith("http")){
        cover  = PlayerCtrl.class.getResource("/assets/mycover.jpg").toString();
      }
      PlayListMeta myPlayListMeta =  new PlayListMeta.PlayListMetaBuilder()
              .id( UUID.randomUUID().toString())
              .title(myListTitle.getText())
              .sourceurl(null)
              .imgurl(cover)
              .build();
      PlayList myPlayList = new PlayList(myPlayListMeta,tracks);

      observableMyList.add(myPlayList);

      loadPlaylists(getPlaylists(),true);

      playerCtrl.getAdd2MyListPopup().hide();

    });

    cancelCreateNewPlayListBtn.setOnMouseClicked(e->{
      if(observableMyList.isEmpty()){
        playerCtrl.getAdd2MyListPopup().hide();
      }else{
        add2ExistedListVBox.setVisible(true);
        add2CreateListVBox.setVisible(false);
      }
    });


    btnContainer2.getChildren().addAll(createNewPlayListAndAddBtn,cancelCreateNewPlayListBtn);
    add2CreateListVBox.getChildren().addAll(myListTitle,myListCover,btnContainer2);

    add2MyListStackPane.getChildren().addAll(add2ExistedListVBox,add2CreateListVBox);

    add2ExistedListVBox.setVisible(!observableMyList.isEmpty());
    add2CreateListVBox.setVisible(observableMyList.isEmpty());

    add2myListView.setCellFactory(new Callback<ListView<PlayList>, ListCell<PlayList>>(){
      @Override
      public ListCell<PlayList> call(ListView<PlayList> mylist){
        HBox cellItemContainer = new HBox(5);
        cellItemContainer.setAlignment(Pos.CENTER_LEFT);
        ImageView listImage = new ImageView(new Image(PlayerCtrl.class.getResource("/assets/mycover.jpg").toString()));
        Label listLabel = new Label();
        listLabel.getStyleClass().add("normal-lable");
        ListCell<PlayList> cell = new ListCell<PlayList>() {
          @Override
          public void updateItem(PlayList item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {
              listImage.setImage(new Image(item.playListMeta.cover_img_url,true));
              listImage.setFitHeight(80);
              listImage.setFitWidth(80);
              listLabel.setText(item.playListMeta.title);
              cellItemContainer.getChildren().clear();
              cellItemContainer.getChildren().addAll(listImage,listLabel);
              setGraphic(cellItemContainer);
              setText(null);
            }
          }
        };
        return cell;
      }
    });
    return add2MyListStackPane;

  }


  private void loadPlaylists(List<VBox> mylistItems,boolean clear){
    if (clear){
      flowPane.getChildren().clear();
    }
    flowPane.getChildren().addAll(mylistItems);
  }

  private  List<VBox> getPlaylists(){
    return  observableMyList
            .stream().parallel().map(mylist ->{
              return new MyListItem(mylist).getMylistItemVBox();
            }).collect(Collectors.toList());
  }



  public Tab getTab() {
    return tab;
  }

  public class MyListItem {
    public PlayList myPlayList;
    private StackPane mylistItemStackPane;
    private ImageView mylistItemImageView;
    private Image mylistItemImage;
    private Button mylistItemPlayButton;
    private Label mylistNameLabel;
    private VBox mylistItemVBox;
    private String playlistID;


    public MyListItem(PlayList playList) {
      ImageView playIcon = new ImageView(new Image(MyListItem.class.getResource("/assets/play.png").toString(),16,16,false,true));
      mylistItemVBox = new VBox(5);
      mylistItemVBox.setAlignment(Pos.CENTER);
      mylistNameLabel = new Label(playList.playListMeta.title);
      mylistItemStackPane = new StackPane();
      mylistItemImage = new Image(playList.playListMeta.cover_img_url,140,140,false,true,true);
      mylistItemImageView = new ImageView(mylistItemImage);
      mylistItemPlayButton = new Button("",playIcon);
      myPlayList = playList;
      init();
    }

    private void init(){
      mylistItemStackPane.setPrefSize(140,140);
      mylistItemPlayButton.setPrefSize(16,16);
      mylistNameLabel.getStyleClass().add("playlist-label");
      mylistItemPlayButton.getStyleClass().add("icon-btn");
      mylistItemStackPane.setAlignment(Pos.BOTTOM_RIGHT);
      mylistItemStackPane.getChildren().addAll(mylistItemImageView,mylistItemPlayButton);
      mylistItemVBox.getChildren().addAll(mylistItemStackPane,mylistNameLabel);
      addListeners();

    }

    private void addListeners(){
      EventHandler<MouseEvent> showPlaylistDetailHandler  = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
          MyListDetailTabCtrl mylistDetailTabCtrl = new MyListDetailTabCtrl("歌单详情",myPlayList,playerCtrl,currentProvider,homeTabPaneCtrl);
          Tab mylistDetailTab = mylistDetailTabCtrl.getTab();
          homeTabPaneCtrl.addTab(mylistDetailTab);
          homeTabPaneCtrl.getContainerTabPane().getSelectionModel().select(mylistDetailTabCtrl.getTab());
        }
      };

      mylistNameLabel.setOnMouseClicked(showPlaylistDetailHandler);
      mylistItemImageView.setOnMouseClicked(showPlaylistDetailHandler);
      mylistItemPlayButton.setOnMouseClicked(e->{
        playerCtrl.playPlayList(myPlayList);
      });
    }
    public VBox getMylistItemVBox() {
      return mylistItemVBox;
    }
  }
}
