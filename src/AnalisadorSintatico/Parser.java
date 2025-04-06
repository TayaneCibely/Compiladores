package AnalisadorSintatico;

import AnalisadorLexico.*;
import GeradorCodigo.GeradorCodigo;
import java.util.List;

public class Parser {
    private final List<Token> tokens;
    private int pos = 0;
    private TabelaSimbolos tabelaSimbolos;
    private GeradorCodigo geradorCodigo;
    private boolean debug = false;

    public Parser(List<Token> tokens, TabelaSimbolos tabelaSimbolos) {
        this.tokens = tokens;
        this.tabelaSimbolos = tabelaSimbolos;
    }

    public Parser(List<Token> tokens, TabelaSimbolos tabelaSimbolos, GeradorCodigo geradorCodigo) {
        this.tokens = tokens;
        this.tabelaSimbolos = tabelaSimbolos;
        this.geradorCodigo = geradorCodigo;
    }

    private void adicionarSimbolo(Token token, String tipo, String valor) {
        if (token.getTipo() == TipoToken.IDENTIFICADOR) {
            Simbolo simbolo = new Simbolo(token.getValor(), tipo, token.getLinha(), valor);
            tabelaSimbolos.adicionarSimbolo(simbolo);
        }
    }

    public void parse() {
        try {
            parsePrograma();
            System.out.println("Programa sem erros léxicos ou sintáticos");
        } catch (RuntimeException e) {
            System.err.println("Erro de parsing: " + e.getMessage());
        }
    }

    private void parsePrograma() {
        while (pos < tokens.size() &&
                (check(TipoToken.INTEIRO, "int") || check(TipoToken.BOOLEANO, "bool") ||
                        check(TipoToken.PROCEDIMENTO, "procedure") || check(TipoToken.FUNCAO, "function"))) {

            if (debug) System.out.println("DEBUG: Processando declaração global: " + peek());

            if (check(TipoToken.INTEIRO, "int") || check(TipoToken.BOOLEANO, "bool")) {
                parseDeclaracaoVariaveis();
            } else if (check(TipoToken.PROCEDIMENTO)) {
                parseDeclaracaoProcedimento();
            } else if (check(TipoToken.FUNCAO)) {
                parseDeclaracaoFuncao();
            }
        }

        if (debug) System.out.println("DEBUG: Processando bloco main: " + peek());

        consume(TipoToken.MAIN, "main", "Erro: 'main' esperado");
        consume(TipoToken.IDENTIFICADOR, "Erro: Identificador esperado após 'main'");
        consume(TipoToken.ABRE_PAREN, "(", "Erro: '(' esperado após identificador");
        consume(TipoToken.FECHA_PAREN, ")", "Erro: ')' esperado após '('");
        consume(TipoToken.ABRE_CHAVES, "{", "Erro: '{' esperado após ')'");

        tabelaSimbolos.entrarEscopo();
        parseBloco();

        consume(TipoToken.FECHA_CHAVES, "}", "Erro: '}' esperado após bloco");
        consume(TipoToken.END, "end", "Erro: 'end' esperado");
    }

    private void parseBloco() {
        while (!check(TipoToken.FECHA_CHAVES, "}")) {
            if (check(TipoToken.INTEIRO, "int") || check(TipoToken.BOOLEANO, "bool")) {
                parseDeclaracaoVariaveis();
            } else if (check(TipoToken.RETORNO)) {
                parseReturn();
            } else {
                parseComando();
            }
        }
    }

    private void parseDeclaracaoVariaveis() {
        if (debug) System.out.println("DEBUG: Processando declaração de variáveis: " + peek());

        while (check(TipoToken.INTEIRO, "int") || check(TipoToken.BOOLEANO, "bool")) {
            parseDeclaracaoVariavel();
        }
    }

