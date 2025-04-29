package com.senac.model;

public class Jogador {
    private String nick;
    private int pontuacao;

    public Jogador (String nick, int pontuacao) {
        this.nick = nick;
        this.pontuacao = pontuacao;
    }

    public String getNick() {
        return nick;
    }

    public int getPontuacao() {
        return pontuacao;
    }

    @Override
    public String toString() {
        return nick + " | " + pontuacao + "pontos";
    }
}
