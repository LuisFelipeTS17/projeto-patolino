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
    private Set<Pergunta> perguntasJaUtilizadasGlobalmente;


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
        perguntaLabel.setHorizontalAlignment(SwingConstants.CENTER);
        perguntaLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        perguntaLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        painelPrincipal.add(Box.createVerticalStrut(15));
        painelPrincipal.add(perguntaLabel);

        painelPrincipal.add(Box.createVerticalStrut(25));

//        JPanel alternativasPanel = new JPanel();
//        alternativasPanel.setLayout(new BoxLayout(alternativasPanel, BoxLayout.Y_AXIS));
//        alternativasPanel.setAlignmentX(Component.CENTER_ALIGNMENT);


        JPanel alternativasWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        alternativasWrapper.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel alternativasPanel = new JPanel();
        alternativasPanel.setLayout(new BoxLayout(alternativasPanel, BoxLayout.Y_AXIS));

        grupoAlternativas = new ButtonGroup();
        alternativasRadio = new JRadioButton[4];

        for (int i = 0; i < 4; i++) {
            alternativasRadio[i] = new JRadioButton();
            alternativasRadio[i].setFont(new Font("Arial", Font.PLAIN, 14));
            grupoAlternativas.add(alternativasRadio[i]);
            alternativasRadio[i].setAlignmentX(Component.LEFT_ALIGNMENT);
            alternativasPanel.add(alternativasRadio[i]);
            if (i < alternativasRadio.length - 1) {
                alternativasPanel.add(Box.createVerticalStrut(5));
            }
        }

        alternativasWrapper.add(alternativasPanel);
        painelPrincipal.add(alternativasWrapper);

        painelPrincipal.add(Box.createVerticalStrut(25));

        responderButton = new JButton("Responder");
        responderButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        responderButton.setFont(new Font("Arial", Font.BOLD, 14));
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
        perguntasJaUtilizadasGlobalmente = new HashSet<>();

        for (int i = 0; i < numJogadores; i++) {
            fasePorJogador.put(i, 1);
            perguntasRespondidas.put(i, new HashSet<>());
        }

        carregarProximaPergunta();
        setVisible(true);
    }

    private void carregarProximaPergunta() {
        int loopGuard = 0;
        while (vidasJogadores[jogadorAtual] <= 0 || fasePorJogador.get(jogadorAtual) > perguntaService.getFases().size()) {
            jogadorAtual = (jogadorAtual + 1) % nicks.length;
            loopGuard++;
            if (verificarFimDoJogo()) {
                return;
            }
            if (loopGuard > nicks.length && nicks.length > 0) {
                System.err.println("JogoService.carregarProximaPergunta: Nenhum jogador ativo encontrado, mas o jogo não foi finalizado. Verifique a lógica de verificarFimDoJogo.");
                if (!verificarFimDoJogo()) {
                    JOptionPane.showMessageDialog(this, "Erro crítico: Impossível prosseguir. Nenhum jogador ativo.", "Erro no Jogo", JOptionPane.ERROR_MESSAGE);
                    dispose();
                }
                return;
            }
        }

        int faseNumeroJogador = fasePorJogador.get(jogadorAtual);

        if (faseNumeroJogador <= 0 || faseNumeroJogador > perguntaService.getFases().size()) {
            System.err.println("Jogador " + nicks[jogadorAtual] + " está em uma fase inválida: " + faseNumeroJogador + ". Tentando corrigir ou finalizar.");
            fasePorJogador.put(jogadorAtual, perguntaService.getFases().size() + 1);
            if (!verificarFimDoJogo()) {
                carregarProximaPergunta();
            }
            return;
        }
        String nomeFase = perguntaService.getFases().get(faseNumeroJogador - 1);

        List<Pergunta> todasPerguntasDaFase = perguntaService.getPerguntasDaFase(nomeFase);
        List<Pergunta> perguntasRealmenteDisponiveis = new ArrayList<>();

        for (Pergunta p : todasPerguntasDaFase) {
            boolean usadaGlobalmente = perguntasJaUtilizadasGlobalmente.contains(p);
            boolean respondidaPeloJogadorAtual = perguntasRespondidas.get(jogadorAtual).contains(p);

            if (!usadaGlobalmente && !respondidaPeloJogadorAtual) {
                perguntasRealmenteDisponiveis.add(p);
            }
        }

        if (perguntasRealmenteDisponiveis.isEmpty()) {

            fasePorJogador.put(jogadorAtual, faseNumeroJogador + 1);

            if (verificarFimDoJogo()) {
                return;
            }
            carregarProximaPergunta();
            return;
        }

        perguntaAtual = perguntasRealmenteDisponiveis.get(new Random().nextInt(perguntasRealmenteDisponiveis.size()));

        perguntasRespondidas.get(jogadorAtual).add(perguntaAtual);
        perguntasJaUtilizadasGlobalmente.add(perguntaAtual);

        cabecalhoLabel.setText("Jogador: " + nicks[jogadorAtual] + " | Fase " + faseNumeroJogador + ": " + nomeFase);
        perguntaLabel.setText("<html><body style='text-align: center;'>" + perguntaAtual.getPergunta() + "</body></html>");

        String[] alternativas = perguntaAtual.getAlternativas();
        for (int i = 0; i < alternativasRadio.length; i++) {
            if (i < alternativas.length) {
                alternativasRadio[i].setText((char) ('A' + i) + ") " + alternativas[i]);
                alternativasRadio[i].setSelected(false);
                alternativasRadio[i].setVisible(true);
            } else {
                alternativasRadio[i].setVisible(false);
            }
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

                    if (vidasJogadores[jogadorAtual] == 0) {
                        JOptionPane.showMessageDialog(JogoService.this, "Jogador " + nicks[jogadorAtual] + " foi eliminado!");
                    }

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
            JOptionPane.showMessageDialog(this, "Erro ao processar a resposta. A pergunta pode não ter sido carregada corretamente.");
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

        boolean todosCompletaramOuForamEliminados = true;
        if (jogadoresAtivos == 0 && nicks.length > 0) {
            todosCompletaramOuForamEliminados = true;
        } else {
            for (int i = 0; i < nicks.length; i++) {
                if (vidasJogadores[i] > 0 && fasePorJogador.get(i) <= perguntaService.getFases().size()) {
                    todosCompletaramOuForamEliminados = false;
                    break;
                }
            }
        }

        if (todosCompletaramOuForamEliminados) {
            StringBuilder msgFinal = new StringBuilder("Fim de jogo!\n\nRanking final:\n");
            List<Map.Entry<String, Integer>> rankingList = new ArrayList<>();
            for(int i=0; i < nicks.length; i++){
                rankingList.add(new AbstractMap.SimpleEntry<>(nicks[i], pontuacoes[i]));
            }
            rankingList.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));


            for (Map.Entry<String, Integer> entry : rankingList) {
                String nickJogador = entry.getKey();
                int pontuacaoJogador = entry.getValue();
                int indiceOriginal = -1;
                for(int j=0; j<nicks.length; j++){
                    if(nicks[j].equals(nickJogador)){
                        indiceOriginal = j;
                        break;
                    }
                }

                try {
                    rankingService.atualizarRanking(nickJogador, pontuacaoJogador);
                } catch (Exception e) {
                    System.err.println("Erro ao atualizar ranking para " + nickJogador + ": " + e.getMessage());
                }
                msgFinal.append(nickJogador).append(" - ").append(pontuacaoJogador).append(" pontos");
                if (vidasJogadores[indiceOriginal] == 0) {
                    msgFinal.append(" (Eliminado)");
                } else if (fasePorJogador.get(indiceOriginal) > perguntaService.getFases().size()){
                    msgFinal.append(" (Concluiu todas as fases)");
                }
                msgFinal.append("\n");
            }

            JOptionPane.showMessageDialog(this, msgFinal.toString(), "Fim de Jogo - Ranking", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            return true;
        }
        return false;
    }
}