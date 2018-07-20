package com.listen2.components.tabs;


import com.listen2.components.HomeTabPaneCtrl;
import com.listen2.components.PlayerCtrl;
import com.listen2.models.PlayList;
import com.listen2.models.Track;
import com.listen2.providers.*;
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

public class PlayListTabCtrl{

  static Map<String,IProvider> providerMap = new HashMap<String,IProvider>(){{
      put("netease",new Netease());
      put("xiami",new Xiami());
      put("qq",new QQ());
      put("kuwo",new Kuwo());
      put("kugou",new Kugou());
    }};

  private Tab tab;
  private TabPane playListSourceTabPane;
  private List<PlayListItem> playListItemList;
  private VBox playListTabVBox;
  private HBox playListSourceHBox;
  private ScrollPane scrollPane;
  private FlowPane flowPane;
  private ToggleGroup sourceToggleGroup;
  private RadioButton neRadio,xmRadio,qqRadio,kwRadio,kgRadio;
  private IProvider currentProvider;
  private int curpage;
  private PlayerCtrl playerCtrl;
  private HomeTabPaneCtrl homeTabPaneCtrl;




  public PlayListTabCtrl(PlayerCtrl playerCtrl, HomeTabPaneCtrl parentTabPaneCtrl) {
    this.playerCtrl = playerCtrl;
    homeTabPaneCtrl = parentTabPaneCtrl;
    tab = new Tab("精选歌单");
    playListItemList = new ArrayList<>(50);
    flowPane = new FlowPane();
    scrollPane = new ScrollPane();
    playListTabVBox = new VBox();
//    vBoxList = FXCollections.observableArrayList();

    playListSourceHBox = new HBox(10);
    sourceToggleGroup = new ToggleGroup();
    neRadio = new RadioButton("网易");
    xmRadio = new RadioButton("虾米");
    qqRadio = new RadioButton("QQ");
    kwRadio = new RadioButton("酷我");
    kgRadio = new RadioButton("酷狗");

    init();
    addListeners();
  }

  private void init(){
    tab.setId("playlist-tab");
    tab.setClosable(false);
    neRadio.setUserData("netease");
    xmRadio.setUserData("xiami");
    qqRadio.setUserData("qq");
    kwRadio.setUserData("kuwo");
    kgRadio.setUserData("kugou");

    neRadio.setToggleGroup(sourceToggleGroup);
    xmRadio.setToggleGroup(sourceToggleGroup);
    qqRadio.setToggleGroup(sourceToggleGroup);
    kwRadio.setToggleGroup(sourceToggleGroup);
    kgRadio.setToggleGroup(sourceToggleGroup);

    playListSourceHBox.getChildren().addAll(neRadio,xmRadio,qqRadio,kwRadio,kgRadio);
    playListSourceHBox.setAlignment(Pos.CENTER);

    sourceToggleGroup.selectToggle(neRadio);
    currentProvider = providerMap.get("netease");
    loadPlaylists(getPlaylists(currentProvider,0),true);

    scrollPane.getStyleClass().add("scroll-pane-dark");
    scrollPane.setFitToWidth(true);
    scrollPane.setContent(flowPane);
    playListTabVBox.getChildren().addAll(playListSourceHBox,scrollPane);
    flowPane.setVgap(20);
    flowPane.setHgap(20);
    flowPane.setAlignment(Pos.TOP_LEFT);
    tab.setContent(playListTabVBox);
  }
  private void loadPlaylists(List<VBox> playListItems,boolean clear){
    if (clear){
      flowPane.getChildren().clear();
    }
    flowPane.getChildren().addAll(playListItems);
  }

  private  List<VBox> getPlaylists(IProvider provider,int curpage){
   return  provider.get_playlists(curpage)
            .stream().parallel().map(playListMeta ->{
              return new PlayListItem(playListMeta.title,playListMeta.cover_img_url,playListMeta.id).getPlayListItemVBox();
            }).collect(Collectors.toList());
  }

  public void addListeners(){

    scrollPane.vvalueProperty().addListener(
            (obs,lv,nv) -> {
              if (nv.doubleValue()==1){
                //load more
                loadPlaylists(getPlaylists(currentProvider,++curpage),false);
              }
            });



    sourceToggleGroup.selectedToggleProperty().addListener((obv,ov,nv)->{
      curpage = 0;
      String providerCode = nv.getUserData().toString();
      currentProvider = providerMap.get(providerCode);
        flowPane.getChildren().clear();
        flowPane.getChildren().addAll(getPlaylists(currentProvider,curpage));
    });
  }

  public Tab getTab() {
    return tab;
  }



  public class PlayListItem {
    private StackPane playListItemStackPane;
    private ImageView playListItemImageView;
    private Image playListItemImage;
    private Button playListItemPlayButton;
    private Label playListNameLabel;
    private VBox playListItemVBox;
    private String playlistID;



    public PlayListItem(String playListName, String imgurl, String id) {
      ImageView playIcon = new ImageView(new Image(PlayListItem.class.getResource("/assets/play.png").toString(),16,16,false,true));
      playListItemVBox = new VBox(5);
      playListNameLabel = new Label(playListName);
      playListItemStackPane = new StackPane();
      playListItemImage = new Image(imgurl,140,140,false,true,true);
      playListItemImageView = new ImageView(playListItemImage);
      playListItemPlayButton = new Button("",playIcon);
      playlistID = id;
      init();
    }
    
    private void init(){
      playListItemStackPane.setPrefSize(140,140);
      playListItemPlayButton.setPrefSize(16,16);

      playListNameLabel.getStyleClass().add("playlist-label");
      playListItemPlayButton.getStyleClass().add("icon-btn");
      playListItemStackPane.setAlignment(Pos.BOTTOM_RIGHT);
      playListItemStackPane.getChildren().addAll(playListItemImageView,playListItemPlayButton);
      playListItemVBox.getChildren().addAll(playListItemStackPane,playListNameLabel);

      addListeners();

    }

    private void addListeners(){
      EventHandler<MouseEvent> showPlaylistDetailHandler  = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
          PlayList playList =  currentProvider.get_playlist(playlistID);
          PlayListDetailTabCtrl playListDetailTabCtrl = new PlayListDetailTabCtrl("歌单详情",playList,playerCtrl,currentProvider,homeTabPaneCtrl);
          Tab playListDetailTab = playListDetailTabCtrl.getTab();
          homeTabPaneCtrl.addTab(playListDetailTab);
          homeTabPaneCtrl.getContainerTabPane().getSelectionModel().select(playListDetailTabCtrl.getTab());
        }
      };

      playListNameLabel.setOnMouseClicked(showPlaylistDetailHandler);
      playListItemImageView.setOnMouseClicked(showPlaylistDetailHandler);
      playListItemPlayButton.setOnMouseClicked(e->{
        PlayList playList =  currentProvider.get_playlist(playlistID);
        playerCtrl.playPlayList(playList);
      });
    }

    public StackPane getPlayListItemContaioner() {
      return playListItemStackPane;
    }

    public ImageView getPlayListItemImageView() {
      return playListItemImageView;
    }

    public Image getPlayListItemImage() {
      return playListItemImage;
    }

    public Button getPlayListItemPlayButton() {
      return playListItemPlayButton;
    }

    public VBox getPlayListItemVBox() {
      return playListItemVBox;
    }
  }
}
