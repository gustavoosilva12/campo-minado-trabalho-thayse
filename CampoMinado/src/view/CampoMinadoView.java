package view;

import controller.AcoesJogador;
import model.LeituraTabuleiro;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * VIEW da arquitetura MVC: cuida só de desenhar a tela e capturar
 * interações do usuário. Nunca decide o que um clique "significa" em
 * termos de regra de jogo — ela apenas repassa o clique para quem
 * implementa {@link AcoesJogador} (o Controller) e espera ser chamada de
 * volta para atualizar o que aparece na tela.
 */
public class CampoMinadoView extends JFrame {

    private static final Color COR_FUNDO = new Color(30, 30, 35);
    private static final Color COR_FUNDO_CLARO = new Color(45, 45, 52);
    private static final Color COR_DESTAQUE = new Color(70, 130, 180);

    // Célula OCULTA: escura e "elevada" — ainda não foi clicada.
    private static final Color COR_CELULA_OCULTA = new Color(72, 78, 96);
    private static final Color COR_CELULA_OCULTA_HOVER = new Color(90, 97, 118);
    private static final Color COR_BORDA_OCULTA = new Color(100, 107, 128);

    // Célula REVELADA: clara e "afundada" — contraste forte e
    // inconfundível com a célula oculta, como no Campo Minado clássico.
    private static final Color COR_CELULA_REVELADA = new Color(228, 228, 233);
    private static final Color COR_BORDA_REVELADA = new Color(195, 195, 202);
    private static final Color COR_TEXTO_SOBRE_REVELADA = new Color(40, 40, 45);

    private static final Color COR_MINA = new Color(220, 60, 60);
    private static final Color COR_MINA_FUNDO = new Color(60, 20, 20);
    private static final Color COR_VITORIA = new Color(50, 180, 80);
    private static final Color COR_TEXTO_PRINCIPAL = new Color(230, 230, 235);
    private static final Color COR_TEXTO_SECUNDARIO = new Color(150, 150, 160);
    private static final Color COR_BORDA = new Color(80, 80, 90);
    private static final Color COR_CARD = new Color(50, 50, 58);
    private static final Color COR_CARD_HOVER = new Color(65, 65, 78);
    private static final Color COR_BANDEIRA = new Color(230, 180, 50);

    // Cinco temas de fundo e cinco de tabuleiro — mais opções de cor do
    // que a versão original, escolhidas para terem contraste suficiente
    // com o texto e com as células reveladas em qualquer combinação.
    private static final String[] TEMAS_FUNDO = {"Escuro", "Claro", "Campo", "Roxo", "Oceano"};
    private static final String[] TEMAS_TABULEIRO = {"Clássico", "Noite", "Verde", "Sunset", "Gelo"};
    private static final String[] TEMPOS_JOGO = {"Sem limite", "1 minuto", "2 minutos", "3 minutos", "5 minutos"};

    private static final Font FONTE_CELULA = new Font("Segoe UI Emoji", Font.BOLD, 20);
    private static final Font FONTE_TITULO = new Font("Segoe UI", Font.BOLD, 28);
    private static final Font FONTE_SUBTITULO = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font FONTE_NORMAL = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONTE_NUMERO = new Font("Consolas", Font.BOLD, 18);
    private static final Font FONTE_PEQUENA = new Font("Segoe UI", Font.PLAIN, 12);

    private static final String EMOJI_BOMBA = "\uD83D\uDCA3";
    private static final String EMOJI_BANDEIRA = "\uD83D\uDEA9";
    private static final String EMOJI_TROFEU = "\uD83C\uDFC6";
    private static final String EMOJI_EXPLOSAO = "\uD83D\uDCA5";
    private static final String EMOJI_RELOGIO = "\u23F1";
    private static final String EMOJI_JOGADA = "\uD83D\uDC46";
    private static final String EMOJI_PAUSA = "\u23F8";
    // Antes havia um "quadradinho" (\u25A0) usado como ícone de estatística.
    // Trocado pelo emoji de bomba, como pedido.
    private static final String EMOJI_ICONE_ESTATISTICA = EMOJI_BOMBA;

    // Esquema clássico do Campo Minado, pensado para boa leitura sobre o
    // fundo claro (COR_CELULA_REVELADA) da célula já revelada.
    private static final Color[] CORES_NUMEROS = {
            null,
            new Color(25, 118, 210),   // 1 - azul
            new Color(56, 142, 60),    // 2 - verde
            new Color(211, 47, 47),    // 3 - vermelho
            new Color(13, 71, 161),    // 4 - azul-marinho
            new Color(136, 14, 14),    // 5 - vinho
            new Color(0, 131, 143),    // 6 - teal
            new Color(33, 33, 33),     // 7 - preto
            new Color(97, 97, 97)      // 8 - cinza-escuro
    };

    private AcoesJogador ouvinte;
    private JButton[][] botoes;
    private JLabel labelStatus;
    private JButton btnPausa;

    private JLabel lblTempo;
    private JLabel lblMinasRestantes;
    private JLabel lblCelulasReveladas;
    private JLabel lblJogadas;
    private JProgressBar barraProgresso;

