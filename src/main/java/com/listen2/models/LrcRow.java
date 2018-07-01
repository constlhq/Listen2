package com.listen2.models;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LrcRow {
  public String content;
  public int milliseconds;
  public int rowIndex;

  public LrcRow(String lyricrow,int rowIndex) {
    this.rowIndex = rowIndex;
    Pattern pattern = Pattern.compile("\\[(\\d+):(\\d+\\.\\d+)](.*)");
    Matcher m = pattern.matcher(lyricrow);
    if(m.find()) {
     milliseconds =  Integer.parseInt(m.group(1))*60000 + (int)(Double.parseDouble(m.group(2))*1000);
     content = m.group(3);
    }
  }
}
