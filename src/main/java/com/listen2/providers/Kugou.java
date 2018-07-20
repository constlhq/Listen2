package com.listen2.providers;

import com.fasterxml.jackson.databind.JsonNode;
import com.listen2.models.PlayList;
import com.listen2.models.PlayListMeta;
import com.listen2.models.SearchResult;
import com.listen2.models.Track;
import com.listen2.utils.UtilTools;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.Md5Crypt;
import org.apache.commons.text.StringEscapeUtils;



import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class Kugou implements IProvider {

  public static void addSongImg(Track track){
    track.img_url = UtilTools.get("http://www.kugou.com/yy/index.php?r=play/getdata&hash="+track.id);
  }


  private Track _convert_song(JsonNode track_json) {
    Track temp_track;
    String song_id = track_json.get("FileHash").asText();
    temp_track = new Track.TrackBuilder()
            .id(song_id)
            .title(StringEscapeUtils.unescapeXml(track_json.get("SongName").asText()))
            .artist(track_json.get("SingerName").asText())
            .artist_id(track_json.get("SingerId").asText())
            .album(track_json.get("AlbumName").asText())
            .album_id(track_json.get("AlbumID").asText())
            .source("kugou")
            .source_url("http://www.kugou.com/song/#hash=" + song_id + "&album_id=" + track_json.get("AlbumID").asText())
            .img_url("") // load when needed
            .url(song_id)
            .lyric_url(song_id)
            .disabled(false)
            .build();
    return temp_track;
  }


  public Track _convert_song(Track.TrackBuilder trackBuilder, JsonNode track_patch) {
         return trackBuilder
                .title(track_patch.get("songName").asText())
                .artist(track_patch.get("singerId").asInt() == 0 ? "佚名" :track_patch.get("singerName").asText())
                .artist_id(track_patch.get("singerId").asText())
                .source("kugou")
                .img_url(track_patch.get("imgUrl").asText().replaceAll("\\{size}","400"))
                .disabled(false)
                .build();
  }

  public List<PlayListMeta> get_playlists(int curpage) {
    String target_url = String.format("http://www2.kugou.kugou.com/yueku/v9/special/index/getData/getData.html&cdn=cdn&p=%d&pagesize=30&t=5&c=&is_ajax=1", curpage);
    String responseBody = UtilTools.get(target_url);
    System.out.println(responseBody);
    List<PlayListMeta> playListMetas = new ArrayList<>(30);
    try {
      JsonNode dataNode = mapper.readTree(responseBody);
      dataNode.get("special_db").forEach(li -> {
        String id = li.get("specialid").asText();
        String title = li.get("specialname").asText();
        String cover_img_url = li.get("img").asText();
        String source_url = String.format("http://www.kugou.com/yy/special/single/%s.html", id);

        playListMetas.add(new PlayListMeta.PlayListMetaBuilder()
                .id(id)
                .title(title)
                .imgurl(cover_img_url)
                .sourceurl(source_url)
                .build()
        );
      });
    } catch (Exception e) {
      e.printStackTrace();
    }
    return playListMetas;
  }


  public PlayList get_playlist(String id) {
    String target_url = String.format("http://m.kugou.com/plist/list/%s?json=true", id);
    String responseBody = UtilTools.get(target_url);
    try {
      JsonNode dataNode = mapper.readTree(responseBody);
      PlayListMeta playListMeta = new PlayListMeta.PlayListMetaBuilder()
              .id(id)
              .sourceurl(String.format("http://www.kugou.com/yy/special/single/%s.html", id))
              .title(dataNode.get("info").get("list").get("specialname").asText())
              .imgurl(dataNode.get("info").get("list").has("imgurl") ? dataNode.get("info").get("list").get("imgurl").asText().replaceAll("\\{size}", "400") : Kugou.class.getResource("/assets/mycover.jpg").toString())
              .build();

      List<JsonNode> track_list = new ArrayList<>(30);
      dataNode.get("list").get("list").get("info").forEach(track_json -> track_list.add(track_json));

      List<Track> trackList = track_list.parallelStream().map(track_json -> {
        String hash = track_json.get("hash").asText();
        String album_id = track_json.get("album_id").asText();
        String album = "";
        String songPatchResponse = UtilTools.get("http://m.kugou.com/app/i/getSongInfo.php?cmd=playInfo&hash=" + hash);
        String albumNamePatchResponse = UtilTools.get("http://mobilecdnbj.kugou.com/api/v3/album/info?albumid="+album_id);

        try {
          JsonNode track_patch = mapper.readTree(songPatchResponse);
          JsonNode album_patch = mapper.readTree(albumNamePatchResponse);
          if(album_patch.get("status").asInt()==1){
            album = album_patch.get("data").get("albumname").asText();
          }else{
            album = "unknown";
          }
          Track.TrackBuilder  trackBuilder = new Track.TrackBuilder();
          trackBuilder
                  .id(hash)
                  .album_id(album_id)
                  .source_url("http://www.kugou.com/song/#hash=" + hash + "&album_id=" + album_id)
                  .url(hash)
                  .album(album)
                  .lyric_url(hash);


          Track track = _convert_song(trackBuilder, track_patch);
          return track;
        } catch (IOException e) {
          e.printStackTrace();
          return null;
        }
      }).collect(Collectors.toList());
      return new PlayList(playListMeta, trackList);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public String bootstrap_track(Track track) {

    String md5 =  DigestUtils.md5Hex(track.id + "kgcloudv2").toLowerCase();
    String target_url = "http://www.kugou.com/yy/index.php?r=play/getdata&hash=" + track.id;

    String target_url_vip = String.format("http://trackercdnbj.kugou.com/i/v2/?cmd=23&pid=1&behavior=download&hash=%s&key=%s", track.id, md5);
    String responseBody = UtilTools.get(target_url);

//    System.out.println(target_url+" "+responseBody);

    try {
      JsonNode urlNode = mapper.readTree(responseBody).get("data");
      if (!urlNode.get("play_url").asText().isEmpty()) {
        return urlNode.get("play_url").asText();
      }else{
        urlNode =  mapper.readTree(UtilTools.get(target_url_vip));
        if(urlNode.get("status").asInt()==1){
          return  urlNode.get("url").asText();
        }else{
          return null;
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public SearchResult search(String keyword, int curpage) {
    String target_url = String.format("http://songsearch.kugou.com/song_search_v2?keyword=%s&page=%d", keyword, curpage);
    String responseBody = UtilTools.get(target_url);

    System.out.println(responseBody);

    List<Track> tracks = new ArrayList<>(20);
    try {
      JsonNode dataNode = mapper.readTree(responseBody).get("data");
      dataNode.get("lists").forEach(track_json -> tracks.add(_convert_song(track_json)));
      return new SearchResult(tracks, dataNode.get("total").asInt());
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public PlayList album(String id) {
    String target_url = String.format("http://mobilecdnbj.kugou.com/api/v3/album/info?albumid=%s", id);
    String responseBody = UtilTools.get(target_url);
//    String jsonBody = responseBody.substring("asonglist1459961045566(".length()+1, responseBody.length() - 1);

    try {
      JsonNode dataNode = mapper.readTree(responseBody).get("data");
      PlayListMeta playListMeta = new PlayListMeta.PlayListMetaBuilder()
              .id(id)
              .title(dataNode.get("albumname").asText())
              .imgurl(dataNode.get("imgurl").asText().replace("{size}", "400"))
              .sourceurl("http://www.kuwo.cn/album/" + id)
              .build();

      target_url = String.format("http://mobilecdnbj.kugou.com/api/v3/album/song?albumid=%s&page=1&pagesize=-1", id);
      responseBody = UtilTools.get(target_url);
      System.out.println(responseBody);
      dataNode = mapper.readTree(responseBody).get("data");

      List<JsonNode> track_list = new ArrayList<>();

      dataNode.get("info").forEach(track_info -> {
        track_list.add(track_info);
      });

      List<Track> trackList = track_list.parallelStream().map(track_json -> {

        String hash = track_json.get("hash").asText();
        String album_id = track_json.get("album_id").asText();

        String target_url2 = "http://m.kugou.com/app/i/getSongInfo.php?cmd=playInfo&hash=" + track_json.get("hash").asText();
        String responseBody2 = UtilTools.get(target_url2);

        try {
          JsonNode track_patch = mapper.readTree(responseBody2);

          Track.TrackBuilder  trackBuilder = new Track.TrackBuilder();
          trackBuilder
                  .id(hash)
                  .album_id(playListMeta.id)
                  .source_url("http://www.kugou.com/song/#hash=" + hash + "&album_id=" + album_id)
                  .url(hash)
                  .album(playListMeta.title)
                  .lyric_url(hash);

          return _convert_song(trackBuilder, track_patch);

        } catch (IOException e) {
          e.printStackTrace();
        }
        return null;
      }).collect(Collectors.toList());

      return new PlayList(playListMeta, trackList);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public String lyric(Track track) {
    String target_url = "http://www.kugou.com/yy/index.php?r=play/getdata&hash=" + track.id;
    String response = UtilTools.get(target_url);
    System.out.println(response);
    try{
      return mapper.readTree(response).get("data").get("lyrics").asText();
    }catch (IOException e){
      e.printStackTrace();
    }
    return null;
  }

  public String codeName() {
    return "kugou";
  }
}