    private JComboBox<String> comboTemaFundo;
    private JComboBox<String> comboTemaTabuleiro;
    private JComboBox<String> comboTempo;

    private Color corFundo = COR_FUNDO;
    private Color corFundoClaro = COR_FUNDO_CLARO;
    private Color corDestaque = COR_DESTAQUE;
    private Color corTextoPrincipal = COR_TEXTO_PRINCIPAL;
    private Color corTextoSecundario = COR_TEXTO_SECUNDARIO;
    private Color corCardHover = COR_CARD_HOVER;
    private Color corBorda = COR_BORDA;
    private Color corCelulaOculta = COR_CELULA_OCULTA;
    private Color corCelulaOcultaHover = COR_CELULA_OCULTA_HOVER;
    private Color corBordaOculta = COR_BORDA_OCULTA;
    private Color corCelulaRevelada = COR_CELULA_REVELADA;
    private Color corBordaRevelada = COR_BORDA_REVELADA;
    private Color corTextoSobreRevelada = COR_TEXTO_SOBRE_REVELADA;
    private Color corMinaFundo = COR_MINA_FUNDO;

    public CampoMinadoView() {
        super("Campo Minado");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(COR_FUNDO);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    /** Define quem recebe os eventos de clique/escolha (o Controller). */
    public void setOuvinte(AcoesJogador ouvinte) {
        this.ouvinte = ouvinte;
    }

    // ================================================================
    // TELA INICIAL
    // ================================================================

    public void mostrarTelaInicial() {
        getContentPane().removeAll();
        setLayout(new BorderLayout());

        JPanel painelCentral = new JPanel(new GridBagLayout());
        painelCentral.setBackground(COR_FUNDO);
        painelCentral.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));

        JPanel painelConteudo = new JPanel();
        painelConteudo.setLayout(new BoxLayout(painelConteudo, BoxLayout.Y_AXIS));
        painelConteudo.setBackground(COR_FUNDO);
        painelConteudo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titulo = new JLabel(EMOJI_BOMBA + " Campo Minado");
        titulo.setFont(FONTE_TITULO);
        titulo.setForeground(COR_TEXTO_PRINCIPAL);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelConteudo.add(titulo);

