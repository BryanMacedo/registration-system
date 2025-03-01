package service;

import db.DB;
import db.DbException;
import domain.User;
import exceptions.*;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
    private List<String> mainQuestionsDB = new ArrayList<>();

    Scanner sc = new Scanner(System.in);

    public void readQuestions() {
        Connection conn = null;
        PreparedStatement st = null;

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

        try {
            conn = DB.getConnection();
            st = conn.prepareStatement("INSERT INTO questions " +
                    "(Question) " +
                    "VALUES " +
                    "(?)");

            for (int i = 0; i < mainQuestions.size(); i++) {
                st.setString(1, mainQuestions.get(i));
                st.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
        }
    }

    public void registerUser() {
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;

        boolean check = true;
        boolean checkTable = false;

        String folderPath = "D:\\java-estudos\\registration-system\\src\\userDirectory";
        File directory = new File(folderPath);

        try {
            conn = DB.getConnection();
            st = conn.prepareStatement("SELECT EXISTS (SELECT 1 FROM questions LIMIT 1)");
            rs = st.executeQuery();
            if (rs.next()) {
                checkTable = rs.getBoolean(1);
            }
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
        }

        if (!checkTable) {
            readQuestions();
        }

        if (mainQuestionsDB.isEmpty()) {
            try {
                conn = DB.getConnection();
                st = conn.prepareStatement("SELECT * FROM questions");
                rs = st.executeQuery();

                while (rs.next()) {
                    mainQuestionsDB.add(rs.getString("Question"));
                }
            } catch (SQLException e) {
                throw new DbException(e.getMessage());
            }
        }

        while (check) {
            try {
                System.out.println("--- Cadastrando um usuário ---");
                System.out.print(mainQuestionsDB.get(0) + " ");
                String fullName = sc.nextLine();
                if (fullName.length() < 10) {
                    throw new NameSmallerThanExpectedException();
                }

                System.out.print(mainQuestionsDB.get(1) + " ");
                String email = sc.nextLine();
                Pattern pattern = Pattern.compile("@");
                Matcher matcher = pattern.matcher(email);
                if (!matcher.find()) {
                    throw new InvalidEmailFormatException();
                }

                boolean isEqual = false;
//                for (User findEmails : users) {
//                    isEqual = findEmails.getEmail().equals(email);
//                    if (isEqual){
//                        throw new EmailAlreadyRegisteredException();
//                    }
//                }

                System.out.print(mainQuestionsDB.get(2) + " ");

                int age = sc.nextInt();
                sc.nextLine();

                if (age < 18) {
                    throw new YoungerThanThePermittedAgeException();
                }

                System.out.print(mainQuestionsDB.get(3) + " ");
                String strHeight = sc.nextLine();

                if (!strHeight.contains(",")) {
                    throw new InvalidHeightFormatException();
                }

                double height = Double.parseDouble(strHeight.replace(",", "."));

                try {
                    conn = DB.getConnection();
                    st = conn.prepareStatement("INSERT INTO users " +
                            "(FullName, Email, Age, Height) " +
                            "VALUES " +
                            "(?, ?, ?, ?)");

                    st.setString(1, fullName);
                    st.setString(2, email);
                    st.setInt(3, age);
                    st.setDouble(4, height);

                    st.executeUpdate();
                } catch (SQLException e) {
                    throw new DbException(e.getMessage());
                } finally {
                    DB.closeStatement(st);
                }

                check = false;
            } catch (NameSmallerThanExpectedException e) {
                System.out.println("\nTamanho de nome invalido, seu nome deve ter no mínimo 10 caracteres.\n");
            } catch (InvalidEmailFormatException e) {
                System.out.println("\nFormato de email invalido, seu email deve conter o caractere @.\n");
            } catch (InvalidHeightFormatException e) {
                System.out.println("\nFormato de altura invalido, sua altura deve ser informada no seguinte formato: \"1,70\".\n");
            } catch (YoungerThanThePermittedAgeException e) {
                System.out.println("\nVocê não atinge a idade minima para o cadastro, só é permitido o cadastro de usuários com idade maior ou igual a 18.\n");
            } catch (EmailAlreadyRegisteredException e) {
                System.out.println("\nNão é possível cadastrar dois ou mais usuários com um mesmo email, por favor informe um email que ainda não foi cadastrado.\n");
            }

        }

//        if (!newQuestions.isEmpty()) {
//            int count = 5;
//            List<String> answers = new ArrayList<>();
//            for (String newQuestion : newQuestions) {
//                System.out.print(count + " - " + newQuestion + " ");
//                String answer = sc.nextLine();
//                answers.add(answer);
//                count++;
//            }
//            user.setNewAnswer(answers);
//        } MUDAR A LOGICA DAS PERGUNTAS ADICIONAIS


        System.out.println("\nUsuário cadastrado!\n");
    }

//    public void verifyUsers() {
//        String folderPath = "D:\\java-estudos\\registration-system\\src\\userDirectory";
//        File directory = new File(folderPath);
//
//        // verifica se já tem usuários cadastrados e caso tenha adiciona ele no List
//        if (directory.exists() && directory.isDirectory()) {
//            File[] txtFiles = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".txt"));
//            if (txtFiles != null && txtFiles.length > 0) {
//                for (int i = 0; i < txtFiles.length; i++) {
//                    try (BufferedReader bReader = new BufferedReader(new FileReader(txtFiles[i]))) {
//                        String line01 = bReader.readLine();
//                        String line02 = bReader.readLine();
//                        String line03 = bReader.readLine();
//                        String line04 = bReader.readLine();
//
//                        String fullName = line01;
//                        String email = line02;
//                        int age = Integer.parseInt(line03);
//                        double height = Double.parseDouble(line04);
//
//                        User user = new User(fullName, email, age, height);
//                        users.add(user);
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
//    }

    public void listUsers() {
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            conn = DB.getConnection();
            st = conn.prepareStatement("SELECT * FROM users");
            rs = st.executeQuery();

            while (rs.next()) {
                String fullName = rs.getString("FullName");
                String email = rs.getString("Email");
                int age = rs.getInt("Age");
                double height = rs.getDouble("Height");

                User userDB = new User(fullName, email, age, height);
                users.add(userDB);


            }

            for (User user : users) {
                System.out.println(user.getFullName());
                System.out.println(user.getEmail());
                System.out.println(user.getAge());
                System.out.println(user.getHeight());
                System.out.println("-------------------------------");
            }

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
    }

    public void newQuestion() {
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;

        try{
            conn = DB.getConnection();
            st = conn.prepareStatement("SELECT COUNT(*) FROM additional_questions");
            rs = st.executeQuery();

            int countLine = 0;
            if (rs.next()){
                countLine = rs.getInt(1);
            }

            if (countLine >= 3){
                // criar uma exception especifica
                throw new DbException("Limite de perguntas cadastradas atingida, exclua uma pergunta para cadastrar outra.");
            }
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
        }

        System.out.println("Digite uma nova pergunta:");
        String newUserQuestion = sc.nextLine();


        try {
            conn = DB.getConnection();
            st = conn.prepareStatement("INSERT INTO additional_questions " +
                    "(Users_questions) " +
                    "VALUES " +
                    "(?)");
            st.setString(1, newUserQuestion);
            st.executeUpdate();
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
        }
    }

   public void deleteNewQuestion() {
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;

        System.out.println("Listando as perguntas existentes: ");

        // perguntas principais
        try {
            conn = DB.getConnection();
            st = conn.prepareStatement("SELECT * FROM questions");
            rs = st.executeQuery();

            while (rs.next()) {
                String question = rs.getString("Main_Questions");
                System.out.println(question);
            }

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
        }

        //perguntas adicionais
        try {
            conn = DB.getConnection();
            st = conn.prepareStatement("SELECT * FROM additional_questions ");
            rs = st.executeQuery();

            while (rs.next()){
                newQuestions.add(rs.getString("Users_questions"));
            }

            int lineCounter = 5;
            for (String newQuestion : newQuestions) {
                System.out.println(lineCounter + " - " + newQuestion);
                lineCounter++;
            }
        }catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
        }

        System.out.print("\nDigite o número da pergunta que deseja excluir: ");
        int choice = sc.nextInt();

        if (choice >= 1 && choice <= 4) {
            //criar uma exception especifica
            throw new DbException("Não é possível excluir uma das 4 perguntas originais.");
        }else{
            choice -= 4; // subtrai o número de perguntas principais
            choice -= 1; // subtrai 1 pq o offset começa em 0

            try {
                conn =DB.getConnection();
                st = conn.prepareStatement("SELECT Id FROM additional_questions ORDER BY Id LIMIT 1 OFFSET ?");
                st.setInt(1,choice);
                rs = st.executeQuery();

                int idToDelete = 0;
                if (rs.next()){
                    idToDelete = rs.getInt("Id");
                }

                st = conn.prepareStatement("DELETE FROM additional_questions WHERE Id = ?");
                st.setInt(1, idToDelete);
                st.executeUpdate();
            }catch (SQLException e) {
                throw new DbException(e.getMessage());
            } finally {
                DB.closeStatement(st);
            }
        }

        // DELETAR PERGUNTA
    }

