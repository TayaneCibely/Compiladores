package AnalisadorLexico;

import java.util.*;
import java.util.regex.*;

public class Lexer {
    // Palavras-chave suportadas pela linguagem
    private static final Set<String> PALAVRAS_CHAVE = Set.of("main", "end", "int", "bool", "procedure", "function", "return", "while", "scanf", "printf", "break", "continue");

    private static final Set<String> LOGICOS = Set.of("or", "not", "and", "true", "false");

    private static final Set<String> CONDICIONAIS = Set.of("if", "else");

    private static final Set<String> OPERADORES = Set.of("*", "+", "-", "/", "==", "=", "<", "<=", ">", ">=", "%");

    private static final Set<String> DELIMITADORES = Set.of("{", "}", ",", ";", "“", "”");

    private static final Set<String> ABRE_PAREN = Set.of("(");

    private static final Set<String> FECHA_PAREN = Set.of(")");

    private static final Pattern PADRAO_NUMERO = Pattern.compile("\\d+");

    private static final Pattern PADRAO_IDENTIFICADOR = Pattern.compile("[a-zA-Z_][a-zA-Z0-9_]*");

    private final String codigoFonte;

    public Lexer(String codigoFonte) {
        this.codigoFonte = codigoFonte;
    }

    public List<Token> analisar() {
        List<Token> tokens = new ArrayList<>();
        int tamanho = codigoFonte.length();
        int linhaAtual = 1;
        int pos = 0;

        while (pos < tamanho) {
            char currentChar = codigoFonte.charAt(pos);

            if (currentChar == '\n') {
                linhaAtual++;
                pos++;
                continue;
            }

            if (Character.isWhitespace(currentChar)) {
                pos++;
                continue;
            }

            Matcher matcher = PADRAO_IDENTIFICADOR.matcher(codigoFonte.substring(pos));
            if (matcher.lookingAt()) {
                String identificador = matcher.group();
                if (PALAVRAS_CHAVE.contains(identificador)) {
                    tokens.add(new Token(TipoToken.PALAVRA_CHAVE, identificador, linhaAtual));
                } else if (CONDICIONAIS.contains(identificador)) {
                    tokens.add(new Token(TipoToken.CONDICIONAIS, identificador, linhaAtual));
                } else if (LOGICOS.contains(identificador)) {
                    tokens.add(new Token(TipoToken.LOGICO, identificador, linhaAtual));
                } else {
                    tokens.add(new Token(TipoToken.IDENTIFICADOR, identificador, linhaAtual));
                }
                pos += identificador.length();
                continue;
            }

            matcher = PADRAO_NUMERO.matcher(codigoFonte.substring(pos));
            if (matcher.lookingAt()) {
                String numero = matcher.group();
                tokens.add(new Token(TipoToken.NUMERO, numero, linhaAtual));
                pos += numero.length();
                continue;
            }

            if (OPERADORES.contains(String.valueOf(currentChar))) {
                tokens.add(new Token(TipoToken.OPERADOR, String.valueOf(currentChar), linhaAtual));
                pos++;
                continue;
            }

            if (DELIMITADORES.contains(String.valueOf(currentChar))) {
                tokens.add(new Token(TipoToken.DELIMITADOR, String.valueOf(currentChar), linhaAtual));
                pos++;
                continue;
            }

            if (ABRE_PAREN.contains(String.valueOf(currentChar))) {
                tokens.add(new Token(TipoToken.ABRE_PAREN, "(", linhaAtual));
                pos++;
                continue;
            }

            if (FECHA_PAREN.contains(String.valueOf(currentChar))) {
                tokens.add(new Token(TipoToken.FECHA_PAREN, ")", linhaAtual));
                pos++;
                continue;
            }

            if(currentChar == '“'){
                tokens.add(new Token(TipoToken.ABRE_CHA, String.valueOf(currentChar), linhaAtual));
                pos++;
                continue;
            }
            
            if(currentChar == '”'){
                tokens.add(new Token(TipoToken.FECHA_CHA, String.valueOf(currentChar), linhaAtual));
                pos++;
                continue;
            }

            throw new RuntimeException("Erro: Caractere inesperado: " + currentChar + " na linha: " + linhaAtual);
        }

        return tokens;
    }
}
