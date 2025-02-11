package test;

import domain.User;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String pathFormTxt = "D:\\java-estudos\\registration-system\\src\\recurso\\formulario.txt";
        User user;
        List<User> users = new ArrayList<>();

        List<String> mainQuastions = new ArrayList<>();
        try (BufferedReader bReader = new BufferedReader(new FileReader(pathFormTxt))) {
            String line;
            while ((line = bReader.readLine()) != null) {
                mainQuastions.add(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("--- Cadastrando um usuário ---");
        System.out.print(mainQuastions.get(0) + " ");
        String fullName = sc.nextLine();

        System.out.print(mainQuastions.get(1) + " ");
        String email = sc.nextLine();

        System.out.print(mainQuastions.get(2) + " ");
        int age = sc.nextInt();

        System.out.print(mainQuastions.get(3) + " ");
        double height = sc.nextDouble();

        user = new User(fullName, email, age, height);
        users.add(user);

        System.out.println(users.get(0));

        sc.close();
    }
}
