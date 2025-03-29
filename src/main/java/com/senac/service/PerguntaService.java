package com.senac.service;

import com.senac.model.Pergunta;
import java.io.*;
import java.util.*;

public class PerguntaService {
    private List<Pergunta> perguntas = new ArrayList<>();
    private int faseAtual = 1;
    private static final String ARQUIVO = "src/main/resources/perguntas.txt";
    private static List<String> fases = new ArrayList<>();
    private static Map<String, Integer> prioridadeFases = new HashMap<>();

    public PerguntaService() {
        carregarPrioridades();
        carregarDados();
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

//    public void adicionarPergunta(String pergunta, String[] alternativas, String resposta, String fase) {
//        perguntas.add(new Pergunta(pergunta, alternativas, resposta, fase));
//        salvarDados();
//    }

    public void jogar() {
        if (faseAtual > fases.size()) {
            System.out.println("Parabéns! Você concluiu todas as fases!");
        }

        String faseNome = fases.get(faseAtual - 1);

        List<Pergunta> perguntasFase = new ArrayList<>(perguntas.stream()
                .filter(p -> p.getFase().equalsIgnoreCase(faseNome))
                .toList());

        if (perguntasFase.isEmpty()) {
            System.out.println("Nenhuma pergunta encontrada para esta fase.");
        }

        Collections.shuffle(perguntasFase);
        Scanner scanner = new Scanner(System.in);
        int acertos = 0;

        for (Pergunta p : perguntasFase) {
            System.out.println("Pergunta: " + p.getPergunta());
            Map<String, String> alternativasMap = new HashMap<>();
            String[] alternativas = {"a", "b", "c", "d"};

            for (int i = 0; i < p.getAlternativas().length; i++) {
                alternativasMap.put(alternativas[i], p.getAlternativas()[i]);
                System.out.println(alternativas[i] + ") " + p.getAlternativas()[i]);
            }
            System.out.print("Resposta: ");
            String respostaUsuario = scanner.nextLine();

            if (alternativasMap.get(respostaUsuario).equalsIgnoreCase(p.getResposta())) {
                System.out.println("Resposta correta!");
                acertos++;
            } else {
                System.out.println("Resposta errada! A resposta correta era: " + p.getResposta());
            }
        }

        if (acertos == perguntasFase.size()) {
            System.out.println("Você avançou para a próxima fase!");
            faseAtual++;
        } else {
            System.out.println("Tente novamente para avançar de fase.");
        }
    }

    private void salvarDados() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO))) {
            for (Pergunta p : perguntas) {
                writer.write(p.getPergunta() + " | " + Arrays.stream(p.getAlternativas()).toList() + p.getResposta() + " | " + p.getFase() + "\n");
            }
        } catch (IOException e) {
            System.out.println("Erro ao salvar dados.");
        }
    }

    private void carregarDados() {
        try (BufferedReader reader = new BufferedReader(new FileReader(ARQUIVO))) {
            perguntas.clear();
            String linha;
            while ((linha = reader.readLine()) != null) {
                String[] partes = linha.split("\\|");
                if (partes.length == 7) {
                    String[] alternativas = {partes[1].trim(), partes[2].trim(), partes[3].trim(), partes[4].trim()};
                    perguntas.add(new Pergunta(partes[0].trim(),  alternativas, partes[5].trim(), partes[6].trim()));
                }
            }
        } catch (IOException e) {
            System.out.println("Nenhuma pergunta encontrada. Começando novo jogo.");
        }
    }
}
