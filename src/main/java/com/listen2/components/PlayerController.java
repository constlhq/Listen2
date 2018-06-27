package com.listen2.components;


import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.listen2.models.Sound;
import com.listen2.models.Track;
import com.listen2.providers.IProvider;
import com.listen2.providers.Netease;
import com.listen2.providers.QQ;
import com.listen2.providers.Xiami;
import com.sun.org.apache.xerces.internal.util.SynchronizedSymbolTable;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

import java.security.SecureRandom;
import java.util.*;
import java.util.function.Function;

public class PlayerController {

  static final String SVG_PLAY = "M424.4 214.7L72.4 6.6C43.8-10.3 0 6.1 0 47.9V464c0 37.5 40.7 60.1 72.4 41.3l352-208c31.4-18.5 31.5-64.1 0-82.6z";
  static final String SVG_PAUSE = "M144 479H48c-26.5 0-48-21.5-48-48V79c0-26.5 21.5-48 48-48h96c26.5 0 48 21.5 48 48v352c0 26.5-21.5 48-48 48zm304-48V79c0-26.5-21.5-48-48-48h-96c-26.5 0-48 21.5-48 48v352c0 26.5 21.5 48 48 48h96c26.5 0 48-21.5 48-48z";
  static final String SVG_BACKWORD = "M11.5 280.6l192 160c20.6 17.2 52.5 2.8 52.5-24.6V96c0-27.4-31.9-41.8-52.5-24.6l-192 160c-15.3 12.8-15.3 36.4 0 49.2zm256 0l192 160c20.6 17.2 52.5 2.8 52.5-24.6V96c0-27.4-31.9-41.8-52.5-24.6l-192 160c-15.3 12.8-15.3 36.4 0 49.2z";
  static final String SVG_FORWORD = "M500.5 231.4l-192-160C287.9 54.3 256 68.6 256 96v320c0 27.4 31.9 41.8 52.5 24.6l192-160c15.3-12.8 15.3-36.4 0-49.2zm-256 0l-192-160C31.9 54.3 0 68.6 0 96v320c0 27.4 31.9 41.8 52.5 24.6l192-160c15.3-12.8 15.3-36.4 0-49.2z";
  static final String SVG_VOLUME_UP = "M469.333333 853.333333c-8.533333 0-17.066667-4.266667-25.6-8.533333L238.933333 682.666667H85.333333c-25.6 0-42.666667-17.066667-42.666666-42.666667V384c0-25.6 17.066667-42.666667 42.666666-42.666667h153.6l200.533334-162.133333c12.8-8.533333 29.866667-12.8 46.933333-4.266667 17.066667 8.533333 25.6 21.333333 25.6 38.4v597.333334c0 17.066667-8.533333 29.866667-25.6 38.4-4.266667 4.266667-12.8 4.266667-17.066667 4.266666z m-341.333333-256h128c8.533333 0 17.066667 4.266667 25.6 8.533334l145.066667 115.2V302.933333L281.6 418.133333c-8.533333 4.266667-17.066667 8.533333-25.6 8.533334H128v170.666666zM814.933333 857.6c-12.8 0-21.333333-4.266667-29.866666-12.8-17.066667-17.066667-17.066667-42.666667 0-59.733333 149.333333-149.333333 149.333333-392.533333 0-541.866667-17.066667-17.066667-17.066667-42.666667 0-59.733333 17.066667-17.066667 42.666667-17.066667 59.733333 0 183.466667 183.466667 183.466667 482.133333 0 665.6-8.533333 4.266667-21.333333 8.533333-29.866667 8.533333z m-153.6-153.6c-12.8 0-21.333333-4.266667-29.866666-12.8-17.066667-17.066667-17.066667-42.666667 0-59.733333 68.266667-68.266667 68.266667-174.933333 0-243.2-17.066667-17.066667-17.066667-42.666667 0-59.733334 17.066667-17.066667 42.666667-17.066667 59.733333 0 98.133333 98.133333 98.133333 260.266667 0 362.666667-4.266667 8.533333-17.066667 12.8-29.866667 12.8z";
  static final String SVG_VOLUME_DOWN = "M469.333333 853.333333c-8.533333 0-17.066667-4.266667-25.6-8.533333L238.933333 682.666667H85.333333c-25.6 0-42.666667-17.066667-42.666666-42.666667V384c0-25.6 17.066667-42.666667 42.666666-42.666667h153.6l200.533334-162.133333c12.8-8.533333 29.866667-12.8 46.933333-4.266667 17.066667 8.533333 25.6 21.333333 25.6 38.4v597.333334c0 17.066667-8.533333 29.866667-25.6 38.4-4.266667 4.266667-12.8 4.266667-17.066667 4.266666z m-341.333333-256h128c8.533333 0 17.066667 4.266667 25.6 8.533334l145.066667 115.2V302.933333L281.6 418.133333c-8.533333 4.266667-17.066667 8.533333-25.6 8.533334H128v170.666666zM661.333333 704c-12.8 0-21.333333-4.266667-29.866666-12.8-17.066667-17.066667-17.066667-42.666667 0-59.733333 68.266667-68.266667 68.266667-174.933333 0-243.2-17.066667-17.066667-17.066667-42.666667 0-59.733334 17.066667-17.066667 42.666667-17.066667 59.733333 0 98.133333 98.133333 98.133333 260.266667 0 362.666667-4.266667 8.533333-17.066667 12.8-29.866667 12.8z";
  static final String SVG_VOLUME_OFF = "M469.333333 853.333333c-8.533333 0-17.066667-4.266667-25.6-8.533333L238.933333 682.666667H85.333333c-25.6 0-42.666667-17.066667-42.666666-42.666667V384c0-25.6 17.066667-42.666667 42.666666-42.666667h153.6l200.533334-162.133333c12.8-8.533333 29.866667-12.8 46.933333-4.266667 17.066667 8.533333 25.6 21.333333 25.6 38.4v597.333334c0 17.066667-8.533333 29.866667-25.6 38.4-4.266667 4.266667-12.8 4.266667-17.066667 4.266666z m-341.333333-256h128c8.533333 0 17.066667 4.266667 25.6 8.533334l145.066667 115.2V302.933333L281.6 418.133333c-8.533333 4.266667-17.066667 8.533333-25.6 8.533334H128v170.666666zM725.333333 682.666667c-12.8 0-21.333333-4.266667-29.866666-12.8-17.066667-17.066667-17.066667-42.666667 0-59.733334l256-256c17.066667-17.066667 42.666667-17.066667 59.733333 0 17.066667 17.066667 17.066667 42.666667 0 59.733334l-256 256c-8.533333 8.533333-17.066667 12.8-29.866667 12.8zM981.333333 682.666667c-12.8 0-21.333333-4.266667-29.866666-12.8l-256-256c-17.066667-17.066667-17.066667-42.666667 0-59.733334 17.066667-17.066667 42.666667-17.066667 59.733333 0l256 256c17.066667 17.066667 17.066667 42.666667 0 59.733334-8.533333 8.533333-17.066667 12.8-29.866667 12.8z";
  static final String SVG_ADD = "M888.18 331.756l-135.712 135.712c-2.544 2.544-6.362 3.816-9.756 3.816-7.208 0-13.57-5.938-13.57-13.57v-81.428h-108.57c-56.406 0-83.126 38.594-106.874 84.396-12.3 23.75-22.902 48.348-33.08 72.522-47.076 109.418-102.21 223.078-240.042 223.078h-95c-7.634 0-13.572-5.938-13.572-13.57v-81.43c0-7.632 5.938-13.57 13.572-13.57h95c56.406 0 83.124-38.594 106.874-84.396 12.298-23.75 22.902-48.348 33.08-72.522 47.076-109.42 102.208-223.078 240.042-223.078h108.57V186.29c0-7.634 5.938-13.572 13.57-13.572 3.818 0 7.21 1.698 10.18 4.242l135.29 135.29c2.544 2.542 3.816 6.362 3.816 9.754s-1.274 7.206-3.818 9.752zM356.354 478.92c-24.598-51.316-51.742-102.632-115.78-102.632h-95c-7.634 0-13.572-5.938-13.572-13.572v-81.428c0-7.632 5.938-13.57 13.572-13.57h95c75.49 0 131.048 35.2 173.882 95.422-23.748 36.472-41.138 75.914-58.102 115.78zM888.18 711.752l-135.712 135.712c-2.544 2.544-6.362 3.818-9.756 3.818-7.208 0-13.57-6.364-13.57-13.572v-81.428c-125.958 0-203.57 14.844-282.028-95.424 23.326-36.472 40.714-75.914 57.678-115.78 24.598 51.316 51.74 102.632 115.78 102.632h108.57v-81.428c0-7.634 5.938-13.572 13.57-13.572 3.818 0 7.21 1.698 10.18 4.242l135.29 135.29c2.544 2.544 3.816 6.362 3.816 9.754s-1.274 7.212-3.818 9.756z";
  static final String SVG_FOLDER = "M458.666667 213.333333l-85.333334-128H0v661.333334c0 106.666667 85.333333 192 192 192h640c106.666667 0 192-85.333333 192-192V213.333333H458.666667zM981.333333 746.666667c0 83.2-66.133333 149.333333-149.333333 149.333333H192c-83.2 0-149.333333-66.133333-149.333333-149.333333V128h309.333333l85.333333 128H981.333333v490.666667zM384 704c0 36.266667 27.733333 64 64 64h8.533333l42.666667-6.4c32-4.266667 55.466667-32 55.466667-64V490.666667l106.666666 21.333333v-102.4L512 384v247.466667l-72.533333 8.533333c-32 6.4-55.466667 32-55.466667 64z";
  static final String SVG_QUEUE = "M98.7008 380.213333l205.397333-136.930133c13.2224-8.814933 13.258667-28.2304 0.0704-37.0944L98.7712 68.117333C83.944533 58.148267 64 68.7744 64 86.638933V361.642667c0 17.826133 19.867733 28.458667 34.7008 18.570666zM416 256h512c17.6 0 32-14.4 32-32s-14.4-32-32-32H416c-17.6 0-32 14.4-32 32s14.4 32 32 32zM928 896H96c-17.6 0-32 14.4-32 32s14.4 32 32 32h832c17.6 0 32-14.4 32-32s-14.4-32-32-32zM928 544H96c-17.6 0-32 14.4-32 32s14.4 32 32 32h832c17.6 0 32-14.4 32-32s-14.4-32-32-32z";
  static final String SVG_REPEATONE = "M727.377 887.323h0.006c20.531 0 37.175 16.643 37.175 37.175 0 20.531-16.643 37.175-37.175 37.175H416.03c-211.978-0.291-383.741-172.055-384.032-384.005v-16.919c0-101.927 39.606-197.966 111.46-270.46 6.714-6.595 15.927-10.669 26.091-10.669 20.566 0 37.238 16.672 37.238 37.238 0 10.069-3.996 19.204-10.489 25.907-55.539 55.66-89.888 132.502-89.888 217.367l0.001 0.65v16.794c0 170.71 138.847 309.682 309.682 309.682h311.282z m200.27-710.543c211.956 0.291 383.705 172.018 384.032 383.937v16.859c0 127.328-62.961 246.083-168.406 317.744-5.871 4.1-13.156 6.55-21.014 6.55-12.725 0-23.948-6.427-30.603-16.211a37.019 37.019 0 0 1-6.537-21.062c0-12.742 6.41-23.985 16.181-30.683 82.567-56.563 135.959-150.22 135.965-256.347v-16.818c-0.182-170.96-138.723-309.501-309.665-309.684H578.422v99.943c0 41.462-28.345 56.818-63.088 34.167l-198.99-130.079c-34.615-22.65-34.615-59.761 0-82.411L515.398 42.733c34.745-22.65 63.088-7.358 63.088 34.103v99.943h349.161z m24.506 312.754c10.813 0 19.515 8.766 19.515 19.515v445.968c0 10.877-8.702 19.579-19.515 19.579h-54.578c-10.777 0-19.515-8.737-19.515-19.515V647.894c-39.58 27.376-86.196 47.847-136.448 58.46 70.807-17.753 19.598 0.905-35.211 8.15 86.928-22.094 84.619-21.66 82.208-21.66-10.793 0-19.553-8.704-19.642-19.476v-52.795c0-7.55 4.415-14.395 11.261-17.66 50.525-23.698 92.27-58.793 123.189-102.143-27.085 34.576-12.305 16.532-3.923-0.295 3.269-6.484 9.865-10.857 17.486-10.877h35.173z";
  static final String SVG_REPEATALL = "M60.235294 542.117647c0 132.879059 103.062588 240.941176 229.677177 240.941177v60.235294C130.048 843.294118 0 708.186353 0 542.117647s130.048-301.176471 289.912471-301.176471h254.735058L445.500235 141.793882l42.586353-42.586353L659.998118 271.058824 488.146824 442.970353l-42.646589-42.646588L544.707765 301.176471h-254.795294C163.297882 301.176471 60.235294 409.238588 60.235294 542.117647z m673.852235-301.176471v60.235295C860.702118 301.176471 963.764706 409.238588 963.764706 542.117647s-103.062588 240.941176-229.677177 240.941177h-254.795294l99.147294-99.147295-42.586353-42.586353L364.001882 813.176471l171.91153 171.911529 42.586353-42.586353L479.292235 843.294118h254.735059C893.952 843.294118 1024 708.186353 1024 542.117647s-130.048-301.176471-289.912471-301.176471z";
  static final String SVG_RANDOM = "M888.18 331.756l-135.712 135.712c-2.544 2.544-6.362 3.816-9.756 3.816-7.208 0-13.57-5.938-13.57-13.57v-81.428h-108.57c-56.406 0-83.126 38.594-106.874 84.396-12.3 23.75-22.902 48.348-33.08 72.522-47.076 109.418-102.21 223.078-240.042 223.078h-95c-7.634 0-13.572-5.938-13.572-13.57v-81.43c0-7.632 5.938-13.57 13.572-13.57h95c56.406 0 83.124-38.594 106.874-84.396 12.298-23.75 22.902-48.348 33.08-72.522 47.076-109.42 102.208-223.078 240.042-223.078h108.57V186.29c0-7.634 5.938-13.572 13.57-13.572 3.818 0 7.21 1.698 10.18 4.242l135.29 135.29c2.544 2.542 3.816 6.362 3.816 9.754s-1.274 7.206-3.818 9.752zM356.354 478.92c-24.598-51.316-51.742-102.632-115.78-102.632h-95c-7.634 0-13.572-5.938-13.572-13.572v-81.428c0-7.632 5.938-13.57 13.572-13.57h95c75.49 0 131.048 35.2 173.882 95.422-23.748 36.472-41.138 75.914-58.102 115.78zM888.18 711.752l-135.712 135.712c-2.544 2.544-6.362 3.818-9.756 3.818-7.208 0-13.57-6.364-13.57-13.572v-81.428c-125.958 0-203.57 14.844-282.028-95.424 23.326-36.472 40.714-75.914 57.678-115.78 24.598 51.316 51.74 102.632 115.78 102.632h108.57v-81.428c0-7.634 5.938-13.572 13.57-13.572 3.818 0 7.21 1.698 10.18 4.242l135.29 135.29c2.544 2.544 3.816 6.362 3.816 9.754s-1.274 7.212-3.818 9.756z";
  static final String SVG_LINK = "M735.877395 64.24214c-59.811224 0-116.027573 23.288402-158.304446 65.574484L409.662729 297.723775c-42.293245 42.25129-65.572438 98.497315-65.572438 158.273746 0 76.547397 38.600133 146.987702 103.270015 188.483792 9.80225 6.279007 21.220301 7.990998 31.759331 5.625114 10.441816-2.297322 20.001543-8.573259 26.235524-18.313088 6.258541-9.775644 7.981788-21.126156 5.659907-31.620162-2.298346-10.492982-8.584516-20.132526-18.339694-26.373671-40.46869-25.960255-64.629973-69.994144-64.629973-117.801986 0-37.383422 14.549368-72.500221 40.98239-98.908684l167.917384-167.907151c26.414603-26.443255 61.561078-40.984437 98.933243-40.984437 77.148078 0 139.924843 62.760392 139.924843 139.891074 0 37.381375-14.559601 72.500221-40.98239 98.940406l-79.651085 79.668481c-8.393157 8.402367-12.483312 19.444864-12.295024 30.455639 0.190335 10.492982 4.28663 20.883633 12.295024 28.910447 8.196683 8.195659 18.939351 12.277628 29.683043 12.277628 10.751878 0 21.494547-4.081968 29.673833-12.277628l79.651085-79.668481c42.294268-42.253336 65.579601-98.496292 65.579601-158.306492C959.756325 164.659177 859.323938 64.24214 735.877395 64.24214zM576.627415 379.51664c-9.79918-6.308683-21.220301-8.023744-31.755238-5.655813-10.435677 2.295276-20.002566 8.607028-26.237571 18.313088-6.247284 9.773598-7.980765 21.125133-5.649674 31.619138 2.289136 10.495028 8.574283 20.131503 18.330484 26.372647 40.4779 25.962302 64.638159 69.995167 64.638159 117.805056 0 37.380352-14.557554 72.498174-40.984437 98.940406L387.053802 834.82036c-26.41665 26.40744-61.559031 40.98239-98.934267 40.98239-77.147054 0-139.922796-62.795185-139.922796-139.921773 0-37.383422 14.549368-72.501244 40.98239-98.94143l79.642898-79.668481c8.393157-8.367575 12.490475-19.445888 12.301164-30.453592-0.188288-10.459213-4.294816-20.88568-12.301164-28.910447-8.197706-8.197706-18.940374-12.277628-29.67588-12.277628-10.760065 0-21.4925 4.079922-29.68202 12.277628l-79.650061 79.667457c-42.295292 42.25129-65.572438 98.495268-65.572438 158.307516 0 123.425054 100.43341 223.876883 223.877907 223.876883 59.811224 0 116.021433-23.285333 158.297282-65.606207l167.909198-167.909198c42.295292-42.25129 65.580624-98.497315 65.580624-158.270676C679.907663 491.423359 641.300366 420.98203 576.627415 379.51664z";

//  static  EventHandler playhandler =  new EventHandler<MouseEvent>() {
//    public void handle(MouseEvent e){
//      System.out.println("what?");
//    }
//  };

