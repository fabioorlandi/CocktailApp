package com.example.cocktailapp.model;

public class Ingredient {

    private Long id;
    private String name;
    private String description;
    private IngredientType ingredientType;
    private Boolean isAlcoholic;
    private Quantity quantity;
    private Integer ABV;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public IngredientType getIngredientType() {
        return ingredientType;
    }

    public void setIngredientType(IngredientType ingredientType) {
        this.ingredientType = ingredientType;
    }

    public Boolean getAlcoholic() {
        return isAlcoholic;
    }

    public void setAlcoholic(Boolean alcoholic) {
        isAlcoholic = alcoholic;
    }

    public Quantity getQuantity() {
        return quantity;
    }

    public void setQuantity(Quantity quantity) {
        this.quantity = quantity;
    }

    public Integer getABV() {
        return ABV;
    }

    public void setABV(Integer ABV) {
        this.ABV = ABV;
    }
}
