package domain;

import java.util.List;

public class User {
    private String fullName;
    private String email;
    private int age;
    private double height;
    private List<String> newAnswer;
    int userId;

    public User() {
    }

    public User(String fullName, String email, int age, double height, int userId) {
        this.fullName = fullName;
        this.email = email;
        this.age = age;
        this.height = height;
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Nome completo: " + this.fullName +
                "\nE-mail: " + this.email +
                "\nIdade: " + this.age +
                "\nAltura: " + this.height;
    }


    public int getUserId() {
        return userId;
    }

    public double getHeight() {
        return height;
    }

    public int getAge() {
        return age;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }

    public List<String> getNewAnswer() {
        return newAnswer;
    }

    public void setNewAnswer(List<String> newAnswer) {
        this.newAnswer = newAnswer;
    }
}
