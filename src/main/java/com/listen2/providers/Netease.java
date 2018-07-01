package com.listen2.providers;

import com.fasterxml.jackson.databind.JsonNode;
import com.listen2.models.*;
import org.apache.commons.text.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import sun.misc.BASE64Encoder;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.*;


import static com.listen2.utils.UtilTools.*;
import static java.util.stream.Collectors.toList;

public class Netease implements IProvider{

  private static String _create_secret_key(int size) {
    StringBuilder result = new StringBuilder();
    String[] choice = "012345679abcdef".split("");
    for (int i=0; i<size; i++) {
      int index = (int)Math.floor(Math.random() * 15);
      result.append(choice[index]);
    }
    return result.toString();
  }

  private static String   _aes_encrypt(String text, String sec_key) {
    int pad = 16-text.length() % 16;
    StringBuilder sb = new StringBuilder(text);
    for (int i=0; pad < 16 && i < pad; i++) {
      sb.append ((char)pad);
    }
    text = sb.toString();
    byte[] key =sec_key.getBytes() ;
    byte[] ivParameter = "0102030405060708".getBytes();
    try{
      byte[] textBytes = text.getBytes("utf-8");
      SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
      IvParameterSpec iv = new IvParameterSpec(ivParameter);
      cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
      String result = new BASE64Encoder().encode(cipher.doFinal(textBytes)).replaceAll("\\s","");
      return result;
    }catch (Exception e){
      e.printStackTrace();
      return  "";
    }
  }

  private static Map<String,String> _encrypted_request(String text){
    String modulus = "00e0b509f6259df8642dbc35662901477df22677ec152b5ff68ace615bb7b725152b3ab17a876aea8a5aa76d2e417629ec4ee341f56135fccf695280104e0312ecbda92557c93870114af6c9d05c4f7f0c3685b7a46bee255932575cce10b424d813cfe4875d3e82047b97ddef52741d546b8e289dc6935b3ece0462db0a22b8e7";
    String nonce = "0CoJUm6Qyw8W8jud";
    String pubKey = "010001";
    String sec_key = _create_secret_key(16);
    String enc_text = _aes_encrypt(_aes_encrypt(text, nonce), sec_key);
//    System.out.println("enc_text:"+enc_text);
    String enc_sec_key = _rsa_encrypt(sec_key, pubKey, modulus);
    Map<String,String> data = new HashMap<>();
    data.put("params",enc_text);
    data.put("encSecKey",enc_sec_key);
    return data;
  }


  private static String _rsa_encrypt( String text, String pubKey,String modulus) {
    text =  new StringBuffer(text).reverse().toString();
    BigInteger base = new BigInteger(_hexify(text), 16);
    BigInteger exp = new BigInteger(pubKey, 16);
    BigInteger mod = new BigInteger(modulus, 16);
    BigInteger bigNumber = _expmod(base, exp, mod);
    String rs = bigNumber.toString(16);
    return _zfill(rs, 256).toLowerCase();
  }

  private static String _zfill(String num, int size) {
    int fillSize = size - num.length();
    StringBuilder sb = new StringBuilder();
    while (fillSize>0){
      sb.append(0);
      fillSize--;
    }
    return  sb.append(num).toString();
  }

  private static String _hexify(String str){
    try{
      return String.format("%x", new BigInteger(1, str.getBytes("UTF-8")));
    }catch(UnsupportedEncodingException e){
      return "";
    }
  }

  private static BigInteger _expmod(BigInteger base, BigInteger exp,BigInteger mymod ) {
    BigInteger Big2 = BigInteger.ONE.add(BigInteger.ONE);
    if (exp.equals(BigInteger.ZERO)){
      return BigInteger.ONE;
    }
    else if (exp.mod(Big2).equals(BigInteger.ZERO)) {
      BigInteger newexp = new BigInteger(exp.toByteArray());
      newexp = newexp.shiftRight(1);
      return _expmod( base, newexp, mymod).pow(2).mod(mymod);
    }
    else {
      return _expmod(base, exp.subtract(BigInteger.ONE), mymod).multiply(base).mod(mymod);
    }
  }

