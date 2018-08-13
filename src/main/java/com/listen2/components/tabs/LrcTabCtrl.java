package com.listen2.components.tabs;

import com.listen2.components.HomeTabPaneCtrl;
import com.listen2.components.PlayerCtrl;
import com.listen2.models.LrcRow;
import com.listen2.models.PlayList;
import com.listen2.models.Track;
import com.listen2.providers.IProvider;
import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.Transition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;


public class LrcTabCtrl {

  private IProvider provider;

  private Tab tab;
  private GridPane gridPane;
  private VBox metaVBox;
  private ImageView coverImageView;
  private Label songNameLable,singerNameLable,albumNameLable;

  private ListView<LrcRow> lrcListView;
  private ObservableList<LrcRow> lrcRowObservableList;
  private PlayerCtrl playerCtrl;
  private HomeTabPaneCtrl homeTabPaneCtrl;
  private Map<Integer,Integer> time2RowIndex;
  private List<Integer> timeTicks;
  private Track track;


  public LrcTabCtrl(List<LrcRow> lrcRowList,Track track,PlayerCtrl playerCtrl,HomeTabPaneCtrl homeTabPaneCtrl,IProvider provider) {
    tab = new Tab("当前歌词");
    this.playerCtrl = playerCtrl;
    this.homeTabPaneCtrl = homeTabPaneCtrl;
    this.provider = provider;
    gridPane = new GridPane();
    metaVBox = new VBox(10);
    this.track = track;
    lrcListView = new ListView<>();
    lrcRowObservableList = FXCollections.observableArrayList(lrcRowList);
    init();
    addListeners();
  }

  public void init(){
    time2RowIndex = new HashMap<>();
    timeTicks = new LinkedList<>();
    lrcRowObservableList.stream().forEach(row->{
      time2RowIndex.put(row.milliseconds,row.rowIndex);
      timeTicks.add(row.milliseconds);
    });

    coverImageView = new ImageView(new Image(track.img_url,true));
    songNameLable = new Label("曲目: " + track.title);
    singerNameLable = new Label("歌手: " + track.artist);
    albumNameLable = new Label("专辑: " + track.album);

    songNameLable.getStyleClass().add("detail-plain-lable");
    singerNameLable.getStyleClass().add("detail-link-lable");
    albumNameLable.getStyleClass().add("detail-link-lable");


    gridPane.getChildren().clear();
    gridPane.add(metaVBox,0,0);
    gridPane.add(lrcListView,1,0);
    metaVBox.getChildren().clear();
    metaVBox.getChildren().addAll(coverImageView,songNameLable,singerNameLable,albumNameLable);
    tab.setContent(gridPane);
    metaVBox.setAlignment(Pos.TOP_CENTER);
    GridPane.setMargin(metaVBox,new Insets(25,0,0,80));
//    VBox.setMargin(coverImageView,new Insets(25,0,0,0));
    coverImageView.setFitHeight(224);
    coverImageView.setFitWidth(224);
    lrcListView.setPrefSize(960-224,620);
    lrcListView.setCellFactory(new Callback<ListView<LrcRow>, ListCell<LrcRow>>() {
      @Override
      public ListCell<LrcRow> call(ListView<LrcRow> param) {
        ListCell<LrcRow> cell = new ListCell<LrcRow>() {
          @Override
          public void updateItem(LrcRow item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {
              setText(item.content);
            }
          }
        }; // ListCell
        cell.setAlignment(Pos.CENTER);
        return cell;
      }
    });
    lrcListView.setItems(lrcRowObservableList);

  }


  private void addListeners(){
    tab.setOnClosed(e->{
      int tabCount =  homeTabPaneCtrl.getContainerTabPane().getTabs().size();
      homeTabPaneCtrl.getContainerTabPane().getSelectionModel().select(tabCount==4?0:tabCount-1);
    });

    albumNameLable.setOnMouseClicked(e->{
      PlayList playList = provider.album(track.album_id);
      PlayListDetailTabCtrl playListDetailTabCtrl = new MyListDetailTabCtrl("专辑详情",playList,playerCtrl,provider,homeTabPaneCtrl);
      Tab playListDetailTab = playListDetailTabCtrl.getTab();
      homeTabPaneCtrl.addTab(playListDetailTab);
      homeTabPaneCtrl.getContainerTabPane().getSelectionModel().select(playListDetailTabCtrl.getTab());
      homeTabPaneCtrl.getContainerTabPane().getSelectionModel().selectLast();
    });

  }

  public void updateInfo(List<LrcRow> lrcRowList,Track track, IProvider provider) {
    lrcRowObservableList.clear();
    lrcRowObservableList.addAll(lrcRowList);
    this.track = track;
    this.provider = provider;
  }

  public void scrollToTime(Double duration){
    if (time2RowIndex.containsKey(duration)){
      int curIndex =time2RowIndex.get(duration);
      lrcListView.scrollTo(curIndex-5>0?curIndex-5:0);
      lrcListView.getSelectionModel().select(curIndex);
    }else{
      int[] tempNote = new int[]{Integer.MAX_VALUE,0,0};
      timeTicks.stream().forEach(tick->{
        double delta = duration - tick;
        if(delta > 0 && delta < tempNote[0]){
          tempNote[0] = (int)delta;
          tempNote[1] = tempNote[2];
        }
        tempNote[2]++;
      });
          lrcListView.scrollTo(tempNote[1]-5>0?tempNote[1]-5:0);
          lrcListView.getSelectionModel().select(tempNote[1]);
    }
  }

  public Tab getTab() {
    return tab;
  }

  public ListView<LrcRow> getLrcListView() {
    return lrcListView;
  }

}
