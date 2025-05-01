package com.senac;

import java.util.Scanner;

import com.senac.model.GerenciadorDeArquivos;
import com.senac.service.JogoService;
import com.senac.service.PerguntaService;
import com.senac.service.RankingService;

public class Main {
    public static void main(String[] args) {
        GerenciadorDeArquivos arquivos = new GerenciadorDeArquivos();
        PerguntaService perguntaService = new PerguntaService(arquivos);
        RankingService rankingService = new RankingService();
        JogoService jogoService = new JogoService(perguntaService, rankingService);

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== MENU PRINCIPAL =====");
            System.out.println("1 - Jogar");
            System.out.println("2 - Ver Ranking");
            System.out.println("3 - Sair");
            System.out.print("Escolha: ");
            int escolha = scanner.nextInt();
            scanner.nextLine();

            switch (escolha) {
                case 1:
                    limparConsole();
                    System.out.print("Quantos jogadores vão jogar? (1 a 4): ");
                    int total = scanner.nextInt();
                    scanner.nextLine();

                    if (total < 1 || total > 4) {
                        System.out.println("Número de jogadores inválidos. Digite entre 1 e 4");
                        break;
                    }

                    String[] nicks = new String[total];
                    for (int i = 0; i < total; i++) {
                        System.out.print("Jogador " + (i + 1) + ", digite seu nick: ");
                        nicks[i] = scanner.nextLine();
                    }

                    jogoService.jogar(nicks); // Passar todos os nicks de uma vez
                    break;
                case 2:
                    rankingService.exibirRanking();
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
