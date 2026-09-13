package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Representa o tabuleiro do Campo Minado: uma matriz bidimensional de
 * {@link Celula}. É a única classe que conhece a grade inteira, que sabe
 * posicionar minas e calcular vizinhança.
 * <p>
 * Parte do MODEL na arquitetura MVC. Implementa {@link LeituraTabuleiro}
 * para que a View possa consultar o estado do jogo sem depender da API
 * completa (mutável) desta classe.
 */
public class Tabuleiro implements LeituraTabuleiro {

    private final int linhas;
    private final int colunas;
    private final int numMinas;
    private final Celula[][] grade;

    // Quando o tabuleiro é criado com sorteio (construtor "de jogo"), as
    // minas só são efetivamente posicionadas no primeiro revelar() —
    // veja o comentário em posicionarMinasEvitandoZona() para o motivo.
    private final boolean posicionamentoAleatorio;
    private boolean minasPosicionadas;

    private boolean jogoEncerrado;
    private boolean derrota;

    /**
     * Cria um tabuleiro novo pronto para uma partida sorteada. As minas
     * ainda NÃO são posicionadas aqui: isso só acontece na primeira
     * chamada de {@link #revelar(int, int)}, para garantir que o primeiro
     * clique do jogador nunca resulte em derrota (regra clássica do
     * Campo Minado, adicionada como melhoria sobre o sorteio imediato).
     *
     * @param linhas   número de linhas do tabuleiro
     * @param colunas  número de colunas do tabuleiro
     * @param numMinas quantidade de minas a posicionar
     */
    public Tabuleiro(int linhas, int colunas, int numMinas) {
        if (linhas <= 0 || colunas <= 0) {
            throw new IllegalArgumentException("Linhas e colunas devem ser maiores que zero.");
        }
        if (numMinas < 0 || numMinas >= linhas * colunas) {
            throw new IllegalArgumentException("Número de minas inválido para esse tabuleiro.");
        }

        this.linhas = linhas;
        this.colunas = colunas;
        this.numMinas = numMinas;
        this.grade = new Celula[linhas][colunas];
        this.posicionamentoAleatorio = true;
        this.minasPosicionadas = false;
        inicializarGrade();
    }

    /**
     * Construtor auxiliar que recebe as posições das minas explicitamente,
     * em vez de sortear. Pensado para ser usado em testes unitários, onde
     * é preciso saber exatamente onde as minas estão para verificar o
     * comportamento da cascata e da contagem de vizinhas. Aqui as minas
     * já entram posicionadas, sem o adiamento do primeiro clique.
     *
     * @param linhas         número de linhas do tabuleiro
     * @param colunas        número de colunas do tabuleiro
     * @param posicoesMinas  array de pares {linha, coluna} com as minas
     */
    public Tabuleiro(int linhas, int colunas, int[][] posicoesMinas) {
        this.linhas = linhas;
        this.colunas = colunas;
        this.numMinas = posicoesMinas.length;
        this.grade = new Celula[linhas][colunas];
        this.posicionamentoAleatorio = false;
        this.minasPosicionadas = true;
        inicializarGrade();
        for (int[] posicao : posicoesMinas) {
            grade[posicao[0]][posicao[1]].setMinada(true);
        }
        calcularMinasVizinhasDeTodasAsCelulas();
    }

    private void inicializarGrade() {
        for (int i = 0; i < linhas; i++) {
            for (int j = 0; j < colunas; j++) {
                grade[i][j] = new Celula();
            }
        }
    }

