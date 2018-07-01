package com.listen2.providers;


import com.fasterxml.jackson.databind.JsonNode;
import com.listen2.models.*;
import com.listen2.utils.UtilTools;
import com.sun.org.apache.xerces.internal.util.SynchronizedSymbolTable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;


public class Xiami implements IProvider{

  Logger loger = LoggerFactory.getLogger(Xiami.class);

  static String caesar(String location){
    int num = Integer.parseInt(location.substring(0,1));
    int avg_len = (int)Math.floor((location.length()-1)/ num);
    int remainder = (location.length()-1) % num;
    List<String> result = new ArrayList<>();
    for (int i=0; i<remainder; i++) {
      result.add(location.substring(i * (avg_len + 1) + 1, (i + 1) * (avg_len + 1) + 1));
    }

    for (int i=0; i<num-remainder; i++) {
      String line = location.substring((avg_len + 1) * remainder,location.length());
      result.add(line.substring(i * avg_len + 1, (i + 1) * avg_len + 1));
    }

    List<Character> s = new ArrayList<>();
    for (int i=0; i< avg_len; i++) {
      for (int j=0; j<num; j++){
        s.add(result.get(j).charAt(i));
      }
    }

    for (int i=0; i<remainder; i++) {
      String temp = result.get(i);
      s.add(temp.charAt(temp.length()-1));
    }
    try{
      return  URLDecoder.decode(s.stream().map(Serializable::toString).collect(joining("")),"utf-8").replaceAll("\\^", "0");
    }catch (UnsupportedEncodingException e){
      return "";
    }
  }

     static String handleProtocolRelativeUrl(String url){
        String regex = "^.*?//";
        return url.replaceAll(regex, "http://");
    }

    static String  xm_retina_url(String s){
        int s_last = s.length()-1;
        if (s.substring(s_last-6,s_last-4).equals("_1")){
            return s.substring(0, s_last-6) + s.substring(s_last-4);
        }
        return s;
    }

    public List<PlayListMeta> get_playlists(int curpage){
//    int curpage = Integer.parseInt(UtilTools.getParameterByName("curpage",url));
    String target_url = "http://www.xiami.com/collect/recommend/page/" + curpage;
    String responseBody = UtilTools.get(target_url);

    Document doc = Jsoup.parse(responseBody);
    Elements block_list = doc.select(".block_list ul li");
    return block_list.stream().map(li->{
      String albumid = li.selectFirst("a").attr("href").split("/")[2];
      String title = li.selectFirst("a").attr("title");
      String cover_img_url = handleProtocolRelativeUrl(li.selectFirst("img").attr("src")) ;
      String source_url = "http://www.xiami.com/collect/" + albumid;
      String id = albumid;
      return new PlayListMeta.PlayListMetaBuilder()
              .id(id)
              .title(title)
              .imgurl(cover_img_url)
              .sourceurl(source_url)
              .build();
    }).collect(toList());
  }

  public PlayList get_playlist(String list_id) {
//    String list_id =  UtilTools.getParameterByName("list_id", url).split("_")[1];
    String target_url = "http://api.xiami.com/web?v=2.0&app_key=1&id=" + list_id +"&callback=jsonp122&r=collect/detail";
    String responseBody =  UtilTools.get(target_url);
    String jsonBody = responseBody.substring("jsonp122(".length(),responseBody.length()-1);
    try{
      JsonNode dataNode = mapper.readTree(jsonBody).get("data");
      PlayListMeta playListMeta= new PlayListMeta.PlayListMetaBuilder()
              .sourceurl("http://www.xiami.com/collect/" + list_id)
              .title(dataNode.get("collect_name").asText())
              .imgurl(dataNode.get("logo").asText())
              .id(list_id)
              .build();

      List<Track> trackList = new ArrayList<>();
      dataNode.get("songs").forEach(song->{
        Track track = convert_song(song,"artist_name");
        trackList.add(track);
      });
      return new PlayList(playListMeta,trackList);
    }catch (IOException e){
      return null;
    }
  }

  public Track convert_song(JsonNode song_info,String artist_field_name) {
    System.out.println("$$$$$"+song_info.get("lyric").asText());
     return  new Track.TrackBuilder()
             .id( song_info.get("song_id").asText())
             .title( song_info.get("song_name").asText())
             .artist(song_info.get(artist_field_name).asText())
             .artist_id( song_info.get("artist_id").asText())
             .album( song_info.get("album_name").asText())
             .album_id(song_info.get("album_id").asText())
             .source("xiami")
             .source_url( "http://www.xiami.com/song/" + song_info.get("song_id").asText())
             .img_url( song_info.get("album_logo").asText())
             .url( song_info.get("song_id").asText())
             .lyric_url( song_info.get("lyric")==null ?"":song_info.get("lyric").asText())
             .disabled(false)
             .build();
     }

