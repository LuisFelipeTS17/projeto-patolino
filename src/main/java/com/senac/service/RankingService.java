package com.senac.service;

import com.senac.model.Jogador;

import javax.swing.*; // Importa componentes Swing
import java.awt.*;    // Importa componentes AWT como Font, BorderLayout, Frame, etc.
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections; // Para Collections.sort se preferir
import java.util.Comparator;  // Para Comparator.comparingInt
import java.util.List;

public class RankingService {
    private static final String ARQUIVO_RANKING = "src/main/resources/ranking.txt";

    public List<Jogador> carregarRanking() {
        List<Jogador> ranking = new ArrayList<>();
        File arquivo = new File(ARQUIVO_RANKING);

        if (!arquivo.exists()) {
            System.out.println("Arquivo de ranking não encontrado: " + ARQUIVO_RANKING + ". Um novo será criado ao salvar.");
            return ranking;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                if (linha.trim().isEmpty()) {
                    continue;
                }
                try {
                    ranking.add(Jogador.fromString(linha));
                } catch (IllegalArgumentException e) {
                    System.err.println("Linha ignorada no arquivo de ranking devido a formato inválido ('" + linha + "'): " + e.getMessage());
                }
            }
        } catch (FileNotFoundException e) {

            System.err.println("Erro crítico: Arquivo de ranking não encontrado apesar da verificação prévia. " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Erro ao carregar o ranking do arquivo " + ARQUIVO_RANKING + ": " + e.getMessage());
        }
        return ranking;
    }

    public void salvarRanking(List<Jogador> ranking) {
        File arquivo = new File(ARQUIVO_RANKING);
        try {
            File diretorioPai = arquivo.getParentFile();
            if (diretorioPai != null && !diretorioPai.exists()) {
                if (!diretorioPai.mkdirs()) {
                    System.err.println("Falha ao criar o diretório para o arquivo de ranking: " + diretorioPai.getAbsolutePath());
                    return;
                }
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(arquivo))) {
                for (Jogador jogador : ranking) {
                    writer.write(jogador.toString());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar o ranking no arquivo " + ARQUIVO_RANKING + ": " + e.getMessage());
        }
    }

    public void atualizarRanking(String nick, int pontuacao) {
        List<Jogador> ranking = carregarRanking();
        Jogador jogadorExistente = null;

        for (Jogador jogador : ranking) {
            if (jogador.getNick().equalsIgnoreCase(nick)) {
                jogadorExistente = jogador;
                break;
            }
        }

        if (jogadorExistente != null) {
            if (pontuacao > jogadorExistente.getPontuacao()) {
                jogadorExistente.setPontuacao(pontuacao);
            }
        } else {
            ranking.add(new Jogador(nick, pontuacao));
        }

        // ranking.sort(Comparator.comparingInt(Jogador::getPontuacao).reversed());
        salvarRanking(ranking);
    }

    public void exibirRanking() {
        exibirRanking(null);
    }

    public void exibirRanking(Frame owner) {
        List<Jogador> ranking = carregarRanking();
        ranking.sort(Comparator.comparingInt(Jogador::getPontuacao).reversed());

        final StringBuilder rankingTextBuilder = new StringBuilder();
        rankingTextBuilder.append("======= RANKING DE JOGADORES =======\n\n");

        if (ranking.isEmpty()) {
            rankingTextBuilder.append("Ainda não há jogadores no ranking.\n");
        } else {
            for (int i = 0; i < ranking.size(); i++) {
                Jogador jogador = ranking.get(i);
                String medalha = "";
                if (i < 3) {
                    switch (i) {
                        case 0: medalha = "🥇 "; break;
                        case 1: medalha = "🥈 "; break;
                        case 2: medalha = "🥉 "; break;
                    }
                }
                rankingTextBuilder.append(String.format("%2d. %s%s - %d pontos\n",
                        (i + 1),
                        medalha,
                        jogador.getNick(),
                        jogador.getPontuacao()));
            }
        }

        SwingUtilities.invokeLater(() -> {
            JDialog rankingDialog = new JDialog(owner, "Ranking de Jogadores", true);
            rankingDialog.setLayout(new BorderLayout(10, 10));

            JTextArea textArea = new JTextArea(rankingTextBuilder.toString());
            textArea.setEditable(false);
            textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
            textArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JScrollPane scrollPane = new JScrollPane(textArea);
            rankingDialog.add(scrollPane, BorderLayout.CENTER);

            JButton okButton = new JButton("OK");
            okButton.addActionListener(e -> rankingDialog.dispose());

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            buttonPanel.add(okButton);
            rankingDialog.add(buttonPanel, BorderLayout.SOUTH);

            rankingDialog.setSize(450, 400);
            rankingDialog.setLocationRelativeTo(owner);
            rankingDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            rankingDialog.setVisible(true);
        });
    }

    public String getRankingComoStringFormatada() {
        List<Jogador> ranking = carregarRanking();
        ranking.sort(Comparator.comparingInt(Jogador::getPontuacao).reversed());

        StringBuilder sb = new StringBuilder();
        if (ranking.isEmpty()) {
            sb.append("Ainda não há jogadores no ranking.\n");
        } else {
            for (int i = 0; i < ranking.size(); i++) {
                Jogador jogador = ranking.get(i);
                String medalha = "";
                if (i < 3) {
                    switch (i) {
                        case 0: medalha = "🥇 "; break;
                        case 1: medalha = "🥈 "; break;
                        case 2: medalha = "🥉 "; break;
                    }
                }
                sb.append(String.format("%2d. %s%s - %d pontos\n",
                        (i + 1),
                        medalha,
                        jogador.getNick(),
                        jogador.getPontuacao()));
            }
        }
        return sb.toString();
    }
}