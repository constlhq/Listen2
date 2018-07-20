package com.listen2.providers;

import com.fasterxml.jackson.databind.JsonNode;
import com.listen2.models.PlayList;
import com.listen2.models.PlayListMeta;
import com.listen2.models.SearchResult;
import com.listen2.models.Track;
import com.listen2.utils.UtilTools;
import javafx.util.Duration;
import org.apache.commons.text.StringEscapeUtils;
import java.util.ArrayList;
import java.util.List;


public class Kuwo implements  IProvider{


  public static void addSongImg(Track track){
    track.img_url = UtilTools.get("http://artistpicserver.kuwo.cn/pic.web?type=rid_pic&pictype=url&size=240&rid="+track.id);
  }

  private static Track _convert_song(JsonNode track_json,String type) {
    String song_id;
    Track temp_track;
    switch (type) {
      case "playlist":
        song_id =  track_json.get("id").asText();
        temp_track = new Track.TrackBuilder()
                .id(song_id)
                .title(StringEscapeUtils.unescapeXml(track_json.get("name").asText()))
                .artist(track_json.get("artist").asText())
                .artist_id(track_json.get("artistid").asText())
                .album(track_json.get("album").asText())
                .album_id(track_json.get("albumid").asText())
                .source("kuwo")
                .source_url("http://www.kuwo.cn/yinyue/" + song_id)
                .img_url("") // load when needed
                .url(song_id)
                .lyric_url(song_id)
                .disabled(false)
                .build();
        return temp_track;
      case "search":
        song_id = track_json.get("MUSICRID").asText().split("_")[1];
        temp_track = new Track.TrackBuilder()
                .id(song_id)
                .title(StringEscapeUtils.unescapeXml(track_json.get("SONGNAME").asText()))
                .artist(track_json.get("ARTIST").asText())
                .artist_id(track_json.get("ARTISTID").asText())
                .album(track_json.get("ALBUM").asText())
                .album_id(track_json.get("ALBUMID").asText())
                .source("kuwo")
                .source_url("http://www.kuwo.cn/yinyue/" + song_id)
                .img_url("") // load when needed
                .url(song_id)
                .lyric_url(song_id)
                .disabled(false)
                .build();
        return temp_track;
      default:
        return null;
    }
  }

  //espically for converting album tracks
  private static Track _convert_song(JsonNode track_json,String album_name,String album_id){

    String song_id =  track_json.get("id").asText();
    Track temp_track = new Track.TrackBuilder()
            .id(song_id)
            .title(StringEscapeUtils.unescapeXml(track_json.get("name").asText()))
            .artist(track_json.get("artist").asText())
            .artist_id(track_json.get("artistid").asText())
            .album(album_name)
            .album_id(album_id)
            .source("kuwo")
            .source_url("http://www.kuwo.cn/yinyue/" + song_id)
            .img_url("") // load when needed
            .url(song_id)
            .lyric_url(song_id)
            .disabled(false)
            .build();
    return temp_track;
  }

