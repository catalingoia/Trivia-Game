package com.example.joctrivia.model;

public class Question {
    private String answer;
    private boolean answerTrue;

    public Question(){

    }
    public Question(String answer, boolean answerTrue){
        this.answer = answer;
        this.answerTrue = answerTrue;
    }

    public String getAnswer() {
        return answer;
    }

    public boolean isAnswerTrue() {
        return answerTrue;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void setAnswerTrue(boolean answerTrue) {
        this.answerTrue = answerTrue;
    }

    @Override
    public String toString() {
        return "Question{" +
                "answer='" + answer + '\'' +
                ", answerTrue=" + answerTrue +
                '}';
    }
}
