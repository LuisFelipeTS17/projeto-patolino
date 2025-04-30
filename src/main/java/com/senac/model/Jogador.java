package com.senac.model;

public class Jogador {
    private String nick;
    private int pontuacao;

    public Jogador(String nick, int pontucao) {
        this.nick = nick;
        this.pontuacao = pontuacao;
        System.out.println("[DEBUG] Jogador criado" + nick + ", Pontuação:" + pontuacao);
    }

    public String getNick(){
        return nick;
    }

    public int getPontuacao() {
        return pontuacao;
    }

    @Override
    public String toString() {
        return nick + "|" + pontuacao;
    }

    public static Jogador fromString(String linha) {
        String[] partes = linha.split("\\|");
        System.out.println("[DEBUG] Convertendo linha para jogador" + linha); // depuração
        return new Jogador(partes[0], Integer.parseInt(partes[1]));
    }
}
