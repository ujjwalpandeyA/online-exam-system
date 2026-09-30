package com.exam.dto;

public class OptionRequestDto {

    private String optionText;
    private boolean isCorrect;

    public OptionRequestDto() {
    }

    public OptionRequestDto(String optionText, boolean isCorrect) {
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
        isCorrect = correct;
    }
}