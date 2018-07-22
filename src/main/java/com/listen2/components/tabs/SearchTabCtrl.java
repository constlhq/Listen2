package com.listen2.components.tabs;

import com.listen2.components.HomeTabPaneCtrl;
import com.listen2.components.PlayerCtrl;
import com.listen2.models.PlayList;
import com.listen2.models.SearchResult;
import com.listen2.models.Track;
import com.listen2.providers.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Callback;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SearchTabCtrl{

  static Map<String,IProvider> providerMap = new HashMap<String,IProvider>(){{
    put("netease",new Netease());
    put("xiami",new Xiami());
    put("qq",new QQ());
    put("kugou",new Kugou());
    put("kuwo",new Kuwo());
  }};

  static Map<String,Integer> providerIndexMap = new HashMap<String,Integer>(){{
    put("netease",0);
    put("xiami",1);
    put("qq",2);
    put("kugou",3);
    put("kuwo",4);
  }};


  private Tab tab;
  private HomeTabPaneCtrl homeTabPaneCtrl;
  private PlayerCtrl playerCtrl;
  private TableView<Track> resultTrackTable;
  private int[] currentPageBook;
  private int[] totalPageBook;
  private IProvider currentProvider;
  private int currentProviderIndex;
  private TextField searchInput;
  private RadioButton neRadio,xmRadio,qqRadio,kwRadio,kgRadio;
  private Button clearBtn,nextPageBtn,previousPageBtn;
  private VBox containerVBox;
  private HBox searchSourceHBox;
  private StackPane searchInputPane;
  private ToggleGroup sourceToggleGroup;
  private ObservableList<Track> trackObservableList;
  private Label pageLabel;
  private HBox paginHBox;

  public SearchTabCtrl(PlayerCtrl playerCtrl, HomeTabPaneCtrl homeTabPaneCtrl) {
    tab  = new Tab("快速搜索");
    this.playerCtrl = playerCtrl;
    this.homeTabPaneCtrl = homeTabPaneCtrl;
    containerVBox = new VBox();
    resultTrackTable = new TableView<>();
    trackObservableList = FXCollections.observableArrayList();
    searchSourceHBox = new HBox();
    searchInputPane  = new StackPane();
    searchInput = new TextField();
    currentPageBook = new int[]{1,1,1,1,1};
    totalPageBook = new int[]{0,0,0,0,0};
    clearBtn = new Button("",new ImageView(new Image(SearchTabCtrl.class.getResource("/assets/backspace.png").toString(),32,32,true,true)));
    paginHBox = new HBox(5);
    previousPageBtn = new Button("上一页");
    pageLabel = new Label();
    nextPageBtn = new Button("下一页");
    sourceToggleGroup = new ToggleGroup();
    neRadio = new RadioButton("网易");
    xmRadio = new RadioButton("虾米");
    qqRadio = new RadioButton("QQ");
    kgRadio = new RadioButton("酷狗");
    kwRadio = new RadioButton("酷我");

    init();
  }

  private void init(){
    tab.setId("search-tab");
    tab.setClosable(false);
    clearBtn.getStyleClass().add("icon-btn");
    clearBtn.setVisible(false);
    searchInput.getStyleClass().add("search-text-field");
    containerVBox.setAlignment(Pos.TOP_CENTER);
    searchInputPane.setAlignment(Pos.CENTER_RIGHT);
    paginHBox.setAlignment(Pos.CENTER);
    paginHBox.setVisible(false);
    previousPageBtn.getStyleClass().add("transparent-btn");
    nextPageBtn.getStyleClass().add("transparent-btn");
    StackPane.setMargin(searchInput,new Insets(10,220,0,220));
    StackPane.setMargin(clearBtn,new Insets(10,220,0,0));
    paginHBox.getChildren().addAll(previousPageBtn,pageLabel,nextPageBtn);
    searchSourceHBox.setAlignment(Pos.CENTER);

    resultTrackTable.setPrefSize(960,560);
    resultTrackTable.setVisible(false);

    neRadio.setToggleGroup(sourceToggleGroup);
    xmRadio.setToggleGroup(sourceToggleGroup);
    qqRadio.setToggleGroup(sourceToggleGroup);
    kwRadio.setToggleGroup(sourceToggleGroup);
    kgRadio.setToggleGroup(sourceToggleGroup);

    neRadio.setUserData("netease");
    xmRadio.setUserData("xiami");
    qqRadio.setUserData("qq");
    kwRadio.setUserData("kuwo");
    kgRadio.setUserData("kugou");

    sourceToggleGroup.selectToggle(neRadio);
    currentProvider = providerMap.get("netease");
    currentProviderIndex =0;

    searchSourceHBox.getChildren().addAll(neRadio,xmRadio,qqRadio,kgRadio,kwRadio);
    searchInputPane.getChildren().addAll(searchInput,clearBtn);
    VBox.setMargin(resultTrackTable,new Insets(-15 ,10, 0, 10));
    containerVBox.getChildren().addAll(searchInputPane,searchSourceHBox,resultTrackTable,paginHBox);

    tab.setContent(containerVBox);
    addListeners();
    buildTable();
    resultTrackTable.setItems(trackObservableList);
  }

  private void doSearch(){
    String keyword = searchInput.getText();
    int curpage = currentPageBook[currentProviderIndex];
    System.out.println(curpage+"CUYRPAGE");
    if(!keyword.isEmpty()){

      SearchResult searchResult = currentProvider.search(keyword,curpage);
      trackObservableList.clear();
      trackObservableList.addAll(searchResult.tracks);
      totalPageBook[currentProviderIndex] = (int)Math.ceil(searchResult.total/20.0);
      resultTrackTable.setVisible(!trackObservableList.isEmpty());
      paginHBox.setVisible(totalPageBook[currentProviderIndex]>1);
      pageLabel.setText((currentPageBook[currentProviderIndex]) + "/"+totalPageBook[currentProviderIndex]);
      pageLabel.setVisible(totalPageBook[currentProviderIndex]>1);
      if(currentPageBook[currentProviderIndex]==1){
        previousPageBtn.setDisable(true);
      }else{
        previousPageBtn.setDisable(false);
      }
      if(currentPageBook[currentProviderIndex] == totalPageBook[currentProviderIndex]){
        nextPageBtn.setDisable(true);
      }else{
        nextPageBtn.setDisable(false);
      }
    }
  }

  private void addListeners(){
    searchInput.textProperty().addListener((obv,ov,nv)->{
      if (!nv.isEmpty() && !nv.equals(ov)){
        Arrays.fill(currentPageBook,1);
        Arrays.fill(totalPageBook,0);
        clearBtn.setVisible(true);
        doSearch();
      }else{
        clearBtn.setVisible(false);
        trackObservableList.clear();
        resultTrackTable.setVisible(false);
      }
    });

    previousPageBtn.setOnMouseClicked(e->{
      currentPageBook[currentProviderIndex]--;
      doSearch();
    });

    nextPageBtn.setOnMouseClicked(e->{
      currentPageBook[currentProviderIndex]++;
      doSearch();
    });

    sourceToggleGroup.selectedToggleProperty().addListener((obv,ov,nv)->{
      String providerCode = nv.getUserData().toString();
      currentProvider = providerMap.get(providerCode);
      currentProviderIndex = providerIndexMap.get(providerCode);
      doSearch();
    });

    clearBtn.setOnMouseClicked(e->{searchInput.clear();
    searchInput.requestFocus();
    });
  }

  public void buildTable(){
    TableColumn<Track, String> songCol = new TableColumn<>("曲目");
    songCol.setEditable(true);
    songCol.setCellValueFactory(new PropertyValueFactory("titleProperty"));
    songCol.setPrefWidth(resultTrackTable.getPrefWidth() *0.35);

    TableColumn<Track, String> singerCol = new TableColumn<>("歌手");
    singerCol.setCellValueFactory(new PropertyValueFactory("artistProperty"));
    singerCol.setPrefWidth(resultTrackTable.getPrefWidth() *0.2);

    TableColumn<Track, String> albumCol = new TableColumn<>("专辑");
    albumCol.setCellValueFactory(new PropertyValueFactory("albumProperty"));
    albumCol.setPrefWidth(resultTrackTable.getPrefWidth() *0.25);

    TableColumn actionCol = new TableColumn("操作");
    actionCol.setCellValueFactory(new PropertyValueFactory<>("Dummy"));
    actionCol.setPrefWidth(resultTrackTable.getPrefWidth() *0.15);

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
                    int selindex = resultTrackTable.getSelectionModel().getSelectedIndex();
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
                    Track choosenTrack = trackObservableList.get(resultTrackTable.getSelectionModel().getSelectedIndex());

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
                    Track choosenTrack = trackObservableList.get(resultTrackTable.getSelectionModel().getSelectedIndex());
                    PlayList playList = currentProvider.album(choosenTrack.album_id);
                    PlayListDetailTabCtrl playListDetailTabCtrl = new PlayListDetailTabCtrl("专辑详情",playList,playerCtrl,currentProvider,homeTabPaneCtrl);
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
                  HBox hBox = new HBox(0);


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
                    hBox.setAlignment(Pos.CENTER);


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


    resultTrackTable.getColumns().setAll(songCol,singerCol,albumCol,actionCol);
  }

  public Tab getTab() {
    return tab;
  }
}
