package com.nutrition.dss.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/** Configuration properties for Groq LLM API */
@Configuration
@ConfigurationProperties(prefix = "groq")
public class GroqConfig {

    private String apiKey = "not-configured";
    private String model = "llama-3.1-8b-instant";
    private String baseUrl = "https://api.groq.com/openai/v1";
    private double temperature = 0.7;
    private boolean enabled = true;

    public boolean isConfigured() {
        return enabled && apiKey != null && !apiKey.equals("not-configured") && !apiKey.isBlank();
    }

    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }

    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}
