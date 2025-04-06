package com.senac.model;

public class Pergunta{
    private String pergunta;
    private String[] alternativas;
    private String resposta;
    private String fase;

    public Pergunta(String pergunta, String[] alternativas, String resposta, String fase) {
        this.pergunta = pergunta;
        this.alternativas = alternativas;
        this.resposta = resposta.toLowerCase();
        this.fase = fase;
    }

    public String getPergunta() {
        return pergunta;
    }

    public String[] getAlternativas() {
        return alternativas;
    }

    public String getResposta(){
        return resposta;
    }

    public String getFase() {
        return fase;
    }

    public boolean verificarResposta(String respostaUsuario) {
        return this.resposta.equalsIgnoreCase(respostaUsuario);
    }
}
