package com.senac.service;

import com.senac.model.GerenciadorDeArquivos;

import javax.swing.*;
import java.awt.*;

public class Menu extends JFrame {
    private GerenciadorDeArquivos arquivos = new GerenciadorDeArquivos();
    private PerguntaService perguntaService = new PerguntaService(arquivos);
    private RankingService rankingService = new RankingService();
    private JogoService jogoService = new JogoService(perguntaService, rankingService);

    public Menu(){
        setTitle("Jogo de Perguntas");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        JLabel titleLabel = new JLabel("MENU PRINCIPAL", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JButton btnJogar = new JButton("Jogar");
        JButton btnRanking = new JButton("Ver Ranking");
        JButton btnSair = new JButton("Sair");

        btnJogar.addActionListener(e -> iniciarJogo());
        btnRanking.addActionListener(e -> rankingService.exibirRanking());
        btnSair.addActionListener(e -> System.exit(0));

        panel.add(titleLabel);
        panel.add(btnJogar);
        panel.add(btnRanking);
        panel.add(btnSair);

        add(panel);
    }

    private void iniciarJogo() {
        String input = JOptionPane.showInputDialog(this, "Quantos jogadores vão jogar? (1 a 4):");
        if (input == null) return;

        try {
            int total = Integer.parseInt(input);

            if (total < 1 || total > 4) {
                JOptionPane.showMessageDialog(this, "Número inválido. Digite entre 1 e 4.");
                return;
            }

            String[] nicks = new String[total];
            for (int i = 0; i < total; i++) {
                nicks[i] = JOptionPane.showInputDialog(this, "Jogador " + (i + 1) + ", digite seu nick:");
                if (nicks[i] == null || nicks[i].trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Nick inválido!");
                    return;
                }
            }

            jogoService.jogar(nicks);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Digite um número válido!");
        }
    }

}
