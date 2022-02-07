package com.repsly.lunchsuggestion.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Weather {

  private String title;
  private String description;
  private String temperature;
}
