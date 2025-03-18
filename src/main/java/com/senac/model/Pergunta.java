package com.senac.model;

public class Pergunta{
    private String pergunta;
    private String resposta;
    private String fase;

    public Pergunta(String pergunta, String resposta, String fase) {
        this.pergunta = pergunta;
        this.resposta = resposta.toLowerCase();
        this.fase = fase;
    }

    public String getPergunta() {
        return pergunta;
    }

    public String getResposta(){
        return resposta;
    }

    public String getFase() {
        return fase;
    }

    public boolean verificarResposta(String respostaUsuario) {
        return this.resposta.equals(respostaUsuario.toLowerCase());
    }
}
