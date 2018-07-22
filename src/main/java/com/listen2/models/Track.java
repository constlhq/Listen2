package com.listen2.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Track {
  public Track() {}

  public String id;
  public String title;
  public String artist;
  public String artist_id;
  public String album;
  public String album_id;
  public String source;
  public String source_url;
  public String img_url;
  public String url;
  public String lyric_url;
  public boolean disabled;
  public StringProperty titleProperty;
  public StringProperty artistProperty;
  public StringProperty albumProperty;


  @JsonProperty
  public void setTitleProperty(String titleProperty) {
    this.titleProperty =new SimpleStringProperty(titleProperty);
  }
  @JsonProperty
  public void setArtistProperty(String artistProperty) {
    this.artistProperty =new SimpleStringProperty(artistProperty);
  }
  @JsonProperty
  public void setAlbumProperty(String albumProperty) {
    this.albumProperty =new SimpleStringProperty(albumProperty);
  }

  public Track(TrackBuilder trackBuilder ) {
    this.id = trackBuilder.id;
    this.title = trackBuilder.title;
    this.artist = trackBuilder.artist;
    this.artist_id = trackBuilder.artist_id;
    this.album = trackBuilder.album;
    this.album_id = trackBuilder.album_id;
    this.source = trackBuilder.source;
    this.source_url = trackBuilder.source_url;
    this.img_url = trackBuilder.img_url;
    this.url = trackBuilder.url;
    this.lyric_url = trackBuilder.lyric_url;
    this.disabled = trackBuilder.disabled;

    this.titleProperty = new SimpleStringProperty(title);
    this.artistProperty = new SimpleStringProperty(artist);
    this.albumProperty = new SimpleStringProperty(album);

  }

  public static class TrackBuilder{
     String id;
     String title;
     String artist;
     String artist_id;
     String album;
     String album_id;
     String source;
     String source_url;
     String img_url;
     String url;
     String lyric_url;
     boolean disabled;

    public Track build(){
      return new Track(this);
    }

    public TrackBuilder id(String id) {
      this.id = id;
      return this;
    }

    public TrackBuilder title(String title) {
      this.title = title;
      return this;
    }

    public TrackBuilder artist(String artist) {
      this.artist = artist;
      return this;
    }

    public TrackBuilder artist_id(String artist_id) {
      this.artist_id = artist_id;
      return this;
    }

    public TrackBuilder album(String album) {
      this.album = album;
      return this;
    }

    public TrackBuilder album_id(String album_id) {
      this.album_id = album_id;
      return this;
    }

    public TrackBuilder source(String source) {
      this.source = source;
      return this;
    }

    public TrackBuilder source_url(String source_url) {
      this.source_url = source_url;
      return this;
    }

    public TrackBuilder img_url(String img_url) {
      this.img_url = img_url;
      return this;
    }

    public TrackBuilder url(String url) {
      this.url = url;
      return this;
    }

    public TrackBuilder lyric_url(String lyric_url) {
      this.lyric_url = lyric_url;
      return this;
    }

    public TrackBuilder disabled(boolean disabled) {
      this.disabled = disabled;
      return this;
    }
  }

  public String getTitleProperty() {
    return titleProperty.get();
  }

  public StringProperty titlePropertyProperty() {
    return titleProperty;
  }

  public String getArtistProperty() {
    return artistProperty.get();
  }

  public StringProperty artistPropertyProperty() {
    return artistProperty;
  }

  public String getAlbumProperty() {
    return albumProperty.get();
  }

  public StringProperty albumPropertyProperty() {
    return albumProperty;
  }

//  @Override
//  public String toString() {
//    return "{" +
//            "id:" + id +
//            ", title:" + title +
//            ", artist:" + artist +
//            ", artist_id:" + artist_id +
//            ", album:" + album +
//            ", album_id:" + album_id +
//            ", source:" + source +
//            ", source_url:" + source_url +
//            ", img_url:" + img_url +
//            ", url:" + url +
//            ", lyric_url:" + lyric_url +
//            ", disabled:" + disabled + "}";
//  }
}
