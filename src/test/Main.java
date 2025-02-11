package test;

import service.SystemManager;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        SystemManager systemManager = new SystemManager();
        Scanner sc = new Scanner(System.in);
        System.out.println("--- SISTEMA DE CADASTRO ---");
        System.out.println("1 - Cadastrar o usuário.");
        System.out.println("2 - Listar todos usuários cadastrados");
        System.out.println("3 - Cadastrar nova pergunta no formulário");
        System.out.println("4 - Deletar pergunta do formulário");
        System.out.println("5 - Pesquisar usuário por nome ou idade ou email");
        System.out.print("Escolha: ");
        int choice = sc.nextInt();

        switch (choice){
            case 1 -> {
                systemManager.registerUser();
            }
        }

        sc.close();

    }
}
