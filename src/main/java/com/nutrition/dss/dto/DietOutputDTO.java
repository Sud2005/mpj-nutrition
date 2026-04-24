package com.nutrition.dss.dto;

import java.util.List;

/**
 * Structured diet output: { recommended, limited, avoid }
 * Each category contains a list of FoodItemDTOs.
 */
public class DietOutputDTO {

    private List<FoodItemDTO> recommended;
    private List<FoodItemDTO> limited;
    private List<FoodItemDTO> avoid;
    private String llmRefinement; // Optional LLM-generated notes

    public DietOutputDTO() {}

    public DietOutputDTO(List<FoodItemDTO> recommended, List<FoodItemDTO> limited,
                         List<FoodItemDTO> avoid) {
        this.recommended = recommended;
        this.limited = limited;
        this.avoid = avoid;
    }

    public List<FoodItemDTO> getRecommended() { return recommended; }
    public void setRecommended(List<FoodItemDTO> recommended) { this.recommended = recommended; }

    public List<FoodItemDTO> getLimited() { return limited; }
    public void setLimited(List<FoodItemDTO> limited) { this.limited = limited; }

    public List<FoodItemDTO> getAvoid() { return avoid; }
    public void setAvoid(List<FoodItemDTO> avoid) { this.avoid = avoid; }

    public String getLlmRefinement() { return llmRefinement; }
    public void setLlmRefinement(String llmRefinement) { this.llmRefinement = llmRefinement; }
}
