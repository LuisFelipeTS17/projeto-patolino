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
        cabecalhoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        cabecalhoLabel.setFont(new Font("Arial", Font.BOLD, 16));
        painelPrincipal.add(cabecalhoLabel);

        timerLabel = new JLabel("Tempo restante: 15s");
        timerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 14));
        timerLabel.setForeground(Color.RED);
        painelPrincipal.add(timerLabel);

        perguntaLabel = new JLabel("Pergunta");
        perguntaLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        perguntaLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        painelPrincipal.add(Box.createVerticalStrut(10));
        painelPrincipal.add(perguntaLabel);

        painelPrincipal.add(Box.createVerticalStrut(20));

        JPanel alternativasPanel = new JPanel();
        alternativasPanel.setLayout(new BoxLayout(alternativasPanel, BoxLayout.Y_AXIS));
        alternativasPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        grupoAlternativas = new ButtonGroup();
        alternativasRadio = new JRadioButton[4];
        for (int i = 0; i < 4; i++) {
            alternativasRadio[i] = new JRadioButton();
            grupoAlternativas.add(alternativasRadio[i]);
            alternativasRadio[i].setAlignmentX(Component.LEFT_ALIGNMENT);
            alternativasPanel.add(alternativasRadio[i]);
            // if (i < alternativasRadio.length - 1) {
            // alternativasPanel.add(Box.createVerticalStrut(5));
            // }
        }

        painelPrincipal.add(alternativasPanel);

        painelPrincipal.add(Box.createVerticalStrut(20));

        responderButton = new JButton("Responder");
        responderButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        responderButton.addActionListener(this::processarResposta);
        painelPrincipal.add(responderButton);

        statusLabel = new JLabel(" ");
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
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
        perguntaLabel.setText("<html><body style='width: 500px; text-align: center;'>" + perguntaAtual.getPergunta() + "</body></html>");
        String[] alternativas = perguntaAtual.getAlternativas();

        for (int i = 0; i < alternativas.length; i++) {
            if (i < alternativasRadio.length) {
                alternativasRadio[i].setText((char) ('A' + i) + ") " + alternativas[i]);
                alternativasRadio[i].setSelected(false);
                alternativasRadio[i].setVisible(true);
            }
        }
        for (int i = alternativas.length; i < alternativasRadio.length; i++) {
            alternativasRadio[i].setVisible(false);
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
                    JOptionPane.showMessageDialog(JogoService.this, "Tempo esgotado! O jogador " + nicks[jogadorAtual] + " perdeu uma vida.");
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
            if (alternativasRadio[i].isSelected() && alternativasRadio[i].isVisible()) {
                selecionada = i;
                break;
            }
        }

        if (selecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma alternativa!");
            if (perguntaAtual != null) {
                iniciarTimer();
            }
            return;
        }

        if (perguntaAtual == null || perguntaAtual.getAlternativas() == null || selecionada >= perguntaAtual.getAlternativas().length) {
            JOptionPane.showMessageDialog(this, "Erro ao processar a resposta. Tente novamente.");
            jogadorAtual = (jogadorAtual + 1) % nicks.length;
            if (!verificarFimDoJogo()) {
                carregarProximaPergunta();
            }
            return;
        }

        String respostaEscolhida = perguntaAtual.getAlternativas()[selecionada];
        if (respostaEscolhida.equalsIgnoreCase(perguntaAtual.getResposta())) {
            JOptionPane.showMessageDialog(this, "Correto!\n" + perguntaService.getMensagemDeAcerto());
            pontuacoes[jogadorAtual] += 10;
            if (pontuacoes[jogadorAtual] > 0 && pontuacoes[jogadorAtual] % 50 == 0) {
                int faseAtualDoJogador = fasePorJogador.get(jogadorAtual);
                int proximaFasePotencial = faseAtualDoJogador + 1;

                if (proximaFasePotencial <= perguntaService.getFases().size()) {
                    fasePorJogador.put(jogadorAtual, proximaFasePotencial);
                    JOptionPane.showMessageDialog(this, "Jogador " + nicks[jogadorAtual] + " avançou para a Fase " + proximaFasePotencial + "!");
                } else {

                    fasePorJogador.put(jogadorAtual, proximaFasePotencial);
                    JOptionPane.showMessageDialog(this, "Parabéns, " + nicks[jogadorAtual] + "! Você completou todas as fases com " + pontuacoes[jogadorAtual] + " pontos!");

                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Errado! A resposta correta era: " + perguntaAtual.getResposta() + "\n" + perguntaService.getMensagemDeErro());
            vidasJogadores[jogadorAtual]--;
            if (vidasJogadores[jogadorAtual] == 0) {
                JOptionPane.showMessageDialog(this, "Jogador " + nicks[jogadorAtual] + " foi eliminado!");
            }
        }

        jogadorAtual = (jogadorAtual + 1) % nicks.length;

        if (verificarFimDoJogo()) {
            return;
        }

        carregarProximaPergunta();
    }

    private boolean verificarFimDoJogo() {
        int jogadoresAtivos = 0;
        for (int i = 0; i < nicks.length; i++) {
            if (vidasJogadores[i] > 0) {
                jogadoresAtivos++;
            }
        }
        boolean todosCompletaramOuEliminados = true;
        if (jogadoresAtivos == 0 && nicks.length > 0) {
            todosCompletaramOuEliminados = true;
        } else {
            for (int i = 0; i < nicks.length; i++) {
                if (vidasJogadores[i] > 0 && fasePorJogador.get(i) <= perguntaService.getFases().size()) {
                    todosCompletaramOuEliminados = false;
                    break;
                }
            }
        }


        if (todosCompletaramOuEliminados) {
            StringBuilder msgFinal = new StringBuilder("Fim de jogo!\n\nRanking final:\n");
            List<Integer> indicesJogadores = new ArrayList<>();
            for (int i = 0; i < nicks.length; i++) indicesJogadores.add(i);
            indicesJogadores.sort((j1, j2) -> Integer.compare(pontuacoes[j2], pontuacoes[j1]));


            for (int i : indicesJogadores) {
                try {
                    rankingService.atualizarRanking(nicks[i], pontuacoes[i]);
                } catch (Exception e) {
                    System.err.println("Erro ao atualizar ranking para " + nicks[i] + ": " + e.getMessage());
                }
                msgFinal.append(nicks[i]).append(" - ").append(pontuacoes[i]).append(" pontos");
                if (vidasJogadores[i] == 0) {
                    msgFinal.append(" (Eliminado)");
                }
                msgFinal.append("\n");
            }

            JOptionPane.showMessageDialog(this, msgFinal.toString());
            dispose();
            return true;
        }
        return false;
    }
}