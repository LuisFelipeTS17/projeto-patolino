package com.senac.service;

import com.senac.model.GerenciadorDeArquivos;
import com.senac.model.Pergunta;

import java.util.*;

public class PerguntaService {
    private List<Pergunta> perguntas = new ArrayList<>();
    private int faseAtual = 1;
    private static List<String> fases = new ArrayList<>();
    private final Map<String, Integer> prioridadeFases = new HashMap<>();
    private final GerenciadorDeArquivos gerenciadorDeArquivos;
    String[] frasesAcerto = {
            "Pelo vórtice do saber arcano... Você acertou, jovem aprendiz!",
            "O Mago Implacável te saúda! Esse acerto ecoará por dimensões!",
            "Com um estalar de dedos e sabedoria... a resposta correta surgiu!",
            "Você canalizou a essência da verdade! Muito bem, pequeno conjurador!",
            "Celestia sorri para ti... pois sua mente brilhou como o sol negro!",
            "Pelas pedras de Prophynia! Você dominou esta pergunta com maestria!",
            "O Místico Mago reconhece seu poder... continue nesse caminho!",
            "O cubo mágico explodiu em celebração! Acerto mágico registrado!",
            "Você provou ser digno do poder infinito... por enquanto.",
            "Ahh... até os dragões pararam para aplaudir essa resposta!"
    };

    String[] frasesErro = {
            "Sob o olhar do necromante... essa resposta caiu em ruína.",
            "As estrelas se alinharam... mas não a seu favor.",
            "O Garlon riu... e a resposta se perdeu no abismo.",
            "Tsc, tsc... nem todo aprendiz acerta sempre. Tente de novo.",
            "O fogo sagrado não te iluminou desta vez.",
            "O cubo mágico tremeu... e explodiu em vergonha.",
            "O mago observa em silêncio... e nega com a cabeça.",
            "Ahh, jovem tolo... até um dragão saberia essa.",
            "A Escada Prateada não se ergueu... você tropeçou no primeiro degrau.",
            "Você dormiu como Patolino por três dias... e ainda respondeu errado!"
    };


    public PerguntaService(GerenciadorDeArquivos gerenciadorDeArquivos) {
        this.gerenciadorDeArquivos = gerenciadorDeArquivos;
        carregarPrioridades();
        this.perguntas = gerenciadorDeArquivos.carregarPerguntas();
        organizarFases();
    }

    private void carregarPrioridades() {
        prioridadeFases.put("Java Básico", 1);
        prioridadeFases.put("Variáveis", 2);
        prioridadeFases.put("Operadores", 3);
        prioridadeFases.put("Estruturas de controle", 4);
        prioridadeFases.put("POO", 5);
    }

    private void organizarFases() {
        Set<String> fasesSet = new HashSet<>();
        for (Pergunta p : perguntas) {
            fasesSet.add(p.getFase());
        }
        fases.clear();
        fases.addAll(fasesSet);

        fases.sort(Comparator.comparingInt(f -> prioridadeFases.getOrDefault(f, Integer.MAX_VALUE)));
    }

    public List<Pergunta> getPerguntasDaFaseAtual() {
        if (faseAtual > fases.size()) return Collections.emptyList();
        String faseNome = fases.get(faseAtual - 1);
        return new ArrayList<>(perguntas.stream()
                .filter(p -> p.getFase().equalsIgnoreCase(faseNome))
                .toList());
    }

    public String getMensagemDeAcerto(){
        Random random = new Random();
        int index = random.nextInt(frasesAcerto.length);
        return frasesAcerto[index];
    }

    public String getMensagemDeErro(){
        Random random = new Random();
        int index = random.nextInt(frasesErro.length);
        return frasesErro[index];
    }

    public String getFaseAtual(){
        return faseAtual + " - " + fases.get(faseAtual -1);
    }

    public void avancarFase() {
        faseAtual++;
    }

    public void reiniciarFase() {
        faseAtual--;
    }

    public boolean jogoConcluido() {
        return faseAtual > fases.size();
    }

    public void salvar() {
        gerenciadorDeArquivos.salvarPerguntas(perguntas);
    }

    public void adicionarPergunta(Pergunta p) {
        perguntas.add(p);
        salvar();
    }

}
