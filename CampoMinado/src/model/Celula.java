package model;

/**
 * Representa uma única célula do tabuleiro de Campo Minado.
 * <p>
 * Parte do MODEL (na arquitetura MVC): não conhece Swing, não conhece a
 * View nem o Controller. Todo o estado (minada, revelada, marcada,
 * minasVizinhas) é privado; nenhuma classe externa altera esses valores
 * diretamente, apenas através dos métodos públicos abaixo.
 */
public class Celula {

    private boolean minada;
    private boolean revelada;
    private boolean marcada;
    private int minasVizinhas;

    public Celula() {
        this.minada = false;
        this.revelada = false;
        this.marcada = false;
        this.minasVizinhas = 0;
    }

    // ----- minada -----

    public boolean isMinada() {
        return minada;
    }

    public void setMinada(boolean minada) {
        this.minada = minada;
    }

    // ----- revelada -----

    public boolean isRevelada() {
        return revelada;
    }

    /**
     * Revela a célula. Uma célula marcada com bandeira não pode ser
     * revelada por engano; é preciso desmarcá-la primeiro.
     */
    public void revelar() {
        if (!marcada) {
            this.revelada = true;
        }
    }

    // ----- marcada (bandeira) -----

    public boolean isMarcada() {
        return marcada;
    }

    /**
     * Alterna o estado de "marcada com bandeira". Só é possível marcar
     * uma célula que ainda não foi revelada. Cada instância de Celula
     * guarda o próprio estado — não há nada compartilhado entre células.
     */
    public void alternarMarcacao() {
        if (!revelada) {
            this.marcada = !this.marcada;
        }
    }

    // ----- minasVizinhas -----

    public int getMinasVizinhas() {
        return minasVizinhas;
    }

    public void setMinasVizinhas(int minasVizinhas) {
        this.minasVizinhas = minasVizinhas;
    }

    /**
     * Representação usada para exibir a célula no console.
     */
    @Override
    public String toString() {
        if (marcada) {
            return "F";
        }
        if (!revelada) {
            return ".";
        }
        if (minada) {
            return "*";
        }
        if (minasVizinhas == 0) {
            return " ";
        }
        return String.valueOf(minasVizinhas);
    }
}
