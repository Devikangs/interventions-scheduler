package dev.ace.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.util.Map;

@Service
public class AiReportService {

    private final RestClient http;
    private final String model;
    private final Boolean enabled;

    public AiReportService(
            @Value("${openai.api.base:https://api.openai.com}") String base,
            @Value("${OPEN_AI_KEY}") String key,
            @Value("${openai.model:gpt-4o-mini}") String model
    ) {
        this.model = model;
        this.enabled = (key != null && !key.isBlank());
        RestClient.Builder b = RestClient.builder().baseUrl(base);
        if (enabled) b = b.defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + key);
        this.http = b.build();
    }

    public String summarizeSchedule(String plainTextSchedule, String constraintsNote){
        if(!enabled) return "AI disabled. Set the OPEN_AI_KEY to enable report generation. \n \n"+plainTextSchedule;

        String prompt = """
          You are helping a CERN accelerator coordination team. Given the interventions schedule (plain text list),
          Write a concise report for the coordination meeting:
          - Group by accelerator and day
          - Highlight conflicts and risk areas
          - Call out skill shortages
          - End with 3 concrete actions
        
          Constraints/notes: %s
        
          Schedule:
          %s
          """.formatted(constraintsNote == null ? "" : constraintsNote, plainTextSchedule);

        var body = Map.of(
                "model", model,
                "messages", new Object[]{
                        Map.of("role","system","content","You are an expert planner for accelerator technical interventions."),
                        Map.of("role","user","content", prompt)
                },
                "temperature", 0.2
        );

        var res = http.post()
                .uri(URI.create("/v1/chat/completions"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .toEntity(Map.class);

        var choices = (java.util.List<Map<String,Object>>)res.getBody().get("choices");
        var msg = (Map<String,Object>)choices.get(0).get("message");
        return (String)msg.get("content");
    }
}
