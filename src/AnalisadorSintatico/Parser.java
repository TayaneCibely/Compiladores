package AnalisadorSintatico;

import AnalisadorLexico.*;
import java.util.List;

public class Parser {
    private final List<Token> tokens;
    private int pos = 0;
    private TabelaSimbolos tabelaSimbolos;

    public Parser(List<Token> tokens, TabelaSimbolos tabelaSimbolos) {
        this.tokens = tokens;
        this.tabelaSimbolos = tabelaSimbolos;
    }

    private void adicionarSimbolo(Token token, String tipo, String valor) {
        if (token.getTipo() == TipoToken.IDENTIFICADOR) {
            Simbolo simbolo = new Simbolo(token.getValor(), tipo, token.getLinha(), valor);
            tabelaSimbolos.adicionarSimbolo(simbolo);
        }
    }
     
    public void parse() { 
        try { parsePrograma(); 
            System.out.println("Programa válido!!! \\-_-/"); 
        } catch (RuntimeException e) { 
            System.err.println("Erro de parsing: " + e.getMessage()); 
        }
    }

    private void parsePrograma() {
        while (check(TipoToken.INTEIRO, "int") || check(TipoToken.BOOLEANO, "bool") ||
               check(TipoToken.PROCEDIMENTO, "procedure") || check(TipoToken.FUNCAO, "function")) {
            
            if (check(TipoToken.INTEIRO, "int") || check(TipoToken.BOOLEANO, "bool")) {
                parseDeclaracaoVariaveis();
            } else {
                parseDeclaracaoSubrotinas();
            }
        }
    
        consume(TipoToken.MAIN, "main", "Erro: 'main' esperado");
        consume(TipoToken.IDENTIFICADOR, "Erro: Identificador esperado após 'main'");
        consume(TipoToken.ABRE_PAREN, "(", "Erro: '(' esperado após identificador");
        consume(TipoToken.FECHA_PAREN, ")", "Erro: ')' esperado após '('");
        consume(TipoToken.ABRE_CHAVES, "{", "Erro: '{' esperado após ')'");
        parseBloco();
        consume(TipoToken.FECHA_CHAVES, "}", "Erro: '}' esperado após bloco");
        consume(TipoToken.END, "end", "Erro: 'end' esperado");
    }

    private void parseBloco() {
        while (true) {
            if (check(TipoToken.INTEIRO, "int") || check(TipoToken.BOOLEANO, "bool")) {
                parseDeclaracaoVariaveis();
            }  else if (check(TipoToken.PROCEDIMENTO, "procedure") || check(TipoToken.FUNCAO, "function")) {
                parseDeclaracaoSubrotinas();
            } else if (check(TipoToken.WHILE, "while") || check(TipoToken.IF, "if") || check(TipoToken.PRINT, "print")) {
                parseComando();
            } else {
                break;
            }
        }
    }


    private void parseDeclaracaoVariaveis() {
        while (check(TipoToken.INTEIRO, "int") || check(TipoToken.BOOLEANO, "bool")) {
            parseDeclaracaoVariavel();
        }
    }

    private void parseDeclaracaoVariavel() {
        if (check(TipoToken.INTEIRO, "int") || check(TipoToken.BOOLEANO, "bool")) {
            String tipo = tokens.get(pos).getValor();
            consume(peek().getTipo(), "Erro: Tipo de variável esperado");
            
            // Consome e adiciona o primeiro identificador
            Token identificador = tokens.get(pos);
            consume(TipoToken.IDENTIFICADOR, "Erro: Identificador esperado após o tipo");
        
            String valor = null;
            if (match(TipoToken.OPE_ATRI, "=")) {
                valor = tokens.get(pos).getValor();
                parseExpressao();
            }
            adicionarSimbolo(identificador, tipo, valor);
            
            // Verifica se há mais identificadores na declaração
            while (match(TipoToken.VIRGULA, ",")) {
                identificador = tokens.get(pos);
                consume(TipoToken.IDENTIFICADOR, "Erro: Identificador esperado após ','");
        
                if (match(TipoToken.OPE_ATRI, "=")) {
                    valor = tokens.get(pos).getValor();
                    parseExpressao();
                } else {
                    valor = null;
                }
                adicionarSimbolo(identificador, tipo, valor);
            }
        
            consume(TipoToken.PON_VIR, ";", "Erro: ';' esperado no final da declaração de variáveis");
        } else {
            throw new RuntimeException("Erro: Tipo de variável (int ou bool) esperado");
        }
    }       

