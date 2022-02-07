package com.repsly.lunchsuggestion.utils;

public enum LocationEnum {
  BOSTON, ZAGREB;

  public String getLocationAddress() {
    return switch (this) {
      case BOSTON -> "55 Summer Street, Boston, MA, USA";
      case ZAGREB -> "Petračićeva ulica 4, Zagreb";
    };
  }
}

