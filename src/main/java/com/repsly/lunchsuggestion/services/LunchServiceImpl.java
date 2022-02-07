package com.repsly.lunchsuggestion.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.Identity;
import com.google.cloud.Policy;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.storage.StorageRoles;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.NearbySearchRequest;
import com.google.maps.PlacesApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlaceType;
import com.google.maps.model.PlacesSearchResponse;
import com.repsly.lunchsuggestion.mappers.RestaurantMapper;
import com.repsly.lunchsuggestion.mappers.WeatherMapper;
import com.repsly.lunchsuggestion.pojo.Restaurant;
import com.repsly.lunchsuggestion.pojo.Weather;
import com.repsly.lunchsuggestion.utils.LocationEnum;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LunchServiceImpl implements LunchService {

  @Value("${GOOGLE_API_KEY}")
  private String googleApiKey;

  @Value("${OPEN_WEATHER_API}")
  private String openWeatherApi;

  @Value("${GOOGLE_BUCKET_NAME}")
  private String googleBucketName;

  @Value("${GOOGLE_PROJECT_ID}")
  private String googleProjectId;

  @Override
  public String findNearbyRestaurants(String officeCity)
      throws IOException, InterruptedException, ApiException {
    GeoApiContext context = new GeoApiContext.Builder().apiKey(googleApiKey).build();

    // define office
    String address =
        officeCity.equalsIgnoreCase(LocationEnum.BOSTON.name())
            ? LocationEnum.BOSTON.getLocationAddress()
            : LocationEnum.ZAGREB.getLocationAddress();
    log.info("Sending geo request for:{}", address);
    // get LatLng for required location
    GeocodingResult[] results = GeocodingApi.geocode(context, address).await();
    LatLng location = results[0].geometry.location;

    // find nearby restaurant
    NearbySearchRequest nearbySearchRequest =
        PlacesApi.nearbySearchQuery(context, location).radius(1500).type(PlaceType.RESTAURANT);
    PlacesSearchResponse response = nearbySearchRequest.await();
    log.info("Found restaurant {}", response.results[0].name);

    // get weather information
    log.info("Fetching weather data for nearby area...");
    String weatherResponse = getWeatherResponse(location);

    // map data
    Weather weather = new Weather();
    new WeatherMapper().map(weather, weatherResponse);
    Restaurant restaurant = new Restaurant();
    new RestaurantMapper().map(restaurant, response, weather);

    context.shutdown();

    // Create csv file
    File file = toCSVFile(restaurant);
    // Create public file
    log.info("Sending data to storage.");
    BlobInfo blobInfo = addDataToBucket(file);

    // Return link to the public file
    return blobInfo.getMediaLink();
  }

  private String getWeatherResponse(LatLng location) {
    RestTemplate restTemplate = new RestTemplate();
    URI uri =
        URI.create(
                "https://api.openweathermap.org/data/2.5/weather?lat="
                    + location.lat
                    + "&lon="
                    + location.lng
                    + "&units=metric&appid="
                    + openWeatherApi
                    + "")
            .normalize();

    return restTemplate.getForObject(uri, String.class);
  }

  private BlobInfo addDataToBucket(File file) throws IOException {
    // Authenticate
    GoogleCredentials credentials =
        GoogleCredentials.fromStream(new FileInputStream("src/main/java/places-credentials.json"));

    // Initialize storage
    Storage storage =
        StorageOptions.newBuilder()
            .setCredentials(credentials)
            .setProjectId(googleProjectId)
            .build()
            .getService();

    // Make file public
    Policy originalPolicy = storage.getIamPolicy(googleBucketName);
    storage.setIamPolicy(
        googleBucketName,
        originalPolicy.toBuilder()
            .addIdentity(StorageRoles.objectViewer(), Identity.allUsers())
            .build());

    BlobId blobId = BlobId.of(googleBucketName, "repsly-lunch");
    BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
    return storage.create(blobInfo, Files.readAllBytes(file.toPath()));
  }

  private File toCSVFile(Restaurant restaurant) throws IOException {
    final String COMMA_DELIMITER = ",";
    final String NEW_LINE_SEPARATOR = "\n";
    final String CSV_HEADERS =
        "restaurantName,rating,weather,weatherDescription,weatherTemperature";

    try (FileWriter fileWriter = new FileWriter("restaurant.csv")) {
      fileWriter.append(CSV_HEADERS);
      fileWriter.append(NEW_LINE_SEPARATOR);
      fileWriter.append(restaurant.getName());
      fileWriter.append(COMMA_DELIMITER);
      fileWriter.append(String.valueOf(restaurant.getRating()));
      fileWriter.append(COMMA_DELIMITER);
      fileWriter.append(restaurant.getWeather().getTitle());
      fileWriter.append(COMMA_DELIMITER);
      fileWriter.append(restaurant.getWeather().getDescription());
      fileWriter.append(COMMA_DELIMITER);
      fileWriter.append(restaurant.getWeather().getTemperature());
      fileWriter.flush();
    }
    return new File("restaurant.csv");
  }
}