    private void parseDeclaracaoVariavel() {
        if (check(TipoToken.INTEIRO, "int") || check(TipoToken.BOOLEANO, "bool")) {
            String tipo = tokens.get(pos).getValor();
            consume(peek().getTipo(), "Erro: Tipo de variável esperado");

            Token identificador = tokens.get(pos);
            consume(TipoToken.IDENTIFICADOR, "Erro: Identificador esperado após o tipo");

            String valor = null;
            String tipoExpressao = null;

            if (match(TipoToken.OPE_ATRI, "=")) {
                int exprStartPos = pos;
                parseExpressao();

                if (exprStartPos < tokens.size()) {
                    Token valorToken = tokens.get(exprStartPos);
                    valor = valorToken.getValor();

                    if (valorToken.getTipo() == TipoToken.NUMERO) {
                        tipoExpressao = "int";
                    } else if (valorToken.getTipo() == TipoToken.STRING) {
                        tipoExpressao = "string";
                    } else if (valorToken.getTipo() == TipoToken.VERDADEIRO ||
                            valorToken.getTipo() == TipoToken.FALSO) {
                        tipoExpressao = "bool";
                    } else if (valorToken.getTipo() == TipoToken.IDENTIFICADOR) {
                        Simbolo simboloRef = tabelaSimbolos.buscarSimbolo(valor);
                        if (simboloRef != null) {
                            tipoExpressao = simboloRef.getTipo();
                        }
                    }
                }
            }

            adicionarSimbolo(identificador, tipo, valor);

            Simbolo simbolo = tabelaSimbolos.buscarSimbolo(identificador.getValor());
            if (simbolo != null && tipoExpressao != null) {
                simbolo.setTipoExpressao(tipoExpressao);
            }

            while (match(TipoToken.VIRGULA, ",")) {
                identificador = tokens.get(pos);
                consume(TipoToken.IDENTIFICADOR, "Erro: Identificador esperado após ','");

                valor = null;
                tipoExpressao = null;

                if (match(TipoToken.OPE_ATRI, "=")) {
                    int exprStartPos = pos;
                    parseExpressao();

                    if (exprStartPos < tokens.size()) {
                        Token valorToken = tokens.get(exprStartPos);
                        valor = valorToken.getValor();

                        if (valorToken.getTipo() == TipoToken.NUMERO) {
                            tipoExpressao = "int";
                        } else if (valorToken.getTipo() == TipoToken.STRING) {
                            tipoExpressao = "string";
                        } else if (valorToken.getTipo() == TipoToken.VERDADEIRO ||
                                valorToken.getTipo() == TipoToken.FALSO) {
                            tipoExpressao = "bool";
                        } else if (valorToken.getTipo() == TipoToken.IDENTIFICADOR) {
                            Simbolo simboloRef = tabelaSimbolos.buscarSimbolo(valor);
                            if (simboloRef != null) {
                                tipoExpressao = simboloRef.getTipo();
                            }
                        }
                    }
                }

                adicionarSimbolo(identificador, tipo, valor);

                simbolo = tabelaSimbolos.buscarSimbolo(identificador.getValor());
                if (simbolo != null && tipoExpressao != null) {
                    simbolo.setTipoExpressao(tipoExpressao);
                }
            }

            consume(TipoToken.PON_VIR, ";", "Erro: ';' esperado no final da declaração de variáveis");
        } else {
            throw new RuntimeException("Erro: Tipo de variável (int ou bool) esperado");
        }
    }

