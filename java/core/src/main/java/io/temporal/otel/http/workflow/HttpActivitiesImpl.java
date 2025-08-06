package io.temporal.otel.http.workflow;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpActivitiesImpl implements HttpActivities {
  private static final Logger log = LoggerFactory.getLogger(HttpActivitiesImpl.class);

  @Override
  public String httpGet(String url) {
    log.info("Activity: making HTTP GET call to {}", url);

    try {
      HttpClient client = HttpClient.newHttpClient();
      HttpRequest request =
          HttpRequest.newBuilder()
              .uri(URI.create(url))
              .GET()
              .timeout(Duration.ofSeconds(10))
              .build();

      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
      return response.body();

    } catch (IOException | InterruptedException e) {
      log.error("Error making HTTP GET request to {}: {}", url, e.getMessage());
      throw new RuntimeException("Failed to make HTTP GET request", e);
    }
  }
}
