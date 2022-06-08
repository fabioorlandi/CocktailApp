package com.example.cocktailapp.model.base;

public enum CocktailActivityResult {
    UPDATE(1), DELETE(2), SAVE(4);

    private final int value;

    private CocktailActivityResult(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
