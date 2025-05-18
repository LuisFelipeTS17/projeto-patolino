package com.senac.service;

import com.senac.model.Pergunta;

import javax.swing.*;
import java.util.*;

public class JogoService extends JFrame {

    private final PerguntaService perguntaService;
    private final RankingService rankingService;

    private String[] nicks;
    private int jogadorAtual = 0;
    private int[] vidasJogadores;
    private int[] pontuacoes;
    private Map<Integer, Integer> fasePorJogador;
    private Map<Integer, Set<Pergunta>> perguntasRespondidas;

    private JLabel perguntaLabel;
    private JRadioButton[] alternativasRadio;
    private ButtonGroup grupoAlternativas;
    private JButton responderButton;
    private JLabel statusLabel;

    private Pergunta perguntaAtual;

    public JogoService(PerguntaService perguntaService, RankingService rankingService) {
        this.perguntaService = perguntaService;
        this.rankingService = rankingService;

        setTitle("Jogo de Perguntas");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

    }

    public void jogar(String[] nicks) {
        Scanner scanner = new Scanner(System.in);
        int numJogadores = nicks.length;

        if (numJogadores < 1 || numJogadores > 4) {
            System.out.println("Número de jogadores inválido. Encerrando o jogo.");
            return;
        }

        int[] vidasJogadores = new int[numJogadores];
        Arrays.fill(vidasJogadores, 3);

        int[] pontuacoes = new int[numJogadores];

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
                System.out.println("Parabéns! Todos os jogadores concluíram o jogo ou foram eliminados!");
                atualizarRankingFinal(nicks, pontuacoes, vidasJogadores, fasePorJogador);
                break; // Sair do loop principal
            }

            for (int i = 0; i < numJogadores; i++) {
                if (vidasJogadores[i] <= 0 || fasePorJogador.get(i) > perguntaService.getFases().size()) {
                    System.out.println("Jogador " + nicks[i] + " está fora ou concluiu o jogo.");
                    continue;
                }

                int faseAtualJogador = fasePorJogador.get(i);
                String nomeFase = perguntaService.getFases().get(faseAtualJogador - 1);
                List<Pergunta> perguntasDisponiveis = new ArrayList<>(perguntaService.getPerguntasDaFase(nomeFase));
                perguntasDisponiveis.removeAll(perguntasRespondidas.get(i));

                if (perguntasDisponiveis.isEmpty()) {
                    System.out.println("Jogador " + nicks[i] + " já respondeu todas as perguntas da fase.");
                    fasePorJogador.put(i, faseAtualJogador + 1);
                    continue;
                }


                Pergunta pergunta = perguntasDisponiveis.get(new Random().nextInt(perguntasDisponiveis.size()));
                perguntasRespondidas.get(i).add(pergunta);

                System.out.println("===============================");
                System.out.println("Jogador " + nicks[i] + " - Fase " + faseAtualJogador + ": " + nomeFase + " | " + vidasJogadores[i] + " vidas");
                System.out.println("Pergunta: " + pergunta.getPergunta());

                String[] letras = {"a", "b", "c", "d"};
                Map<String, String> mapa = new HashMap<>();
                for (int j = 0; j < pergunta.getAlternativas().length; j++) {
                    mapa.put(letras[j], pergunta.getAlternativas()[j]);
                    System.out.println(letras[j] + ") " + pergunta.getAlternativas()[j]);
                }

                System.out.print("Resposta: ");
                String respostaUsuario = scanner.nextLine().trim();

                if (!mapa.containsKey(respostaUsuario)) {
                    System.out.println("Resposta inválida! Perdeu 1 vida.");
                    vidasJogadores[i]--;
                    if (vidasJogadores[i] == 0) {
                        System.out.println("Jogador " + nicks[i] + " está eliminado!");
                    }
                    continue;
                }

                if (mapa.get(respostaUsuario).equalsIgnoreCase(pergunta.getResposta())) {
                    System.out.println("Resposta correta!");
                    System.out.println(perguntaService.getMensagemDeAcerto());
                    pontuacoes[i] += 10;
                    fasePorJogador.put(i, faseAtualJogador + 1);
                } else {
                    System.out.println("Resposta errada! A certa era: " + pergunta.getResposta());
                    System.out.println(perguntaService.getMensagemDeErro());
                    vidasJogadores[i]--;
                    if (vidasJogadores[i] == 0) {
                        System.out.println("Jogador " + nicks[i] + " está eliminado!");
                    }
                }
            }
        }
    }

    private void atualizarRankingFinal(String[] nicks, int[] pontuacoes, int[] vidasJogadores, Map<Integer, Integer> fasePorJogador) {
        for (int i = 0; i < nicks.length; i++) {
            if (vidasJogadores[i] > 0) {
                pontuacoes[i] += (fasePorJogador.get(i) - 1) * 10; // Calcular pontuação final com base nas fases concluídas
            }
            try {
                rankingService.atualizarRanking(nicks[i], pontuacoes[i]); // Atualizar ranking
            } catch (Exception e) {
                System.out.println("Erro ao atualizar o ranking para o jogador " + nicks[i] + ": " + e.getMessage());
            }
        }
    }
}
