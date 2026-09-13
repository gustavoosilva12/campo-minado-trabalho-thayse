package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Guarda os melhores tempos (recordes) de cada dificuldade em um pequeno
 * arquivo de propriedades na pasta do usuário, para que fiquem
 * disponíveis mesmo depois de fechar e abrir o jogo de novo.
 * <p>
 * Fica fora dos pacotes model/view/controller de propósito: não é regra
 * do jogo em si (o resultado da partida não depende disso), é apenas uma
 * funcionalidade de "melhorias" — estatísticas persistidas entre sessões.
 */
public class RecordesManager {

    private static final String ARQUIVO = System.getProperty("user.home")
            + File.separator + ".campo_minado_recordes.properties";

    /**
     * @param chaveDificuldade identifica a configuração de jogo, por
     *                         exemplo "9x9x10" (linhas x colunas x minas)
     * @return o melhor tempo em segundos já registrado para essa
     *         configuração, ou -1 se nenhum tempo foi registrado ainda
     */
    public int obterMelhorTempo(String chaveDificuldade) {
        Properties propriedades = carregar();
        String valor = propriedades.getProperty(chaveDificuldade);
        if (valor == null) {
            return -1;
        }
        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException erroDeFormato) {
            return -1;
        }
    }

    /**
     * Registra um tempo de vitória para a dificuldade informada.
     *
     * @return true se este tempo é um novo recorde (menor que o anterior,
     *         ou o primeiro tempo registrado para essa dificuldade)
     */
    public boolean registrarTempo(String chaveDificuldade, int segundos) {
        int melhorAtual = obterMelhorTempo(chaveDificuldade);
        boolean novoRecorde = melhorAtual < 0 || segundos < melhorAtual;
        if (novoRecorde) {
            Properties propriedades = carregar();
            propriedades.setProperty(chaveDificuldade, String.valueOf(segundos));
            salvar(propriedades);
        }
        return novoRecorde;
    }

    private Properties carregar() {
        Properties propriedades = new Properties();
        File arquivo = new File(ARQUIVO);
        if (arquivo.exists()) {
            try (FileInputStream entrada = new FileInputStream(arquivo)) {
                propriedades.load(entrada);
            } catch (IOException falhaDeLeitura) {
                // Se não conseguir ler o arquivo de recordes, o jogo segue
                // funcionando normalmente — apenas sem histórico anterior.
            }
        }
        return propriedades;
    }

    private void salvar(Properties propriedades) {
        try (FileOutputStream saida = new FileOutputStream(ARQUIVO)) {
            propriedades.store(saida, "Recordes do Campo Minado");
        } catch (IOException falhaDeEscrita) {
            // Falha ao salvar não deve interromper a partida em andamento.
        }
    }
}
