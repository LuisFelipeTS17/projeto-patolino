package com.senac;

import com.senac.service.PerguntaService;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        PerguntaService jogo = new PerguntaService();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("1 - Adicionar Pergunta");
            System.out.println("2 - Jogar");
            System.out.println("3 - Sair");
            System.out.print("Escolha: ");
            int escolha = scanner.nextInt();
            scanner.nextLine();

            switch (escolha) {
                case 1:
                    System.out.print("Digite a pergunta: ");
                    String pergunta = scanner.nextLine();
                    System.out.print("Digite a resposta: ");
                    String resposta = scanner.nextLine();
                    jogo.adicionarPergunta(pergunta, resposta);
                    break;
                case 2:
                    jogo.jogar();
                    break;
                case 3:
                    System.out.println("Saindo...");
                    return;
                default:
                    System.out.println("Opção inválida!");
            }
        }
    }
}