  public String  bootstrap_track (Track track){
    String target_url = "http://www.xiami.com/song/playlist/id/" + track.id + "/object_name/default/object_id/0/cat/json";
    String responseBody = UtilTools.get(target_url);
    try{
      JsonNode dataNode = mapper.readTree(responseBody).get("data");
      if(!dataNode.has("trackList")){
        return null;
      }else{
        System.out.println(responseBody);
        String location = dataNode.get("trackList").get(0).get("location").asText();
        JsonNode trackNode =  dataNode.get("trackList").get(0);
        track.album_id = trackNode.get("album_id").asText();
        track.album =   trackNode.get("album_name").asText() ;
        track.img_url = xm_retina_url(handleProtocolRelativeUrl(trackNode.get("pic").asText()));
        track.lyric_url = "http://"+ trackNode.get("lyric_url").asText();
        String url = handleProtocolRelativeUrl(caesar(location));
        return url;
      }
    }catch (IOException e){
      loger.error("get xiami track failed");
      return null;
    }
  }

  public SearchResult search(String keyword,int curpage) {
      String target_url = "http://api.xiami.com/web?v=2.0&app_key=1&key=" + keyword + "&page="+ curpage +"&limit=20&callback=jsonp154&r=search/songs";
      String responseBody =  UtilTools.get(target_url);
      String jsonBody = responseBody.substring("jsonp154(".length(), responseBody.length()-1);
      List<Track> tracks = new ArrayList<>();
      try{
        JsonNode dataNode = mapper.readTree(jsonBody).get("data");
        dataNode.get("songs").forEach(song->{
          tracks.add(convert_song(song,"artist_name"));
        });
       return  new SearchResult(tracks,dataNode.get("total").asInt());
      }catch(IOException e){
        return null;
      }
  }

  public PlayList album (String album_id) {

//      String album_id = UtilTools.getParameterByName("album_id", url).split("_")[1];
      String target_url = "http://api.xiami.com/web?v=2.0&app_key=1&id=" + album_id + "&page=1&limit=20&callback=jsonp217&r=album/detail";
      String responseBody =  UtilTools.get(target_url);
      String jsonBody = responseBody.substring("jsonp217(".length(), responseBody.length()-1);
      System.out.println(jsonBody);
      try{
        JsonNode dataNode = mapper.readTree(jsonBody).get("data");
        PlayListMeta playListMeta= new PlayListMeta.PlayListMetaBuilder()
                .id("xmalbum_" + dataNode.get("album_name").asText())
                .title(dataNode.get("album_name").asText())
                .imgurl(dataNode.get("album_logo").asText())
                .sourceurl("http://www.xiami.com/album/" + dataNode.get("album_id").asText())
                .build();

        List<Track> trackList = new ArrayList<>();

        dataNode.get("songs").forEach(song->{
          Track track = convert_song(song,"singers");
          trackList.add(track);
        });

        return new PlayList(playListMeta,trackList);
      }catch (IOException e){
        return null;
      }
  }


  public String lyric(Track track) {
    if(track.lyric_url.equals("http://")){
      return "[00:00.00]暂无歌词";
    }
    return  UtilTools.get(track.lyric_url);
  }


//  public  Map<String,String> parse_url(String url) {
//    Map<String,String>  resultMap = new HashMap<>();
//    Pattern pattern =Pattern.compile("//www.xiami.com/collect/([0-9]+)");
//    Matcher matcher = pattern.matcher(url);
//    if (matcher.matches()) {
//      String playlist_id =  matcher.group(0);
//      resultMap.put("type", "playlist");
//      resultMap.put("xmplaylist_",playlist_id);
//    }
//    return resultMap;
//  }

//  public PlayList  playlist (String url) {
//    String list_id = UtilTools.getParameterByName("list_id",url).split("_")[0];
//    switch(list_id){
//      case "xmplaylist":
//        return get_playlist(url);
//      case "xmalbum":
//        return album(url);
//      case "xmartist":
//        return artist(url);
//        default:
//          return null;
//    }
//  }

  public String codeName(){
    return "xiami";
  }
  //    Pattern pattern =Pattern.compile("");
  public static void main(String[] args) {

    String regex = "\\[(\\d+):(\\d+\\.\\d+)](.*)";

    String input = "[00:23.345]exy123z,xy456z";
    Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
    Matcher m = p.matcher(input);

    if(m.find()) {
      System.out.println(m.group(3));
    }
  }

}