  static final double BTN_SM_SIZE = 24;
  static final double BTN_MD_SIZE = 24;

  private HBox playerBox;
  private AnchorPane anchorPane;
  private ImageView imageCover;
  private Slider playTimeSlider;
  private JFXSlider volumeSilder;
  private Region backwordRegion,playRegion,pauseRegion,forwordRegion,originalLinkRegion,add2QueueRegion,add2PlaylistRegion,queueRegion,repeatAllRegion,repeatOneRegion,randomRegion,volumeUpRegion,volumeDownRegion,volumeOffRegion;
  private StackPane volumeIndicator,playStateIndicator,repeatModeIndicator;
  private Label songName,singerName,trackTime,curTime;
  private MediaView mediaView;
  private MediaPlayer mediaPlayer;
  private Popup queuePopup;
  private PlayQueueController playQueueController;
  private Map<String,IProvider> providerMap;
  private Timer timer;


  public PlayerController() {
    playerBox = new HBox();
    anchorPane = new AnchorPane();
    volumeIndicator = new StackPane();
    playStateIndicator = new StackPane();
    repeatModeIndicator = new StackPane();
    backwordRegion = new Region();
    playRegion = new Region();
    pauseRegion = new Region();
    forwordRegion = new Region();
    add2QueueRegion = new Region();
    add2PlaylistRegion = new Region();
    queueRegion = new Region();
    repeatAllRegion = new Region();
    repeatOneRegion = new Region();
    randomRegion = new Region();
    volumeUpRegion = new Region();
    volumeDownRegion = new Region();
    volumeOffRegion = new Region();
    originalLinkRegion = new Region();
    playTimeSlider = new Slider();
    volumeSilder = new JFXSlider();
    queuePopup = new Popup();
    songName  =  new Label("");
    singerName = new Label("");
    trackTime = new Label("/00:00");
    curTime = new Label("00:00");
    playQueueController = new PlayQueueController();
    imageCover = new ImageView(new Image(PlayerController.class.getResource("/assets/mycover.jpg").toString()));

    providerMap = new HashMap<String,IProvider>(){{
      put("netease",new Netease());
      put("qq",new QQ());
      put("xiami",new Xiami());
    }};


    Track track = playQueueController.currentTrack();

    if (track !=null){
      System.out.println(track.img_url);
      String url =  providerMap.get(track.source).bootstrap_track(track);
      Image songCover =  new Image(track.img_url);
      imageCover.setImage(songCover);
      songName.setText(track.title);
      singerName.setText(track.artist);
      if(url != null){
        mediaPlayer = new MediaPlayer(new Media(url));
        bindHandlers();
      }
    }
    init();
  }

