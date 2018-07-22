package com.listen2.components.tabs;

import com.listen2.components.HomeTabPaneCtrl;
import com.listen2.components.PlayerCtrl;
import com.listen2.models.PlayList;
import com.listen2.providers.*;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class PlayListTabCtrl{

  static Map<String,IProvider> providerMap = new HashMap<String,IProvider>(){{
      put("netease",new Netease());
      put("xiami",new Xiami());
      put("qq",new QQ());
      put("kugou",new Kugou());
      put("kuwo",new Kuwo());
  }};

  private Tab tab;
  private TabPane playListSourceTabPane;
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
    flowPane = new FlowPane();
    scrollPane = new ScrollPane();
    playListTabVBox = new VBox();

    playListSourceHBox = new HBox(10);
    sourceToggleGroup = new ToggleGroup();
    neRadio = new RadioButton("网易");
    xmRadio = new RadioButton("虾米");
    qqRadio = new RadioButton("QQ");
    kgRadio = new RadioButton("酷狗");
    kwRadio = new RadioButton("酷我");

    init();
    addListeners();
  }

  private void init(){
    tab.setId("playlist-tab");
    tab.setClosable(false);
    neRadio.setUserData("netease");
    xmRadio.setUserData("xiami");
    qqRadio.setUserData("qq");
    kgRadio.setUserData("kugou");
    kwRadio.setUserData("kuwo");

    neRadio.setToggleGroup(sourceToggleGroup);
    xmRadio.setToggleGroup(sourceToggleGroup);
    qqRadio.setToggleGroup(sourceToggleGroup);
    kgRadio.setToggleGroup(sourceToggleGroup);
    kwRadio.setToggleGroup(sourceToggleGroup);

    playListSourceHBox.getChildren().addAll(neRadio,xmRadio,qqRadio,kgRadio,kwRadio);
    playListSourceHBox.setAlignment(Pos.CENTER);

    sourceToggleGroup.selectToggle(neRadio);
    currentProvider = providerMap.get("netease");


    scrollPane.getStyleClass().add("pane-dark");
    scrollPane.setFitToWidth(true);
    scrollPane.setContent(flowPane);
    playListTabVBox.getChildren().addAll(playListSourceHBox,scrollPane);
    flowPane.setVgap(20);
    flowPane.setHgap(20);
    flowPane.setAlignment(Pos.TOP_LEFT);
    tab.setContent(playListTabVBox);
    loadPlaylists(currentProvider,0,true);
  }

  private void loadPlaylists(IProvider provider, int cpage, boolean clear) {
    ExecutorService es = Executors.newCachedThreadPool();
    Task task = new Task() {
      @Override
      protected Object call() throws Exception {
        System.out.println("taks");
        List<VBox> items = provider.get_playlists(cpage)
                .parallelStream().map(playListMeta ->
                        new PlayListItem(playListMeta.title, playListMeta.cover_img_url, playListMeta.id).getPlayListItemVBox()
                ).collect(Collectors.toList());

        Platform.runLater(() -> {
          if (clear) {
            flowPane.getChildren().clear();
          }
          if (items != null)
            flowPane.getChildren().addAll(items);
        });
        return null;
      }
    };
    Future future = es.submit(task);
  }

  public void addListeners(){

    scrollPane.vvalueProperty().addListener(
            (obs,lv,nv) -> {
              if (nv.doubleValue()==1){
                //load more
                loadPlaylists(currentProvider,++curpage, false);
              }
            });

    sourceToggleGroup.selectedToggleProperty().addListener((obv,ov,nv)->{
      curpage = 0;
      String providerCode = nv.getUserData().toString();
      currentProvider = providerMap.get(providerCode);
      loadPlaylists(currentProvider,curpage,true);
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
      playListItemVBox.setAlignment(Pos.CENTER);
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
    public VBox getPlayListItemVBox() {
      return playListItemVBox;
    }
  }
}
