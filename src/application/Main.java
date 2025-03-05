package application;

import db.DB;
import service.SystemManager;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        SystemManager systemManager = new SystemManager();

        Scanner sc = new Scanner(System.in);
        int choice = 0;

        while (choice != 8){
            System.out.println("--- SISTEMA DE CADASTRO ---");
            System.out.println("1 - Cadastrar o usuário.");
            System.out.println("2 - Listar todos usuários cadastrados.");
            System.out.println("3 - Cadastrar nova pergunta no formulário.");
            System.out.println("4 - Deletar pergunta do formulário.");
            System.out.println("5 - Pesquisar usuário por nome,idade ou email.");
            System.out.println("6 - Editar informações do usuário.");
            System.out.println("7 - Exibir todas as informações de um usuário.");
            System.out.println("8 - Encerrar o programa.");
            System.out.print("Escolha: ");
            choice = sc.nextInt();
            System.out.println();

            switch (choice){
                case 1 -> {
                    systemManager.registerUser();
                }case 2 -> {
                    systemManager.listUsers();
                }case 3 -> {
                    systemManager.newQuestion();
                }case 4 -> {
                    systemManager.deleteNewQuestion();
                }case 5 -> {
                    systemManager.searchUser();
                }case 6 -> {
                    systemManager.editUser();
                }case 7 -> {
                    systemManager.showUserData();
                }
                case 8 -> {
                    System.out.println("Encerrando o programa.");
                }
            }
        }
        sc.close();
        DB.closeConnection();
    }
}