    private void parseDeclaracaoProcedimento() {
        if (debug) System.out.println("DEBUG: Processando declaração de procedimento: " + peek());

        consume(TipoToken.PROCEDIMENTO, "procedure", "Erro: 'procedure' esperado");
        Token procedimento = tokens.get(pos);
        consume(TipoToken.IDENTIFICADOR, "Erro: Identificador esperado após 'procedure'");
        adicionarSimbolo(procedimento, "procedure", null);

        consume(TipoToken.ABRE_PAREN, "(", "Erro: '(' esperado após o identificador do procedimento");

        tabelaSimbolos.entrarEscopo();
        parseParametros();

        consume(TipoToken.FECHA_PAREN, ")", "Erro: ')' esperado após os parâmetros");
        consume(TipoToken.ABRE_CHAVES, "{", "Erro: '{' esperado no início do bloco do procedimento");

        parseBloco();

        consume(TipoToken.FECHA_CHAVES, "}", "Erro: '}' esperado após o bloco do procedimento");
        tabelaSimbolos.sairEscopo();
    }

    private String funcaoAtual = null;

    private void parseDeclaracaoFuncao() {
        if (debug) System.out.println("DEBUG: Processando declaração de função: " + peek());

        consume(TipoToken.FUNCAO, "function", "Erro: 'function' esperado");

        if (!check(TipoToken.INTEIRO, "int") && !check(TipoToken.BOOLEANO, "bool")) {
            throw new RuntimeException("Erro: Tipo de retorno inválido");
        }

        String tipoRetorno = tokens.get(pos).getValor();
        consume(peek().getTipo(), "Erro: Tipo de retorno esperado");

        Token funcao = tokens.get(pos);
        String nomeFuncao = funcao.getValor();
        this.funcaoAtual = nomeFuncao;

        consume(TipoToken.IDENTIFICADOR, "Erro: Identificador da função esperado após o tipo");
        adicionarSimbolo(funcao, tipoRetorno, null);

        consume(TipoToken.ABRE_PAREN, "(", "Erro: '(' esperado após o identificador da função");

        tabelaSimbolos.entrarEscopo();
        parseParametros();

        consume(TipoToken.FECHA_PAREN, ")", "Erro: ')' esperado após os parâmetros");
        consume(TipoToken.ABRE_CHAVES, "{", "Erro: '{' esperado no início do bloco da função");

        parseBloco();

        if (check(TipoToken.RETORNO)) {
            parseReturn();
        }

        consume(TipoToken.FECHA_CHAVES, "}", "Erro: '}' esperado após o bloco da função");
        tabelaSimbolos.sairEscopo();

        this.funcaoAtual = null;
    }

    private void parseComando() {
        if (debug) System.out.println("DEBUG: Processando comando: " + peek());

        if (check(TipoToken.WHILE, "while")) {
            parseComandoWhile();
        } else if (check(TipoToken.IF, "if")) {
            parseComandoIf();
        } else if (check(TipoToken.PRINT, "print")) {
            parseComandoPrint();
        } else if (check(TipoToken.IDENTIFICADOR)) {
            if (tokens.size() > pos + 1 && tokens.get(pos + 1).getTipo() == TipoToken.OPE_ATRI) {
                parseAtribuicao();
            } else {
                parseChamadaFuncao();
            }
        } else if(check(TipoToken.BREAK, "break")){
            parseComandoBreak();
        } else if(check(TipoToken.CONTINUE, "continue")){
            parseComandoContinue();
        } else {
            throw new RuntimeException("Erro: Comando inválido");
        }
    }

