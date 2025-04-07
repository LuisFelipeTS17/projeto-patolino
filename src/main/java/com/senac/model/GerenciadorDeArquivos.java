package com.senac.model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GerenciadorDeArquivos {
    private static final String ARQUIVO = "src/main/resources/perguntas.txt";

    public List<Pergunta> carregarPerguntas() {
        List<Pergunta> perguntas = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(ARQUIVO))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                String[] partes = linha.split("\\|");
                if (partes.length == 7) {
                    String[] alternativas = {
                            partes[1].trim(), partes[2].trim(),
                            partes[3].trim(), partes[4].trim()
                    };
                    perguntas.add(new Pergunta(
                            partes[0].trim(),
                            alternativas,
                            partes[5].trim(),
                            partes[6].trim()
                    ));
                }
            }
        } catch (IOException e) {
            System.out.println("Erro ao carregar perguntas: " + e.getMessage());
        }
        return perguntas;
    }

    public void salvarPerguntas(List<Pergunta> perguntas) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO))) {
            for (Pergunta p : perguntas) {
                writer.write(p.getPergunta() + " | "
                        + String.join(" | ", p.getAlternativas()) + " | "
                        + p.getResposta() + " | " + p.getFase() + "\n");
            }
        } catch (IOException e) {
            System.out.println("Erro ao salvar perguntas: " + e.getMessage());
        }
    }
}
