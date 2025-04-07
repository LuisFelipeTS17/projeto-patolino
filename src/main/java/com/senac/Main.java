package com.senac;

import java.util.Scanner;

import com.senac.model.GerenciadorDeArquivos;
import com.senac.service.JogoService;
import com.senac.service.PerguntaService;

public class Main {
    public static void main(String[] args) {
        GerenciadorDeArquivos arquivos = new GerenciadorDeArquivos();
        PerguntaService perguntaService = new PerguntaService(arquivos);
        JogoService jogoService = new JogoService(perguntaService);
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

                    break;
                case 2:
                    limparConsole();
                    jogoService.jogar();
                    break;
                case 3:
                    System.out.println("Saindo...");
                    return;
                default:
                    System.out.println("Opção inválida!");
            }
        }
    }

    public static void limparConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }


}