    private void parseAtribuicao() {
        if (debug) System.out.println("DEBUG: Processando atribuição: " + peek());

        Token idToken = tokens.get(pos);
        consume(TipoToken.IDENTIFICADOR, "Erro: Identificador esperado");

        Simbolo simbolo = tabelaSimbolos.buscarSimbolo(idToken.getValor());
        consume(TipoToken.OPE_ATRI, "=", "Erro: '=' esperado após identificador");

        int exprStartPos = pos;
        parseExpressao();

        String valorExpressao = null;
        String tipoExpressao = null;

        if(exprStartPos < tokens.size()) {
            Token valorToken = tokens.get(exprStartPos);

            if (valorToken.getTipo() == TipoToken.NUMERO) {
                valorExpressao = valorToken.getValor();
                tipoExpressao = "int";
            } else if (valorToken.getTipo() == TipoToken.STRING) {
                valorExpressao = valorToken.getValor();
                tipoExpressao = "string";
            } else if (valorToken.getTipo() == TipoToken.VERDADEIRO ||
                    valorToken.getTipo() == TipoToken.FALSO) {
                valorExpressao = valorToken.getValor();
                tipoExpressao = "bool";
            } else if (valorToken.getTipo() == TipoToken.IDENTIFICADOR) {
                valorExpressao = valorToken.getValor();
                Simbolo simboloRef = tabelaSimbolos.buscarSimbolo(valorExpressao);
                if (simboloRef != null) {
                    tipoExpressao = simboloRef.getTipo();
                }
            }

            if (simbolo != null) {
                simbolo.setValor(valorExpressao);
                simbolo.setTipoExpressao(tipoExpressao);
            }
        }

        consume(TipoToken.PON_VIR, ";", "Erro: ';' esperado após expressão");
    }

    private void parseComandoBreak(){
        if (debug) System.out.println("DEBUG: Processando break: " + peek());

        consume(TipoToken.BREAK, "break", "Erro: 'break' esperado");
        consume(TipoToken.PON_VIR, ";", "Erro: ';' esperado após o comando 'break'");
    }

    private void parseComandoContinue() {
        if (debug) System.out.println("DEBUG: Processando continue: " + peek());

        consume(TipoToken.CONTINUE, "continue", "Erro: 'continue' esperado");
        consume(TipoToken.PON_VIR, ";", "Erro: ';' esperado após o comando 'continue'");
    }

    private void parseChamadaFuncao() {
        if (debug) System.out.println("DEBUG: Processando chamada de função: " + peek());

        Token funcaoToken = tokens.get(pos);
        consume(TipoToken.IDENTIFICADOR, "Erro: Identificador de função esperado");

        tabelaSimbolos.registrarUsoVariavel(funcaoToken.getValor(), funcaoToken.getLinha());

        consume(TipoToken.ABRE_PAREN, "(", "Erro: '(' esperado após o identificador da função");
        parseArgumentos();
        consume(TipoToken.FECHA_PAREN, ")", "Erro: ')' esperado após os argumentos");
        consume(TipoToken.PON_VIR, ";", "Erro: ';' esperado após a chamada da função");
    }

    private void parseArgumentos() {
        if (debug) System.out.println("DEBUG: Processando argumentos: " + peek());

        if (!check(TipoToken.FECHA_PAREN)) {
            parseExpressao();
            while (match(TipoToken.VIRGULA, ",")) {
                parseExpressao();
            }
        }
    }

    private void parseComandoWhile() {
        if (debug) System.out.println("DEBUG: Processando while: " + peek());

        consume(TipoToken.WHILE, "while", "Erro: 'while' esperado");
        consume(TipoToken.ABRE_PAREN, "(", "Erro: '(' esperado após 'while'");
        parseExpressao();
        consume(TipoToken.FECHA_PAREN, ")", "Erro: ')' esperado após a expressão no 'while'");
        consume(TipoToken.ABRE_CHAVES, "{", "Erro: '{' esperado após ')'");

        tabelaSimbolos.entrarEscopo();
        parseBloco();

        consume(TipoToken.FECHA_CHAVES, "}", "Erro: '}' esperado após o bloco do 'while'");
        tabelaSimbolos.sairEscopo();
    }

