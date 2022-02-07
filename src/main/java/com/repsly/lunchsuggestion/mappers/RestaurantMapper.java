package com.repsly.lunchsuggestion.mappers;

import com.google.maps.model.PlacesSearchResponse;
import com.repsly.lunchsuggestion.pojo.Restaurant;
import com.repsly.lunchsuggestion.pojo.Weather;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class RestaurantMapper {

  public void map(Restaurant restaurant, PlacesSearchResponse response, Weather weather) {
    restaurant.setName(response.results[0].name);
    restaurant.setRating(response.results[0].rating);
    restaurant.setWeather(weather);
  }
}
