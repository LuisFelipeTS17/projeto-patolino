package com.senac.service;

public class PatolinoGm {

    public static void main(String[] args) {
        introducao();
    }

    public static void introducao() {

            escrever("🧙‍♂️Patolino... outora um misero mago de magia, dominava feitiços comuns.System.err.println.");
            escrever("Mas algo dentro dele ansiava por mais...");
            escrever("Ele descobriu  um grimório antigo, perdido entre código e curcuitos.");
            escrever("📘Seu nome: Algoritmos e Estruturas de Dados.");
            escrever("");
            escrever("Foi então que Patolino decidiu transcender a magia tradicional...");
            escrever("E se tornar o SUPREMO MAGO DA PROGRAMAÇÃO!💻✨");
            escrever("");
            escrever("Sua missão? Derrotar os 6 grandes chefões do conhecimento em java.");
            escrever("Mas cuidado... a cada resposta errada, uma vida será perdida.");
            escrever("Se todas as vidas se forem, o mago cairá... e tudo começará de novo.💀");
            escrever("");
            escrever("Prepare sua mente.");
            escrever("Afie sua lógica.");
            escrever("E entre do reino encantada dos códigos com Patolino!");
            escrever("🔥O desafio vai começar...🔥");
            System.out.println("\n==========================================\n");

    
    }
        public static void escrever(String texto){
            try{
            System.out.println(texto);
            Thread.sleep(1500);
        }catch (InterruptedException e) {
            System.out.println("Algo deu errado na introdução");
        }
    }
}
