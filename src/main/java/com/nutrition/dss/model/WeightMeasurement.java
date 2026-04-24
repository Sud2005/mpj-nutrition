package com.nutrition.dss.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "weight_measurements")
public class WeightMeasurement {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private LocalDateTime measuredAt;
    private double heightCm;
    private double weightKg;
    private double bmi;
    private String source;

    public WeightMeasurement() {
        this.measuredAt = LocalDateTime.now();
    }

    public WeightMeasurement(User user, double heightCm, double weightKg, double bmi, String source) {
        this.user = user;
        this.heightCm = heightCm;
        this.weightKg = weightKg;
        this.bmi = bmi;
        this.source = source;
        this.measuredAt = LocalDateTime.now();
    }

    public String getFormattedMeasuredAt() {
        if (measuredAt == null) return "Unknown";
        return measuredAt.format(DATE_FORMATTER);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public LocalDateTime getMeasuredAt() { return measuredAt; }
    public void setMeasuredAt(LocalDateTime measuredAt) { this.measuredAt = measuredAt; }

    public double getHeightCm() { return heightCm; }
    public void setHeightCm(double heightCm) { this.heightCm = heightCm; }

    public double getWeightKg() { return weightKg; }
    public void setWeightKg(double weightKg) { this.weightKg = weightKg; }

    public double getBmi() { return bmi; }
    public void setBmi(double bmi) { this.bmi = bmi; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
}
