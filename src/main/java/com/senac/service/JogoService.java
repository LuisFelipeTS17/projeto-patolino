package com.senac.service;

import com.senac.model.Pergunta;

import java.util.*;

public class JogoService {

    private final PerguntaService perguntaService;

    public JogoService(PerguntaService perguntaService) {
        this.perguntaService = perguntaService;
    }

    public void jogar() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Digite o número de jogadores (1 a 4): ");
        int numJogadores = Integer.parseInt(scanner.nextLine());

        if (numJogadores < 1 || numJogadores > 4) {
            System.out.println("Número de jogadores inválido. Encerrando o jogo.");
            return;
        }

        int[] vidasJogadores = new int[numJogadores];
        Arrays.fill(vidasJogadores, 3);

        while (!perguntaService.jogoConcluido()) {
            List<Pergunta> perguntasFase = perguntaService.getPerguntasDaFaseAtual();

            if (perguntasFase.isEmpty()) {
                System.out.println("Nenhuma pergunta encontrada para esta fase.");
                break;
            }

            Collections.shuffle(perguntasFase);
            int[] acertosPorJogador = new int[numJogadores];

            System.out.println("===============================");
            System.out.println("Fase " + perguntaService.getFaseAtual());

            for (int i = 0; i < numJogadores; i++) {
                if (vidasJogadores[i] <= 0) {
                    System.out.println("Jogador " + (i + 1) + " está fora do jogo!");
                    continue;
                }

                Pergunta pergunta = perguntasFase.get(new Random().nextInt(perguntasFase.size()));
                System.out.println("===============================");
                System.out.println("Jogador " + (i + 1) + " - " + vidasJogadores[i] + " vidas restantes");
                System.out.println("Pergunta: " + pergunta.getPergunta());

                String[] alternativas = {"a", "b", "c", "d"};
                Map<String, String> alternativasMap = new HashMap<>();
                for (int j = 0; j < pergunta.getAlternativas().length; j++) {
                    alternativasMap.put(alternativas[j], pergunta.getAlternativas()[j]);
                    System.out.println(alternativas[j] + ") " + pergunta.getAlternativas()[j]);
                }

                System.out.print("Resposta: ");
                String respostaUsuario = scanner.nextLine();

                if (alternativasMap.get(respostaUsuario) != null &&
                        alternativasMap.get(respostaUsuario).equalsIgnoreCase(pergunta.getResposta())) {
                    System.out.println("Resposta correta!");
                    System.out.println(perguntaService.getMensagemDeAcerto());
                    acertosPorJogador[i]++;
                } else {
                    System.out.println("Resposta errada! A resposta correta era: " + pergunta.getResposta());
                    System.out.println(perguntaService.getMensagemDeErro());
                    vidasJogadores[i]--;
                    if (vidasJogadores[i] == 0) {
                        System.out.println("Jogador " + (i + 1) + " perdeu todas as vidas!");
                    }
                }
            }

            boolean todosPerderam = true;
            for (int vidas : vidasJogadores) {
                if (vidas > 0) {
                    todosPerderam = false;
                    break;
                }
            }

            if (todosPerderam) {
                System.out.println("Todos os jogadores perderam. O jogo recomeçará!");
                Arrays.fill(vidasJogadores, 3);
                perguntaService.reiniciarFase();
                continue;
            }

            int totalJogadoresVivos = 0;
            int totalAcertos = 0;
            for (int i = 0; i < numJogadores; i++) {
                if (vidasJogadores[i] > 0) {
                    totalJogadoresVivos++;
                    totalAcertos += acertosPorJogador[i];
                }
            }

            if (totalJogadoresVivos > 0 && totalAcertos >= totalJogadoresVivos) {
                System.out.println("Parabéns! Vocês avançaram para a próxima fase!");
                perguntaService.avancarFase();
            } else {
                System.out.println("Nem todos foram bem... Tentem novamente!");
            }
        }

        if (perguntaService.jogoConcluido()) {
            System.out.println("Parabéns! Todos os jogadores concluíram o jogo!");
        }
    }
}
