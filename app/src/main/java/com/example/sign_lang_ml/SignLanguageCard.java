package com.example.sign_lang_ml;
public class SignLanguageCard {
    private String letter;
    private int imageResourceId;

    public SignLanguageCard(String letter, int imageResourceId) {
        this.letter = letter;
        this.imageResourceId = imageResourceId;
    }

    public String getLetter() {
        return letter;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }
}