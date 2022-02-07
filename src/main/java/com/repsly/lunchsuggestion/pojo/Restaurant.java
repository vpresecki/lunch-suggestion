package com.repsly.lunchsuggestion.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Restaurant {

  private String name;
  private float rating;
  private Weather weather;
}
