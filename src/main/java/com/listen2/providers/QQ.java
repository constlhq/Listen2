package com.listen2.providers;

import com.fasterxml.jackson.databind.JsonNode;
import com.listen2.models.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.apache.commons.text.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.listen2.utils.UtilTools.get;
import static com.listen2.utils.UtilTools.getParameterByName;
import static com.listen2.utils.UtilTools.post;
import static java.util.stream.Collectors.toList;

public class QQ implements IProvider {

  private static String _get_image_url(String qqimgid, String img_type){
    if (qqimgid.isEmpty()) {
      return "";
    }
    String category =img_type.equals("artist")?"mid_singer_300":"mid_album_300";
    int midlen = qqimgid.length();
    String s = String.format("%s/%c/%c/%s", category,qqimgid.charAt(midlen-2),qqimgid.charAt(midlen-1),qqimgid);
    return String.format("http://imgcache.qq.com/music/photo/%s.jpg",s);
  }

  private static boolean _is_playable( String switchCode) {
    String switch_flag = new StringBuilder(Integer.toBinaryString(Integer.parseInt(switchCode))).reverse().toString();
    // flag switch table meaning(after reverse):
    // ["foo","play_lq", "play_hq", "play_sq", "down_lq", "down_hq", "down_sq", "soso", "fav", "share", "bgm", "ring", "sing", "radio", "try", "give"]
    char play_flag = switch_flag.charAt(1);
    char try_flag = switch_flag.charAt(14);
    return (play_flag == '1'|| try_flag == '1');
  }



  private  static Track _convert_song(JsonNode track_json) {

        return  new Track.TrackBuilder()
                .id( "qqtrack_" + track_json.get("songmid").asText())
                .title( StringEscapeUtils.unescapeXml(track_json.get("songname").asText()))
                .artist(StringEscapeUtils.unescapeXml(track_json.get("singer").get(0).get("name").asText()))
                .artist_id( "qqartist_" + track_json.get("singer").get(0).get("mid").asText())
                .album( StringEscapeUtils.unescapeXml(track_json.get("albumname").asText()))
                .album_id( "qqalbum_" + track_json.get("albumid").asText())
                .source("qq")
                .source_url( "http://y.qq.com/#type=song&mid=" + track_json.get("songmid").asText() + "&tpl=yqq_song_detail")
                .img_url(_get_image_url(track_json.get("albummid").asText(),"album"))
                .url("qqtrack_" + track_json.get("songid").asText())
                .lyric_url("")
                .disabled(!_is_playable(track_json.get("switch").asText()))
                .build();
  }


  public List<PlayListMeta> get_playlists(String url){
    String offsetRaw =getParameterByName("offset", url);
    int offsetNum = Integer.parseInt(offsetRaw.isEmpty()?"0":offsetRaw);
    String target_url = String.format("https://c.y.qq.com/splcloud/fcgi-bin/fcg_get_diss_by_tag.fcg?rnd=0.4781484879517406&g_tk=732560869&jsonpCallback=MusicJsonCallback&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&categoryId=10000000&sortId=5&sin=%d&ein=%d",offsetNum,offsetNum+49);
    String responseBody = get(target_url);
    String jsonBody = responseBody.substring("MusicJsonCallback(".length(),responseBody.length()-1);
    List<PlayListMeta> playListMetas = new ArrayList<>();
    try{
      JsonNode dataNode = mapper.readTree(jsonBody).get("data");
      dataNode.get("list").forEach(li->{
        String list_id =li.get("dissid").asText();
        String id = "qqplaylist_" + list_id;
        String title = StringEscapeUtils.unescapeXml(li.get("dissname").asText());
        String cover_img_url = li.get("imgurl").asText();
        String source_url = "http://y.qq.com/#type=taoge&id=" + list_id;

        playListMetas.add(new PlayListMeta.PlayListMetaBuilder()
                .id(id)
                .title(title)
                .imgurl(cover_img_url)
                .sourceurl(source_url)
                .build()
        );
      });
    }catch(Exception e){

    }
    return playListMetas;
  }