    /**
     * Posiciona as minas aleatoriamente, evitando (sempre que possível) a
     * célula clicada e suas 8 vizinhas — a "zona segura" do primeiro
     * clique. Se o tabuleiro for pequeno demais ou tiver minas demais
     * para respeitar a zona inteira, cai no modo antigo, evitando ao
     * menos a própria célula clicada.
     */
    private void posicionarMinasEvitandoZona(int linhaSegura, int colunaSegura) {
        Random sorteio = new Random();
        int celulasForaDaZona = linhas * colunas - contarCelulasNaZona(linhaSegura, colunaSegura);
        boolean usarZonaSegura = celulasForaDaZona >= numMinas;

        int minasColocadas = 0;
        while (minasColocadas < numMinas) {
            int linha = sorteio.nextInt(linhas);
            int coluna = sorteio.nextInt(colunas);

            if (grade[linha][coluna].isMinada()) {
                continue;
            }
            if (usarZonaSegura && dentroDaZonaSegura(linha, coluna, linhaSegura, colunaSegura)) {
                continue;
            }
            if (!usarZonaSegura && linha == linhaSegura && coluna == colunaSegura) {
                continue;
            }

            grade[linha][coluna].setMinada(true);
            minasColocadas++;
        }
    }

    private int contarCelulasNaZona(int linhaSegura, int colunaSegura) {
        int total = 0;
        for (int deltaLinha = -1; deltaLinha <= 1; deltaLinha++) {
            for (int deltaColuna = -1; deltaColuna <= 1; deltaColuna++) {
                if (dentroDosLimites(linhaSegura + deltaLinha, colunaSegura + deltaColuna)) {
                    total++;
                }
            }
        }
        return total;
    }

    private boolean dentroDaZonaSegura(int linha, int coluna, int linhaSegura, int colunaSegura) {
        return Math.abs(linha - linhaSegura) <= 1 && Math.abs(coluna - colunaSegura) <= 1;
    }

    private void calcularMinasVizinhasDeTodasAsCelulas() {
        for (int i = 0; i < linhas; i++) {
            for (int j = 0; j < colunas; j++) {
                grade[i][j].setMinasVizinhas(contarMinasVizinhas(i, j));
            }
        }
    }

    private int contarMinasVizinhas(int linha, int coluna) {
        int total = 0;
        for (int deltaLinha = -1; deltaLinha <= 1; deltaLinha++) {
            for (int deltaColuna = -1; deltaColuna <= 1; deltaColuna++) {
                if (deltaLinha == 0 && deltaColuna == 0) {
                    continue;
                }
                int vizinhoLinha = linha + deltaLinha;
                int vizinhoColuna = coluna + deltaColuna;
                if (dentroDosLimites(vizinhoLinha, vizinhoColuna)
                        && grade[vizinhoLinha][vizinhoColuna].isMinada()) {
                    total++;
                }
            }
        }
        return total;
    }

    private boolean dentroDosLimites(int linha, int coluna) {
        return linha >= 0 && linha < linhas && coluna >= 0 && coluna < colunas;
    }