  private void init(){

    playerBox.setId("player-box");
    playerBox.setFillHeight(true);
    playerBox.setPrefHeight(80);
    playerBox.setMaxHeight(80);
    playerBox.setMinHeight(80);
    anchorPane.setPrefWidth(960);
    playerBox.setAlignment(Pos.CENTER);

    SVGPath   backword = new SVGPath();
    SVGPath   play = new SVGPath();
    SVGPath   pause = new SVGPath();
    SVGPath   forword = new SVGPath();
    SVGPath   originalLink = new SVGPath();
    SVGPath   add2Queue = new SVGPath();
    SVGPath   add2Playlist = new SVGPath();
    SVGPath   queue = new SVGPath();
    SVGPath   repeatAll = new SVGPath();
    SVGPath   repeatOne = new SVGPath();
    SVGPath   random = new SVGPath();
    SVGPath   volumeUp = new SVGPath();
    SVGPath   volumeDown = new SVGPath();
    SVGPath   volumeOff = new SVGPath();

    backword.setContent(SVG_BACKWORD);
    play.setContent(SVG_PLAY);
    pause.setContent(SVG_PAUSE);
    forword.setContent(SVG_FORWORD);
    originalLink.setContent(SVG_LINK);
    add2Queue.setContent(SVG_ADD);
    add2Playlist.setContent(SVG_FOLDER);
    queue.setContent(SVG_QUEUE);
    repeatAll.setContent(SVG_REPEATALL);
    repeatOne.setContent(SVG_REPEATONE);
    random.setContent(SVG_RANDOM);
    volumeUp.setContent(SVG_VOLUME_UP);
    volumeDown.setContent(SVG_VOLUME_DOWN);
    volumeOff.setContent(SVG_VOLUME_OFF);

    backwordRegion.setShape(backword);
    playRegion.setShape(play);
    pauseRegion.setShape(pause);
    forwordRegion.setShape(forword);
    originalLinkRegion.setShape(originalLink);
    add2QueueRegion.setShape(add2Queue);
    add2PlaylistRegion.setShape(add2Playlist);
    queueRegion.setShape(queue);
    repeatAllRegion.setShape(repeatAll);
    repeatOneRegion.setShape(repeatOne);
    randomRegion.setShape(random);
    volumeUpRegion.setShape(volumeUp);
    volumeDownRegion.setShape(volumeDown);
    volumeOffRegion.setShape(volumeOff);

    backwordRegion.setStyle("-fx-background-color: #fff;");
    playRegion.setStyle("-fx-background-color: #fff;");
    pauseRegion.setStyle("-fx-background-color: #fff;");
    forwordRegion.setStyle("-fx-background-color: #fff;");
    originalLinkRegion.setStyle("-fx-background-color: #fff;");
    add2QueueRegion.setStyle("-fx-background-color: #fff;");
    add2PlaylistRegion.setStyle("-fx-background-color: #fff;");
    queueRegion.setStyle("-fx-background-color: #fff;");
    repeatAllRegion.setStyle("-fx-background-color: #fff;");
    repeatOneRegion.setStyle("-fx-background-color: #fff;");
    randomRegion.setStyle("-fx-background-color: #fff;");
    volumeUpRegion.setStyle("-fx-background-color: #fff;");
    volumeDownRegion.setStyle("-fx-background-color: #fff;");
    volumeOffRegion.setStyle("-fx-background-color: #fff;");


    backwordRegion.setPrefSize(BTN_MD_SIZE,BTN_MD_SIZE);
    playRegion.setPrefSize(BTN_MD_SIZE,BTN_MD_SIZE);
    pauseRegion.setPrefSize(BTN_MD_SIZE,BTN_MD_SIZE);
    forwordRegion.setPrefSize(BTN_MD_SIZE,BTN_MD_SIZE);
    originalLinkRegion.setPrefSize(BTN_SM_SIZE,BTN_SM_SIZE);
    add2QueueRegion.setPrefSize(BTN_SM_SIZE,BTN_SM_SIZE);
    add2PlaylistRegion.setPrefSize(BTN_SM_SIZE,BTN_SM_SIZE);
    queueRegion.setPrefSize(BTN_SM_SIZE,BTN_SM_SIZE);
    repeatAllRegion.setPrefSize(BTN_SM_SIZE,BTN_SM_SIZE);
    repeatOneRegion.setPrefSize(BTN_SM_SIZE,BTN_SM_SIZE);
    randomRegion.setPrefSize(BTN_SM_SIZE,BTN_SM_SIZE);
    volumeUpRegion.setPrefSize(BTN_SM_SIZE,BTN_SM_SIZE);
    volumeDownRegion.setPrefSize(BTN_SM_SIZE,BTN_SM_SIZE);
    volumeOffRegion.setPrefSize(BTN_SM_SIZE,BTN_SM_SIZE);



    volumeIndicator.getChildren().addAll(volumeOffRegion,volumeDownRegion,volumeUpRegion);
    volumeIndicator.setAlignment(Pos.CENTER);
    volumeUpRegion.setVisible(true);
    volumeOffRegion.setVisible(false);
    volumeDownRegion.setVisible(false);

    playStateIndicator.getChildren().addAll(playRegion,pauseRegion);
    playStateIndicator.setAlignment(Pos.CENTER);
    playRegion.setVisible(true);
    pauseRegion.setVisible(false);

    repeatModeIndicator.getChildren().addAll(repeatAllRegion,repeatOneRegion,randomRegion);
    repeatModeIndicator.setAlignment(Pos.CENTER);
    repeatOneRegion.setVisible(false);
    randomRegion.setVisible(false);
    repeatAllRegion.setVisible(true);


    imageCover.setFitHeight(72);
    imageCover.setFitWidth(72);

    playTimeSlider.setPrefWidth(500);
    volumeSilder.setPrefWidth(100);


    anchorPane.getChildren().addAll(
            backwordRegion, playStateIndicator, forwordRegion,
            imageCover,
            playTimeSlider,curTime,trackTime,
            songName,singerName,originalLinkRegion,
            add2PlaylistRegion,repeatModeIndicator,queueRegion,volumeIndicator,volumeSilder);
    anchorPane.setId("player-pane");


    setAnchor();
    addListeners();
    bindHandlers();
    queuePopup.setAutoHide(true);

    queuePopup.getContent().add(playQueueController.getQueuePane());

  }