//    public void searchUser() {
//        System.out.println("Opções de pesquisa de usuários:");
//        System.out.println("1 - Nome.");
//        System.out.println("2 - Idade.");
//        System.out.println("3 - Email.");
//        System.out.print("Escolha: ");
//        int choice = sc.nextInt();
//        sc.nextLine();
//
//        switch (choice) {
//            case 1 -> {
//                System.out.print("Digite o nome ou uma parte do nome do usuário que deseja pesquisar: ");
//                String searchString = sc.nextLine().toLowerCase();
//                System.out.println("Cadastros: ");
//                for (User user : users) {
//                    Pattern pattern = Pattern.compile(searchString);
//                    Matcher matcher = pattern.matcher(user.getFullName().toLowerCase());
//                    while (matcher.find()) {
//                        System.out.println(user.getFullName());
//                    }
//                }
//                System.out.println();
//            }
//            case 2 -> {
//                System.out.print("Digite a idade do usuário que deseja pesquisar: ");
//                int searchAge = sc.nextInt();
//                System.out.println("Cadastros: ");
//                for (User user : users) {
//                    Pattern pattern = Pattern.compile("\\b" + searchAge + "\\b");
//                    Matcher matcher = pattern.matcher(String.valueOf(user.getAge()));
//                    while (matcher.find()) {
//                        System.out.println(user.getFullName() + " - Idade: " + user.getAge());
//                    }
//                }
//                System.out.println();
//            }
//            case 3 -> {
//                System.out.print("Digite o email ou uma parte do email do usuário que deseja pesquisar: ");
//                String searchEmail = sc.nextLine();
//                System.out.println("Cadastros: ");
//                for (User user : users) {
//                    Pattern pattern = Pattern.compile(searchEmail);
//                    Matcher matcher = pattern.matcher(user.getEmail().toLowerCase());
//                    while (matcher.find()) {
//                        System.out.println(user.getFullName() + " - Email: " + user.getEmail());
//                    }
//                }
//                System.out.println();
//            }
//        }
//    }
}

