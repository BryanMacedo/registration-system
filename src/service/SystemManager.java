package service;

import db.DB;
import exceptions.dbException.DbException;
import domain.User;
import exceptions.dbException.NumberOfQuestionsReachedTheLimitException;
import exceptions.dbException.UnauthorizedQuestionDeletionAttemptException;
import exceptions.validationExceptions.*;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SystemManager {
    User user = new User();
    List<String> newQuestions = new ArrayList<>();
    List<String> newAnswer = new ArrayList<>();
    List<String> additionalAnswersUser = new ArrayList<>();

    private List<User> users = new ArrayList<>();
    private List<String> mainQuestions = new ArrayList<>();
    private List<String> mainQuestionsDB = new ArrayList<>();
    private List<String> usersName = new ArrayList<>();
    private List<Integer> usersAge = new ArrayList<>();
    private List<String> usersEmail = new ArrayList<>();

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
        int userId = 0;
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;

        boolean check = true;
        boolean checkTable = false;

        try { // verifica se tem as principais perguntas cadastradas
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
                    mainQuestionsDB.add(rs.getString("Main_Questions"));
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


                Pattern pattern = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" +
                        "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
                Matcher matcher = pattern.matcher(email);
                if (!matcher.find()) {
                    throw new InvalidEmailFormatException();
                }

                try {
                    conn = DB.getConnection();
                    st = conn.prepareStatement("SELECT COUNT(*) FROM users WHERE Email = ?");
                    st.setString(1, email);
                    rs = st.executeQuery();

                    if (rs.next()) {
                        int countEmail = rs.getInt(1);

                        if (countEmail > 0) {
                            throw new EmailAlreadyRegisteredException();
                        }
                    }

                } catch (SQLException e) {
                    throw new DbException(e.getMessage());
                } finally {
                    DB.closeStatement(st);
                    DB.closeResultSet(rs);
                }

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

                    st = conn.prepareStatement("SELECT Id FROM users WHERE FullName = ?");
                    st.setString(1, fullName);
                    rs = st.executeQuery();

                    if (rs.next()) {
                        userId = rs.getInt("Id");
                    }
                } catch (SQLException e) {
                    throw new DbException(e.getMessage());
                } finally {
                    DB.closeStatement(st);
                }

                check = false;
            } catch (NameSmallerThanExpectedException e) {
                System.out.println("\nTamanho de nome invalido, seu nome deve ter no mínimo 10 caracteres.\n");
            } catch (InvalidEmailFormatException e) {
                System.out.println("\nFormato de email invalido, seu email deve ser informado no seguinte formato: \"usuario@gmail.com\".\n");
            } catch (InvalidHeightFormatException e) {
                System.out.println("\nFormato de altura invalido, sua altura deve ser informada no seguinte formato: \"1,70\".\n");
            } catch (YoungerThanThePermittedAgeException e) {
                System.out.println("\nVocê não atinge a idade minima para o cadastro, só é permitido o cadastro de usuários com idade maior ou igual a 18.\n");
            } catch (EmailAlreadyRegisteredException e) {
                System.out.println("\nNão é possível cadastrar dois ou mais usuários com um mesmo email, por favor informe um email que ainda não foi cadastrado.\n");
            } catch (InputMismatchException e) {
                sc.nextLine();
                System.out.println("\nOpção invalida, por favor informe apenas números no campo de idade.\n");
            }

        }

        try {
            conn = DB.getConnection();
            st = conn.prepareStatement("SELECT EXISTS (SELECT 1 FROM additional_questions LIMIT 1)");
            rs = st.executeQuery();
            if (rs.next()) {
                checkTable = rs.getBoolean(1);
            }

            if (checkTable) {
                st = conn.prepareStatement("SELECT * FROM additional_questions");
                rs = st.executeQuery();

                newQuestions.clear();
                while (rs.next()) {
                    newQuestions.add(rs.getString("Users_questions"));
                }

                int lineCounter = 5;
                for (String newQuestion : newQuestions) {
                    System.out.print(lineCounter + " - " + newQuestion);
                    String answer = sc.nextLine();
                    newAnswer.add(answer);
                    lineCounter++;
                }

                if (newQuestions.size() == 3) {
                    st = conn.prepareStatement("INSERT INTO additional_answers (Answer01, Answer02, Answer03, User_Id) VALUES (?,?,?,?)");
                    st.setString(1, newAnswer.get(0));
                    st.setString(2, newAnswer.get(1));
                    st.setString(3, newAnswer.get(2));
                    st.setInt(4, userId);
                    st.executeUpdate();
                } else if (newQuestions.size() == 2) {
                    st = conn.prepareStatement("INSERT INTO additional_answers (Answer01, Answer02, User_Id) VALUES (?,?,?)");
                    st.setString(1, newAnswer.get(0));
                    st.setString(2, newAnswer.get(1));
                    st.setInt(3, userId);
                    st.executeUpdate();
                } else if (newQuestions.size() == 1) {
                    st = conn.prepareStatement("INSERT INTO additional_answers (Answer01, User_Id) VALUES (?,?)");
                    st.setString(1, newAnswer.get(0));
                    st.setInt(2, userId);
                    st.executeUpdate();
                }
            }

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
        }

        System.out.println("\nUsuário cadastrado!\n");
    }

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
                int userId = rs.getInt("Id");

                User userDB = new User(fullName, email, age, height, userId);
                users.add(userDB);


            }

            for (User user : users) {
                System.out.println(user.getFullName());
                System.out.println(user.getEmail());
                System.out.println(user.getAge());
                System.out.println(user.getHeight());
                try {
                    conn = DB.getConnection();
                    st = conn.prepareStatement("SELECT * FROM additional_answers WHERE User_Id = ?");
                    st.setInt(1, user.getUserId());
                    rs = st.executeQuery();

                    if (rs.next()) {
                        String answer01 = rs.getString("Answer01");
                        String answer02 = rs.getString("Answer02");
                        String answer03 = rs.getString("Answer03");

                        additionalAnswersUser.add(answer01);
                        additionalAnswersUser.add(answer02);
                        additionalAnswersUser.add(answer03);

                        for (String answer : additionalAnswersUser) {
                            if (answer != null) {
                                System.out.println(answer);
                            }
                        }
                        additionalAnswersUser.clear();
                    }
                } catch (SQLException e) {
                    throw new DbException(e.getMessage());
                } finally {
                    DB.closeStatement(st);
                    DB.closeResultSet(rs);
                }
                System.out.println("-------------------------------");
            }

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }

        System.out.println();
    }

    public void newQuestion() {
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            conn = DB.getConnection();
            st = conn.prepareStatement("SELECT COUNT(*) FROM additional_questions");
            rs = st.executeQuery();

            int countLine = 0;
            if (rs.next()) {
                countLine = rs.getInt(1);
            }

            if (countLine >= 3) {
                throw new NumberOfQuestionsReachedTheLimitException();
            } else {
                System.out.println("Digite uma nova pergunta:");
                String newUserQuestion = sc.nextLine();


                Pattern pattern = Pattern.compile("^[\\wÁ-ÿ\\s,;:.!?()-]+\\?$");
                Matcher matcher = pattern.matcher(newUserQuestion);

                if (!matcher.find()) {
                    throw new InvalidQuestionFormatException();
                }
                newUserQuestion = newUserQuestion + " ";

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

                System.out.println("\nPergunta cadastrada.\n");
            }
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } catch (NumberOfQuestionsReachedTheLimitException e) {
            System.out.println("Limite de perguntas cadastradas atingida, exclua uma pergunta para cadastrar outra.\n");
        } catch (InvalidQuestionFormatException e) {
            System.out.println("\nFormato de pergunta invalida, todas as perguntas devem seguir o seguinte formato: \"Sua pergunta?\".\n");
        } finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
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

            newQuestions.clear();
            while (rs.next()) {
                newQuestions.add(rs.getString("Users_questions"));
            }

            int lineCounter = 5;
            for (String newQuestion : newQuestions) {
                System.out.println(lineCounter + " - " + newQuestion);
                lineCounter++;
            }
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
        }

        System.out.print("\nDigite o número da pergunta que deseja excluir: ");
        int choice = sc.nextInt();
        sc.nextLine();

        try {
            if (choice >= 1 && choice <= 4) {
                //criar uma exception especifica
                throw new UnauthorizedQuestionDeletionAttemptException();
            } else {

                choice -= 4; // subtrai o número de perguntas principais
                choice -= 1; // subtrai 1 pq o offset começa em 0

                try {
                    conn = DB.getConnection();
                    st = conn.prepareStatement("SELECT Id FROM additional_questions ORDER BY Id LIMIT 1 OFFSET ?");
                    st.setInt(1, choice);
                    rs = st.executeQuery();

                    int idToDelete = 0;
                    if (rs.next()) {
                        idToDelete = rs.getInt("Id");
                    }

                    st = conn.prepareStatement("DELETE FROM additional_questions WHERE Id = ?");
                    st.setInt(1, idToDelete);
                    st.executeUpdate();
                } catch (SQLException e) {
                    throw new DbException(e.getMessage());
                } finally {
                    DB.closeStatement(st);
                }
                System.out.println("\nPergunta excluída.\n");
            }
        } catch (UnauthorizedQuestionDeletionAttemptException e) {
            System.out.println("\nNão é possível excluir uma das 4 perguntas originais.\n");
        }

    }

    public void searchUser() {
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;

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
                try {
                    conn = DB.getConnection();
                    st = conn.prepareStatement("SELECT * FROM users WHERE FullName LIKE ?");
                    st.setString(1, "%" + searchString + "%");
                    rs = st.executeQuery();

                    while (rs.next()) {
                        usersName.add(rs.getString("FullName"));
                    }

                    if (usersName.isEmpty()) {
                        System.out.println("Não foi encontrado nenhum usuário com o nome " + searchString);
                    } else {
                        int i = 1;
                        for (String names : usersName) {
                            System.out.println(i + " - " + names);
                            i++;
                        }
                    }
                } catch (SQLException e) {
                    throw new DbException(e.getMessage());
                } finally {
                    DB.closeStatement(st);
                    DB.closeResultSet(rs);
                }
                System.out.println();
            }
            case 2 -> {
                System.out.print("Digite a idade do usuário que deseja pesquisar: ");
                int searchAge = sc.nextInt();
                System.out.println("Cadastros: ");

                try {
                    conn = DB.getConnection();
                    st = conn.prepareStatement("SELECT * FROM users WHERE Age = ?");
                    st.setInt(1, searchAge);
                    rs = st.executeQuery();

                    while (rs.next()) {
                        usersAge.add(rs.getInt("Age"));
                        usersName.add(rs.getString("FullName"));
                    }

                    if (usersAge.isEmpty()) {
                        System.out.println("Não foi encontrado nenhum usuário com a idade " + searchAge);
                    } else {
                        int i = 1;
                        for (int j = 0; j < usersAge.size(); j++) {
                            System.out.println(i + " - " + usersName.get(j) + " - " + "Idade: " + usersAge.get(j));
                            i++;
                        }
                    }
                } catch (SQLException e) {
                    throw new DbException(e.getMessage());
                } finally {
                    DB.closeStatement(st);
                    DB.closeResultSet(rs);
                }

                System.out.println();
            }
            case 3 -> {
                System.out.print("Digite o email ou uma parte do email do usuário que deseja pesquisar: ");
                String searchEmail = sc.nextLine();
                System.out.println("Cadastros: ");

                try {
                    conn = DB.getConnection();
                    st = conn.prepareStatement("SELECT * FROM users WHERE Email LIKE ?");
                    st.setString(1, "%" + searchEmail + "%");
                    rs = st.executeQuery();

                    while (rs.next()) {
                        usersEmail.add(rs.getString("Email"));
                        usersName.add(rs.getString("FullName"));
                    }

                    if (usersEmail.isEmpty()) {
                        System.out.println("Não foi encontrado nenhum usuário com o email " + searchEmail);
                    } else {
                        int i = 1;
                        for (int j = 0; j < usersEmail.size(); j++) {
                            System.out.println(i + " - " + usersName.get(j) + " - " + "Email: " + usersEmail.get(j));
                            i++;
                        }
                    }
                } catch (SQLException e) {
                    throw new DbException(e.getMessage());
                } finally {
                    DB.closeStatement(st);
                    DB.closeResultSet(rs);
                }

                System.out.println();
            }
        }
    }

    public void editUser() {
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        int id = 0;

        System.out.println("Listando usuários cadastrados:");
        try {
            conn = DB.getConnection();
            st = conn.prepareStatement("SELECT * FROM users");
            rs = st.executeQuery();
            while (rs.next()) {
                usersName.add(rs.getString("FullName"));
            }

            int i = 1;
            for (int j = 0; j < usersName.size(); j++) {
                System.out.println(i + " - " + usersName.get(j));
                i++;
            }

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }

        System.out.print("\nDigite o número do usuário que deseja editar: ");
        int choice = sc.nextInt();
        sc.nextLine();

        choice -= 1; // subtrai 1 pq o offset começa pelo 0
        try {
            conn = DB.getConnection();
            st = conn.prepareStatement("SELECT Id FROM users ORDER BY Id LIMIT 1 OFFSET ?");
            st.setInt(1, choice);
            rs = st.executeQuery();

            if (rs.next()) {
                id = rs.getInt("Id");
            }

            st = conn.prepareStatement("SELECT * FROM users WHERE ID = ?");
            st.setInt(1, id);
            rs = st.executeQuery();

            String currentName = null;
            String currentEmail = null;
            int currentAge = 0;
            String currentHeight = null;
            if (rs.next()){
                currentName = rs.getString("FullName");
                currentEmail = rs.getString("Email");
                currentAge = rs.getInt("Age");
                currentHeight = rs.getString("Height");

            }

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }

        System.out.println("\nOpções de campos do usuário para editar:");
        System.out.println("1 - Nome.");
        System.out.println("2 - Email.");
        System.out.println("3 - Idade.");
        System.out.println("4 - Altura.");
        System.out.print("Escolha: ");
        int editChoice = sc.nextInt();
        sc.nextLine();

        switch (editChoice) {
            case 1 -> {

                System.out.print("Digite um novo nome: ");
                String NewFullName = sc.nextLine();
                try {
                    if (NewFullName.length() < 10) {
                        throw new NameSmallerThanExpectedException();
                    }
                    // verificar se o nome atual é igual ao novo nome
                    conn = DB.getConnection();
                    st = conn.prepareStatement("UPDATE users SET FullName = ? WHERE Id = ?");
                    st.setString(1, NewFullName);
                    st.setInt(2, id);
                    st.executeUpdate();

                    System.out.println("\nNome do usuário editado.\n");

                } catch (NameSmallerThanExpectedException e) {
                    System.out.println("\nTamanho de nome invalido, seu nome deve ter no mínimo 10 caracteres.\n");
                }catch (SQLException e){
                    throw new DbException(e.getMessage());
                }
            }
            case 2 -> {
            }
            case 3 -> {
            }
            case 4 -> {
            }
        }
    }

    ;
}

