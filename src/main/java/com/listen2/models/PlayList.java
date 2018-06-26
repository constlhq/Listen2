package com.listen2.models;

import java.util.List;

public class PlayList {
  public PlayListMeta playListMeta;
  public List<Track> tracks;

  public PlayList(PlayListMeta playListMeta, List<Track> tracks) {
    this.playListMeta = playListMeta;
    this.tracks = tracks;
  }
}
