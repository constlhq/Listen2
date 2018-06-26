package com.listen2.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.listen2.models.CustomPlayList;
import com.listen2.models.PlayerState;
import com.listen2.models.Track;
import org.ini4j.Ini;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public interface IniFileManger {
  String USER_HOME =  System.getProperty("user.home");
  String listen2_DIR = USER_HOME+"/listen2";
  String INI_PATH= listen2_DIR+"/listen2.ini";
  ObjectMapper mapper = new ObjectMapper();

  static void createIniFile(){
    try{
    File configFolder = new File(listen2_DIR);
    if (!configFolder.exists())
      configFolder.mkdirs();
    File iniFile = new File(configFolder,"listen2.ini");
    if (!iniFile.exists()){
      iniFile.createNewFile();
    }
    }catch (IOException e){
    }
  }

  static void writeField(String sec,String field,String value) {
    try{
      Ini ini = new Ini(new File(INI_PATH));
      ini.put(sec,field,value);
    }catch (Exception e){
    }
  }

  static PlayerState getPlayerState(){
    try{
      Ini ini = new Ini(new File(INI_PATH));
      Ini.Section section = ini.get("playerstate");
      if(section == null){
        throw new Exception();
      }
      return section.as(PlayerState.class);
    }catch (Exception e){
      return new PlayerState();
    }
  }

  static void setPlayerState(PlayerState playerState){
    try{
      Ini ini = new Ini(new File(INI_PATH));
      Ini.Section section = ini.get("playerstate");
      if(section == null){
        section = ini.add("palyerstate");
      }
       section.from(playerState);
    }catch (Exception e){
    }
  }
  
  static List<Track> getTracks(String secname){
    List<Track> tracks = new LinkedList<>();
    try{
      Ini ini = new Ini(new File(INI_PATH));
      String[] playqueue = ini.get(secname).getAll("track", String[].class);
      for (String track : playqueue){
        tracks.add(mapper.readValue(track, Track.class));
      }
      return tracks;
    }catch (Exception e){
      return null;
    }
  }

  static void putTracks(String secname, List<Track> playQueue){
    try{
      Ini ini = new Ini(new File(INI_PATH));
      Ini.Section section = ini.get(secname);
      if(section==null){
        section = ini.add(secname);
      }
      List<String> queue  = new LinkedList<>();
      for(Track track : playQueue){
        try{
          queue.add(mapper.writeValueAsString(track));
          section.putAll("track",queue);
        }catch (Exception e){

        }
      }
    }catch(Exception e){

    }
  }

  static List<CustomPlayList> getCustomPlayList(){
    List<CustomPlayList> customPlayLists = new LinkedList<>();
    try{
      Ini ini = new Ini(new File(INI_PATH));
      String[] customlist = ini.get("customplaylist").getAll("list", String[].class);
      for (String list : customlist){
        customPlayLists.add(mapper.readValue(list, CustomPlayList.class));
      }
      return customPlayLists;

    }catch(Exception e){
      return null;
    }
  }

  static void putCustomPlayList(List<CustomPlayList> customPlayLists){
    try{
      Ini ini = new Ini(new File(INI_PATH));
      List<String> listsjson  = new LinkedList<>();
      for(CustomPlayList list: customPlayLists){
        try{
          listsjson.add(mapper.writeValueAsString(list));
          ini.get("customplaylist").putAll("list",listsjson);
        }catch (Exception e){
        }
      }

    }catch(Exception e){

    }
  }


  public static void main(String[] args) {

  }
}
