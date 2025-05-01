package com.senac.service;

import com.senac.model.Jogador;
import java.io.*;
import java.util.*;

public class RankingService {
    private final List<Jogador> ranking = new ArrayList<>();
    private static final String ARQUIVO = "src/main/resources/ranking.txt"; // novo arquivo

    public RankingService() {
        System.out.println("[DEBUG] Inicializando RankingService..."); // depuração
        carregarRanking(); // carrega ranking salvo
    }

    public void adicionarJogador(Jogador jogador) {
        System.out.println("[DEBUG] Adicionando jogador ao ranking:" + jogador.getNick());
        ranking.add(jogador);
        ranking.sort(Comparator.comparingInt(Jogador::getPontuacao).reversed());
        salvarRanking(); // salva a posição do ranking
    }

    public void exibirRanking() {
        System.out.println("\n 🏆 RANKING GERAL:");
        for(int i = 0; i < ranking.size(); i++ ) {
            Jogador jogador = ranking.get(i);
            String medalha = switch (i) {
                case 0 -> "🥇";
                case 1 -> "🥈";
                case 2 -> "🥉";
                default -> (i + 1) +  "º";
            };
            System.out.println(medalha + " " + jogador.getNick() + " - " + jogador.getPontuacao() + "pts");
        }
    }

    private void salvarRanking() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO))) {
            for (Jogador j : ranking) {
                writer.write(j.toString());
                writer.newLine();
            }
            System.out.println("[DEBUG] Ranking salvo com sucesso em" + ARQUIVO);
        } catch (IOException e ) {
            System.out.println("Erro ao salvar ranking" + e.getMessage());
        }
    }

    private void carregarRanking() {
        File arquivo = new File(ARQUIVO);
        if (!arquivo.exists()) {
            System.out.println("[DEBUG] Arquivo de ranking nao encontrado. Será criado após o primeiro jogo.");
            return;
        }
        try (BufferedWriter reader = new BufferedWriter(new FileReader(ARQUIVO))) {
            String linha;
            while((linha = reader.readLine()) !=null) {
                ranking.add(Jogador.fromString(linha));
            }
            System.out.println("[DEBUG] Ranking carregado com" + ranking.size() + "jogadores");
        } catch(IOException e ) {
            System.out.println("Erro ao carregar ranking" + e.getMessage());
        }
    }
}