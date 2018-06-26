package com.listen2.providers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.listen2.models.*;

import java.util.List;
import java.util.Map;

public interface IProvider {
  ObjectMapper mapper = new ObjectMapper(); // create once, reuse
  List<PlayListMeta> get_playlists(String url);
  PlayList get_playlist(String url) ;
  boolean  bootstrap_track (Track track ,Sound sound );
  SearchResult search(String url) ;
  PlayList album (String url) ;
  PlayList artist(String url) ;
  String lyric(String url) ;
  PlayList  playlist (String url);
  Map<String,String> parse_url(String url);
}
