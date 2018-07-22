package com.listen2.models;

public class PlayListMeta {


  public  String title ;
  public  String cover_img_url ;
  public  String source_url ;
  public  String id ;

  public PlayListMeta(String title, String cover_img_url, String source_url, String id) {
    this.title = title;
    this.cover_img_url = cover_img_url;
    this.source_url = source_url;
    this.id = id;
  }

  public PlayListMeta() {
    
  }

  PlayListMeta(PlayListMeta.PlayListMetaBuilder PlayListMetaBuilder){
    this.title = PlayListMetaBuilder.title;
    this.cover_img_url = PlayListMetaBuilder.cover_img_url;
    this.source_url = PlayListMetaBuilder.source_url;
    this.id = PlayListMetaBuilder.id;
  }

  public static class PlayListMetaBuilder{
    private String title ;
    private  String cover_img_url ;
    private  String source_url ;
    private  String id ;

    public PlayListMeta.PlayListMetaBuilder title(String title){
      this.title = title;
      return this;
    }
    public PlayListMeta.PlayListMetaBuilder imgurl(String cover_img_url){
      this.cover_img_url = cover_img_url;
      return this;
    }
    public PlayListMeta.PlayListMetaBuilder sourceurl(String source_url){
      this.source_url = source_url;
      return this;
    }
    public PlayListMeta.PlayListMetaBuilder id(String id){
      this.id = id;
      return this;
    }

    public PlayListMeta build(){
      return new PlayListMeta(this);
    }
  }
}
