package controller;

import model.Tabuleiro;
import util.RecordesManager;
import view.CampoMinadoView;

import javax.swing.Timer;
import java.util.List;

/**
 * CONTROLLER da arquitetura MVC: é o único ponto que conhece tanto o
 * {@link Tabuleiro} (Model) quanto a {@link CampoMinadoView} (View).
 * Recebe notificações de clique da View através de {@link AcoesJogador},
 * aplica a jogada no Model e manda a View se redesenhar. A View nunca
 * toca no Model diretamente, e o Model nunca conhece a View.
 */
public class CampoMinadoController implements AcoesJogador {

    private final CampoMinadoView view;
    private final RecordesManager recordesManager = new RecordesManager();

    private Tabuleiro tabuleiro;
    private int totalMinas;
    private int totalCelulas;
    private int celulasReveladas;
    private int jogadas;
    private boolean jogoIniciado;
    private boolean pausado;
    private long tempoInicio;
    private long segundosAcumuladosAntesDaPausa;
    private int limiteSegundos;
    private String chaveDificuldade;
    private Timer timerJogo;

    public CampoMinadoController(CampoMinadoView view) {
        this.view = view;
        this.view.setOuvinte(this);
    }

    public void iniciar() {
        view.mostrarTelaInicial();
        view.setVisible(true);
    }

    // ================================================================
    // AcoesJogador — chamado pela View
    // ================================================================

    @Override
    public void aoEscolherDificuldade(int linhas, int colunas, int minas) {
        try {
            this.tabuleiro = new Tabuleiro(linhas, colunas, minas);
        } catch (IllegalArgumentException configuracaoInvalida) {
            view.mostrarErroConfiguracao(configuracaoInvalida.getMessage());
            return;
        }

        this.totalMinas = minas;
        this.totalCelulas = linhas * colunas - minas;
        this.celulasReveladas = 0;
        this.jogadas = 0;
        this.jogoIniciado = false;
        this.pausado = false;
        this.segundosAcumuladosAntesDaPausa = 0;
        this.chaveDificuldade = linhas + "x" + colunas + "x" + minas;

        pararTimer();
        view.aplicarTemaSelecionado();

        this.limiteSegundos = view.getTempoLimiteSegundosSelecionado();
        view.iniciarTelaDeJogo(linhas, colunas, totalMinas, totalCelulas, limiteSegundos);
        view.atualizarEstatisticas(totalMinas, 0, totalCelulas, 0);
    }

    @Override
    public void aoPedirNovoJogo() {
        pararTimer();
        view.mostrarTelaInicial();
    }

    @Override
    public void aoPedirRecordes() {
        StringBuilder texto = new StringBuilder();
        texto.append("Iniciante (9×9, 10 minas): ").append(formatarRecorde("9x9x10")).append('\n');
        texto.append("Intermediário (16×16, 40 minas): ").append(formatarRecorde("16x16x40")).append('\n');
        texto.append("Avançado (16×30, 99 minas): ").append(formatarRecorde("16x30x99")).append('\n');
        if (chaveDificuldade != null
                && !chaveDificuldade.equals("9x9x10")
                && !chaveDificuldade.equals("16x16x40")
                && !chaveDificuldade.equals("16x30x99")) {
            texto.append("Personalizado atual (").append(chaveDificuldade).append("): ")
                    .append(formatarRecorde(chaveDificuldade)).append('\n');
        }
        view.mostrarRecordes(texto.toString());
    }

    private String formatarRecorde(String chave) {
        int melhor = recordesManager.obterMelhorTempo(chave);
        if (melhor < 0) {
            return "sem recorde ainda";
        }
        return String.format("%02d:%02d", melhor / 60, melhor % 60);
    }

    @Override
    public void aoMarcarCelula(int linha, int coluna) {
        if (tabuleiro == null || tabuleiro.isJogoEncerrado() || pausado) {
            return;
        }
        tabuleiro.alternarMarcacao(linha, coluna);
        view.atualizarCelula(linha, coluna, tabuleiro);
        atualizarEstatisticasNaView();
    }

