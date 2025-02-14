package service;

import domain.User;
import exceptions.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SystemManager {
    User user = new User();
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
        boolean check = true;
        String folderPath = "D:\\java-estudos\\registration-system\\src\\userDirectory";
        File directory = new File(folderPath);

        if (mainQuestions.isEmpty()) {
            readQuestions();
        }
        while (check) {
            try {
                System.out.println("--- Cadastrando um usuário ---");
                System.out.print(mainQuestions.get(0) + " ");
                String fullName = sc.nextLine();
                if (fullName.length() < 10) {
                    throw new NameSmallerThanExpectedException();
                }

                System.out.print(mainQuestions.get(1) + " ");
                String email = sc.nextLine();
                Pattern pattern = Pattern.compile("@");
                Matcher matcher = pattern.matcher(email);
                if (!matcher.find()) {
                    throw new InvalidEmailFormatException();
                }

                boolean isEqual = false;
                for (User findEmails : users) {
                    isEqual = findEmails.getEmail().equals(email);
                    if (isEqual){
                        throw new EmailAlreadyRegisteredException();
                    }
                }

                    System.out.print(mainQuestions.get(2) + " ");

                int age = sc.nextInt();
                sc.nextLine();

                if (age < 18){
                    throw new YoungerThanThePermittedAgeException();
                }

                System.out.print(mainQuestions.get(3) + " ");
                String strHeight = sc.nextLine();

                if (!strHeight.contains(",")) {
                    throw new InvalidHeightFormatException();
                }

                double height = Double.parseDouble(strHeight.replace(",", "."));

                user = new User(fullName, email, age, height);
                users.add(user);

                check = false;
            } catch (NameSmallerThanExpectedException e) {
                System.out.println("\nTamanho de nome invalido, seu nome deve ter no mínimo 10 caracteres.\n");
            } catch (InvalidEmailFormatException e) {
                System.out.println("\nFormato de email invalido, seu email deve conter o caractere @.\n");
            } catch (InvalidHeightFormatException e) {
                System.out.println("\nFormato de altura invalido, sua altura deve ser informada no seguinte formato: \"1,70\".\n");
            }catch (YoungerThanThePermittedAgeException e) {
                System.out.println("\nVocê não atinge a idade minima para o cadastro, só é permitido o cadastro de usuários com idade maior ou igual a 18.\n");
            }catch (EmailAlreadyRegisteredException e){
                System.out.println("\nNão é possível cadastrar dois ou mais usuários com um mesmo email, por favor informe um email que ainda não foi cadastrado.\n");
            }

        }

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
        System.out.println("\nUsuário cadastrado!\n");


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
        System.out.println("\nNova pergunta adicionada.\n");
    }

    public void deleteNewQuestion() {
        System.out.print("Informe o número da pergunta que desejá excluir: ");
        int choice = sc.nextInt();

        if (choice >= 1 && choice <= 4) {
            System.out.println("Não é possível deletar uma das 4 perguntas originais.");
        } else {
            int numberOfNewQuestion = choice - 5;
            if (numberOfNewQuestion + 1 > newQuestions.size()) {
                System.out.println("Não existe uma pergunta com este número");
            } else {
                System.out.println("Removendo a pergunta: \"" + newQuestions.get(numberOfNewQuestion) + "\"\n");
                newQuestions.remove(numberOfNewQuestion);

            }

        }

    }

    public void searchUser() {
        System.out.println("Opções de pesquisa de usuários:");
        System.out.println("1 - Nome.");
        System.out.println("2 - Idade.");
        System.out.println("3 - Email.");
        System.out.print("Escolha: ");
        int choice = sc.nextInt();
        sc.nextLine();

        switch (choice) {
            case 1 -> {
                System.out.print("Digite o nome ou uma parte do nome do usuário que deseja pesquisar: ");
                String searchString = sc.nextLine().toLowerCase();
                System.out.println("Cadastros: ");
                for (User user : users) {
                    Pattern pattern = Pattern.compile(searchString);
                    Matcher matcher = pattern.matcher(user.getFullName().toLowerCase());
                    while (matcher.find()) {
                        System.out.println(user.getFullName());
                    }
                }
                System.out.println();
            }
            case 2 -> {
                System.out.print("Digite a idade do usuário que deseja pesquisar: ");
                int searchAge = sc.nextInt();
                System.out.println("Cadastros: ");
                for (User user : users) {
                    Pattern pattern = Pattern.compile("\\b" + searchAge + "\\b");
                    Matcher matcher = pattern.matcher(String.valueOf(user.getAge()));
                    while (matcher.find()) {
                        System.out.println(user.getFullName() + " - Idade: " + user.getAge());
                    }
                }
                System.out.println();
            }
            case 3 -> {
                System.out.print("Digite o email ou uma parte do email do usuário que deseja pesquisar: ");
                String searchEmail = sc.nextLine();
                System.out.println("Cadastros: ");
                for (User user : users) {
                    Pattern pattern = Pattern.compile(searchEmail);
                    Matcher matcher = pattern.matcher(user.getEmail().toLowerCase());
                    while (matcher.find()) {
                        System.out.println(user.getFullName() + " - Email: " + user.getEmail());
                    }
                }
                System.out.println();
            }
        }
    }
}

