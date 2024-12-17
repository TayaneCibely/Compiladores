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
        consume(TipoToken.PALAVRA_CHAVE, "main", "Erro: 'main' esperado"); 
        consume(TipoToken.IDENTIFICADOR, "Erro: Identificador esperado após 'main'"); 
        consume(TipoToken.DELIMITADOR, "(", "Erro: '(' esperado após identificador"); 
        consume(TipoToken.DELIMITADOR, ")", "Erro: ')' esperado após '('"); 
        consume(TipoToken.DELIMITADOR, "{", "Erro: '{' esperado após ')'"); 
        parseBloco(); 
        consume(TipoToken.DELIMITADOR, "}", "Erro: '}' esperado após bloco"); 
        consume(TipoToken.PALAVRA_CHAVE, "end", "Erro: 'end' esperado"); 
        System.out.println("Programa válido.");
    }

    private void parseBloco() {
        parseDeclaracaoVariaveis();
        parseDeclaracaoSubrotinas();
        parseComandos();
    }

    private void parseDeclaracaoVariaveis() {
        while (check(TipoToken.PALAVRA_CHAVE, "int") || check(TipoToken.PALAVRA_CHAVE, "bool")) {
            parseDeclaracaoVariavel();
        }
    }

    private void parseDeclaracaoVariavel() {
        consume(TipoToken.PALAVRA_CHAVE, "Erro: Tipo de variável esperado");
        consume(TipoToken.IDENTIFICADOR, "Erro: Identificador esperado após o tipo");
        while (match(TipoToken.DELIMITADOR, ",")) {
            consume(TipoToken.PALAVRA_CHAVE, "Erro: Tipo esperado após ','");
            consume(TipoToken.IDENTIFICADOR, "Erro: Identificador esperado após o tipo");
        }
        consume(TipoToken.DELIMITADOR, ";");
    }

    private void parseDeclaracaoSubrotinas() {
        while (check(TipoToken.PALAVRA_CHAVE, "procedure") || check(TipoToken.PALAVRA_CHAVE, "function")) {
            if (match(TipoToken.PALAVRA_CHAVE, "procedure")) {
                parseDeclaracaoProcedimento();
            } else if (match(TipoToken.PALAVRA_CHAVE, "function")) {
                parseDeclaracaoFuncao();
            }
        }
    }

    private void parseDeclaracaoProcedimento() {
        consume(TipoToken.IDENTIFICADOR, "Erro: Identificador esperado após 'procedure'");
        consume(TipoToken.DELIMITADOR, "(", "Erro: '(' esperado após o identificador do procedimento");
        parseParametros();
        consume(TipoToken.DELIMITADOR, ")", "Erro: ')' esperado após os parâmetros");
        consume(TipoToken.DELIMITADOR, "{", "Erro: '{' esperado no início do bloco do procedimento");
        parseBloco();
        consume(TipoToken.DELIMITADOR, "}", "Erro: '}' esperado após o bloco do procedimento");
    }

    private void parseDeclaracaoFuncao() {
        consume(TipoToken.IDENTIFICADOR, "Erro: Identificador esperado após 'function'");
        consume(TipoToken.DELIMITADOR, "(", "Erro: '(' esperado após o identificador da função");
        parseParametros();
        consume(TipoToken.DELIMITADOR, ")", "Erro: ')' esperado após os parâmetros");
        consume(TipoToken.DELIMITADOR, ":", "Erro: ':' esperado após os parâmetros");
        consume(TipoToken.PALAVRA_CHAVE, "Erro: Tipo esperado após ':'");
        consume(TipoToken.DELIMITADOR, "{", "Erro: '{' esperado no início do bloco da função");
        parseBloco();
        consume(TipoToken.DELIMITADOR, "}", "Erro: '}' esperado após o bloco da função");
    }

    private void parseComandos() {
        while (check(TipoToken.PALAVRA_CHAVE, "while") || check(TipoToken.PALAVRA_CHAVE, "if")) {
            parseComando();
        }
    }

    private void parseComando() {
        if (match(TipoToken.PALAVRA_CHAVE, "while")) {
            parseComandoWhile();
        } else if (match(TipoToken.PALAVRA_CHAVE, "if")) {
            parseComandoIf();
        }
    }

    private void parseComandoWhile() {
        consume(TipoToken.PALAVRA_CHAVE, "while");
        parseExpressao();
        parseBloco();
    }

    private void parseComandoIf() {
        consume(TipoToken.PALAVRA_CHAVE, "if");
        parseExpressao();
        parseBloco();
        if (match(TipoToken.PALAVRA_CHAVE, "else")) {
            parseBloco();
        }
    }

    private void parseExpressao() {
        consume(TipoToken.IDENTIFICADOR, "Erro: Identificador esperado em expressão");
        while (check(TipoToken.OPERADOR)) {
            consume(TipoToken.OPERADOR, "Erro: Operador esperado");
            consume(TipoToken.IDENTIFICADOR, "Erro: Identificador esperado após operador");
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
        if (!check(tipo, valor)) {
            throw new RuntimeException(erro);
        }
        System.out.println("Consumindo token: " + tokens.get(pos));
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
}
