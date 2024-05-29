package com.jtruong.ai.chat;

public enum Color {
  Red("red"),
  Orange("orange"),
  Yellow("yellow"),
  Green("green"),
  Blue("blue"),
  Indigo("indigo"),
  Violet("violet"),
  Rainbow("rainbow"),
  Striped("striped"),
  Camouflage("camouflage"),
  Translucent("translucent"),
  ;
  private final String name;

  Color(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
