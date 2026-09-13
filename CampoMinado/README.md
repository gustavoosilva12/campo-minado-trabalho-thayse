# Campo Minado — Java (POO)

Implementação do jogo Campo Minado em Java, com arquitetura MVC, versão em
console e versão gráfica (Swing). Trabalho do 1º bimestre (Opção B) de
Programação Orientada a Objetos.

Além de tudo o que o enunciado pedia, esta versão traz um conjunto de
melhorias extras (veja a seção [Melhorias adicionais](#melhorias-adicionais-além-do-enunciado)).

## Estrutura do projeto

```
CampoMinado/
├── src/
│   ├── model/
│   │   ├── Celula.java            # estado de uma célula (privado, encapsulado)
│   │   ├── Tabuleiro.java         # grade, minas, cascata, vitória/derrota
│   │   └── LeituraTabuleiro.java  # interface somente-leitura usada pela View
│   ├── view/
│   │   └── CampoMinadoView.java   # tela Swing
│   ├── controller/
│   │   ├── CampoMinadoController.java
│   │   └── AcoesJogador.java
│   ├── util/
│   │   └── RecordesManager.java   # salva os melhores tempos entre execuções
│   └── main/
│       ├── JogoCampoMinado.java     # ponto de entrada — console
│       └── JogoCampoMinadoGUI.java  # ponto de entrada — interface gráfica
└── test/
    └── model/
        └── CampoMinadoTest.java   # 12 testes JUnit 5
```

## Melhorias adicionais (além do enunciado)

O enunciado pedia o essencial do Campo Minado; estas são melhorias
implementadas por conta própria, em cima disso:

- **Primeiro clique sempre seguro** — as minas só são sorteadas depois do
  primeiro clique, evitando a célula escolhida e suas 8 vizinhas, então a
  primeira jogada de qualquer partida nunca é uma derrota.
- **Jogada em grupo (chording)** — clique duplo ou clique com o botão do
  meio em uma célula numerada já revelada, cercada pelo número certo de
  bandeiras, revela todas as vizinhas restantes de uma vez.
- **Tabuleiro personalizado** — além de Iniciante/Intermediário/Avançado,
  um quarto card abre um diálogo para escolher linhas, colunas e minas.
- **Mais temas de cor** — 5 temas de fundo (Escuro, Claro, Campo, Roxo,
  Oceano) e 5 temas de tabuleiro (Clássico, Noite, Verde, Sunset, Gelo),
  combináveis livremente.
- **Pausar/retomar** — botão que congela o cronômetro e trava o tabuleiro
  a qualquer momento da partida.
- **Melhores tempos (recordes)** — o tempo de cada vitória é comparado
  com o recorde salvo (por dificuldade) em um arquivo na pasta do
  usuário, e fica disponível entre execuções do jogo pelo botão
  "Melhores tempos" na tela inicial.
- **Tempo rápido opcional** — modos com cronômetro regressivo (1, 2, 3 ou
  5 minutos), encerrando a partida em derrota se o tempo acabar.
- **Console também ganhou o comando `v`** para a jogada em grupo, além do
  aviso de que o primeiro clique é sempre seguro.

## Como compilar

A partir da pasta `CampoMinado`:

```bash
mkdir -p out
javac -d out $(find src -name "*.java")
```

(No Windows/PowerShell, troque `$(find src -name "*.java")` por
`src\model\*.java src\view\*.java src\controller\*.java src\main\*.java`.)

## Como executar

Interface gráfica:

```bash
java -cp out main.JogoCampoMinadoGUI
```

Versão em console:

```bash
java -cp out main.JogoCampoMinado
```

No console, os comandos são:
- `r <linha> <coluna>` — revelar uma célula
- `m <linha> <coluna>` — marcar/desmarcar bandeira
- `v <linha> <coluna>` — revelar vizinhos (jogada em grupo, se já houver
  bandeiras suficientes ao redor da célula numerada)

## Como rodar os testes (JUnit 5)

O projeto já vem com um `pom.xml` (Maven) apontando para as pastas `src` e
`test` que já existiam — ele só serve para trazer o JUnit 5 automaticamente,
sem precisar baixar `.jar` na mão.

**Opção 1 — VS Code (recomendado, já que é o que você está usando):**

1. Instale a extensão **Extension Pack for Java** (da Microsoft) e a
   **Test Runner for Java**, se ainda não tiver.
2. Abra a pasta `CampoMinado` no VS Code (`File > Open Folder`).
3. O VS Code detecta o `pom.xml` e baixa o JUnit sozinho (precisa de
   internet nesse passo). Espere a barra de progresso "Java: Loading
   projects" terminar.
4. As linhas vermelhas em `CampoMinadoTest.java` devem sumir.
5. Clique no ícone de frasco (Testing) na barra lateral, ou nos links
   "Run Test" que aparecem em cima de cada método `@Test`, para rodar os
   12 testes.
6. Alternativa pelo terminal integrado do VS Code: `mvn test`.

**Opção 2 — IntelliJ IDEA:** abra a pasta como projeto Maven (ele detecta o
`pom.xml` sozinho) e rode `test/model/CampoMinadoTest.java` normalmente.

**Opção 3 — linha de comando com Maven instalado:**

```bash
mvn test
```

## Requisitos atendidos

Veja o `Relatorio_CampoMinado.docx` para a explicação da lógica da cascata,
das decisões de encapsulamento e a tabela de requisitos atendidos.

## Pendente antes de entregar

- Tirar um print (ou gravar um vídeo curto) de uma partida vencida e de uma
  partida perdida, jogando de fato a versão gráfica ou a de console.
