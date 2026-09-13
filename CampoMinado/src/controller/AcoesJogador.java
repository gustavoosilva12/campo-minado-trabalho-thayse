package controller;

/**
 * Contrato de ações que a View dispara em resposta à interação do
 * jogador. Quem implementa esta interface é sempre o Controller — a View
 * nunca decide o que essas ações significam, apenas avisa que elas
 * aconteceram.
 */
public interface AcoesJogador {

    /** Disparado quando o jogador escolhe uma dificuldade na tela inicial. */
    void aoEscolherDificuldade(int linhas, int colunas, int minas);

    /** Disparado no clique esquerdo sobre uma célula (revelar). */
    void aoRevelarCelula(int linha, int coluna);

    /** Disparado no clique direito sobre uma célula (marcar/desmarcar bandeira). */
    void aoMarcarCelula(int linha, int coluna);

    /**
     * Disparado na "jogada em grupo" (chording): clique duplo ou clique
     * do botão do meio sobre uma célula numerada já revelada, tentando
     * revelar de uma vez todas as vizinhas não marcadas.
     */
    void aoRevelarVizinhos(int linha, int coluna);

    /** Disparado quando o jogador pede para pausar ou retomar a partida. */
    void aoAlternarPausa();

    /** Disparado quando o jogador pede para ver os melhores tempos salvos. */
    void aoPedirRecordes();

    /** Disparado quando o jogador pede para voltar à tela inicial. */
    void aoPedirNovoJogo();
}
