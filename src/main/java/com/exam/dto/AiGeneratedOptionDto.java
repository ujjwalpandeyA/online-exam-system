package com.exam.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AiGeneratedOptionDto {

    private String optionText;

    @JsonProperty("isCorrect")
    @JsonAlias({"correct", "is_correct"})
    private boolean isCorrect;

    public AiGeneratedOptionDto() {
    }

    public AiGeneratedOptionDto(String optionText, boolean isCorrect) {
        this.optionText = optionText;
        this.isCorrect = isCorrect;
    }

    public String getOptionText() {
        return optionText;
    }

    public void setOptionText(String optionText) {
        this.optionText = optionText;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        this.isCorrect = correct;
    }
}