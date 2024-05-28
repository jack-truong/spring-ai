package com.jtruong.ai.chat.dog;

import java.util.Arrays;
import java.util.Optional;

public enum Characteristic {
  Temperament("temperament"),
  Colors("colors"),
  Weight("weight"),
  Lifespan("lifespan"),
  Quirks("quirks"),
  Origin("origin"),
  HealthIssues("health issues"),
  Popularity("popularity"),
  Cost("cost"),
  FunnyStory("funny story")
  ;
  private final String name;

  Characteristic(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public static Optional<Characteristic> fromName(String name) {
    return Arrays.stream(values()).filter(characteristic -> characteristic.name.equals(name)).findFirst();
  }
}