    private void parseComandoIf() {
        if (debug) System.out.println("DEBUG: Processando if: " + peek());

        consume(TipoToken.IF, "if", "Erro: 'if' esperado");
        consume(TipoToken.ABRE_PAREN, "(", "Erro: '(' esperado após 'if'");
        parseExpressao();
        consume(TipoToken.FECHA_PAREN, ")", "Erro: ')' esperado após a expressão");
        consume(TipoToken.ABRE_CHAVES, "{", "Erro: '{' esperado após a expressão do 'if'");

        tabelaSimbolos.entrarEscopo();
        parseBloco();

        consume(TipoToken.FECHA_CHAVES, "}", "Erro: '}' esperado após o bloco do 'if'");
        tabelaSimbolos.sairEscopo();

        if (match(TipoToken.ELSE, "else")) {
            consume(TipoToken.ABRE_CHAVES, "{", "Erro: '{' esperado após 'else'");
            tabelaSimbolos.entrarEscopo();
            parseBloco();
            consume(TipoToken.FECHA_CHAVES, "}", "Erro: '}' esperado após o bloco do 'else'");
            tabelaSimbolos.sairEscopo();
        }
    }

    private void parseComandoPrint() {
        if (debug) System.out.println("DEBUG: Processando print: " + peek());

        consume(TipoToken.PRINT, "print", "Erro: 'print' esperado");
        consume(TipoToken.ABRE_PAREN, "(", "Erro: '(' esperado após 'print'");

        if (check(TipoToken.STRING)) {
            consume(TipoToken.STRING, "Erro: 'string' esperado após '('");
        } else if (check(TipoToken.IDENTIFICADOR)) {
            Token idToken = tokens.get(pos);
            consume(TipoToken.IDENTIFICADOR, "Erro: Identificador esperado após '('");

            // Registrar o uso da variável
            tabelaSimbolos.registrarUsoVariavel(idToken.getValor(), idToken.getLinha());
        } else {
            throw new RuntimeException("Erro: Esperado uma string ou identificador após '('");
        }

        consume(TipoToken.FECHA_PAREN, ")", "Erro: ')' esperado após o conteúdo do print");
        consume(TipoToken.PON_VIR, ";", "Erro: ';' esperado após o comando 'print'");
    }

    private void parseReturn() {
        if (debug) System.out.println("DEBUG: Processando return: " + peek());

        Token returnToken = tokens.get(pos);
        consume(TipoToken.RETORNO, "return", "Erro: 'return' esperado");

        if (check(TipoToken.IDENTIFICADOR) || check(TipoToken.NUMERO) ||
                check(TipoToken.STRING) || check(TipoToken.VERDADEIRO) || check(TipoToken.FALSO)) {

            Token valorToken = tokens.get(pos);
            String valorRetorno = valorToken.getValor();

            if (valorToken.getTipo() == TipoToken.IDENTIFICADOR) {
                tabelaSimbolos.registrarUsoVariavel(valorRetorno, valorToken.getLinha());
            }

            Simbolo simboloReturn = new Simbolo("return", "return", returnToken.getLinha(), valorRetorno);
            // definir a função pai do return
            simboloReturn.setFuncaoPai(funcaoAtual);
            tabelaSimbolos.adicionarSimbolo(simboloReturn);


            parseExpressao();
        } else {
            throw new RuntimeException("Erro: Esperado um identificador, número, string ou valor booleano após 'return'");
        }

        consume(TipoToken.PON_VIR, ";", "Erro: Falta um ponto e vírgula ';' após o retorno");
    }

    private void parseExpressao() {
        if (debug) System.out.println("DEBUG: Processando expressão: " + peek());

        parseExpressaoSimples();

        if (peek() != null && peek().getTipo() == TipoToken.OPE_REL) {
            consume(TipoToken.OPE_REL, "Erro: Operador relacional esperado");
            parseExpressaoSimples();
        }
    }

    private void parseExpressaoSimples(){
        if (debug) System.out.println("DEBUG: Processando expressão simples: " + peek());

        parseTermo();

        while (peek() != null && peek().getTipo() == TipoToken.OPE_ARIT &&
                (peek().getValor().equals("+") || peek().getValor().equals("-"))) {
            consume(TipoToken.OPE_ARIT, "Erro: Operador aritmético esperado");
            parseTermo();
        }
    }

