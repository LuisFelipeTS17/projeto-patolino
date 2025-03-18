package com.senac.service;

import com.senac.model.Pergunta;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class PerguntaService {
    private List<Pergunta> perguntas = new ArrayList<>();
    private int faseAtual = 1;
    private static final String ARQUIVO = "src/main/resources/perguntas.dat";
    private static final List<String> FASES = List.of("Estrutura de Dados", "Algoritmos");

    public PerguntaService() {
        carregarDados();
    }

    public void adicionarPergunta(String pergunta, String resposta, String fase) {
        perguntas.add(new Pergunta(pergunta, resposta, fase));
        salvarDados();
    }

    public void jogar() {
        if (faseAtual > FASES.size()) {
            System.out.println("Parabéns! Você concluiu todas as fases!");
            return;
        }

        String faseNome = FASES.get(faseAtual - 1);
        List<Pergunta> perguntasFase = perguntas.stream()
                .filter(p -> p.getFase().equalsIgnoreCase(faseNome))
                .toList();

        if (perguntasFase.isEmpty()) {
            System.out.println("Nenhuma pergunta encontrada para esta fase.");
            return;
        }

        Collections.shuffle(perguntasFase);
        Scanner scanner = new Scanner(System.in);
        int acertos = 0;

        for (Pergunta p : perguntasFase) {
            System.out.println("Pergunta: " + p.getPergunta());
            System.out.print("Resposta: ");
            String respostaUsuario = scanner.nextLine();

            if (p.verificarResposta(respostaUsuario)) {
                System.out.println("Resposta correta!");
                acertos++;
            } else {
                System.out.println("Resposta errada! A resposta correta era: " + p.getResposta());
            }
        }

        if (acertos >= (perguntasFase.size() / 2)) {
            System.out.println("Você avançou para a próxima fase!");
            faseAtual++;
        } else {
            System.out.println("Tente novamente para avançar de fase.");
        }
    }

    private void salvarDados() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO))) {
            for (Pergunta p : perguntas) {
                writer.write(p.getPergunta() + " | " + p.getResposta() + " | " + p.getFase() + "\n");
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
                if (partes.length == 3) {
                    perguntas.add(new Pergunta(partes[0].trim(), partes[1].trim(), partes[2].trim()));
                }
            }
        } catch (IOException e) {
            System.out.println("Nenhuma pergunta encontrada. Começando novo jogo.");
        }
    }
}
