package com.mini.labour_chain.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientResponseException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.ArrayList;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/api")
public class ChatController {

    private static final Logger log = LoggerFactory.getLogger(ChatController.class);

    @Value("${gemini.api.key:}")
    private String geminiApiKeyFromProps;

    @Value("${gemini.model:}")
    private String geminiModelFromProps;

    private String resolveApiKey() {
        String env = System.getenv("GEMINI_API_KEY");
        if (env != null && !env.isBlank()) return env;
        return geminiApiKeyFromProps;
    }

    private List<String> resolveModelCandidates() {
        if (geminiModelFromProps != null && !geminiModelFromProps.isBlank()) {
            return List.of(geminiModelFromProps);
        }
        // Fallback order covers common availability differences (prefer newest from user's ListModels)
        return Arrays.asList(
                "gemini-2.5-flash",
                "gemini-2.5-pro",
                "gemini-2.5-flash-lite",
                "gemini-2.0-flash",
                "gemini-2.0-flash-001",
                "gemini-2.0-flash-lite",
                "gemini-2.0-flash-lite-001",
                // older fallbacks
                "gemini-1.5-flash",
                "gemini-1.5-flash-latest",
                "gemini-1.0-pro",
                "gemini-pro"
        );
    }

    @PostMapping(value = "/chat", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> chat(@RequestBody Map<String, Object> payload, HttpSession httpSession) {
        String userMessage = String.valueOf(payload.getOrDefault("message", ""));
        String apiKey = resolveApiKey();
        if (apiKey == null || apiKey.isBlank()) {
            return Map.of("reply", "AI chat is not configured. Please set GEMINI_API_KEY.");
        }

        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("temperature", 0.4);
        generationConfig.put("maxOutputTokens", 512);

        String systemHint =
                "You are the Labour Net Assistant for a labour marketplace web app (Spring Boot + Thymeleaf).\n" +
                "Audience: workers and agencies using the site.\n" +
                "Goals: help with registration, login, account approval states (Pending/Approved), job search, applying, dashboards, safety tips.\n" +
                "Important routes: '/' home, '/workers/register', '/workers/login', '/workers/dashboard', '/agencies/register', '/agencies/login', '/agencies/dashboard', '/admin/login'.\n" +
                "Rules:\n- Never expose secrets or API keys.\n- If account status is 'Pending', explain that admin approval is required before login.\n- Password policy: at least 5 chars, include 1 uppercase, 1 lowercase, 1 special (@#$%^&+=!).\n- Emergency contact must be 10 digits.\n- Agencies must have a valid license and may be blocked if blacklisted.\n" +
                "Style: concise, friendly, step-by-step when needed. If a question lacks detail, ask a short follow-up.\n";

        Map<String, Object> body = new HashMap<>();
        Map<String, Object> guidanceContent = new HashMap<>();
        guidanceContent.put("role", "user");
        guidanceContent.put("parts", List.of(Map.of("text", systemHint)));

        // Append short chat history from session for realism (last 6 turns max)
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> history = (List<Map<String, Object>>) httpSession.getAttribute("chatHistory");
        if (history == null) history = new ArrayList<>();

        List<Map<String, Object>> contents = new ArrayList<>();
        contents.add(guidanceContent);
        int start = Math.max(0, history.size() - 6);
        for (int i = start; i < history.size(); i++) {
            contents.add(history.get(i));
        }

        Map<String, Object> userContent = new HashMap<>();
        userContent.put("role", "user");
        userContent.put("parts", List.of(Map.of("text", userMessage)));
        contents.add(userContent);

        body.put("contents", contents);
        body.put("generationConfig", generationConfig);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        RestTemplate rt = new RestTemplate();

        // Try multiple models and both v1 and v1beta endpoints to avoid 404
        for (String model : resolveModelCandidates()) {
            for (String base : List.of("https://generativelanguage.googleapis.com/v1/models/", "https://generativelanguage.googleapis.com/v1beta/models/")) {
                String url = base + model + ":generateContent?key=" + apiKey;
                try {
                    ResponseEntity<Map> resp = rt.exchange(url, HttpMethod.POST, new HttpEntity<>(body, headers), Map.class);
                    Object reply = extractText(resp.getBody());
                    String replyText = reply == null ? "Sorry, I couldn't generate a response." : String.valueOf(reply);

                    // Update session history with the new turn
                    Map<String, Object> userMsg = new HashMap<>();
                    userMsg.put("role", "user");
                    userMsg.put("parts", List.of(Map.of("text", userMessage)));
                    Map<String, Object> botMsg = new HashMap<>();
                    botMsg.put("role", "model");
                    botMsg.put("parts", List.of(Map.of("text", replyText)));
                    history.add(userMsg);
                    history.add(botMsg);
                    if (history.size() > 12) {
                        history = new ArrayList<>(history.subList(history.size() - 12, history.size()));
                    }
                    httpSession.setAttribute("chatHistory", history);

                    return Map.of("reply", replyText);
                } catch (RestClientResponseException httpEx) {
                    if (httpEx.getRawStatusCode() == 404) {
                        log.warn("Model not found at {} - {}", url, model);
                        continue; // try next candidate
                    }
                    String status = httpEx.getRawStatusCode() + " " + httpEx.getStatusText();
                    String bodyText = httpEx.getResponseBodyAsString();
                    log.error("Gemini API error: {} - {}", status, bodyText);
                    String snippet = bodyText == null ? "" : bodyText.substring(0, Math.min(bodyText.length(), 300));
                    return Map.of("reply", "AI service error (" + status + "): " + snippet);
                } catch (Exception ex) {
                    log.error("Gemini call failed", ex);
                    return Map.of("reply", "Error contacting AI service. Please try again later.");
                }
            }
        }
        return Map.of("reply", "AI model is not available with this API key. Please enable Generative Language API or choose a supported model via gemini.model.");
    }

    private String extractText(Map<?, ?> response) {
        if (response == null) return null;
        Object candidates = response.get("candidates");
        if (!(candidates instanceof List<?> cList) || cList.isEmpty()) return null;
        Object first = cList.get(0);
        if (!(first instanceof Map<?, ?> c0)) return null;
        Object content = c0.get("content");
        if (!(content instanceof Map<?, ?> mContent)) return null;
        Object parts = mContent.get("parts");
        if (!(parts instanceof List<?> pList) || pList.isEmpty()) return null;
        Object p0 = pList.get(0);
        if (!(p0 instanceof Map<?, ?> part0)) return null;
        Object text = part0.get("text");
        return text == null ? null : String.valueOf(text);
    }
}
