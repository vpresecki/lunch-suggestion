package com.repsly.lunchsuggestion.mappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.repsly.lunchsuggestion.pojo.Weather;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class WeatherMapper {

  public void map(Weather weather, String weatherResponse) throws JsonProcessingException {
    JsonNode jsonNode = new ObjectMapper().readTree(weatherResponse);

    weather.setTitle(jsonNode.get("weather").get(0).get("main").asText());
    weather.setDescription(jsonNode.get("weather").get(0).get("description").asText());
    weather.setTemperature(jsonNode.get("main").get("temp").asText());
  }
}