    /**
     * Revela a célula indicada. Se a célula não tiver minas vizinhas, o
     * efeito cascata revela automaticamente as células ao redor (e assim
     * sucessivamente), sem nunca revelar uma célula minada por engano.
     * <p>
     * Na primeira chamada de um tabuleiro sorteado, as minas ainda são
     * posicionadas agora mesmo (veja {@link #posicionarMinasEvitandoZona}),
     * evitando a célula clicada — por isso o primeiro clique de uma
     * partida nunca é uma derrota.
     * <p>
     * A cascata é implementada de forma iterativa usando uma
     * {@link ArrayList} como fila de células pendentes de revelação —
     * evita o uso de recursão profunda em tabuleiros grandes.
     *
     * @param linha  linha da célula a revelar
     * @param coluna coluna da célula a revelar
     * @return a lista das células que foram reveladas nesta jogada, na
     *         ordem em que foram reveladas — útil para quem quiser animar
     *         a cascata célula a célula (ex.: a View). Se a jogada não
     *         revelar nada (célula já revelada, marcada, jogo encerrado,
     *         etc.), retorna uma lista vazia.
     */
    public List<int[]> revelar(int linha, int coluna) {
        List<int[]> ordemRevelacao = new ArrayList<>();

        if (jogoEncerrado || !dentroDosLimites(linha, coluna)) {
            return ordemRevelacao;
        }

        Celula celulaInicial = grade[linha][coluna];
        if (celulaInicial.isRevelada() || celulaInicial.isMarcada()) {
            return ordemRevelacao;
        }

        if (posicionamentoAleatorio && !minasPosicionadas) {
            posicionarMinasEvitandoZona(linha, coluna);
            calcularMinasVizinhasDeTodasAsCelulas();
            minasPosicionadas = true;
        }

        if (celulaInicial.isMinada()) {
            celulaInicial.revelar();
            jogoEncerrado = true;
            derrota = true;
            ordemRevelacao.add(new int[] { linha, coluna });
            return ordemRevelacao;
        }

        List<int[]> pendentes = new ArrayList<>();
        pendentes.add(new int[] { linha, coluna });

        while (!pendentes.isEmpty()) {
            int[] posicaoAtual = pendentes.remove(pendentes.size() - 1);
            int linhaAtual = posicaoAtual[0];
            int colunaAtual = posicaoAtual[1];
            Celula atual = grade[linhaAtual][colunaAtual];

            if (atual.isRevelada() || atual.isMarcada() || atual.isMinada()) {
                continue;
            }

            atual.revelar();
            ordemRevelacao.add(new int[] { linhaAtual, colunaAtual });

            if (atual.getMinasVizinhas() == 0) {
                for (int deltaLinha = -1; deltaLinha <= 1; deltaLinha++) {
                    for (int deltaColuna = -1; deltaColuna <= 1; deltaColuna++) {
                        if (deltaLinha == 0 && deltaColuna == 0) {
                            continue;
                        }
                        int vizinhoLinha = linhaAtual + deltaLinha;
                        int vizinhoColuna = colunaAtual + deltaColuna;
                        if (dentroDosLimites(vizinhoLinha, vizinhoColuna)) {
                            Celula vizinha = grade[vizinhoLinha][vizinhoColuna];
                            if (!vizinha.isRevelada() && !vizinha.isMarcada() && !vizinha.isMinada()) {
                                pendentes.add(new int[] { vizinhoLinha, vizinhoColuna });
                            }
                        }
                    }
                }
            }
        }

        if (verificarVitoria()) {
            jogoEncerrado = true;
        }

        return ordemRevelacao;
    }

    /**
     * "Jogada em grupo" (chording, no jargão clássico do Minesweeper):
     * quando o jogador já revelou uma célula numerada e cercou com
     * bandeiras exatamente o número de minas indicado, esta jogada revela
     * de uma vez todas as vizinhas restantes que não estão marcadas —
     * economizando cliques em tabuleiros grandes.
     * <p>
     * Se a contagem de bandeiras ao redor não bater exatamente com o
     * número da célula, a jogada não faz nada (por segurança: bandeira
     * errada faria isso revelar uma mina). Reaproveita {@link #revelar}
     * para cada vizinha, então uma bandeira mal colocada é detectada como
     * derrota normalmente, exatamente como um clique manual naquela
     * célula.
     *
     * @param linha  linha da célula numerada já revelada
     * @param coluna coluna da célula numerada já revelada
     * @return a lista de células reveladas por esta jogada, na ordem em
     *         que foram reveladas (pode incluir o efeito cascata de cada
     *         vizinha). Vazia se a condição de bandeiras não for atendida
     *         ou se a célula não puder receber essa jogada.
     */
    public List<int[]> revelarVizinhos(int linha, int coluna) {
        List<int[]> reveladas = new ArrayList<>();

        if (jogoEncerrado || !dentroDosLimites(linha, coluna)) {
            return reveladas;
        }

        Celula central = grade[linha][coluna];
        if (!central.isRevelada() || central.isMinada() || central.getMinasVizinhas() == 0) {
            return reveladas;
        }

        int bandeirasVizinhas = 0;
        List<int[]> candidatos = new ArrayList<>();
        for (int deltaLinha = -1; deltaLinha <= 1; deltaLinha++) {
            for (int deltaColuna = -1; deltaColuna <= 1; deltaColuna++) {
                if (deltaLinha == 0 && deltaColuna == 0) {
                    continue;
                }
                int vizinhaLinha = linha + deltaLinha;
                int vizinhaColuna = coluna + deltaColuna;
                if (!dentroDosLimites(vizinhaLinha, vizinhaColuna)) {
                    continue;
                }
                Celula vizinha = grade[vizinhaLinha][vizinhaColuna];
                if (vizinha.isMarcada()) {
                    bandeirasVizinhas++;
                } else if (!vizinha.isRevelada()) {
                    candidatos.add(new int[] { vizinhaLinha, vizinhaColuna });
                }
            }
        }

        if (bandeirasVizinhas != central.getMinasVizinhas()) {
            return reveladas;
        }

        for (int[] posicao : candidatos) {
            if (jogoEncerrado) {
                break;
            }
            reveladas.addAll(revelar(posicao[0], posicao[1]));
        }

        return reveladas;
    }

