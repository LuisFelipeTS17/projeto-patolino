package com.senac.service;

import com.senac.model.Pergunta;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PerguntaService {
    private List<Pergunta> perguntas = new ArrayList<>();
    private String[] texto = new String[5];
    private int nivel = 1;
    private static final String ARQUIVO = "src/main/resources/perguntas.dat";

    public PerguntaService() {
        carregarDados();
    }

    public void adicionarPergunta(String pergunta, String resposta) {
        perguntas.add(new Pergunta(pergunta, resposta));
        salvarDados();
    }

    public void jogar() {
        Scanner scanner = new Scanner(System.in);
        for (Pergunta p : perguntas) {
            System.out.println("Pergunta: " + p.getPergunta());
            System.out.print("Resposta: ");
            String respostaUsuario = scanner.nextLine();

            if (p.verificarResposta(respostaUsuario)) {
                System.out.println("Resposta correta!");
            } else {
                System.out.println("Resposta errada!");
            }
        }
    }

    private void salvarDados() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO))) {
            for (Pergunta p : perguntas) {
                writer.write(p.getPergunta() + "|" + p.getResposta() + "\n");
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
                if (partes.length == 2) {
                    perguntas.add(new Pergunta(partes[0], partes[1]));
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Nenhuma pergunta encontrada. Começando novo jogo.");
        }
    }
}
