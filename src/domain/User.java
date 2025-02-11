package domain;

public class User {
    private String fullName;
    private String email;
    private int age;
    private double height;

    public User() {
    }

    public User(String fullName, String email, int age, double height) {
        this.fullName = fullName;
        this.email = email;
        this.age = age;
        this.height = height;
    }

    @Override
    public String toString() {
        return "Nome completo: " + this.fullName +
                "\nE-mail: " + this.email +
                "\nIdade: " + this.age +
                "\nAltura: " + this.height;
    }
}