  private void setAnchor(){

    playerBox.getChildren().add(anchorPane);

    AnchorPane.setLeftAnchor(backwordRegion,20.0);
    AnchorPane.setLeftAnchor(playStateIndicator,64.0);
    AnchorPane.setLeftAnchor(forwordRegion,104.0);
    AnchorPane.setLeftAnchor(imageCover,140.0);
    AnchorPane.setLeftAnchor(playTimeSlider,220.0);
    AnchorPane.setLeftAnchor(curTime,720.0);
    AnchorPane.setLeftAnchor(trackTime,760.0);
    AnchorPane.setLeftAnchor(songName,220.0);
    AnchorPane.setLeftAnchor(singerName,450.0);

    AnchorPane.setRightAnchor(originalLinkRegion,122.0);
    AnchorPane.setRightAnchor(add2PlaylistRegion,88.0);
    AnchorPane.setRightAnchor(repeatModeIndicator,54.0);
    AnchorPane.setRightAnchor(queueRegion,20.0);
    AnchorPane.setRightAnchor(volumeIndicator,120.0);
    AnchorPane.setRightAnchor(volumeSilder,10.0);


    AnchorPane.setTopAnchor(backwordRegion,28.0);
    AnchorPane.setTopAnchor(playStateIndicator,28.0);
    AnchorPane.setTopAnchor(forwordRegion,28.0);
    AnchorPane.setTopAnchor(imageCover,4.0);
    AnchorPane.setTopAnchor(songName,12.0);
    AnchorPane.setTopAnchor(singerName,12.0);
    AnchorPane.setTopAnchor(originalLinkRegion,8.0);
    AnchorPane.setTopAnchor(add2PlaylistRegion,8.0);
    AnchorPane.setTopAnchor(repeatModeIndicator,8.0);
    AnchorPane.setTopAnchor(queueRegion,8.0);

    AnchorPane.setBottomAnchor(playTimeSlider,15.0);
    AnchorPane.setBottomAnchor(curTime,15.0);
    AnchorPane.setBottomAnchor(trackTime,15.0);
    AnchorPane.setBottomAnchor(volumeIndicator,12.0);
    AnchorPane.setBottomAnchor(volumeSilder,15.0);

  }


