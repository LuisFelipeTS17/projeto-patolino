package com.senac.service;

import com.senac.model.Pergunta;
import com.senac.model.Jogador;
import java.io.*;
import java.util.*;

public class PerguntaService {
    private List<Pergunta> perguntas = new ArrayList<>();
    private int faseAtual = 1;
    String[] alternativas = {"a", "b", "c", "d"};
    private static final String ARQUIVO = "src/main/resources/perguntas.txt";
    private static final String RANKING_ARQUIVO = "src/main/resources/ranking.txt"; // Ranking
    private static List<String> fases = new ArrayList<>();

    public PerguntaService() {
        carregarDados();
        Set<String> fasesSet = new HashSet<>();
        for (Pergunta p : perguntas) {
            fasesSet.add(p.getFase());
        }
        fases.addAll(fasesSet);
    }

    public void jogar() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Digite seu nick: ");
        String nick = scanner.nextLine();
        int pontos = 0; // Pontuação começa em 0

        int vidas = 3;
        if (faseAtual > fases.size()) {
            System.out.println("Parabéns! Você concluiu todas as fases!");
            return;
        }

        String faseNome = fases.get(faseAtual - 1);

        List<Pergunta> perguntasFase = new ArrayList<>(perguntas.stream()
                .filter(p -> p.getFase().equalsIgnoreCase(faseNome))
                .toList());

        if (perguntasFase.isEmpty()) {
            System.out.println("Nenhuma pergunta encontrada para esta fase.");
            return;
        }

        Collections.shuffle(perguntasFase);
        int acertos = 0;

        for (Pergunta p : perguntasFase) {
            System.out.println("\nPergunta: " + p.getPergunta());
            Map<String, String> alternativasMap = new HashMap<>();
            for (int i = 0; i < p.getAlternativas().length; i++) {
                alternativasMap.put(alternativas[i], p.getAlternativas()[i]);
                System.out.println(alternativas[i] + ") " + p.getAlternativas()[i]);
            }

            System.out.println("Você tem 5 minutos para responder...");
            final String[] respostaUsuario = {null};

            Thread inputThread = new Thread(() -> {
                respostaUsuario[0] = scanner.nextLine();
            });
            inputThread.start();

            try {
                inputThread.join(300000); // 5 minutos
                if (respostaUsuario[0] == null) {
                    System.out.println("⏰ Tempo esgotado! Resposta errada.");
                    vidas--;
                    if (vidas == 0) {
                        System.out.println("Você perdeu todas as vidas! O jogo recomeçará!");
                        vidas = 3;
                        faseAtual--;
                        break;
                    }
                    continue;
                }
            } catch (InterruptedException e) {
                System.out.println("Erro no temporizador.");
            }

            String alternativaEscolhida = respostaUsuario[0];
            if (alternativasMap.containsKey(alternativaEscolhida) &&
                    alternativasMap.get(alternativaEscolhida).equalsIgnoreCase(p.getResposta())) {
                System.out.println("✅ Resposta correta!");
                acertos++;
                pontos += 10; // ✅ Ganha 10 pontos por acerto
            } else {
                vidas--;
                System.out.println("❌ Resposta errada! A resposta correta era: " + p.getResposta());
                if (vidas == 0) {
                    System.out.println("Você perdeu todas as vidas! O jogo recomeçará!");
                    vidas = 3;
                    faseAtual--;
                    break;
                }
            }
        }

        if (acertos == perguntasFase.size()) {
            System.out.println("🎉 Você avançou para a próxima fase!");
            faseAtual++;
        } else {
            System.out.println("Tente novamente para avançar de fase.");
        }

        // ✅ Após o jogo/fase
        Jogador jogador = new Jogador(nick, pontos);
        salvarRanking(jogador);

        List<Jogador> ranking = carregarRanking();
        ranking.sort((j1, j2) -> Integer.compare(j2.getPontuacao(), j1.getPontuacao())); // ordena decrescente

        int posicao = 1;
        for (Jogador j : ranking) {
            if (j.getNick().equals(jogador.getNick()) && j.getPontuacao() == jogador.getPontuacao()) {
                break;
            }
            posicao++;
        }

        System.out.println("\n🦆 FIM DE JOGO!");
        System.out.println("Patolino diz: O mago foi implacável" + jogador.getNick() + "!");
        System.out.println("Você fez " + jogador.getPontuacao() + " pontos e ficou em " + posicao + "º lugar no ranking!");
    }

    private void salvarRanking(Jogador jogador) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(RANKING_ARQUIVO, true))) {
            writer.write(jogador.getNick() + " | " + jogador.getPontuacao() + " pontos\n");
        } catch (IOException e) {
            System.out.println("Erro ao salvar no ranking.");
        }
    }

    private List<Jogador> carregarRanking() {
        List<Jogador> ranking = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(RANKING_ARQUIVO))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                String[] partes = linha.split("\\|");
                if (partes.length == 2) {
                    String nick = partes[0].trim();
                    int pontos = Integer.parseInt(partes[1].replace("pontos", "").trim());
                    ranking.add(new Jogador(nick, pontos));
                }
            }
        } catch (IOException e) {
            System.out.println("Erro ao carregar o ranking.");
        }
        return ranking;
    }

    private void salvarDados() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO))) {
            for (Pergunta p : perguntas) {
                writer.write(p.getPergunta() + " | " +
                        p.getAlternativas()[0] + " | " +
                        p.getAlternativas()[1] + " | " +
                        p.getAlternativas()[2] + " | " +
                        p.getAlternativas()[3] + " | " +
                        p.getResposta() + " | " +
                        p.getFase() + "\n");
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
                    String[] alternativas = {
                            partes[1].trim(), partes[2].trim(),
                            partes[3].trim(), partes[4].trim()
                    };
                    perguntas.add(new Pergunta(
                            partes[0].trim(),
                            alternativas,
                            partes[5].trim(),
                            partes[6].trim()));
                }
            }
        } catch (IOException e) {
            System.out.println("Nenhuma pergunta encontrada. Começando novo jogo.");
        }
    }
}
