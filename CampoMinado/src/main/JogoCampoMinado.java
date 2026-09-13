package main;

import java.util.Scanner;
import model.Tabuleiro;

/**
 * Loop principal do jogo, via console. Esta classe só conversa com
 * {@link Tabuleiro} — nunca acessa {@link Celula} diretamente, respeitando
 * o encapsulamento sugerido na arquitetura do enunciado.
 */
public class JogoCampoMinado {

    public static void main(String[] args) {
        Scanner teclado = new Scanner(System.in);

        System.out.println("=== Campo Minado ===");
        System.out.println("(O primeiro clique nunca é uma mina — as minas só são sorteadas depois dele.)");
        int linhas = lerInteiro(teclado, "Número de linhas: ");
        int colunas = lerInteiro(teclado, "Número de colunas: ");
        int minas = lerInteiro(teclado, "Número de minas: ");

        Tabuleiro tabuleiro = new Tabuleiro(linhas, colunas, minas);

        while (!tabuleiro.isJogoEncerrado()) {
            tabuleiro.imprimir(false);
            System.out.println();
            System.out.println("Comandos: 'r linha coluna' revelar | 'm linha coluna' marcar/desmarcar "
                    + "| 'v linha coluna' revelar vizinhos (jogada em grupo)");
            System.out.print("> ");

            String comando = teclado.next();
            int linha = teclado.nextInt();
            int coluna = teclado.nextInt();

            if (comando.equalsIgnoreCase("r")) {
                tabuleiro.revelar(linha, coluna);
            } else if (comando.equalsIgnoreCase("m")) {
                tabuleiro.alternarMarcacao(linha, coluna);
            } else if (comando.equalsIgnoreCase("v")) {
                tabuleiro.revelarVizinhos(linha, coluna);
            } else {
                System.out.println("Comando inválido. Use 'r', 'm' ou 'v'.");
            }
        }

        tabuleiro.imprimir(true);
        if (tabuleiro.isDerrota()) {
            System.out.println("\nVocê pisou em uma mina. Fim de jogo!");
        } else {
            System.out.println("\nParabéns, você venceu! Todas as células seguras foram reveladas.");
        }

        teclado.close();
    }

    private static int lerInteiro(Scanner teclado, String mensagem) {
        System.out.print(mensagem);
        int valor = teclado.nextInt();
        return valor;
    }
}