    @Override
    public void aoRevelarCelula(int linha, int coluna) {
        if (tabuleiro == null || tabuleiro.isJogoEncerrado() || pausado) {
            return;
        }

        prepararInicioDoCronometroSeNecessario();
        if (encerrouPorTempoEsgotado()) {
            return;
        }

        jogadas++;
        List<int[]> reveladas = tabuleiro.revelar(linha, coluna);
        processarCelulasReveladas(reveladas);
    }

    @Override
    public void aoRevelarVizinhos(int linha, int coluna) {
        if (tabuleiro == null || tabuleiro.isJogoEncerrado() || pausado) {
            return;
        }

        prepararInicioDoCronometroSeNecessario();
        if (encerrouPorTempoEsgotado()) {
            return;
        }

        jogadas++;
        List<int[]> reveladas = tabuleiro.revelarVizinhos(linha, coluna);
        processarCelulasReveladas(reveladas);
    }

    @Override
    public void aoAlternarPausa() {
        if (tabuleiro == null || tabuleiro.isJogoEncerrado() || !jogoIniciado) {
            return;
        }
        pausado = !pausado;
        if (pausado) {
            segundosAcumuladosAntesDaPausa += obterSegundosPassados();
            pararTimer();
        } else {
            tempoInicio = System.currentTimeMillis();
            iniciarTimer();
        }
        view.definirPausado(pausado);
    }

    // ================================================================
    // Fluxo comum de uma jogada (revelar simples ou em grupo)
    // ================================================================

    private void prepararInicioDoCronometroSeNecessario() {
        if (!jogoIniciado) {
            jogoIniciado = true;
            tempoInicio = System.currentTimeMillis();
            iniciarTimer();
        }
    }

    private boolean encerrouPorTempoEsgotado() {
        if (limiteSegundos > 0 && obterSegundosPassados() >= limiteSegundos) {
            encerrarPorTempo();
            return true;
        }
        return false;
    }

    private void processarCelulasReveladas(List<int[]> reveladas) {
        celulasReveladas = contarCelulasReveladas();
        int atraso = reveladas.size() > 80 ? 3 : (reveladas.size() > 25 ? 8 : 18);
        animarRevelacao(reveladas, 0, atraso);
    }

    // ================================================================
    // Contagens e sincronização com a View
    // ================================================================

    private int contarCelulasReveladas() {
        int count = 0;
        for (int i = 0; i < tabuleiro.getLinhas(); i++) {
            for (int j = 0; j < tabuleiro.getColunas(); j++) {
                if (tabuleiro.isRevelada(i, j) && !tabuleiro.isMinada(i, j)) {
                    count++;
                }
            }
        }
        return count;
    }

    private int contarMarcadas() {
        int count = 0;
        for (int i = 0; i < tabuleiro.getLinhas(); i++) {
            for (int j = 0; j < tabuleiro.getColunas(); j++) {
                if (tabuleiro.isMarcada(i, j)) {
                    count++;
                }
            }
        }
        return count;
    }

    private void atualizarEstatisticasNaView() {
        int restantes = totalMinas - contarMarcadas();
        view.atualizarEstatisticas(restantes, celulasReveladas, totalCelulas, jogadas);
    }

    // ================================================================
    // Timer do cronômetro
    // ================================================================

    private void iniciarTimer() {
        timerJogo = new Timer(1000, e -> atualizarTempo());
        timerJogo.start();
    }

    private void pararTimer() {
        if (timerJogo != null) {
            timerJogo.stop();
        }
    }

    private long obterSegundosPassados() {
        return segundosAcumuladosAntesDaPausa + (System.currentTimeMillis() - tempoInicio) / 1000;
    }