    private void parseDeclaracaoSubrotinas() {
        while (check(TipoToken.PROCEDIMENTO) || check(TipoToken.FUNCAO)) {
            if (check(TipoToken.PROCEDIMENTO)) {
                Token procedimento = tokens.get(pos + 1);
                adicionarSimbolo(procedimento, "procedure", null);
                parseDeclaracaoProcedimento();
            } else if (check(TipoToken.FUNCAO)) {
                Token funcao = tokens.get(pos + 1);
                String tipoRetorno = tokens.get(pos + 2).getValor();
                adicionarSimbolo(funcao, tipoRetorno, null);
                parseDeclaracaoFuncao();
            }
        }
    }

    private void parseDeclaracaoProcedimento() {
        match(TipoToken.PROCEDIMENTO, "procedure");
        consume(TipoToken.IDENTIFICADOR, "Erro: Identificador esperado após 'procedure'");
        consume(TipoToken.ABRE_PAREN, "(", "Erro: '(' esperado após o identificador do procedimento");
        parseParametros();
        consume(TipoToken.FECHA_PAREN, ")", "Erro: ')' esperado após os parâmetros");
        consume(TipoToken.ABRE_CHAVES, "{", "Erro: '{' esperado no início do bloco do procedimento");
        parseBloco();
        consume(TipoToken.FECHA_CHAVES, "}", "Erro: '}' esperado após o bloco do procedimento");
    }

    private void parseDeclaracaoFuncao() {
        match(TipoToken.FUNCAO, "function");
        String tipoRetorno = tokens.get(pos).getValor();
        consume(TipoToken.INTEIRO, "Erro: Tipo de retorno esperado após 'function'");
        Token funcao = tokens.get(pos);
        consume(TipoToken.IDENTIFICADOR, "Erro: Identificador da função esperado após o tipo");
        adicionarSimbolo(funcao, tipoRetorno, null); 
        consume(TipoToken.ABRE_PAREN, "(", "Erro: '(' esperado após o identificador da função");
        parseParametros();
        consume(TipoToken.FECHA_PAREN, ")", "Erro: ')' esperado após os parâmetros");
        consume(TipoToken.ABRE_CHAVES, "{", "Erro: '{' esperado no início do bloco da função");
        parseBloco();
        parseReturn();
        consume(TipoToken.FECHA_CHAVES, "}", "Erro: '}' esperado após o bloco da função");
    }

    private void parseComando() {
        if (check(TipoToken.WHILE, "while")) {
            parseComandoWhile();
        } else if (check(TipoToken.IF, "if")) {
            parseComandoIf();
        } else if (check(TipoToken.PRINT, "print")) {
            parseComandoPrint();
        } else if (check(TipoToken.IDENTIFICADOR)) {
            parseChamadaFuncao();
        }else {
            throw new RuntimeException("Erro: Comando inválido");
        }
    }
    
    private void parseChamadaFuncao() {
        consume(TipoToken.IDENTIFICADOR, "Erro: Identificador de função esperado");
        consume(TipoToken.ABRE_PAREN, "(", "Erro: '(' esperado após o identificador da função");
        parseArgumentos();
        consume(TipoToken.FECHA_PAREN, ")", "Erro: ')' esperado após os argumentos");
        consume(TipoToken.PON_VIR, ";", "Erro: ';' esperado após a chamada da função");
    }
    
    private void parseArgumentos() {
        if (!check(TipoToken.FECHA_PAREN)) { 
            parseExpressao();
            while (match(TipoToken.VIRGULA, ",")) {
                parseExpressao();
            }
        }
    }

    private void parseComandoWhile() {
        consume(TipoToken.WHILE, "while", "Erro: 'while' esperado");
        consume(TipoToken.ABRE_PAREN, "(", "Erro: '(' esperado após 'while'");
        parseExpressao();
        consume(TipoToken.FECHA_PAREN, ")", "Erro: ')' esperado após a expressão no 'while'");
        consume(TipoToken.ABRE_CHAVES, "{", "Erro: '{' esperado após ')'");
        parseBloco();
        consume(TipoToken.FECHA_CHAVES, "}", "Erro: '}' esperado após o bloco do 'while'");
    }