    private void parseTermo() {
        if (debug) System.out.println("DEBUG: Processando termo: " + peek());

        parseFator();

        while (peek() != null && peek().getTipo() == TipoToken.OPE_ARIT &&
                (peek().getValor().equals("*") || peek().getValor().equals("/") || peek().getValor().equals("%"))) {
            consume(TipoToken.OPE_ARIT, "Erro: Operador de multiplicação/divisão esperado");
            parseFator();
        }
    }

    private void parseFator() {
        if (debug) System.out.println("DEBUG: Processando fator: " + peek());

        if (check(TipoToken.IDENTIFICADOR)) {
            Token idToken = tokens.get(pos);
            tabelaSimbolos.registrarUsoVariavel(idToken.getValor(), idToken.getLinha());
            consume(TipoToken.IDENTIFICADOR, "Erro: Identificador esperado");

            if (check(TipoToken.ABRE_PAREN)) {
                consume(TipoToken.ABRE_PAREN, "(", "Erro: '(' esperado após identificador de função");
                parseArgumentos();
                consume(TipoToken.FECHA_PAREN, ")", "Erro: ')' esperado após argumentos");
            }
        } else if (check(TipoToken.NUMERO)) {
            consume(TipoToken.NUMERO, "Erro: Número esperado");
        } else if (check(TipoToken.STRING)) {
            consume(TipoToken.STRING, "Erro: String esperada");
        } else if (check(TipoToken.VERDADEIRO) || check(TipoToken.FALSO)) {
            consume(peek().getTipo(), "Erro: Valor booleano esperado");
        } else if (match(TipoToken.ABRE_PAREN, "(")) {
            parseExpressao();
            consume(TipoToken.FECHA_PAREN, ")", "Erro: ')' esperado após a expressão");
        } else {
            throw new RuntimeException("Erro: Fator inválido na expressão");
        }
    }

    private void parseParametros() {
        if (debug) System.out.println("DEBUG: Processando parâmetros: " + peek());

        while (check(TipoToken.INTEIRO, "int") || check(TipoToken.BOOLEANO, "bool")) {
            String tipo = peek().getValor();
            consume(peek().getTipo(), "Erro: Tipo esperado em parâmetros");
            Token identificador = tokens.get(pos);
            consume(TipoToken.IDENTIFICADOR, "Erro: Identificador esperado após o tipo");
            adicionarSimbolo(identificador, tipo, null);

            if (!match(TipoToken.VIRGULA, ",")) break;
        }
    }

    private void consume(TipoToken tipo, String erro) {
        if (debug) System.out.println("DEBUG: Consumindo token: " + peek() + " (esperado: " + tipo + ")");

        if (!check(tipo)) {
            System.out.println("Erro ao consumir token. Esperado: " + tipo + ", Encontrado: " + (peek() != null ? peek().getTipo() : "null"));
            throw new RuntimeException(erro);
        }
        pos++;
    }

    private void consume(TipoToken tipo, String valor, String erro) {
        if (debug) System.out.println("DEBUG: Consumindo token: " + peek() + " (esperado: " + tipo + " com valor: " + valor + ")");

        if (!check(tipo)) {
            System.out.println("Erro ao consumir token. Esperado: " + tipo + ", Encontrado: " + (peek() != null ? peek().getTipo() : "null"));
            throw new RuntimeException(erro);
        }
        pos++;
    }

    private boolean check(TipoToken tipo) {
        return pos < tokens.size() && tokens.get(pos).getTipo() == tipo;
    }

    private boolean check(TipoToken tipo, String valor) {
        return pos < tokens.size() && tokens.get(pos).getTipo() == tipo && tokens.get(pos).getValor().equals(valor);
    }

    private boolean match(TipoToken tipo, String valor) {
        if (check(tipo, valor)) {
            pos++;
            return true;
        }
        return false;
    }

    private Token peek(){
        if (pos < tokens.size()) {
            return tokens.get(pos);
        }
        return null;
    }
}