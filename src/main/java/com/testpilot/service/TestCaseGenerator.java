package com.testpilot.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.testpilot.model.Requirement;
import com.testpilot.model.TestCase;
import com.testpilot.util.OllamaClient;

import java.io.IOException;
import java.net.ConnectException;
import java.net.http.HttpTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Asks the LLM for test cases of one requirement and turns the answer into TestCase objects. */
public class TestCaseGenerator {
    private static final int MAX_CASES = 8;

    public static List<TestCase> generate(Requirement r) throws AiException {
        String raw;
        try {
            raw = OllamaClient.generate(buildPrompt(r));
        } catch (ConnectException e) {
            throw new AiException("Cannot reach the AI server (Ollama). Make sure Ollama is running and try again.");
        } catch (HttpTimeoutException e) {
            throw new AiException("The AI took too long to answer. Close other programs and try again.");
        } catch (IOException e) {
            throw new AiException("Could not get an answer from the AI server: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AiException("The request was interrupted. Please try again.");
        }
        return parse(raw, r.getId());
    }

    static String buildPrompt(Requirement r) {
        return "You are an experienced software test engineer.\n"
             + "Write test cases for the requirement below.\n\n"
             + "Return ONLY valid JSON in exactly this format:\n"
             + "{\"test_cases\":[{\"title\":\"...\",\"steps\":[\"step 1\",\"step 2\"],\"expected_result\":\"...\"}]}\n\n"
             + "Rules:\n"
             + "- Write 5 test cases.\n"
             + "- Include positive, negative and edge cases.\n"
             + "- Keep every title under 100 characters and every step short.\n"
             + "- Only test what the requirement says. Do not invent features.\n"
             + "- No text outside the JSON.\n\n"
             + "Requirement title: " + r.getTitle() + "\n"
             + "Description: " + (r.getDescription() == null ? "" : r.getDescription()) + "\n"
             + "Priority: " + r.getPriority() + "\n";
    }

    static List<TestCase> parse(String raw, int requirementId) throws AiException {
        JsonElement root;
        try {
            root = JsonParser.parseString(raw);
        } catch (JsonParseException e) {
            throw new AiException("The AI returned an answer in the wrong format. Please try again.");
        }

        JsonArray array = findArray(root);
        if (array == null) {
            throw new AiException("The AI returned an answer in the wrong format. Please try again.");
        }

        List<TestCase> result = new ArrayList<>();
        for (JsonElement el : array) {
            if (!el.isJsonObject() || result.size() >= MAX_CASES) {
                continue;
            }
            JsonObject o = el.getAsJsonObject();
            String title = limit(text(o, "title", "name"), 200);
            if (title.isEmpty()) {
                continue;
            }
            TestCase tc = new TestCase();
            tc.setRequirementId(requirementId);
            tc.setTitle(title);
            tc.setSteps(limit(text(o, "steps", "test_steps"), 5000));
            tc.setExpectedResult(limit(text(o, "expected_result", "expected", "expectedResult"), 5000));
            tc.setSource("AI");
            result.add(tc);
        }

        if (result.isEmpty()) {
            throw new AiException("The AI did not return any usable test cases. Please try again.");
        }
        return result;
    }

    private static JsonArray findArray(JsonElement root) {
        if (root.isJsonArray()) {
            return root.getAsJsonArray();
        }
        if (root.isJsonObject()) {
            JsonObject obj = root.getAsJsonObject();
            if (obj.has("test_cases") && obj.get("test_cases").isJsonArray()) {
                return obj.get("test_cases").getAsJsonArray();
            }
            for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                if (entry.getValue().isJsonArray()) {
                    return entry.getValue().getAsJsonArray();
                }
            }
        }
        return null;
    }

    /** Reads the first key that exists. Lists (like steps) become numbered lines. */
    private static String text(JsonObject o, String... keys) {
        for (String key : keys) {
            if (!o.has(key) || o.get(key).isJsonNull()) {
                continue;
            }
            JsonElement e = o.get(key);
            if (e.isJsonPrimitive()) {
                return e.getAsString().trim();
            }
            if (e.isJsonArray()) {
                StringBuilder sb = new StringBuilder();
                int n = 1;
                for (JsonElement item : e.getAsJsonArray()) {
                    String line = item.isJsonPrimitive() ? item.getAsString().trim() : item.toString();
                    if (line.isEmpty()) {
                        continue;
                    }
                    if (sb.length() > 0) {
                        sb.append('\n');
                    }
                    sb.append(n++).append(". ").append(line);
                }
                return sb.toString();
            }
            return e.toString();
        }
        return "";
    }

    private static String limit(String s, int max) {
        return s.length() > max ? s.substring(0, max) : s;
    }
}
