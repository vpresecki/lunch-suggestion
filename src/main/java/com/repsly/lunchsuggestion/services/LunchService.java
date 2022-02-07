package com.repsly.lunchsuggestion.services;

import java.io.IOException;

import com.google.maps.errors.ApiException;

public interface LunchService {

  String findNearbyRestaurants(String officeCity)
      throws IOException, InterruptedException, ApiException;
}