  public PlayList get_playlist(String url) {
    String list_id = getParameterByName("list_id", url).split("_")[1];
    String target_url = "http://i.y.qq.com/qzone-music/fcg-bin/fcg_ucc_getcdinfo_byids_cp.fcg?type=1&json=1&utf8=1&onlysong=0&jsonpCallback=jsonCallback&nosign=1&disstid=list_id&g_tk=5381&loginUin=0&hostUin=0&format=jsonp&inCharset=GB2312&outCharset=utf-8&notice=0&platform=yqq&jsonpCallback=jsonCallback&needNewCode=0";
    String responseBody = get(target_url);
    String jsonBody = responseBody.substring("jsonCallback(".length(), responseBody.length() - 1);
    try {
      JsonNode dataNode = mapper.readTree(jsonBody);
      PlayListMeta playListMeta = new PlayListMeta.PlayListMetaBuilder()
              .sourceurl("http://y.qq.com/#type=taoge&id=" + list_id)
              .title(dataNode.get("cdlist").get(0).get("dissname").asText())
              .imgurl(dataNode.get("cdlist").get(0).get("logo").asText())
              .id("qqplaylist_" + list_id)
              .build();
      List<Track> trackList = new ArrayList<>();
      dataNode.get("cdlist").get(0).get("songlist").forEach(track_json -> {
        Track track = _convert_song(track_json);
        trackList.add(track);
      });
      return new PlayList(playListMeta, trackList);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
  

  public boolean  bootstrap_track (Track track ,Sound sound ){
    String target_url = String.format("https://c.y.qq.com/base/fcgi-bin/fcg_music_express_mobile3.fcg?g_tk=195219765&jsonpCallback=MusicJsonCallback004680169373158849&loginUin=1297716249&hostUin=0&format=json&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&cid=205361747&callback=MusicJsonCallback004680169373158849&uin=1297716249&songmid=%s&filename=C400%s.m4a&guid=7332953645", track.id.substring(8),track.id.substring(8));
    String responseBody = get(target_url);
    System.out.println(responseBody);
    String jsonBody = responseBody.substring(responseBody.indexOf('(')+1,responseBody.length()-1);
    try{
      JsonNode dataNode = mapper.readTree(jsonBody).get("data");
      String token = dataNode.get("items").get(0).get("vkey").asText();
      String url = "http://dl.stream.qqmusic.qq.com/C400" + track.id.split("_")[1]+".m4a?vkey="+ token +"&uin=1297716249&fromtag=0&guid=7332953645";
      sound.url = url;
      return true;
    }catch (Exception e){
      return false;
    }
  }

  public SearchResult search(String url) {
    String keyword = getParameterByName("keywords", url);
    String curpage = getParameterByName("curpage", url);
    String target_url = String.format("http://i.y.qq.com/s.music/fcgi-bin/search_for_qq_cp?g_tk=938407465&uin=0&format=jsonp&inCharset=utf-8&outCharset=utf-8&notice=0&platform=h5&needNewCode=1&w=%s&zhidaqu=1&catZhida=1&t=0&flag=1&ie=utf-8&sem=1&aggr=0&perpage=20&n=20&p=%s&remoteplace=txt.mqq.all&_=1459991037831&jsonpCallback=jsonp4", keyword,curpage);
    String responseBody = get(target_url);
    String jsonBody = responseBody.substring("jsonp4(".length(), responseBody.length() - 1);
    List<Track> tracks = new ArrayList<>();
    try {
      JsonNode dataNode = mapper.readTree(jsonBody).get("data");
      dataNode.get("song").get("list").forEach(li -> tracks.add(_convert_song(li)));
      return new SearchResult(tracks, dataNode.get("song").get("totalnum").asInt());
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }


  public PlayList album (String url){
    String album_id = getParameterByName("list_id", url).split("_")[1];
    String target_url = String.format("http://i.y.qq.com/v8/fcg-bin/fcg_v8_album_info_cp.fcg?platform=h5page&albummid=%s&g_tk=938407465&uin=0&format=jsonp&inCharset=utf-8&outCharset=utf-8&notice=0&platform=h5&needNewCode=1&_=1459961045571&jsonpCallback=asonglist1459961045566", album_id);
    String responseBody = get(target_url);
    String jsonBody = responseBody.substring(" asonglist1459961045566(".length(), responseBody.length() - 1);
    try{
      JsonNode dataNode = mapper.readTree(jsonBody).get("data");
      PlayListMeta playListMeta= new PlayListMeta.PlayListMetaBuilder()
              .id("qqalbum_" + album_id)
              .title(dataNode.get("name").asText())
              .imgurl(_get_image_url(album_id, "album"))
              .sourceurl("http://y.qq.com/#type=album&mid=" + album_id)
              .build();

      List<Track> trackList = new ArrayList<>();
      dataNode.get("list").forEach(song->{
        Track track = _convert_song(song);
        trackList.add(track);
      });
      return new PlayList(playListMeta,trackList);
    }catch (Exception e){
      e.printStackTrace();
      return null;
    }
  }


  public PlayList artist(String url) {
    String artist_id = getParameterByName("artist_id", url).split("_")[1];
    String target_url = String.format("http://i.y.qq.com/v8/fcg-bin/fcg_v8_singer_track_cp.fcg?platform=h5page&order=listen&begin=0&num=50&singermid=%s&g_tk=938407465&uin=0&format=jsonp&inCharset=utf-8&outCharset=utf-8&notice=0&platform=h5&needNewCode=1&from=h5&_=1459960621777&jsonpCallback=ssonglist1459960621772", artist_id);
    String responseBody = get(target_url);
//    System.out.println(responseBody);
    String jsonBody = responseBody.substring(" ssonglist1459960621772(".length(), responseBody.length() - 1);
    try{
      JsonNode dataNode = mapper.readTree(jsonBody).get("data");
      PlayListMeta playListMeta= new PlayListMeta.PlayListMetaBuilder()
              .id("qqalbum_" + artist_id)
              .title(dataNode.get("singer_name").asText())
              .imgurl(_get_image_url(artist_id, "artist"))
              .sourceurl("http://y.qq.com/#type=singer&mid=" + artist_id)
              .build();
      List<Track> trackList = new ArrayList<>();
      dataNode.get("list").forEach(song->{
        Track track = _convert_song(song.get("musicData"));
        trackList.add(track);
      });
      return new PlayList(playListMeta,trackList);
    }catch (Exception e){
      e.printStackTrace();
      return null;
    }
  }

  public String lyric(String url) {
    String track_id = getParameterByName("track_id", url).split("_")[1];
    String target_url = String.format("http://i.y.qq.com/lyric/fcgi-bin/fcg_query_lyric.fcg?songmid=%s&loginUin=0&hostUin=0&format=jsonp&inCharset=GB2312&outCharset=utf-8&notice=0&platform=yqq&jsonpCallback=MusicJsonCallback&needNewCode=0", track_id);
    String responseBody = get(target_url);
    String jsonBody = responseBody.substring("MusicJsonCallback(".length(), responseBody.length() - 1);
    try{
      JsonNode dataNode = mapper.readTree(jsonBody);
      if (dataNode.has("lyric")){
        return new String(new BASE64Decoder().decodeBuffer(dataNode.get("lyric").asText()),"Utf-8");
      }

    }catch (Exception e){

    }
    return "";
  }

  public PlayList  playlist (String url){
    String list_id = getParameterByName("list_id",url).split("_")[0];
    switch(list_id){
      case "qqplaylist":
        return get_playlist(url);
      case "qqalbum":
        return album(url);
      case "qqartist":
        return artist(url);
      default:
        return null;
    }
  }

  public Map<String,String> parse_url (String url) {
    Map<String,String>  resultMap = new HashMap<>();
    Pattern pattern =Pattern.compile("//y.qq.com/n/yqq/playlist/([0-9]+)");
    Matcher matcher = pattern.matcher(url);
    if (matcher.matches()) {
      String playlist_id =  matcher.group(0);
      resultMap.put("type", "playlist");
      resultMap.put("qqplaylist_",playlist_id);
    }
    return resultMap;
  }


  public static void main(String[] args) throws Exception {
  QQ qq = new QQ();
    Track t= qq.artist("/playlist?artist_id=qqartist_001MXQUi1tlLon").tracks.get(0);
    Sound s = new Sound();
    qq.bootstrap_track(t,s);

    System.out.println(s.url);

  }
}