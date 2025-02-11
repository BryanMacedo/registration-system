package test;

import domain.User;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String pathFormTxt = "D:\\java-estudos\\registration-system\\src\\resource\\formulario.txt";
        List<User> users = new ArrayList<>();

        List<String> mainQuestions = new ArrayList<>();
        try (BufferedReader bReader = new BufferedReader(new FileReader(pathFormTxt))) {
            String line;
            while ((line = bReader.readLine()) != null) {
                mainQuestions.add(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("--- Cadastrando um usuário ---");
        System.out.print(mainQuestions.get(0) + " ");
        String fullName = sc.nextLine();

        System.out.print(mainQuestions.get(1) + " ");
        String email = sc.nextLine();

        System.out.print(mainQuestions.get(2) + " ");
        int age = sc.nextInt();

        System.out.print(mainQuestions.get(3) + " ");
        double height = sc.nextDouble();

        User user = new User(fullName, email, age, height);
        users.add(user);

        System.out.println(users.get(0));

        String fileName = ("1-"+users.get(0).getFullName().toUpperCase()+".txt").replaceAll(" ", "");
        String folderPath = "D:\\java-estudos\\registration-system\\src\\userDirectory";String filePath = folderPath + fileName;

        File directory = new File(folderPath);
        if (!directory.exists()){
            directory.mkdir();
        }

        File userFile = new File(folderPath, fileName);

        try (BufferedWriter bWriter = new BufferedWriter(new FileWriter(userFile))){
            bWriter.write(users.get(0).getFullName());
            bWriter.newLine();
            bWriter.write(users.get(0).getEmail());
            bWriter.newLine();
            bWriter.write(String.valueOf(users.get(0).getAge()));
            bWriter.newLine();
            bWriter.write(String.valueOf(users.get(0).getHeight()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        sc.close();
    }
}