    /**
     * Encerra a partida por tempo esgotado, contando como derrota — usado
     * quando o cronômetro regressivo (modo "tempo rápido") chega a zero
     * antes do jogador terminar. Não altera nenhuma célula; apenas trava
     * o tabuleiro para que novas jogadas sejam ignoradas.
     */
    public void encerrarPorTempoEsgotado() {
        if (!jogoEncerrado) {
            jogoEncerrado = true;
            derrota = true;
        }
    }

    /**
     * Marca ou desmarca uma célula com bandeira, sem revelá-la.
     */
    public void alternarMarcacao(int linha, int coluna) {
        if (jogoEncerrado || !dentroDosLimites(linha, coluna)) {
            return;
        }
        grade[linha][coluna].alternarMarcacao();
    }

    /**
     * O jogo é vencido quando todas as células que não são minas já
     * foram reveladas.
     */
    public boolean verificarVitoria() {
        for (int i = 0; i < linhas; i++) {
            for (int j = 0; j < colunas; j++) {
                Celula celula = grade[i][j];
                if (!celula.isMinada() && !celula.isRevelada()) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean isDerrota() {
        return derrota;
    }

    @Override
    public boolean isJogoEncerrado() {
        return jogoEncerrado;
    }

    @Override
    public int getLinhas() {
        return linhas;
    }

    @Override
    public int getColunas() {
        return colunas;
    }

    public int getNumMinas() {
        return numMinas;
    }

    // ----- implementação de LeituraTabuleiro (usada pela View) -----

    @Override
    public boolean isRevelada(int linha, int coluna) {
        return grade[linha][coluna].isRevelada();
    }

    @Override
    public boolean isMarcada(int linha, int coluna) {
        return grade[linha][coluna].isMarcada();
    }

    @Override
    public boolean isMinada(int linha, int coluna) {
        return grade[linha][coluna].isMinada();
    }

    @Override
    public int getMinasVizinhas(int linha, int coluna) {
        return grade[linha][coluna].getMinasVizinhas();
    }

    /**
     * Retorna a célula em uma posição específica. Mantido para uso interno
     * do próprio Model e para os testes unitários — a View nunca deve
     * chamar este método diretamente; ela usa {@link LeituraTabuleiro}.
     */
    public Celula getCelula(int linha, int coluna) {
        return grade[linha][coluna];
    }

    /**
     * Imprime o tabuleiro no console. Quando revelarTudo é true (por
     * exemplo, ao final de uma derrota), mostra também as minas.
     */
    public void imprimir(boolean revelarTudo) {
        StringBuilder cabecalho = new StringBuilder("   ");
        for (int j = 0; j < colunas; j++) {
            cabecalho.append(String.format("%2d", j));
        }
        System.out.println(cabecalho);

        for (int i = 0; i < linhas; i++) {
            StringBuilder linhaTexto = new StringBuilder(String.format("%2d ", i));
            for (int j = 0; j < colunas; j++) {
                Celula celula = grade[i][j];
                if (revelarTudo && celula.isMinada()) {
                    linhaTexto.append(" *");
                } else {
                    linhaTexto.append(" ").append(celula);
                }
            }
            System.out.println(linhaTexto);
        }
    }
}
