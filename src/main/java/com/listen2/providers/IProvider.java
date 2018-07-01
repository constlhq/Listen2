package com.listen2.providers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.listen2.models.*;

import java.util.List;
import java.util.Map;

public interface IProvider {
  ObjectMapper mapper = new ObjectMapper();
  List<PlayListMeta> get_playlists(int curpage);
  PlayList get_playlist(String id) ;
  String  bootstrap_track (Track track);
  SearchResult search(String keywork,int curpage);
  PlayList album (String id) ;
  String lyric(Track track) ;
  String codeName();
}
