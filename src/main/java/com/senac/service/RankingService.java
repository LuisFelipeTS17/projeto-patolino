package com.senac.service;

import com.senac.model.Jogador;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class RankingService {
    private static final String ARQUIVO_RANKING = "src/main/resources/ranking.txt";

    public List<Jogador> carregarRanking() {
        List<Jogador> ranking = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(ARQUIVO_RANKING))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                try {
                    ranking.add(Jogador.fromString(linha));
                } catch (IllegalArgumentException e) {
                    System.out.println("Linha ignorada no ranking: " + e.getMessage());
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Arquivo de ranking não encontrado. Um novo será criado.");
        } catch (IOException e) {
            System.out.println("Erro ao carregar o ranking: " + e.getMessage());
        }
        return ranking;
    }

    public void salvarRanking(List<Jogador> ranking) {
        File arquivo = new File(ARQUIVO_RANKING);
        try {
            // Garantir que o diretório existe
            File diretorio = arquivo.getParentFile();
            if (!diretorio.exists()) {
                diretorio.mkdirs();
            }

            // Salvar o ranking no arquivo
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(arquivo))) {
                for (Jogador jogador : ranking) {
                    writer.write(jogador.toString());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Erro ao salvar o ranking: " + e.getMessage());
        }
    }

    public void atualizarRanking(String nick, int pontuacao) {
        List<Jogador> ranking = carregarRanking();
        boolean jogadorExistente = false;

        for (Jogador jogador : ranking) {
            if (jogador.getNick().equalsIgnoreCase(nick)) {
                jogadorExistente = true;
                if (pontuacao > jogador.getPontuacao()) {
                    jogador.setPontuacao(pontuacao);
                }
                break;
            }
        }

        if (!jogadorExistente) {
            ranking.add(new Jogador(nick, pontuacao));
        }

        salvarRanking(ranking);
    }

    public void exibirRanking() {
        List<Jogador> ranking = carregarRanking();
        ranking.sort((j1, j2) -> Integer.compare(j2.getPontuacao(), j1.getPontuacao()));

        System.out.println("\n===== RANKING =====");
        for (int i = 0; i < ranking.size(); i++) {
            Jogador jogador = ranking.get(i);
            String medalha = switch (i) {
                case 0 -> "🥇";
                case 1 -> "🥈";
                case 2 -> "🥉";
                default -> "";
            };
            System.out.println((i + 1) + "º " + medalha + " " + jogador.getNick() + " - " + jogador.getPontuacao() + " pontos");
        }
    }
}