  public SearchResult search(String keyword ,int curpage) {
    // API From https://blog.csdn.net/u011354613/article/details/52756467
    curpage = curpage - 1;
    String target_url = String.format("http://search.kuwo.cn/r.s?ft=music&itemset=web_2013&client=kt&rformat=json&encoding=utf8&all=%s&pn=%d&rn=20",keyword,curpage);
    String responseBody = UtilTools.get(target_url).replaceAll("\'","\"");

    List<Track> tracks = new ArrayList<>(20);
    try {
      JsonNode dataNode = mapper.readTree(responseBody);
      dataNode.get("abslist").forEach(li -> tracks.add(_convert_song(li,"search")));

      return new SearchResult(tracks, dataNode.get("TOTAL").asInt());
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }


public String bootstrap_track(Track track){
    String target_url  = String.format("http://antiserver.kuwo.cn/anti.s?type=convert_url&format=aac|mp3|wma&response=url&rid=MUSIC_%s", track.id);
    String responseBody = UtilTools.get(target_url);
    if(responseBody.length()>0){
      return responseBody;
    }else{
      return null;
    }
  }

  public String lyric(Track track){
    String target_url = String.format("http://m.kuwo.cn/newh5/singles/songinfoandlrc?musicId=%s", track.id);
    String responseBody = UtilTools.get(target_url);
    try{
      JsonNode dataNode = mapper.readTree(responseBody).get("data");
      StringBuilder sb = new StringBuilder();
      dataNode.get("lrclist").forEach(line->{
        double time =  line.get("time").asDouble();
        Duration duration = new Duration(time*1000);
       String timeTick = String.format("%02d:%02d.%d",(int)duration.toMinutes(),(int)duration.toSeconds()%60,(int)duration.toMillis()%1000);
       sb.append("[");
       sb.append(timeTick);
       sb.append("]");
       sb.append(line.get("lineLyric").asText());
       sb.append("\n");
      });
      return sb.toString();
    }catch(Exception e){
      return null;
    }
  }

  public PlayList album(String album_id){
    String target_url = String.format("http://search.kuwo.cn/r.s?pn=0&rn=1000&stype=albuminfo&albumid=%s&alflac=1&pcmp4=1&encoding=utf8&vipver=MUSIC_8.7.7.0_W4", album_id);
//    System.out.println(target_url);
    String responseBody = UtilTools.get(target_url).replaceAll("\'","\"");
//    String jsonBody = responseBody.substring("asonglist1459961045566(".length()+1, responseBody.length() - 1);
//    System.out.println(jsonBody);
    try{
      JsonNode dataNode = mapper.readTree(responseBody).get("data");
      PlayListMeta playListMeta= new PlayListMeta.PlayListMetaBuilder()
              .id(album_id)
              .title(StringEscapeUtils.unescapeHtml4(dataNode.get("name").asText()))
              .imgurl("http://img1.sycdn.kuwo.cn/star/albumcover/" + dataNode.get("pic").asText())
              .sourceurl("http://www.kuwo.cn/album/" + album_id)
              .build();

      List<Track> trackList = new ArrayList<>();
      dataNode.get("musiclist").forEach(song->{
        Track track = _convert_song(song,playListMeta.title,album_id);
        trackList.add(track);
      });
      return new PlayList(playListMeta,trackList);
    }catch (Exception e){
      e.printStackTrace();
      return null;
    }
  }

  public List<PlayListMeta> get_playlists(int curpage){

    /*
      {
        1265: '经典', 577: '纯音乐', 621: '网络', 155: '怀旧', 1879: '网红',
        220: '佛乐', 180: '影视', 578: '器乐', 1877: '游戏', 181: '二次元',
        882: 'KTV', 216: '喊麦', 1366: '3D', 146: '伤感', 62: '放松', 58: '励志',
        143: '开心', 137: '甜蜜', 139: '兴奋', 67: '安静', 66: '治愈', 147: '寂寞',
        160: '四年', 366: '运动', 354: '睡前', 378: '跳舞', 1876: '学习',
        353: '清晨', 359: '夜店', 382: '校园', 544: '亲热', 363: '咖啡店',
        375: '旅行', 371: '散步', 386: '工作', 336: '婚礼', 637: '70后', 638: '80后',
        639: '90后', 640: '00后', 268: '10后', 393: '流行', 391: '电子',
        389: '摇滚', 1921: '民歌', 392: '民谣', 399: '乡村', 35: '欧洲', 37: '华语',
      }
   */
    String target_url = String.format("http://www.kuwo.cn/www/categoryNew/getPlayListInfoUnderCategory?type=taglist&digest=10000&id=%d&start=%d&count=30",37,curpage*30);
    String responseBody =UtilTools.get(target_url);
    System.out.println(responseBody);

    List<PlayListMeta> playListMetas = new ArrayList<>(30);

    try{

      JsonNode dataNode = mapper.readTree(responseBody).get("data").get(0);

      dataNode.get("data").forEach(li->{
        String id = li.get("id").asText();
        String title = li.get("name").asText();
        String cover_img_url = li.get("img").asText();
        String source_url = "http://www.kuwo.cn/playlist/index?pid=" + id;

        playListMetas.add(new PlayListMeta.PlayListMetaBuilder()
                .id(id)
                .title(title)
                .imgurl(cover_img_url)
                .sourceurl(source_url)
                .build()
        );
      });
    }catch(Exception e){
      e.printStackTrace();
    }
    return playListMetas;
  }


  public  PlayList get_playlist(String list_id){
    String target_url = String.format("http://nplserver.kuwo.cn/pl.svc?op=getlistinfo&pn=0&rn=100&encode=utf-8&keyset=pl2012&pcmp4=1&pid=%s", list_id);
    String responseBody = UtilTools.get(target_url);
    try {
      JsonNode dataNode = mapper.readTree(responseBody);
      PlayListMeta playListMeta = new PlayListMeta.PlayListMetaBuilder()
              .id(list_id)
              .sourceurl("http://www.kuwo.cn/playlist/index?pid=" + list_id)
              .title(dataNode.get("title").asText())
              .imgurl(dataNode.get("pic").asText())
              .build();
      List<Track> trackList = new ArrayList<>();
      dataNode.get("musiclist").forEach(track_json -> {
        Track track = _convert_song(track_json,"playlist");
        trackList.add(track);
      });
      return new PlayList(playListMeta, trackList);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public String codeName(){
    return "kuwo";
  }
}