  public Popup getQueuePopup() {
    return queuePopup;
  }


  private void playNext(){
    Track track = playQueueController.nextTrack();
    if (track !=null){
      Image songCover =  new Image(track.img_url);
      imageCover.setImage(songCover);
      songName.setText(track.title);
      singerName.setText(track.artist);
      String url =  providerMap.get(track.source).bootstrap_track(track);

      mediaPlayer.stop();
      if(url != null){
        mediaPlayer = new MediaPlayer(new Media(url));
        bindHandlers();
        mediaPlayer.play();
      }else{
        playNext();
      }

    }else{
      mediaPlayer.stop();
    }
  }

  private void playPrevious(){
    Track track = playQueueController.perviousTrack();
    if (track !=null){
      Image songCover =  new Image(track.img_url);
      imageCover.setImage(songCover);
      songName.setText(track.title);
      singerName.setText(track.artist);
      String url =  providerMap.get(track.source).bootstrap_track(track);

      mediaPlayer.stop();
      if(url != null){
        mediaPlayer = new MediaPlayer(new Media(url));
        bindHandlers();
        mediaPlayer.play();
      }else{
        playNext();
      }
    }else{
      mediaPlayer.stop();
    }
  }

  private void bindHandlers(){

    mediaPlayer.volumeProperty().bindBidirectional(volumeSilder.valueProperty());

    mediaPlayer.setOnReady(()->{
      playTimeSlider.setMax(mediaPlayer.getTotalDuration().toSeconds());
    });

    volumeSilder.setOnScroll(e -> {
      double zoomFactor = 0.001;
      double deltaY = e.getDeltaY();
      if (deltaY > 0) {
        mediaPlayer.setVolume(mediaPlayer.getVolume() + zoomFactor * e.getDeltaY() > 1 ? 1 : mediaPlayer.getVolume() + zoomFactor * e.getDeltaY());
      } else {
        mediaPlayer.setVolume(mediaPlayer.getVolume() + zoomFactor * e.getDeltaY() < 0 ? 0 : mediaPlayer.getVolume() + zoomFactor * e.getDeltaY());
      }
    });

    mediaPlayer.setOnStopped(() -> {
      playRegion.setVisible(true);
      pauseRegion.setVisible(false);
      playTimeSlider.setValue(0);


    });

    mediaPlayer.setOnPaused(() -> {
      playRegion.setVisible(true);
      pauseRegion.setVisible(false);
    });


    mediaPlayer.setOnEndOfMedia(() -> {
      mediaPlayer.stop();
      playNext();
    });


    mediaPlayer.statusProperty().addListener((obvs,ov,nv)->{

      System.out.println("ov+++++"+ov);
      System.out.println("nv+++++"+nv);

      if(nv ==MediaPlayer.Status.STALLED){
        // buffering
        mediaPlayer.seek(Duration.ZERO);
      }


    });

    mediaPlayer.setOnPlaying(() -> {
      playTimeSlider.setMax(mediaPlayer.getTotalDuration().toSeconds());
      playTimeSlider.setMin(0);
      playRegion.setVisible(false);
      pauseRegion.setVisible(true);
    });

    playTimeSlider.valueProperty().addListener((obvs,ov,nv)->{

      if (Math.abs(playTimeSlider.getValue() - mediaPlayer.getCurrentTime().toSeconds()) >= 1)
        mediaPlayer.seek(Duration.millis(playTimeSlider.getValue() * 1000));

    });

    mediaPlayer.currentTimeProperty().addListener((obvs,ov,nv)->{
      curTime.setText(trackTime(mediaPlayer.getCurrentTime()));
      trackTime.setText("/" + trackTime(mediaPlayer.getTotalDuration()));
      playTimeSlider.setValue(nv.toSeconds());
    });


    playRegion.setOnMouseClicked(e -> {
      if (mediaPlayer.getStatus() == MediaPlayer.Status.READY || mediaPlayer.getStatus() == MediaPlayer.Status.PAUSED) {
        mediaPlayer.play();
      }
    });

    pauseRegion.setOnMouseClicked(e -> {
      if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
        mediaPlayer.pause();
      }
    });

  }

  private void addListeners() {

    volumeSilder.setMax(1);
    volumeSilder.setMin(0);
    volumeSilder.setValue(0.5);

    volumeSilder.valueProperty().addListener((obs, odv, nv) -> {
      if (nv.doubleValue() == 0) {
        volumeOffRegion.setVisible(true);
        volumeUpRegion.setVisible(false);
        volumeDownRegion.setVisible(false);
      } else if (nv.doubleValue() > 0 && nv.doubleValue() < 0.5) {
        volumeDownRegion.setVisible(true);
        volumeOffRegion.setVisible(false);
        volumeUpRegion.setVisible(false);
      } else {
        volumeUpRegion.setVisible(true);
        volumeOffRegion.setVisible(false);
        volumeDownRegion.setVisible(false);
      }
    });



    forwordRegion.setOnMouseClicked(e->{
      playNext();
    });

    backwordRegion.setOnMouseClicked(e->{
      playPrevious();
    });

    queueRegion.setOnMouseClicked(e -> {

      if (queuePopup.isShowing()) {
        queuePopup.hide();
      } else {
        queuePopup.show(queueRegion,e.getScreenX()-540+(24-e.getX()+20),e.getScreenY()-480-e.getY()-12);
      }
    });
  }

  private static String trackTime(Duration duration){
    return String.format("%02d:%02d",(int)duration.toMinutes(),(int)duration.toSeconds()%60);
  }

  public HBox getPlayerBox() {
    return playerBox;
  }

  private enum Repeat_Mode {
    REPEAT_ALL,REPEAT_ONE,RANDOM
  }

  private static class PlayQueueController{

    private VBox playQueueContainer;
    private TableView<Track> queueTableView;
    private ObservableList<Track> observableTrackList;
    private HBox popUpHeaderContainer;
    private Label formTitle;
    private JFXButton saveAll,removeAll;
    private int currentPlayingIndex;
    private Repeat_Mode repeat_mode;

    public Track nextTrack(){
      switch(repeat_mode){
        case REPEAT_ALL:
          int next = ++currentPlayingIndex < observableTrackList.size()? currentPlayingIndex:0;
          if ( next < observableTrackList.size()){
            return observableTrackList.get(next);
          }
          return null;
        case REPEAT_ONE:
          if (currentPlayingIndex < observableTrackList.size()){
            return observableTrackList.get(currentPlayingIndex);
          }
            return null;
        case RANDOM:
          if (observableTrackList.size()>0){
             return observableTrackList.get(new SecureRandom().nextInt(observableTrackList.size()));
          }
          return null;
          default:
            return null;
      }
    }


    public Track perviousTrack(){
      switch(repeat_mode){
        case REPEAT_ALL:

          int previous = --currentPlayingIndex >= 0 ? currentPlayingIndex:observableTrackList.size()-1;
          if (previous>=0){
            return observableTrackList.get(previous);
          }
          return null;
        case REPEAT_ONE:
          if (currentPlayingIndex < observableTrackList.size()){
            return observableTrackList.get(currentPlayingIndex);
          }
          return null;
        case RANDOM:
          if (observableTrackList.size()>0){
            return observableTrackList.get(new SecureRandom().nextInt(observableTrackList.size()));
          }
          return null;
        default:
          return null;
      }
    }

    public Track currentTrack(){
      if (observableTrackList.size() > currentPlayingIndex){
        return observableTrackList.get(currentPlayingIndex);
      }
      return null;
    }

    public int getCurrentPlayingIndex() {
      return currentPlayingIndex;
    }

    public void setCurrentPlayingIndex(int currentPlayingIndex) {
      this.currentPlayingIndex = currentPlayingIndex;
    }

    public VBox getQueuePane() {
      return playQueueContainer;
    }

    public HBox getPopUpHeaderContainer() {
      return popUpHeaderContainer;
    }

    private void init(){
      buildPopupContent();
      buildTableColumn();
      addListener();
    }

    private void buildTableColumn(){
      TableColumn<Track, String> titleCol = new TableColumn<>("歌曲");
      titleCol.setEditable(true);
      titleCol.setCellValueFactory(new PropertyValueFactory("titleProperty"));
      titleCol.setPrefWidth(queueTableView.getPrefWidth() *0.5);


      TableColumn actionCol = new TableColumn();
      actionCol.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));
      actionCol.setPrefWidth(queueTableView.getPrefWidth() *0.1);

      TableColumn<Track, String> singerCol = new TableColumn<>("歌手");
      singerCol.setCellValueFactory(new PropertyValueFactory("artistProperty"));
      singerCol.setPrefWidth(queueTableView.getPrefWidth() *0.3);

      Callback<TableColumn<Track, String>, TableCell<Track, String>> cellFactory =
              new Callback<TableColumn<Track, String>, TableCell<Track, String>>() {
                @Override
                public TableCell call(final TableColumn<Track, String> param) {
                  final TableCell<Track, String> cell = new TableCell<Track, String>() {
                    ImageView imgv = new ImageView(new Image(PlayerController.class.getResource("/assets/del.png").toString()));
                    JFXButton btn = new JFXButton("",imgv);
                    @Override
                    public void updateItem(String item, boolean empty) {
                      imgv.setFitHeight(24);
                      imgv.setFitWidth(24);
                      super.updateItem(item, empty);
                      if (empty) {
                        setGraphic(null);
                        setText(null);
                      } else {
                        btn.setOnMouseClicked(event -> {
                          getTableView().getItems().remove(getIndex());
                        });
                        setGraphic(btn);
                        setText(null);
                      }
                    }
                  };
                  return cell;
                }
              };

      actionCol.setCellFactory(cellFactory);
      queueTableView.getColumns().setAll(titleCol,actionCol,singerCol);
    }

    private void buildPopupContent(){

      playQueueContainer.setPrefSize(540,480);
      playQueueContainer.setMaxSize(540,480);
      queueTableView.setPrefWidth(540);
      queueTableView.setPrefHeight(450);
      observableTrackList  = getTracks();
      queueTableView.setItems(observableTrackList);
      popUpHeaderContainer.setPrefHeight(25);

      popUpHeaderContainer.setAlignment(Pos.CENTER_LEFT);


      HBox.setMargin(formTitle,new Insets(0,0,0,20));
      HBox.setMargin(saveAll,new Insets(0,0,0,300));
      HBox.setMargin(removeAll,new Insets(0,0,0,15));

      saveAll.setButtonType(JFXButton.ButtonType.RAISED);
      saveAll.getStyleClass().add("btn-grey");
      removeAll.setButtonType(JFXButton.ButtonType.RAISED);
      removeAll.getStyleClass().add("btn-grey");

      popUpHeaderContainer.getChildren().addAll(formTitle,saveAll,removeAll);
      popUpHeaderContainer.setId("popup-header-container");

      playQueueContainer.getChildren().addAll(popUpHeaderContainer,queueTableView);
    }

    private void  addListener(){
      removeAll.setOnMouseClicked(e->{
        observableTrackList.clear();
      });
    }

    public void setRepeat_mode(Repeat_Mode repeat_mode) {
      this.repeat_mode = repeat_mode;
    }

    public PlayQueueController() {
      playQueueContainer = new VBox();
      queueTableView = new TableView<>();
      popUpHeaderContainer = new HBox();
      formTitle = new Label("播放列表");
      saveAll = new JFXButton("收藏全部");
      removeAll = new JFXButton("清空列表");
      observableTrackList = FXCollections.observableArrayList();
      repeat_mode = Repeat_Mode.REPEAT_ALL;
      currentPlayingIndex = 0;
      init();
    }

//    public PlayQueueController(ObservableList<Track> observableTrackList) {
//      playQueueContainer = new VBox();
//      queueTableView = new TableView<>();
//      popUpHeaderContainer = new HBox();
//      this.observableTrackList = observableTrackList;
//    }

    public ObservableList<Track> getObservableTrackList() {
      return observableTrackList;
    }

    private ObservableList<Track> getTracks() {
      QQ qq = new QQ();
      ObservableList<Track> people = FXCollections.observableArrayList();
      List<Track> ts= qq.artist("/playlist?artist_id=qqartist_001MXQUi1tlLon").tracks;

      System.out.println(ts.get(2).toString());
      people.addAll(ts);
      return people;
    }
  }
}
