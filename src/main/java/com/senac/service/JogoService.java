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

        if (numJogadores < 1 || numJogadores > 5) {
            System.out.println("Número de jogadores inválido. Encerrando o jogo.");
            return;
        }

        int[] vidasJogadores = new int[numJogadores];
        Arrays.fill(vidasJogadores, 3);

        Map<Integer, Integer> fasePorJogador = new HashMap<>();
        for (int i = 0; i < numJogadores; i++) fasePorJogador.put(i, 1);

        Map<Integer, Set<Pergunta>> perguntasRespondidas = new HashMap<>();
        for (int i = 0; i < numJogadores; i++) perguntasRespondidas.put(i, new HashSet<>());


        while (true) {
            List<Pergunta> perguntasFase = perguntaService.getPerguntasDaFaseAtual();

            boolean jogoConcluido = true;

            for (int i = 0; i < numJogadores; i++) {
                if (vidasJogadores[i] > 0 && fasePorJogador.get(i) <= perguntaService.getFases().size()) {
                    jogoConcluido = false;
                    break;
                }
            }

            if (jogoConcluido) {
                System.out.println("Parabéns! Todos os jogadores concluíram o jogo!");
                break;
            }

            for (int i = 0; i < numJogadores; i++) {
                if (vidasJogadores[i] <= 0 || fasePorJogador.get(i) > perguntaService.getFases().size()) {
                    System.out.println("Jogador " + (i + 1) + " está fora ou concluiu o jogo.");
                    continue;
                }

                int faseAtualJogador = fasePorJogador.get(i);
                String nomeFase = perguntaService.getFases().get(faseAtualJogador - 1);
                List<Pergunta> perguntasDisponiveis = new ArrayList<>(perguntaService.getPerguntasDaFase(nomeFase));
                perguntasDisponiveis.removeAll(perguntasRespondidas.get(i));

                if (perguntasDisponiveis.isEmpty()) {
                    System.out.println("Jogador " + (i + 1) + " já respondeu todas as perguntas da fase.");
                    continue;
                }


                Pergunta pergunta = perguntasDisponiveis.get(new Random().nextInt(perguntasDisponiveis.size()));
                perguntasRespondidas.get(i).add(pergunta);

                System.out.println("===============================");
                System.out.println("Jogador " + (i + 1) + " - Fase " + faseAtualJogador + ": " + nomeFase + " | " + vidasJogadores[i] + " vidas");
                System.out.println("Pergunta: " + pergunta.getPergunta());

                String[] letras = {"a", "b", "c", "d"};
                Map<String, String> mapa = new HashMap<>();
                for (int j = 0; j < pergunta.getAlternativas().length; j++) {
                    mapa.put(letras[j], pergunta.getAlternativas()[j]);
                    System.out.println(letras[j] + ") " + pergunta.getAlternativas()[j]);
                }

                String respostaUsuario = null;
                final boolean[] respondeu = {false};

                System.out.println("Você tem 15 segundos para responder...");

                System.out.print("\rTempo restante: 15 segundos");
                System.out.print("\rResposta: ");


                Thread cronometro = new Thread(() -> {
                    for (int s = 15; s >= 0; s--) {
                        if (respondeu[0]) return;
                        System.out.print("\rTempo restante: " + s + " segundos");
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            return;
                        }
                    }
                });


                cronometro.start();

                long inicio = System.currentTimeMillis();
                while ((System.currentTimeMillis() - inicio) < 15000 && !respondeu[0]) {
                    if (scanner.hasNextLine()) {
                        respostaUsuario = scanner.nextLine().trim();
                        respondeu[0] = true;
                    }
                }

                cronometro.interrupt();
                System.out.println();

                if (!respondeu[0] || !mapa.containsKey(respostaUsuario)) {
                    System.out.println("Resposta inválida ou tempo esgotado! Perdeu 1 vida.");
                    vidasJogadores[i]--;
                    if (vidasJogadores[i] == 0) {
                        System.out.println("Jogador " + (i + 1) + " está eliminado!");
                    }
                    continue;
                }

                if (mapa.get(respostaUsuario).equalsIgnoreCase(pergunta.getResposta())) {
                    System.out.println("Resposta correta!");
                    System.out.println(perguntaService.getMensagemDeAcerto());
                    fasePorJogador.put(i, faseAtualJogador + 1);
                } else {
                    System.out.println("Resposta errada! A certa era: " + pergunta.getResposta());
                    System.out.println(perguntaService.getMensagemDeErro());
                    vidasJogadores[i]--;
                    if (vidasJogadores[i] == 0) {
                        System.out.println("Jogador " + (i + 1) + " está eliminado!");
                    }
                }
            }
        }
    }
}