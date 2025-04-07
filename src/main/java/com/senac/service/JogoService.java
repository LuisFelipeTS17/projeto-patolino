package com.senac.service;

import com.senac.model.Pergunta;

import java.util.*;

public class JogoService {

    private final PerguntaService perguntaService;

    public JogoService(PerguntaService perguntaService) {
        this.perguntaService = perguntaService;
    }

    public void jogar() {
        Scanner scanner = new Scanner(System.in);
        int vidas = 3;

        while (!perguntaService.jogoConcluido()) {
            List<Pergunta> perguntasFase = perguntaService.getPerguntasDaFaseAtual();

            if (perguntasFase.isEmpty()) {
                System.out.println("Nenhuma pergunta encontrada para esta fase.");
                break;
            }

            Collections.shuffle(perguntasFase);
            int acertos = 0;
            System.out.println("===============================");
            System.out.println("Fase "+perguntaService.getFaseAtual()+" - "+vidas+" vidas restantes");
            for (Pergunta p : perguntasFase) {
                System.out.println("===============================");
                System.out.println("Pergunta: " + p.getPergunta());
                String[] alternativas = {"a", "b", "c", "d"};
                Map<String, String> alternativasMap = new HashMap<>();

                for (int i = 0; i < p.getAlternativas().length; i++) {
                    alternativasMap.put(alternativas[i], p.getAlternativas()[i]);
                    System.out.println(alternativas[i] + ") " + p.getAlternativas()[i]);
                }
                System.out.print("Resposta: ");
                String respostaUsuario = scanner.nextLine();

                if (alternativasMap.get(respostaUsuario).equalsIgnoreCase(p.getResposta())) {
                    System.out.println("===============================");
                    System.out.println("Resposta correta!");
                    System.out.println(perguntaService.getMensagemDeAcerto());
                    acertos++;
                } else {
                    System.out.println("===============================");
                    System.out.println("Resposta errada! A resposta correta era: " + p.getResposta());
                    System.out.println(perguntaService.getMensagemDeErro());
                    vidas--;
                    if (vidas == 0) {
                        System.out.println("Você perdeu todas as vidas! O jogo recomeçara!");
                        vidas = 3;
                        perguntaService.reiniciarFase();
                        break;
                    }
                }
            }

            if (acertos == perguntasFase.size() - 2) {
                System.out.println("Você avançou para a próxima fase!");
                perguntaService.avancarFase();
            } else {
                System.out.println("Tente novamente para avançar de fase.");
            }
        }
        if (perguntaService.jogoConcluido()) {
            System.out.println("Parabéns! Você concluiu todas as fases!");
        }
    }
}
