package com.listen2.components.tabs;


import com.listen2.components.HomeTabPaneCtrl;
import com.listen2.components.PlayerCtrl;
import com.listen2.models.PlayList;
import com.listen2.models.Track;
import com.listen2.providers.IProvider;
import com.listen2.providers.Netease;
import com.listen2.providers.QQ;
import com.listen2.providers.Xiami;
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
import javafx.scene.text.TextAlignment;



import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class MyListTabCtrl{


  private Tab tab;
  private List<MyListItem> mylistItemList;
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

  public MyListTabCtrl(PlayerCtrl playerCtrl, HomeTabPaneCtrl parentTabPaneCtrl) {
    this.playerCtrl = playerCtrl;
    homeTabPaneCtrl = parentTabPaneCtrl;
    tab = new Tab("我的歌单");
    mylistItemList = new ArrayList<>();
    flowPane = new FlowPane();
    scrollPane = new ScrollPane();
    mylistTabVBox = new VBox();

    init();
    addListeners();
  }

  private void init(){
    tab.setId("mylist-tab");
    tab.setClosable(false);
    scrollPane.getStyleClass().add("scroll-pane-dark");
    scrollPane.setFitToWidth(true);
    scrollPane.setContent(flowPane);
    mylistTabVBox.getChildren().add(scrollPane);
    flowPane.setVgap(20);
    flowPane.setHgap(20);
    flowPane.setAlignment(Pos.TOP_LEFT);
    tab.setContent(mylistTabVBox);
  }
  private void loadPlaylists(List<VBox> mylistItems,boolean clear){
    if (clear){
      flowPane.getChildren().clear();
    }
    flowPane.getChildren().addAll(mylistItems);
  }

  private  List<VBox> getPlaylists(IProvider provider,int curpage){
    return  provider.get_playlists(curpage)
            .stream().parallel().map(mylistMeta ->{
              return new MyListItem(mylistMeta.title,mylistMeta.cover_img_url,mylistMeta.id).getPlayListItemVBox();
            }).collect(Collectors.toList());
  }

  public void addListeners(){

  }

  public Tab getTab() {
    return tab;
  }


  public class MyListItem {
    private StackPane mylistItemStackPane;
    private ImageView mylistItemImageView;
    private Image mylistItemImage;
    private Button mylistItemPlayButton;
    private Label mylistNameLabel;
    private VBox mylistItemVBox;
    private String playlistID;


    public MyListItem(String mylistName, String imgurl, String id) {
      ImageView playIcon = new ImageView(new Image(MyListItem.class.getResource("/assets/play.png").toString(),16,16,false,true));
      mylistItemVBox = new VBox(5);
      mylistNameLabel = new Label(mylistName);
      mylistItemStackPane = new StackPane();
      mylistItemImage = new Image(imgurl,140,140,false,true,true);
      mylistItemImageView = new ImageView(mylistItemImage);
      mylistItemPlayButton = new Button("",playIcon);
      playlistID = id;
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
          PlayList mylist =  currentProvider.get_playlist(playlistID);
          PlayListDetailTabCtrl mylistDetailTabCtrl = new PlayListDetailTabCtrl("歌单详情",mylist,playerCtrl,currentProvider,homeTabPaneCtrl);
          Tab mylistDetailTab = mylistDetailTabCtrl.getTab();
          homeTabPaneCtrl.addTab(mylistDetailTab);
          homeTabPaneCtrl.getContainerTabPane().getSelectionModel().select(mylistDetailTabCtrl.getTab());
        }
      };

      mylistNameLabel.setOnMouseClicked(showPlaylistDetailHandler);
      mylistItemImageView.setOnMouseClicked(showPlaylistDetailHandler);
      mylistItemPlayButton.setOnMouseClicked(e->{
        PlayList mylist =  currentProvider.get_playlist(playlistID);
        playerCtrl.playPlayList(mylist);
      });
    }

    public StackPane getPlayListItemContaioner() {
      return mylistItemStackPane;
    }

    public ImageView getPlayListItemImageView() {
      return mylistItemImageView;
    }

    public Image getPlayListItemImage() {
      return mylistItemImage;
    }

    public Button getPlayListItemPlayButton() {
      return mylistItemPlayButton;
    }

    public VBox getPlayListItemVBox() {
      return mylistItemVBox;
    }
  }
}
