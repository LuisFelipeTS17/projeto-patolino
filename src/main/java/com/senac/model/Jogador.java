package com.senac.model;

public class Jogador {
    private String nick;
    private int pontuacao;

    public Jogador(String nick, int pontuacao) {
        this.nick = nick;
        this.pontuacao = pontuacao;
    }

    public String getNick() {
        return nick;
    }

    public int getPontuacao() {
        return pontuacao;
    }

    public void setPontuacao(int pontuacao) {
        this.pontuacao = pontuacao;
    }

    @Override
    public String toString() {
        return nick + "|" + pontuacao;
    }

    public static Jogador fromString(String linha) {
        if (linha == null || linha.trim().isEmpty()) {
            throw new IllegalArgumentException("Linha inválida ou vazia.");
        }

        String[] partes = linha.split("\\|");
        if (partes.length != 2) {
            throw new IllegalArgumentException("Formato inválido para a linha: " + linha);
        }

        String nick = partes[0].trim();
        int pontuacao;
        try {
            pontuacao = Integer.parseInt(partes[1].trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Pontuação inválida na linha: " + linha);
        }

        return new Jogador(nick, pontuacao);
    }
}
