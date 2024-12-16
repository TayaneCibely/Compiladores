package lexer;

import java.util.*;
import java.util.regex.*;

/**
 * Classe responsável por realizar a análise léxica do código-fonte.
 */
public class Lexer {
    // Palavras-chave suportadas pela linguagem
    private static final Set<String> PALAVRAS_CHAVE = Set.of("main", "end", "int", "bool", "procedure", "function", "return"
            ,"while", "scanf", "printf", "break", "continue"
     );

    private static final Set<String> LOGICOS = Set.of("or", "not", "and", "true", "false");

    private static final Set<String> CONDICIONAIS = Set.of("if", "else");

    private static final Set<String> OPERADORES = Set.of(
            "*", "+", "-", "/", "==", "=", "<", "<=", ">", ">=", "%"
    );

    private static final Set<String> DELIMITADORES = Set.of(
            "{", "}", ",", ";", "“", "”"
    );

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

        for (int i = 0; i < tamanho; ) {
            char caractereAtual = codigoFonte.charAt(i);

            if (caractereAtual == '\n') {
                linhaAtual++;
                i++;
                continue;
            }

            if (Character.isWhitespace(caractereAtual)) {
                i++;
                continue;
            }

            if (Character.isLetter(caractereAtual) || caractereAtual == '_') {
                StringBuilder identificador = new StringBuilder();
                while (i < tamanho && (Character.isLetterOrDigit(codigoFonte.charAt(i)) || codigoFonte.charAt(i) == '_')) {
                    identificador.append(codigoFonte.charAt(i));
                    i++;
                }
                String valor = identificador.toString();
                if (PALAVRAS_CHAVE.contains(valor)) {
                    tokens.add(new Token(TipoToken.PALAVRA_CHAVE, valor, linhaAtual));
                } else if (CONDICIONAIS.contains(valor)) {
                    tokens.add(new Token(TipoToken.CONDICIONAIS, valor, linhaAtual));
                } else if (LOGICOS.contains(valor)) {
                    tokens.add(new Token(TipoToken.LOGICO, valor, linhaAtual));
                } else {
                    tokens.add(new Token(TipoToken.IDENTIFICADOR, valor, linhaAtual));
                }
                continue;
            }

            if (Character.isDigit(caractereAtual)) {
                StringBuilder numero = new StringBuilder();
                while (i < tamanho && Character.isDigit(codigoFonte.charAt(i))) {
                    numero.append(codigoFonte.charAt(i));
                    i++;
                }
                tokens.add(new Token(TipoToken.NUMERO, numero.toString(), linhaAtual));
                continue;
            }

            String operador = String.valueOf(caractereAtual);
            if (OPERADORES.contains(operador)) {
                tokens.add(new Token(TipoToken.OPERADOR, operador, linhaAtual));
                i++;
                continue;
            }

            if(DELIMITADORES.contains(String.valueOf(caractereAtual))){
                tokens.add(new Token(TipoToken.DELIMITADOR, String.valueOf(caractereAtual), linhaAtual));
                i++;
                continue;
            }

            if (ABRE_PAREN.contains(String.valueOf(caractereAtual))) {
                tokens.add(new Token(TipoToken.ABRE_PAREN, "(", linhaAtual));
                i++;
                continue;
            }

            if (FECHA_PAREN.contains(String.valueOf(caractereAtual))) {
                tokens.add(new Token(TipoToken.FECHA_PAREN, ")", linhaAtual));
                i++;
                continue;
            }

            tokens.add(new Token(TipoToken.DESCONHECIDO, String.valueOf(caractereAtual), linhaAtual));
            i++;
        }

        return tokens;
    }
}
