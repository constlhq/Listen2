package com.listen2.models;

import java.util.List;

public class SearchResult {
  public List<Track> tracks;
  public int total;

  public SearchResult(List<Track> tracks, int total) {
    this.tracks = tracks;
    this.total = total;
  }
}