        JLabel subtitulo = new JLabel("Escolha sua dificuldade");
        subtitulo.setFont(FONTE_NORMAL);
        subtitulo.setForeground(COR_TEXTO_SECUNDARIO);
        subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 30, 0));
        painelConteudo.add(subtitulo);

        JPanel painelCards = new JPanel(new GridLayout(1, 4, 12, 0));
        painelCards.setBackground(COR_FUNDO);
        painelCards.setAlignmentX(Component.CENTER_ALIGNMENT);

        painelCards.add(criarCardDificuldade("Iniciante", "9 × 9", "10 minas", 9, 9, 10));
        painelCards.add(criarCardDificuldade("Intermediário", "16 × 16", "40 minas", 16, 16, 40));
        painelCards.add(criarCardDificuldade("Avançado", "16 × 30", "99 minas", 16, 30, 99));
        painelCards.add(criarCardPersonalizado());

        painelConteudo.add(painelCards);

        JLabel dica = new JLabel("<html><center>\uD83D\uDDB1\uFE0F Esquerdo: revelar • Direito: bandeira • "
                + "Duplo clique/meio: revelar vizinhos</center></html>");
        dica.setFont(FONTE_PEQUENA);
        dica.setForeground(COR_TEXTO_SECUNDARIO);
        dica.setAlignmentX(Component.CENTER_ALIGNMENT);
        dica.setBorder(BorderFactory.createEmptyBorder(25, 0, 0, 0));
        painelConteudo.add(dica);
        painelConteudo.add(Box.createVerticalStrut(20));
        painelConteudo.add(criarPainelOpcoes());

        painelCentral.add(painelConteudo);
        add(painelCentral, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
        revalidate();
        repaint();
    }

    /**
     * @SuppressWarnings("unchecked") aqui porque os três casts de
     * getClientProperty("combo") para JComboBox&lt;String&gt; são seguros:
     * o próprio método criarLinhaSelecao logo abaixo é quem guarda esse
     * valor, e sempre guarda um JComboBox&lt;String&gt;. O compilador não
     * tem como saber disso (o valor entra e sai como Object), por isso o
     * aviso — mas não há risco real de ClassCastException aqui.
     */
    @SuppressWarnings("unchecked")
    private JPanel criarPainelOpcoes() {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBackground(COR_FUNDO);
        painel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel linha1 = criarLinhaSelecao("Tema de fundo:", TEMAS_FUNDO);
        comboTemaFundo = (JComboBox<String>) linha1.getClientProperty("combo");
        painel.add(linha1);
        painel.add(Box.createVerticalStrut(10));

        JPanel linha2 = criarLinhaSelecao("Cor do tabuleiro:", TEMAS_TABULEIRO);
        comboTemaTabuleiro = (JComboBox<String>) linha2.getClientProperty("combo");
        painel.add(linha2);
        painel.add(Box.createVerticalStrut(10));

        JPanel linha3 = criarLinhaSelecao("Tempo rápido:", TEMPOS_JOGO);
        comboTempo = (JComboBox<String>) linha3.getClientProperty("combo");
        painel.add(linha3);
        painel.add(Box.createVerticalStrut(15));

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        painelBotoes.setBackground(COR_FUNDO);
        painelBotoes.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnTutorial = criarBotaoSecundario("Ver tutorial");
        btnTutorial.addActionListener(e -> mostrarTutorial());
        painelBotoes.add(btnTutorial);

        JButton btnRecordes = criarBotaoSecundario("Melhores tempos");
        btnRecordes.addActionListener(e -> {
            if (ouvinte != null) {
                ouvinte.aoPedirRecordes();
            }
        });
        painelBotoes.add(btnRecordes);

        painel.add(painelBotoes);

        return painel;
    }

    private JButton criarBotaoSecundario(String texto) {
        JButton botao = new JButton(texto);
        botao.setFont(FONTE_NORMAL);
        botao.setForeground(COR_TEXTO_PRINCIPAL);
        botao.setBackground(COR_FUNDO_CLARO);
        botao.setFocusPainted(false);
        botao.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botao.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                botao.setBackground(COR_CARD_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                botao.setBackground(COR_FUNDO_CLARO);
            }
        });
        return botao;
    }

    private JPanel criarLinhaSelecao(String texto, String[] opcoes) {
        JPanel painel = new JPanel(new BorderLayout(10, 0));
        painel.setBackground(COR_FUNDO);
        painel.setMaximumSize(new Dimension(320, 40));

        JLabel lbl = new JLabel(texto);
        lbl.setFont(FONTE_PEQUENA);
        lbl.setForeground(COR_TEXTO_SECUNDARIO);
        painel.add(lbl, BorderLayout.WEST);

        JComboBox<String> combo = new JComboBox<>(opcoes);
        combo.setFont(FONTE_PEQUENA);
        combo.setBackground(COR_FUNDO_CLARO);
        combo.setForeground(COR_TEXTO_PRINCIPAL);
        combo.setBorder(BorderFactory.createLineBorder(COR_BORDA));
        painel.add(combo, BorderLayout.EAST);
        painel.putClientProperty("combo", combo);

        return painel;
    }

    private void mostrarTutorial() {
        getContentPane().removeAll();
        setLayout(new BorderLayout());

        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBackground(COR_FUNDO);
        painel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel titulo = new JLabel("Como jogar Campo Minado");
        titulo.setFont(FONTE_TITULO);
        titulo.setForeground(COR_TEXTO_PRINCIPAL);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        painel.add(titulo);
        painel.add(Box.createVerticalStrut(20));

        String texto = "1. Escolha uma dificuldade (ou monte um tabuleiro personalizado) e um tempo rápido.\n"
                + "2. Clique com o botão esquerdo para revelar uma célula — o primeiro clique da\n"
                + "   partida nunca é uma mina, então pode abrir com confiança.\n"
                + "3. Clique com o botão direito para marcar/desmarcar uma bandeira.\n"
                + "4. Numa célula numerada já revelada, dê um clique duplo ou clique com o botão\n"
                + "   do meio para revelar as vizinhas de uma vez, se já cercou o número certo de\n"
                + "   bandeiras ao redor dela (jogada em grupo).\n"
                + "5. Revele todas as células sem minas para vencer.\n"
                + "6. Se explodir uma mina, o jogo termina em derrota.\n"
                + "7. O tempo selecionado limita a partida; se chegar a zero, você perde.\n"
                + "8. Use o botão Pausar a qualquer momento para congelar o cronômetro.\n";

        JTextArea area = new JTextArea(texto);
        area.setFont(FONTE_NORMAL);
        area.setForeground(COR_TEXTO_PRINCIPAL);
        area.setBackground(COR_FUNDO_CLARO);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        painel.add(area);
        painel.add(Box.createVerticalStrut(15));

        JLabel dicas = new JLabel("Dicas: use bandeiras para marcar minas e tente abrir áreas sem números.");
        dicas.setFont(FONTE_PEQUENA);
        dicas.setForeground(COR_TEXTO_SECUNDARIO);
        dicas.setAlignmentX(Component.CENTER_ALIGNMENT);
        painel.add(dicas);
        painel.add(Box.createVerticalStrut(25));

        JButton voltar = new JButton("Voltar");
        voltar.setFont(FONTE_NORMAL);
        voltar.setForeground(COR_TEXTO_PRINCIPAL);
        voltar.setBackground(COR_FUNDO_CLARO);
        voltar.setFocusPainted(false);
        voltar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        voltar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        voltar.addActionListener(e -> mostrarTelaInicial());
        painel.add(voltar);

        add(painel, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
        revalidate();
        repaint();
    }

    /** Mostra em um diálogo os melhores tempos de cada dificuldade. */
    public void mostrarRecordes(String texto) {
        JOptionPane.showMessageDialog(this, texto, "Melhores tempos", JOptionPane.INFORMATION_MESSAGE);
    }

    /** Mostra um erro amigável quando a configuração de tabuleiro é inválida. */
    public void mostrarErroConfiguracao(String mensagem) {
        JOptionPane.showMessageDialog(this,
                "Não foi possível criar esse tabuleiro:\n" + mensagem,
                "Configuração inválida", JOptionPane.WARNING_MESSAGE);
    }

    public int getTempoLimiteSegundosSelecionado() {
        if (comboTempo == null) {
            return 0;
        }
        String selecionado = (String) comboTempo.getSelectedItem();
        if (selecionado == null || selecionado.startsWith("Sem")) {
            return 0;
        }
        if (selecionado.contains("1 minuto")) {
            return 60;
        }
        if (selecionado.contains("2 minutos")) {
            return 120;
        }
        if (selecionado.contains("3 minutos")) {
            return 180;
        }
        if (selecionado.contains("5 minutos")) {
            return 300;
        }
        return 0;
    }

    public void aplicarTemaSelecionado() {
        if (comboTemaFundo != null) {
            String tema = (String) comboTemaFundo.getSelectedItem();
            if ("Claro".equals(tema)) {
                corFundo = new Color(245, 245, 250);
                corFundoClaro = new Color(230, 230, 235);
                corTextoPrincipal = new Color(25, 25, 30);
                corTextoSecundario = new Color(95, 95, 110);
                corCardHover = new Color(225, 225, 235);
                corDestaque = new Color(35, 100, 190);
                corBorda = new Color(180, 180, 190);
            } else if ("Campo".equals(tema)) {
                corFundo = new Color(25, 35, 25);
                corFundoClaro = new Color(45, 65, 45);
                corTextoPrincipal = new Color(220, 230, 200);
                corTextoSecundario = new Color(170, 190, 150);
                corCardHover = new Color(55, 75, 55);
                corDestaque = new Color(140, 200, 120);
                corBorda = new Color(60, 80, 60);
            } else if ("Roxo".equals(tema)) {
                corFundo = new Color(35, 25, 50);
                corFundoClaro = new Color(55, 40, 75);
                corTextoPrincipal = new Color(230, 225, 240);
                corTextoSecundario = new Color(180, 160, 205);
                corCardHover = new Color(70, 50, 95);
                corDestaque = new Color(175, 125, 225);
                corBorda = new Color(90, 65, 115);
            } else if ("Oceano".equals(tema)) {
                corFundo = new Color(15, 35, 45);
                corFundoClaro = new Color(25, 55, 70);
                corTextoPrincipal = new Color(220, 240, 245);
                corTextoSecundario = new Color(140, 190, 205);
                corCardHover = new Color(30, 70, 90);
                corDestaque = new Color(70, 185, 215);
                corBorda = new Color(40, 90, 110);
            } else {
                corFundo = COR_FUNDO;
                corFundoClaro = COR_FUNDO_CLARO;
                corTextoPrincipal = COR_TEXTO_PRINCIPAL;
                corTextoSecundario = COR_TEXTO_SECUNDARIO;
                corCardHover = COR_CARD_HOVER;
                corDestaque = COR_DESTAQUE;
                corBorda = COR_BORDA;
            }
        }

        if (comboTemaTabuleiro != null) {
            String tema = (String) comboTemaTabuleiro.getSelectedItem();
            if ("Noite".equals(tema)) {
                corCelulaOculta = new Color(20, 30, 45);
                corCelulaOcultaHover = new Color(35, 50, 75);
                corBordaOculta = new Color(70, 90, 120);
                corCelulaRevelada = new Color(55, 65, 80);
                corBordaRevelada = new Color(80, 95, 115);
                corTextoSobreRevelada = new Color(230, 230, 240);
                corMinaFundo = new Color(180, 40, 40);
            } else if ("Verde".equals(tema)) {
                corCelulaOculta = new Color(40, 70, 45);
                corCelulaOcultaHover = new Color(60, 95, 65);
                corBordaOculta = new Color(70, 105, 80);
                corCelulaRevelada = new Color(220, 235, 210);
                corBordaRevelada = new Color(155, 175, 145);
                corTextoSobreRevelada = new Color(25, 45, 25);
                corMinaFundo = new Color(170, 40, 40);
            } else if ("Sunset".equals(tema)) {
                corCelulaOculta = new Color(90, 50, 40);
                corCelulaOcultaHover = new Color(115, 70, 55);
                corBordaOculta = new Color(140, 90, 70);
                corCelulaRevelada = new Color(255, 225, 190);
                corBordaRevelada = new Color(220, 175, 140);
                corTextoSobreRevelada = new Color(60, 30, 20);
                corMinaFundo = new Color(150, 40, 30);
            } else if ("Gelo".equals(tema)) {
                corCelulaOculta = new Color(60, 90, 110);
                corCelulaOcultaHover = new Color(80, 115, 135);
                corBordaOculta = new Color(110, 150, 170);
                corCelulaRevelada = new Color(230, 245, 250);
                corBordaRevelada = new Color(180, 210, 220);
                corTextoSobreRevelada = new Color(30, 50, 60);
                corMinaFundo = new Color(160, 60, 60);
            } else {
                corCelulaOculta = COR_CELULA_OCULTA;
                corCelulaOcultaHover = COR_CELULA_OCULTA_HOVER;
                corBordaOculta = COR_BORDA_OCULTA;
                corCelulaRevelada = COR_CELULA_REVELADA;
                corBordaRevelada = COR_BORDA_REVELADA;
                corTextoSobreRevelada = COR_TEXTO_SOBRE_REVELADA;
                corMinaFundo = COR_MINA_FUNDO;
            }
        }

        getContentPane().setBackground(corFundo);
    }

    private JPanel criarCardDificuldade(String titulo, String dimensao, String minasTexto,
                                         int linhas, int colunas, int minas) {
        JPanel card = criarCardBase(titulo, dimensao, minasTexto);
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                aplicarEstiloHoverCard(card, true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                aplicarEstiloHoverCard(card, false);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (ouvinte != null) {
                    ouvinte.aoEscolherDificuldade(linhas, colunas, minas);
                }
            }
        });
        return card;
    }

    /**
     * Card de dificuldade "Personalizado": em vez de escolher a
     * dificuldade direto, abre um diálogo onde o jogador escolhe linhas,
     * colunas e minas — uma das melhorias sobre as três dificuldades
     * fixas do enunciado original.
     */
    private JPanel criarCardPersonalizado() {
        JPanel card = criarCardBase("Personalizado", "? × ?", "Você escolhe");
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                aplicarEstiloHoverCard(card, true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                aplicarEstiloHoverCard(card, false);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                abrirDialogoPersonalizado();
            }
        });
        return card;
    }

    private void abrirDialogoPersonalizado() {
        JSpinner spinnerLinhas = new JSpinner(new SpinnerNumberModel(9, 5, 40, 1));
        JSpinner spinnerColunas = new JSpinner(new SpinnerNumberModel(9, 5, 40, 1));
        JSpinner spinnerMinas = new JSpinner(new SpinnerNumberModel(10, 1, 400, 1));

        JPanel painel = new JPanel(new GridLayout(3, 2, 8, 8));
        painel.add(new JLabel("Linhas:"));
        painel.add(spinnerLinhas);
        painel.add(new JLabel("Colunas:"));
        painel.add(spinnerColunas);
        painel.add(new JLabel("Minas:"));
        painel.add(spinnerMinas);

        int opcao = JOptionPane.showConfirmDialog(this, painel, "Tabuleiro personalizado",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (opcao != JOptionPane.OK_OPTION || ouvinte == null) {
            return;
        }

        int linhas = (Integer) spinnerLinhas.getValue();
        int colunas = (Integer) spinnerColunas.getValue();
        int minas = (Integer) spinnerMinas.getValue();

        int maximoMinas = linhas * colunas - 1;
        if (minas > maximoMinas) {
            minas = maximoMinas;
        }

        ouvinte.aoEscolherDificuldade(linhas, colunas, minas);
    }

    private JPanel criarCardBase(String titulo, String dimensao, String minasTexto) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(COR_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA, 1),
                BorderFactory.createEmptyBorder(20, 18, 20, 18)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(FONTE_SUBTITULO);
        lblTitulo.setForeground(COR_DESTAQUE);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lblTitulo);

        JLabel lblDim = new JLabel(dimensao);
        lblDim.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblDim.setForeground(COR_TEXTO_PRINCIPAL);
        lblDim.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblDim.setBorder(BorderFactory.createEmptyBorder(8, 0, 4, 0));
        card.add(lblDim);

        JLabel lblMinas = new JLabel(EMOJI_BOMBA + " " + minasTexto);
        lblMinas.setFont(FONTE_NORMAL);
        lblMinas.setForeground(COR_TEXTO_SECUNDARIO);
        lblMinas.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lblMinas);

        return card;
    }

    private void aplicarEstiloHoverCard(JPanel card, boolean sobreHover) {
        if (sobreHover) {
            card.setBackground(COR_CARD_HOVER);
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(COR_DESTAQUE, 2),
                    BorderFactory.createEmptyBorder(19, 17, 19, 17)
            ));
        } else {
            card.setBackground(COR_CARD);
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(COR_BORDA, 1),
                    BorderFactory.createEmptyBorder(20, 18, 20, 18)
            ));
        }
    }

    // ================================================================
    // TELA DE JOGO
    // ================================================================

    /**
     * Monta a tela de jogo do zero para um tabuleiro de {@code linhas} x
     * {@code colunas}. Não recebe o {@link Tabuleiro}, apenas as
     * dimensões — quem decide o que cada célula mostra depois é sempre
     * o Controller, chamando {@link #atualizarCelula}.
     */
    public void iniciarTelaDeJogo(int linhas, int colunas, int totalMinas, int totalCelulas, int tempoLimiteSegundos) {
        getContentPane().removeAll();
        setLayout(new BorderLayout(0, 0));

        add(criarPainelSuperior(), BorderLayout.NORTH);

        JPanel painelPrincipal = new JPanel(new BorderLayout(15, 0));
        painelPrincipal.setBackground(corFundo);
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));

        painelPrincipal.add(criarPainelTabuleiro(linhas, colunas), BorderLayout.CENTER);
        painelPrincipal.add(criarPainelEstatisticas(totalMinas, totalCelulas), BorderLayout.EAST);

        add(painelPrincipal, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
        revalidate();
        repaint();
    }

    private JPanel criarPainelSuperior() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(corFundo);
        painel.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));

        JButton btnNovo = new JButton("← Novo Jogo");
        btnNovo.setFont(FONTE_NORMAL);
        btnNovo.setForeground(corTextoPrincipal);
        btnNovo.setBackground(corFundoClaro);
        btnNovo.setFocusPainted(false);
        btnNovo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(corBorda),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        btnNovo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnNovo.addActionListener(e -> {
            if (ouvinte != null) {
                ouvinte.aoPedirNovoJogo();
            }
        });
        btnNovo.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnNovo.setBackground(corCardHover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnNovo.setBackground(corFundoClaro);
            }
        });

        btnPausa = new JButton(EMOJI_PAUSA + " Pausar");
        btnPausa.setFont(FONTE_NORMAL);
        btnPausa.setForeground(corTextoPrincipal);
        btnPausa.setBackground(corFundoClaro);
        btnPausa.setFocusPainted(false);
        btnPausa.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(corBorda),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        btnPausa.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnPausa.addActionListener(e -> {
            if (ouvinte != null) {
                ouvinte.aoAlternarPausa();
            }
        });
        btnPausa.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnPausa.setBackground(corCardHover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnPausa.setBackground(corFundoClaro);
            }
        });

        JPanel painelBotoesEsquerda = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        painelBotoesEsquerda.setBackground(corFundo);
        painelBotoesEsquerda.add(btnNovo);
        painelBotoesEsquerda.add(btnPausa);

        labelStatus = new JLabel("Boa sorte!", SwingConstants.CENTER);
        labelStatus.setFont(FONTE_SUBTITULO);
        labelStatus.setForeground(corTextoSecundario);

        painel.add(painelBotoesEsquerda, BorderLayout.WEST);
        painel.add(labelStatus, BorderLayout.CENTER);

        return painel;
    }

    private JPanel criarPainelEstatisticas(int totalMinas, int totalCelulas) {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBackground(corFundoClaro);
        painel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(corBorda, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        int largura = 180 + Math.min(100, totalMinas * 2);
        painel.setPreferredSize(new Dimension(largura, 0));

        JLabel lblTitulo = new JLabel("Estatísticas");
        lblTitulo.setFont(FONTE_SUBTITULO);
        lblTitulo.setForeground(COR_DESTAQUE);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        painel.add(lblTitulo);
        painel.add(Box.createVerticalStrut(20));

        JPanel pnlTempo = criarItemEstatistica(EMOJI_RELOGIO + " Tempo", "00:00");
        lblTempo = (JLabel) pnlTempo.getClientProperty("valor");
        painel.add(pnlTempo);
        painel.add(Box.createVerticalStrut(15));

        JPanel pnlMinas = criarItemEstatistica(EMOJI_BOMBA + " Minas", String.valueOf(totalMinas));
        lblMinasRestantes = (JLabel) pnlMinas.getClientProperty("valor");
        painel.add(pnlMinas);
        painel.add(Box.createVerticalStrut(15));

        JPanel pnlReveladas = criarItemEstatistica(EMOJI_ICONE_ESTATISTICA + " Reveladas", "0 / " + totalCelulas);
        lblCelulasReveladas = (JLabel) pnlReveladas.getClientProperty("valor");
        painel.add(pnlReveladas);
        painel.add(Box.createVerticalStrut(15));

        JPanel pnlJogadas = criarItemEstatistica(EMOJI_JOGADA + " Jogadas", "0");
        lblJogadas = (JLabel) pnlJogadas.getClientProperty("valor");
        painel.add(pnlJogadas);
        painel.add(Box.createVerticalStrut(20));

        JLabel lblProgTitulo = new JLabel("Progresso");
        lblProgTitulo.setFont(FONTE_NORMAL);
        lblProgTitulo.setForeground(COR_TEXTO_SECUNDARIO);
        lblProgTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        painel.add(lblProgTitulo);

        barraProgresso = new JProgressBar(0, Math.max(totalCelulas, 1));
        barraProgresso.setValue(0);
        barraProgresso.setStringPainted(true);
        barraProgresso.setString("0%");
        barraProgresso.setForeground(corDestaque);
        barraProgresso.setBackground(corFundo);
        barraProgresso.setBorder(BorderFactory.createLineBorder(COR_BORDA));
        barraProgresso.setPreferredSize(new Dimension(150, 20));
        barraProgresso.setMaximumSize(new Dimension(150, 20));
        barraProgresso.setAlignmentX(Component.CENTER_ALIGNMENT);
        painel.add(barraProgresso);
        painel.add(Box.createVerticalStrut(15));

        painel.add(Box.createVerticalGlue());

        JLabel lblDica = new JLabel("<html><center>\uD83D\uDDB1\uFE0F Esquerdo: revelar<br>"
                + "\uD83D\uDDB1\uFE0F Direito: bandeira<br>"
                + "\uD83D\uDDB1\uFE0F Duplo/meio: vizinhos</center></html>");
        lblDica.setFont(FONTE_PEQUENA);
        lblDica.setForeground(corTextoSecundario);
        lblDica.setAlignmentX(Component.CENTER_ALIGNMENT);
        painel.add(lblDica);

        return painel;
    }

    /**
     * Cria um item de estatística (título + valor) como um único painel,
     * guardando a referência ao label de valor via putClientProperty para
     * que possa ser atualizado depois. (Antes o valor era retornado
     * "solto", sem o painel-pai ser adicionado à tela — corrigido aqui.)
     */
    private JPanel criarItemEstatistica(String titulo, String valorInicial) {
        JPanel painelItem = new JPanel();
        painelItem.setLayout(new BoxLayout(painelItem, BoxLayout.Y_AXIS));
        painelItem.setBackground(corFundoClaro);
        painelItem.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(FONTE_PEQUENA);
        lblTitulo.setForeground(corTextoSecundario);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblValor = new JLabel(valorInicial);
        lblValor.setFont(FONTE_NUMERO);
        lblValor.setForeground(corTextoPrincipal);
        lblValor.setAlignmentX(Component.CENTER_ALIGNMENT);

        painelItem.add(lblTitulo);
        painelItem.add(lblValor);
        painelItem.putClientProperty("valor", lblValor);

        return painelItem;
    }

    private JPanel criarPainelTabuleiro(int linhas, int colunas) {
        JPanel grade = new JPanel(new GridLayout(linhas, colunas, 2, 2));
        grade.setBackground(corFundo);

        botoes = new JButton[linhas][colunas];
        for (int i = 0; i < linhas; i++) {
            for (int j = 0; j < colunas; j++) {
                JButton botao = criarBotaoCelula(i, j);
                botoes[i][j] = botao;
                grade.add(botao);
            }
        }
        return grade;
    }

    /**
     * Cria o botão de uma célula. AQUI ESTAVA O BUG DA BANDEIRA: o código
     * original detectava o clique direito em mousePressed. Em trackpads
     * (Mac, e alguns drivers de notebook Windows/Linux) o clique direito
     * simulado por toque com dois dedos nem sempre reporta corretamente
     * qual botão foi pressionado no evento de "pressed" — só fica
     * confiável no evento de "released". Por isso o primeiro clique
     * direito costumava funcionar e os seguintes eram ignorados ou
     * tratados como clique esquerdo. A correção é ouvir mouseReleased.
     * <p>
     * Também é aqui que o clique duplo (ou o botão do meio) sobre uma
     * célula já revelada dispara a jogada em grupo (chording).
     */
    private JButton criarBotaoCelula(int linha, int coluna) {
        JButton botao = new JButton();
        botao.setPreferredSize(new Dimension(36, 36));
        botao.setFont(FONTE_CELULA);
        botao.setFocusPainted(false);
        botao.setBackground(corCelulaOculta);
        botao.setForeground(corTextoPrincipal);
        botao.setMargin(new Insets(0, 0, 0, 0));
        botao.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA, 1),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)
        ));
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));

        botao.putClientProperty("revelada", Boolean.FALSE);

        botao.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // Só aplica o realce de "hover" em células ainda ocultas;
                // caso contrário isso sobrescreveria a cor clara da
                // célula já revelada sempre que o mouse passasse por cima.
                if (Boolean.FALSE.equals(botao.getClientProperty("revelada"))) {
                    botao.setBackground(corCelulaOcultaHover);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (Boolean.FALSE.equals(botao.getClientProperty("revelada"))) {
                    botao.setBackground(corCelulaOculta);
                }
            }

            @Override
            public void mouseReleased(MouseEvent evento) {
                if (ouvinte == null) {
                    return;
                }
                // e.getButton() é checado explicitamente além de
                // SwingUtilities.isRightMouseButton para cobrir cliques
                // direitos simulados por trackpad de forma confiável.
                boolean botaoDireito = SwingUtilities.isRightMouseButton(evento)
                        || evento.getButton() == MouseEvent.BUTTON3;
                boolean botaoMeio = SwingUtilities.isMiddleMouseButton(evento)
                        || evento.getButton() == MouseEvent.BUTTON2;

                if (botaoDireito) {
                    ouvinte.aoMarcarCelula(linha, coluna);
                } else if (botaoMeio) {
                    ouvinte.aoRevelarVizinhos(linha, coluna);
                } else if (SwingUtilities.isLeftMouseButton(evento)) {
                    if (evento.getClickCount() >= 2) {
                        ouvinte.aoRevelarVizinhos(linha, coluna);
                    } else {
                        ouvinte.aoRevelarCelula(linha, coluna);
                    }
                }
            }
        });
        return botao;
    }

    // ================================================================
    // ATUALIZAÇÕES CHAMADAS PELO CONTROLLER
    // ================================================================

    /** Redesenha uma célula com base no estado atual do tabuleiro. */
    public void atualizarCelula(int linha, int coluna, LeituraTabuleiro leitura) {
        JButton botao = botoes[linha][coluna];
        botao.putClientProperty("revelada", leitura.isRevelada(linha, coluna));

        if (leitura.isMarcada(linha, coluna)) {
            botao.setText(EMOJI_BANDEIRA);
            botao.setForeground(COR_BANDEIRA);
            botao.setBackground(corCelulaOculta);
            botao.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(COR_BANDEIRA, 1),
                    BorderFactory.createEmptyBorder(2, 2, 2, 2)
            ));
            return;
        }

        if (!leitura.isRevelada(linha, coluna)) {
            botao.setText("");
            botao.setBackground(corCelulaOculta);
            botao.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(corBordaOculta, 1),
                    BorderFactory.createEmptyBorder(2, 2, 2, 2)
            ));
            return;
        }

        if (leitura.isMinada(linha, coluna)) {
            botao.setText(EMOJI_BOMBA);
            botao.setBackground(corMinaFundo);
            botao.setForeground(COR_MINA);
            botao.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(COR_MINA, 1),
                    BorderFactory.createEmptyBorder(2, 2, 2, 2)
            ));
        } else {
            // Célula revelada e segura: fundo claro e "afundado",
            // nitidamente diferente do fundo escuro da célula oculta.
            botao.setBackground(corCelulaRevelada);
            botao.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(corBordaRevelada, 1),
                    BorderFactory.createEmptyBorder(2, 2, 2, 2)
            ));
            int vizinhas = leitura.getMinasVizinhas(linha, coluna);
            if (vizinhas == 0) {
                botao.setText("");
                botao.setForeground(corTextoSobreRevelada);
            } else {
                botao.setText(String.valueOf(vizinhas));
                botao.setForeground(CORES_NUMEROS[vizinhas]);
            }
        }
    }

    public void atualizarTempo(String texto) {
        if (lblTempo != null) {
            lblTempo.setText(texto);
        }
    }

    public void atualizarEstatisticas(int minasRestantes, int celulasReveladas, int totalCelulas, int jogadas) {
        if (lblMinasRestantes != null) {
            lblMinasRestantes.setText(String.valueOf(minasRestantes));
        }
        if (lblCelulasReveladas != null) {
            lblCelulasReveladas.setText(celulasReveladas + " / " + totalCelulas);
        }
        if (lblJogadas != null) {
            lblJogadas.setText(String.valueOf(jogadas));
        }

        int progresso = totalCelulas > 0 ? (int) ((celulasReveladas * 100.0) / totalCelulas) : 0;
        if (barraProgresso != null) {
            barraProgresso.setValue(celulasReveladas);
            barraProgresso.setString(progresso + "%");
            if (progresso < 30) {
                barraProgresso.setForeground(new Color(220, 80, 80));
            } else if (progresso < 70) {
                barraProgresso.setForeground(new Color(220, 180, 60));
            } else {
                barraProgresso.setForeground(COR_VITORIA);
            }
        }
    }

    public void mostrarDerrota() {
        labelStatus.setText(EMOJI_EXPLOSAO + " Você perdeu!");
        labelStatus.setForeground(COR_MINA);
    }

    /**
     * @param novoRecorde true se o tempo desta vitória bateu o recorde
     *                    salvo para esta dificuldade
     * @param segundos    tempo total da partida, em segundos
     */
    public void mostrarVitoria(boolean novoRecorde, int segundos) {
        String tempoTexto = String.format("%02d:%02d", segundos / 60, segundos % 60);
        if (novoRecorde) {
            labelStatus.setText(EMOJI_TROFEU + " Você venceu! Novo recorde: " + tempoTexto);
        } else {
            labelStatus.setText(EMOJI_TROFEU + " Você venceu em " + tempoTexto);
        }
        labelStatus.setForeground(COR_VITORIA);
    }

    /** Alterna a aparência da tela de jogo entre pausada e normal. */
    public void definirPausado(boolean pausado) {
        if (botoes != null) {
            for (JButton[] linhaDeBotoes : botoes) {
                for (JButton botao : linhaDeBotoes) {
                    botao.setEnabled(!pausado);
                }
            }
        }
        if (btnPausa != null) {
            btnPausa.setText(pausado ? "\u25B6 Retomar" : EMOJI_PAUSA + " Pausar");
        }
        if (labelStatus != null) {
            if (pausado) {
                labelStatus.setText(EMOJI_PAUSA + " Jogo pausado");
                labelStatus.setForeground(corTextoSecundario);
            } else {
                labelStatus.setText("Boa sorte!");
                labelStatus.setForeground(corTextoSecundario);
            }
        }
    }

    public void piscarFundoDeExplosao(boolean explodindo) {
        getContentPane().setBackground(explodindo ? COR_MINA_FUNDO : corFundo);
    }

    public void marcarMinaExplodida(int linha, int coluna) {
        JButton botao = botoes[linha][coluna];
        botao.setText(EMOJI_BOMBA);
        botao.setForeground(COR_MINA);
        botao.setBackground(COR_MINA_FUNDO);
        botao.setBorder(BorderFactory.createLineBorder(COR_MINA, 1));
    }

    public void destacarCelulaVencedora(int linha, int coluna) {
        botoes[linha][coluna].setBackground(new Color(40, 100, 60));
    }
}