    private void atualizarTempo() {
        long segundosPassados = obterSegundosPassados();
        if (limiteSegundos > 0) {
            long restantes = Math.max(0, limiteSegundos - segundosPassados);
            view.atualizarTempo(String.format("-%02d:%02d", restantes / 60, restantes % 60));
            if (restantes <= 0) {
                encerrarPorTempo();
                return;
            }
        } else {
            view.atualizarTempo(String.format("%02d:%02d", segundosPassados / 60, segundosPassados % 60));
        }
    }

    private void encerrarPorTempo() {
        pararTimer();
        if (tabuleiro != null) {
            tabuleiro.encerrarPorTempoEsgotado();
        }
        view.mostrarDerrota();
        revelarMinasComAnimacao();
    }

    // ================================================================
    // Animações (o Controller decide o ritmo; a View só desenha um passo)
    // ================================================================

    private void animarRevelacao(List<int[]> celulas, int indice, int atraso) {
        if (indice >= celulas.size()) {
            finalizarJogada();
            return;
        }
        int[] posicao = celulas.get(indice);
        view.atualizarCelula(posicao[0], posicao[1], tabuleiro);

        Timer timer = new Timer(atraso, e -> animarRevelacao(celulas, indice + 1, atraso));
        timer.setRepeats(false);
        timer.start();
    }

    private void finalizarJogada() {
        atualizarEstatisticasNaView();

        if (!tabuleiro.isJogoEncerrado()) {
            return;
        }

        pararTimer();

        if (tabuleiro.isDerrota()) {
            view.mostrarDerrota();
            animarExplosao();
        } else {
            int segundosFinais = (int) obterSegundosPassados();
            boolean novoRecorde = recordesManager.registrarTempo(chaveDificuldade, segundosFinais);
            view.mostrarVitoria(novoRecorde, segundosFinais);
            animarVitoria();
        }
    }

    private void animarExplosao() {
        Timer piscar = new Timer(100, null);
        int[] contador = {0};
        piscar.addActionListener(e -> {
            contador[0]++;
            view.piscarFundoDeExplosao(contador[0] % 2 == 1);
            if (contador[0] >= 6) {
                piscar.stop();
                view.piscarFundoDeExplosao(false);
                revelarMinasComAnimacao();
            }
        });
        piscar.start();
    }

    private void revelarMinasComAnimacao() {
        List<int[]> minasNaoReveladas = new java.util.ArrayList<>();
        for (int i = 0; i < tabuleiro.getLinhas(); i++) {
            for (int j = 0; j < tabuleiro.getColunas(); j++) {
                if (tabuleiro.isMinada(i, j) && !tabuleiro.isRevelada(i, j)) {
                    minasNaoReveladas.add(new int[]{i, j});
                }
            }
        }
        revelarMinasPasso(minasNaoReveladas, 0);
    }

    private void revelarMinasPasso(List<int[]> minas, int indice) {
        if (indice >= minas.size()) {
            return;
        }
        int[] posicao = minas.get(indice);
        view.marcarMinaExplodida(posicao[0], posicao[1]);

        Timer timer = new Timer(80, e -> revelarMinasPasso(minas, indice + 1));
        timer.setRepeats(false);
        timer.start();
    }

    private void animarVitoria() {
        List<int[]> celulasSeguras = new java.util.ArrayList<>();
        for (int i = 0; i < tabuleiro.getLinhas(); i++) {
            for (int j = 0; j < tabuleiro.getColunas(); j++) {
                if (tabuleiro.isRevelada(i, j) && !tabuleiro.isMinada(i, j)) {
                    celulasSeguras.add(new int[]{i, j});
                }
            }
        }
        vitoriaPasso(celulasSeguras, 0);
    }

    private void vitoriaPasso(List<int[]> celulas, int indice) {
        if (indice >= celulas.size()) {
            return;
        }
        int[] atual = celulas.get(indice);
        view.destacarCelulaVencedora(atual[0], atual[1]);

        Timer timer = new Timer(8, e -> vitoriaPasso(celulas, indice + 1));
        timer.setRepeats(false);
        timer.start();
    }
}