  private  static Track _convert_song(JsonNode track_json,String type) {
    switch (type){
      case "playlist":
        return  new Track.TrackBuilder()
                .id( track_json.get("id").asText())
                .title( track_json.get("name").asText())
                .artist(track_json.get("ar").get(0).get("name").asText())
                .artist_id(track_json.get("ar").get(0).get("id").asText())
                .album( track_json.get("al").get("name").asText())
                .album_id(track_json.get("al").get("id").asText())
                .source("netease")
                .source_url( "http://music.163.com/#/song?id=" + track_json.get("id").asText())
                .img_url( track_json.get("al").get("picUrl").asText())
                .url(track_json.get("id").asText())
                .lyric_url("")
                .disabled(track_json.get("st").asInt()<0||track_json.get("fee").asInt()==4)
                .build();
      case "search":
        return  new Track.TrackBuilder()
                .id(track_json.get("id").asText())
                .title( track_json.get("name").asText())
                .artist(track_json.get("artists").get(0).get("name").asText())
                .artist_id(track_json.get("artists").get(0).get("id").asText())
                .album( track_json.get("album").get("name").asText())
                .album_id(track_json.get("album").get("id").asText())
                .source("netease")
                .source_url( "http://music.163.com/#/song?id=" + track_json.get("id").asText())
                .img_url( track_json.get("album").get("picUrl").asText())
                .url(track_json.get("id").asText())
                .lyric_url("")
                .disabled(track_json.get("status").asInt()<0||track_json.get("fee").asInt()==4)
                .build();
      default:
        return null;
    }
  }



  public List<PlayListMeta> get_playlists(int curpage){
    String target_url = "http://music.163.com/discover/playlist/?order=hot&limit=30&offset=" + curpage*30;


    String responseBody = get(target_url);
    Document doc = Jsoup.parse(responseBody);
    Elements mcvrlst = doc.select(".m-cvrlst li");

    return mcvrlst.stream().map(li->{
      String relative_url  = li.selectFirst("a").attr("href");
      String list_id = getParameterByName("id",relative_url);
      String id = list_id;
      String title = li.selectFirst("a").attr("title");
      String cover_img_url = li.selectFirst("img").attr("src") ;
      String source_url = "http://music.163.com/#/playlist?id=" + list_id;

      return new PlayListMeta.PlayListMetaBuilder()
              .id(id)
              .title(title)
              .imgurl(cover_img_url)
              .sourceurl(source_url)
              .build();
    }).collect(toList());
  }

  public PlayList get_playlist(String list_id) {
    System.out.println(list_id);
      String target_url = "http://music.163.com/weapi/v3/playlist/detail";
      String d = String.format("{\"id\":\"%s\",\"offset\":0,\"total\":true,\"limit\":1000,\"n\":1000,\"csrf_token\":\"\"}", list_id);
      try{
      Map<String,String> data = _encrypted_request(d);
      String responseBody =  post(target_url,data);
      JsonNode dataNode = mapper.readTree(responseBody);
      PlayListMeta playListMeta= new PlayListMeta.PlayListMetaBuilder()
              .sourceurl("http://music.163.com/#/playlist?id=" + list_id)
              .title(dataNode.get("playlist").get("name").asText())
              .imgurl(dataNode.get("playlist").get("coverImgUrl").asText())
              .id(list_id)
              .build();
      List<Track> trackList = new ArrayList<>();
      dataNode.get("playlist").get("tracks").forEach(track_json->{
        Track track = _convert_song(track_json,"playlist");
        trackList.add(track);
      });
      return new PlayList(playListMeta,trackList);
    }catch (Exception e){
        e.printStackTrace();
      return null;
    }
  }

