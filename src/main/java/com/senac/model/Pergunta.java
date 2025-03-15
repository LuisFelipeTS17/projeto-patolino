package com.senac.model;

public class Pergunta{
    private String pergunta;
    private String resposta;

    public Pergunta(String pergunta, String resposta) {
        this.pergunta = pergunta;
        this.resposta = resposta.toLowerCase();
    }

    public String getPergunta() {
        return pergunta;
    }

    public String getResposta(){
        return resposta;
    }

    public boolean verificarResposta(String respostaUsuario) {
        return this.resposta.equals(respostaUsuario.toLowerCase());
    }
}
