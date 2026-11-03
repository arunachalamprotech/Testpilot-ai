package com.testpilot.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/** Talks to the local Ollama server (http://localhost:11434). */
public class OllamaClient {
    private static final String URL = "http://localhost:11434/api/generate";
    private static final String MODEL = "qwen2.5-coder:3b";

    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    public static String generate(String prompt) throws IOException, InterruptedException {
        JsonObject options = new JsonObject();
        options.addProperty("temperature", 0.4);
        options.addProperty("num_predict", 1200);

        JsonObject body = new JsonObject();
        body.addProperty("model", MODEL);
        body.addProperty("prompt", prompt);
        body.addProperty("stream", false);
        body.addProperty("format", "json");
        body.add("options", options);

        HttpRequest request = HttpRequest.newBuilder(URI.create(URL))
                .timeout(Duration.ofSeconds(300))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();

        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IOException("Ollama returned HTTP " + response.statusCode());
        }
        return JsonParser.parseString(response.body()).getAsJsonObject().get("response").getAsString();
    }
}
