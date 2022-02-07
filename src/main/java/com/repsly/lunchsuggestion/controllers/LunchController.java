package com.repsly.lunchsuggestion.controllers;

import java.io.IOException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.google.maps.errors.ApiException;
import com.repsly.lunchsuggestion.services.LunchService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class LunchController {

  private final LunchService lunchService;

  @GetMapping("/{cityName}")
  public String getLunchSuggestion(@PathVariable String cityName)
      throws IOException, InterruptedException, ApiException {
    return "Please find lunch suggestion on this link: "
        + lunchService.findNearbyRestaurants(cityName);
  }
}
