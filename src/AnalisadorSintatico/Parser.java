package AnalisadorSintatico;

import AnalisadorLexico.*;
import java.util.List;

public class Parser {
    private final List<Token> tokens;
    private int pos = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public void parse() {
        parsePrograma();
    }

    private void parsePrograma() {
        consume(TipoToken.MAIN, "main", "Erro: 'main' esperado"); 
        consume(TipoToken.IDENTIFICADOR, "Erro: Identificador esperado após 'main'"); 
        consume(TipoToken.ABRE_PAREN, "(", "Erro: '(' esperado após identificador"); 
        consume(TipoToken.FECHA_PAREN, ")", "Erro: ')' esperado após '('"); 
        consume(TipoToken.ABRE_CHAVES, "{", "Erro: '{' esperado após ')'"); 
        parseBloco(); 
        consume(TipoToken.FECHA_CHAVES, "}", "Erro: '}' esperado após bloco"); 
        consume(TipoToken.END, "end", "Erro: 'end' esperado"); 
        System.out.println("Programa válido.");
    }

    private void parseBloco() {
        parseDeclaracaoVariaveis();
        parseDeclaracaoSubrotinas();
        parseComandos();
    }

    private void parseDeclaracaoVariaveis() {
        while (check(TipoToken.INTEIRO, "int") || check(TipoToken.BOOLEANO, "bool")) {
            parseDeclaracaoVariavel();
        }
    }

    private void parseDeclaracaoVariavel() {
        if(check(TipoToken.INTEIRO, "int") || check(TipoToken.BOOLEANO, "bool")){
            consume(TipoToken.INTEIRO, "int", "Erro: Tipo de variável esperado");
            consume(TipoToken.IDENTIFICADOR, "Erro: Identificador esperado após o tipo");
            
            if (match(TipoToken.OPE_ATRI, "=")) {
                parseExpressao();
            }

            while (match(TipoToken.DELIMITADOR, ",")) {
                consume(TipoToken.INTEIRO,  "int", "Erro: Tipo esperado após ','");
                consume(TipoToken.IDENTIFICADOR, "Erro: Identificador esperado após o tipo");

                if (match(TipoToken.OPE_ATRI, "=")) {
                    parseExpressao();
                }
            }
            consume(TipoToken.DELIMITADOR, ";", "Erro: ';' esperado no final da declaração de variáveis");
        }else{
            throw new RuntimeException("Erro: Tipo de variável (int ou bool) esperado");
        }
    }

    private void parseDeclaracaoSubrotinas() {
        while (check(TipoToken.PROCEDIMENTO, "procedure") || check(TipoToken.FUNCAO, "function")) {
            if (match(TipoToken.PROCEDIMENTO, "procedure")) {
                parseDeclaracaoProcedimento();
            } else if (match(TipoToken.FUNCAO, "function")) {
                parseDeclaracaoFuncao();
            }
        }
    }

    private void parseDeclaracaoProcedimento() {
        consume(TipoToken.PROCEDIMENTO, "Erro: Identificador esperado após 'procedure'");
        consume(TipoToken.ABRE_PAREN, "(", "Erro: '(' esperado após o identificador do procedimento");
        parseParametros();
        consume(TipoToken.FECHA_PAREN, ")", "Erro: ')' esperado após os parâmetros");
        consume(TipoToken.ABRE_CHAVES, "{", "Erro: '{' esperado no início do bloco do procedimento");
        parseBloco();
        consume(TipoToken.FECHA_CHAVES, "}", "Erro: '}' esperado após o bloco do procedimento");
    }

    private void parseDeclaracaoFuncao() {
        consume(TipoToken.IDENTIFICADOR, "Erro: Identificador esperado após 'function'");
        consume(TipoToken.ABRE_PAREN, "(", "Erro: '(' esperado após o identificador da função");
        parseParametros();
        consume(TipoToken.FECHA_PAREN, ")", "Erro: ')' esperado após os parâmetros");
        consume(TipoToken.DELIMITADOR, ":", "Erro: ':' esperado após os parâmetros");
        consume(TipoToken.PALAVRA_CHAVE, "Erro: Tipo esperado após ':'");
        consume(TipoToken.ABRE_CHAVES, "{", "Erro: '{' esperado no início do bloco da função");
        parseBloco();
        consume(TipoToken.FECHA_CHAVES, "}", "Erro: '}' esperado após o bloco da função");
    }

    private void parseComandos() {
        while (check(TipoToken.WHILE, "while") || check(TipoToken.IF, "if")) {
            parseComando();
        }
    }

    private void parseComando() {
        if (match(TipoToken.WHILE, "while")) {
            parseComandoWhile();
        } else if (match(TipoToken.IF, "if")) {
            parseComandoIf();
        }
    }

    private void parseComandoWhile() {
        consume(TipoToken.WHILE, "while");
        parseExpressao();
        parseBloco();
    }

    private void parseComandoIf() {
        consume(TipoToken.IF, "if");
        parseExpressao();
        parseBloco();
        if (match(TipoToken.ELSE, "else")) {
            parseBloco();
        }
    }

    private void parseExpressao() {
        parseExpressaoSimples();

        consume(TipoToken.IDENTIFICADOR, "Erro: Identificador esperado em expressão");
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
        if (check(TipoToken.IDENTIFICADOR)) {
            consume(TipoToken.IDENTIFICADOR, "Erro: Identificador esperado em parâmetros");
            while (match(TipoToken.DELIMITADOR, ",")) {
                consume(TipoToken.IDENTIFICADOR, "Erro: Identificador esperado após ','");
            }
        }
    }

    private void consume(TipoToken tipo, String erro) {
        if (!check(tipo)) {
            throw new RuntimeException(erro);
        }
        System.out.println("Consumindo token: " + tokens.get(pos));
        pos++;
    }

    private void consume(TipoToken tipo, String valor, String erro) {
        System.out.println("Consumindo token esperado: " + tipo + " com valor: " + valor);
        System.out.println("Próximo token: " + tokens.get(pos));
        if (!check(tipo, valor)) {
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

    //Metódos auxiliares para 

    //verificar o próximo token na lista de tokens sem consumi-lo
    private Token peek(){
        if (pos < tokens.size()) {
            return tokens.get(pos);
            
        }
        return null;
    }
}
