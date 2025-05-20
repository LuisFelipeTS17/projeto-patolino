package com.senac.service;

import com.senac.model.Pergunta;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.List;
import javax.swing.Timer;

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

    private Timer timer;
    private int tempoRestante = 15;
    private JLabel timerLabel;

    private JLabel cabecalhoLabel;

    private Pergunta perguntaAtual;

    public JogoService(PerguntaService perguntaService, RankingService rankingService) {
        this.perguntaService = perguntaService;
        this.rankingService = rankingService;

        setTitle("Jogo de Perguntas");
        setSize(700, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        JPanel painelPrincipal = new JPanel();
        painelPrincipal.setLayout(new BoxLayout(painelPrincipal, BoxLayout.Y_AXIS));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        cabecalhoLabel = new JLabel("Jogador: --- | Fase: ---");
        cabecalhoLabel.setAlignmentX(CENTER_ALIGNMENT);
        cabecalhoLabel.setFont(new Font("Arial", Font.BOLD, 16));
        painelPrincipal.add(cabecalhoLabel);

        timerLabel = new JLabel("Tempo restante: 15s");
        timerLabel.setAlignmentX(CENTER_ALIGNMENT);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 14));
        timerLabel.setForeground(Color.RED);
        painelPrincipal.add(timerLabel);

        perguntaLabel = new JLabel("Pergunta");
        perguntaLabel.setAlignmentX(CENTER_ALIGNMENT);
        perguntaLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        painelPrincipal.add(Box.createVerticalStrut(10));
        painelPrincipal.add(perguntaLabel);

        painelPrincipal.add(Box.createVerticalStrut(20));

        grupoAlternativas = new ButtonGroup();
        alternativasRadio = new JRadioButton[4];
        for (int i = 0; i < 4; i++) {
            alternativasRadio[i] = new JRadioButton();
            grupoAlternativas.add(alternativasRadio[i]);
            alternativasRadio[i].setAlignmentX(CENTER_ALIGNMENT);
            painelPrincipal.add(alternativasRadio[i]);
        }

        painelPrincipal.add(Box.createVerticalStrut(20));

        responderButton = new JButton("Responder");
        responderButton.setAlignmentX(CENTER_ALIGNMENT);
        responderButton.addActionListener(this::processarResposta);
        painelPrincipal.add(responderButton);

        statusLabel = new JLabel(" ");
        statusLabel.setAlignmentX(CENTER_ALIGNMENT);
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        statusLabel.setForeground(Color.BLUE);
        painelPrincipal.add(Box.createVerticalStrut(10));
        painelPrincipal.add(statusLabel);

        add(painelPrincipal);
    }


    public void jogar(String[] nicks) {
        this.nicks = nicks;
        int numJogadores = nicks.length;

        vidasJogadores = new int[numJogadores];
        Arrays.fill(vidasJogadores, 3);
        pontuacoes = new int[numJogadores];
        fasePorJogador = new HashMap<>();
        perguntasRespondidas = new HashMap<>();

        for (int i = 0; i < numJogadores; i++) {
            fasePorJogador.put(i, 1);
            perguntasRespondidas.put(i, new HashSet<>());
        }

        carregarProximaPergunta();
        setVisible(true);
    }

    private void carregarProximaPergunta() {
        while (vidasJogadores[jogadorAtual] <= 0 || fasePorJogador.get(jogadorAtual) > perguntaService.getFases().size()) {
            jogadorAtual = (jogadorAtual + 1) % nicks.length;
            if (verificarFimDoJogo()) return;
        }

        int faseAtual = fasePorJogador.get(jogadorAtual);
        String nomeFase = perguntaService.getFases().get(faseAtual - 1);
        List<Pergunta> disponiveis = new ArrayList<>(perguntaService.getPerguntasDaFase(nomeFase));
        disponiveis.removeAll(perguntasRespondidas.get(jogadorAtual));

        if (disponiveis.isEmpty()) {
            fasePorJogador.put(jogadorAtual, faseAtual + 1);
            carregarProximaPergunta();
            return;
        }

        perguntaAtual = disponiveis.get(new Random().nextInt(disponiveis.size()));
        perguntasRespondidas.get(jogadorAtual).add(perguntaAtual);

        cabecalhoLabel.setText("Jogador: " + nicks[jogadorAtual] + " | Fase " + faseAtual + ": " + nomeFase);
        perguntaLabel.setText(perguntaAtual.getPergunta());
        String[] alternativas = perguntaAtual.getAlternativas();

        for (int i = 0; i < alternativas.length; i++) {
            alternativasRadio[i].setText((char) ('A' + i) + ") " + alternativas[i]);
            alternativasRadio[i].setSelected(false);
        }

        statusLabel.setText("Vidas restantes: " + vidasJogadores[jogadorAtual] + " | Pontuação: " + pontuacoes[jogadorAtual]);

        iniciarTimer();
    }

    private void iniciarTimer() {
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }

        tempoRestante = 15;
        timerLabel.setText("Tempo restante: " + tempoRestante + "s");

        timer = new Timer(1000, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tempoRestante--;
                timerLabel.setText("Tempo restante: " + tempoRestante + "s");

                if (tempoRestante <= 0) {
                    timer.stop();
                    vidasJogadores[jogadorAtual]--;
                    JOptionPane.showMessageDialog(null, "Tempo esgotado! O jogador " + nicks[jogadorAtual] + " perdeu uma vida.");
                    jogadorAtual = (jogadorAtual + 1) % nicks.length;
                    if (!verificarFimDoJogo()) {
                        carregarProximaPergunta();
                    }
                }
            }
        });
        timer.start();
    }



    private void processarResposta(ActionEvent e) {
        int selecionada = -1;
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }

        for (int i = 0; i < alternativasRadio.length; i++) {
            if (alternativasRadio[i].isSelected()) {
                selecionada = i;
                break;
            }
        }

        if (selecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma alternativa!");
            return;
        }

        String respostaEscolhida = perguntaAtual.getAlternativas()[selecionada];
        if (respostaEscolhida.equalsIgnoreCase(perguntaAtual.getResposta())) {
            JOptionPane.showMessageDialog(this, "Correto!\n" + perguntaService.getMensagemDeAcerto());
            pontuacoes[jogadorAtual] += 10;
            if (pontuacoes[jogadorAtual] >= 50){
            fasePorJogador.put(jogadorAtual, fasePorJogador.get(jogadorAtual) + 1);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Errado! A certa era: " + perguntaAtual.getResposta() + "\n" + perguntaService.getMensagemDeErro());
            vidasJogadores[jogadorAtual]--;
            if (vidasJogadores[jogadorAtual] == 0) {
                JOptionPane.showMessageDialog(this, "Jogador " + nicks[jogadorAtual] + " foi eliminado!");
            }
        }

        jogadorAtual = (jogadorAtual + 1) % nicks.length;

        if (verificarFimDoJogo()) return;
        carregarProximaPergunta();
    }

    private boolean verificarFimDoJogo() {
        boolean todosConcluidos = true;
        for (int i = 0; i < nicks.length; i++) {
            if (vidasJogadores[i] > 0 && fasePorJogador.get(i) <= perguntaService.getFases().size()) {
                todosConcluidos = false;
                break;
            }
        }

        if (todosConcluidos) {
            StringBuilder msgFinal = new StringBuilder("Fim de jogo!\n\nRanking final:\n");
            for (int i = 0; i < nicks.length; i++) {
                if (vidasJogadores[i] > 0) {
                    pontuacoes[i] += (fasePorJogador.get(i) - 1) * 10;
                }
                try {
                    rankingService.atualizarRanking(nicks[i], pontuacoes[i]);
                } catch (Exception e) {
                    System.out.println("Erro ao atualizar ranking: " + e.getMessage());
                }
                msgFinal.append(nicks[i]).append(" - ").append(pontuacoes[i]).append(" pontos\n");
            }

            JOptionPane.showMessageDialog(this, msgFinal.toString());
            dispose();
            return true;
        }

        return false;
    }
}