  public String  bootstrap_track (Track track){
    String target_url = "http://music.163.com/weapi/song/enhance/player/url?csrf_token=";
    String song_id = track.id;
    song_id = song_id;
    String d = String.format("{\"ids\": [%s],\"br\": 320000, \"csrf_token\":\"\"}",song_id);
    Map<String,String> data = _encrypted_request(d);
    try{
      String responseBody =  post(target_url,data);
      System.out.println(responseBody);
      JsonNode dataNode = mapper.readTree(responseBody);
      String url = dataNode.get("data").get(0).get("url").asText();
      return url;
    }catch (Exception e){
      e.printStackTrace();
      return null;
    }
  }

  public SearchResult search(String keyword,int curpage) {
    // use chrome extension to modify referer.
    String target_url = "http://music.163.com/api/search/pc";
    Map<String,String> data = new HashMap<String,String>(){{
      put("s",keyword);
      put("offset",20*(curpage-1)+"");
      put("limit","20");
      put("type","1");
    }};
    System.out.println(data);
    List<Track> tracks = new ArrayList<>();
    try{
      String responseBody = post(target_url,data);
      JsonNode dataNode = mapper.readTree(responseBody);

      System.out.println(responseBody);
      dataNode.get("result").get("songs").forEach(song->tracks.add(_convert_song(song,"search")));
      return  new SearchResult(tracks,dataNode.get("result").get("songCount").asInt());
    }catch(Exception e){
      e.printStackTrace();
      return null;
    }
  }

  public PlayList album (String album_id){
    String target_url = "http://music.163.com/api/album/" + album_id;
    try{
      String responseBody = get(target_url);
      JsonNode dataNode = mapper.readTree(responseBody);
      PlayListMeta playListMeta= new PlayListMeta.PlayListMetaBuilder()
              .id("nealbum_" + dataNode.get("album").get("id").asText())
              .title(dataNode.get("album").get("name").asText())
              .imgurl(dataNode.get("album").get("picUrl").asText())
              .sourceurl("http://music.163.com/#/album?id=" + dataNode.get("album").get("id").asText())
              .build();
      List<Track> trackList = new ArrayList<>();

      dataNode.get("album").get("songs").forEach(song->{
        Track track = _convert_song(song,"search");
        trackList.add(track);
      });
      return new PlayList(playListMeta,trackList);
    }catch (IOException e){
      return null;
    }
  }

  public PlayList artist(String artist_id) {
    String target_url = "http://music.163.com/api/artist/" + artist_id;
    try{
      String responseBody = get(target_url);
      JsonNode dataNode = mapper.readTree(responseBody);
//      System.out.println(responseBody);
      PlayListMeta playListMeta= new PlayListMeta.PlayListMetaBuilder()
              .id("neartist_" + dataNode.get("artist").get("id").asText())
              .title(dataNode.get("artist").get("name").asText())
              .imgurl(dataNode.get("artist").get("picUrl").asText())
              .sourceurl("http://music.163.com/#/artist?id=" + dataNode.get("artist").get("id").asText())
              .build();
      List<Track> trackList = new ArrayList<>();

      dataNode.get("hotSongs").forEach(song->{
        Track track = _convert_song(song,"search");
        trackList.add(track);
      });
      return new PlayList(playListMeta,trackList);
    }catch (IOException e){
      return null;
    }
  }

  public String lyric(Track track) {
    String track_id = track.id;
    String target_url = "http://music.163.com/weapi/song/lyric?csrf_token=";
    String d = String.format("{\"id\":%s,\"lv\":-1,\"tv\":-1,\"csrf_token\":\"\"}",track_id);
    Map<String,String> data = _encrypted_request(d);
    try{
      String responseBody =  post(target_url,data);
      JsonNode dataNode = mapper.readTree(responseBody);
      if (dataNode.has("lrc")){
        return dataNode.get("lrc").get("lyric").asText();
      }
    }catch (Exception e){
      e.printStackTrace();
      return null;
    }
    return null;
  }
  public String codeName(){
    return "netease";
  }
}















