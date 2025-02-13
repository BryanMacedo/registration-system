package service;

import domain.User;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SystemManager {
    List<String> newQuestions = new ArrayList<>();

    private List<User> users = new ArrayList<>();
    private List<String> mainQuestions = new ArrayList<>();

    Scanner sc = new Scanner(System.in);

    public void readQuestions() {
        String pathFormTxt = "D:\\java-estudos\\registration-system\\src\\resource\\formulario.txt";

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
    }

    public void registerUser() {
        String folderPath = "D:\\java-estudos\\registration-system\\src\\userDirectory";
        File directory = new File(folderPath);

        if (mainQuestions.isEmpty()) {
            readQuestions();
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
        sc.nextLine();

        User user = new User(fullName, email, age, height);
        users.add(user);

        if (!newQuestions.isEmpty()) {
            int count = 5;
            List<String> answers = new ArrayList<>();
            for (String newQuestion : newQuestions) {
                System.out.print(count + " - " + newQuestion + " ");
                String answer = sc.nextLine();
                answers.add(answer);
                count++;
            }
            user.setNewAnswer(answers);
        }
        System.out.println();


        if (!directory.exists()) {
            directory.mkdir();

        }

        // verifica a quantidade de arquivos txt
        File[] verifyFiles = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".txt"));
        int quantityFile = (verifyFiles != null) ? verifyFiles.length : 0;

        String fileName = (quantityFile + 1 + "-" + users.get(quantityFile).getFullName().toUpperCase() + ".txt").replaceAll(" ", "");

        File userFile = new File(folderPath, fileName);

        try (BufferedWriter bWriter = new BufferedWriter(new FileWriter(userFile))) {
            bWriter.write(users.get(quantityFile).getFullName());
            bWriter.newLine();
            bWriter.write(users.get(quantityFile).getEmail());
            bWriter.newLine();
            bWriter.write(String.valueOf(users.get(quantityFile).getAge()));
            bWriter.newLine();
            bWriter.write(String.valueOf(users.get(quantityFile).getHeight()));
            if (!newQuestions.isEmpty()) {
                for (String answer : user.getNewAnswer()) {
                    bWriter.newLine();
                    bWriter.write(answer);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void verifyUsers() {
        String folderPath = "D:\\java-estudos\\registration-system\\src\\userDirectory";
        File directory = new File(folderPath);

        // verifica se já tem usuários cadastrados e caso tenha adiciona ele no List
        if (directory.exists() && directory.isDirectory()) {
            File[] txtFiles = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".txt"));
            if (txtFiles != null && txtFiles.length > 0) {
                for (int i = 0; i < txtFiles.length; i++) {
                    try (BufferedReader bReader = new BufferedReader(new FileReader(txtFiles[i]))) {
                        String line01 = bReader.readLine();
                        String line02 = bReader.readLine();
                        String line03 = bReader.readLine();
                        String line04 = bReader.readLine();

                        String fullName = line01;
                        String email = line02;
                        int age = Integer.parseInt(line03);
                        double height = Double.parseDouble(line04);

                        User user = new User(fullName, email, age, height);
                        users.add(user);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void listUsers() {
        for (int i = 0; i < users.size(); i++) {
            System.out.println(i + 1 + "-" + users.get(i).getFullName() + "\n");
        }
    }

    public void newQuestion() {
        System.out.println("Digite uma nova pergunta:");
        String newUserQuestion = sc.nextLine();

        newQuestions.add(newUserQuestion);
    }

    public void deleteNewQuestion() {
        System.out.print("Informe o número da pergunta que desejá excluir: ");
        int choice = sc.nextInt();

        if (choice >= 1 && choice <= 4) {
            System.out.println("Não é possível deletar uma das 4 perguntas originais.");
        } else {
            int numberOfNewQuestion = choice - 5;
            if (numberOfNewQuestion + 1 != newQuestions.size()) {
                System.out.println("Não existe uma pergunta com este número");
            } else {
                System.out.println("Removendo a perfunta: " + newQuestions.get(numberOfNewQuestion));
                newQuestions.remove(numberOfNewQuestion);

            }

        }

    }
}