    private void parseComandoIf() {
        consume(TipoToken.IF, "if", "Erro: 'if' esperado");
        consume(TipoToken.ABRE_PAREN, "(", "Erro: '(' esperado após 'if'");
        parseExpressao();
        consume(TipoToken.FECHA_PAREN, ")", "Erro: ')' esperado após a expressão");
        consume(TipoToken.ABRE_CHAVES, "{", "Erro: '{' esperado após a expressão do 'if'");
        parseBloco();
        consume(TipoToken.FECHA_CHAVES, "}", "Erro: '}' esperado após o bloco do 'if'");

        if (match(TipoToken.ELSE, "else")) {
            consume(TipoToken.ABRE_CHAVES, "{", "Erro: '{' esperado após 'else'");
            parseBloco();
            consume(TipoToken.FECHA_CHAVES, "}", "Erro: '}' esperado após o bloco do 'else'");
        }
    }

    private void parseComandoPrint() {
        consume(TipoToken.PRINT, "Erro: 'print' esperado");
        consume(TipoToken.ABRE_PAREN, "Erro: '(' esperado após 'print'");
        if (check(TipoToken.STRING)) {
            consume(TipoToken.STRING, "Erro: 'string' esperado após '('");
        } else if (check(TipoToken.IDENTIFICADOR)) {
            consume(TipoToken.IDENTIFICADOR, "Erro: Identificador esperado após '('");
        } else {
            throw new RuntimeException("Erro: Esperado uma string ou identificador após '('");
        }
        consume(TipoToken.FECHA_PAREN, "Erro: ')' esperado após 'string'");
        consume(TipoToken.PON_VIR, ";", "Erro: ';' esperado após o comando 'print'");
    }

    private void parseReturn() {
        consume(TipoToken.RETORNO, "return", "Erro: 'return' esperado");

        if (check(TipoToken.IDENTIFICADOR) || check(TipoToken.NUMERO) || check(TipoToken.STRING) || check(TipoToken.VERDADEIRO) || check(TipoToken.FALSO)) {
            parseExpressao();
        } else {
            throw new RuntimeException("Erro: Esperado um identificador, número, string ou valor booleano após 'return'");
        }

        consume(TipoToken.PON_VIR, ";", "Erro: Falta um ponto e vírgula ';' após o retorno");
    }

    private void parseExpressao() {
        parseExpressaoSimples();

        if (peek() != null && peek().getTipo() == TipoToken.OPE_REL) {
            consume(TipoToken.OPE_REL, "Erro: Operador relacional esperado");
            parseExpressaoSimples();
        }
    }

    private void parseExpressaoSimples(){
        parseTermo();

        while (peek() != null && peek().getTipo() == TipoToken.OPE_ARIT) {
            consume(TipoToken.OPE_ARIT, "Erro: Operador aritmético esperado");
            parseTermo();
        }
    }

    private void parseTermo() {
        parseFator();

        while (peek() != null && peek().getTipo() == TipoToken.OPE_ARIT &&
                (peek().getValor().equals("*") || peek().getValor().equals("/") || peek().getValor().equals("%"))) {
            consume(TipoToken.OPE_ARIT, "Erro: Operador de multiplicação/divisão esperado");
            parseFator();
        }
    }

    private void parseFator() {
        if (check(TipoToken.IDENTIFICADOR) || check(TipoToken.NUMERO)) {
            consume(peek().getTipo(), "Erro: Identificador ou número esperado");
        } else if (match(TipoToken.ABRE_PAREN, "(")) {
            parseExpressao();
            consume(TipoToken.FECHA_PAREN, "Erro: ')' esperado após a expressão");
        } else {
            throw new RuntimeException("Erro: Fator inválido na expressão");
        }
    }


    private void parseParametros() {
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
        System.out.println("Esperando token: " + tipo + ", Token atual: " + peek());
        if (!check(tipo)) {
            System.out.println("Erro ao consumir token. Esperado: " + tipo + ", Encontrado: " + (peek() != null ? peek().getTipo() : "null"));
            throw new RuntimeException(erro);
        }
        System.out.println("Consumindo token: " + tokens.get(pos));
        pos++;
    }

    private void consume(TipoToken tipo, String valor, String erro) {
        if (!check(tipo)) {
            System.out.println("Erro ao consumir token. Esperado: " + tipo + ", Encontrado: " + (peek() != null ? peek().getTipo() : "null"));
            throw new RuntimeException(erro);
        }
        System.out.println("Consumindo token: " + tokens.get(pos));
        pos++;  // Consome o token e avança
        if (pos < tokens.size()) {
            System.out.println("Próximo token: " + tokens.get(pos));
        }
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

    //Metódos auxiliares para

    //verificar o próximo token na lista de tokens sem consumi-lo
    private Token peek(){
        if (pos < tokens.size()) {
            return tokens.get(pos);

        }
        return null;
    }
}
