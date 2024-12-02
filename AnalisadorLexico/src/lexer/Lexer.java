package lexer;

import java.util.*;
import java.util.regex.*;

/**
 * Classe responsável por realizar a análise léxica do código-fonte.
 */
public class Lexer {
    // Palavras-chave suportadas pela linguagem
    private static final Set<String> PALAVRAS_CHAVE = Set.of("se", "senao", "enquanto", "retorne");

    // Operadores reconhecidos
    private static final Set<String> OPERADORES = Set.of("+", "-", "*", "/", "=");

    // Símbolos reconhecidos
    private static final Set<Character> SIMBOLOS = Set.of(';', '(', ')', '{', '}');

    // Padrões para números e identificadores
    private static final Pattern PADRAO_NUMERO = Pattern.compile("\\d+");
    private static final Pattern PADRAO_IDENTIFICADOR = Pattern.compile("[a-zA-Z_][a-zA-Z0-9_]*");

    private final String codigoFonte; // Código fonte que será analisado

    public Lexer(String codigoFonte) {
        this.codigoFonte = codigoFonte;
    }

    /**
     * Realiza a análise léxica no código-fonte e retorna a lista de tokens.
     */
    public List<Token> analisar() {
        List<Token> tokens = new ArrayList<>();
        int tamanho = codigoFonte.length();

        for (int i = 0; i < tamanho; ) {
            char caractereAtual = codigoFonte.charAt(i);

            // Ignorar espaços em branco
            if (Character.isWhitespace(caractereAtual)) {
                i++;
                continue;
            }

            // Identificar palavras-chave e identificadores
            if (Character.isLetter(caractereAtual) || caractereAtual == '_') {
                StringBuilder identificador = new StringBuilder();
                while (i < tamanho && (Character.isLetterOrDigit(codigoFonte.charAt(i)) || codigoFonte.charAt(i) == '_')) {
                    identificador.append(codigoFonte.charAt(i));
                    i++;
                }
                String valor = identificador.toString();
                if (PALAVRAS_CHAVE.contains(valor)) {
                    tokens.add(new Token(TipoToken.PALAVRA_CHAVE, valor));
                } else {
                    tokens.add(new Token(TipoToken.IDENTIFICADOR, valor));
                }
                continue;
            }

            // Identificar números
            if (Character.isDigit(caractereAtual)) {
                StringBuilder numero = new StringBuilder();
                while (i < tamanho && Character.isDigit(codigoFonte.charAt(i))) {
                    numero.append(codigoFonte.charAt(i));
                    i++;
                }
                tokens.add(new Token(TipoToken.NUMERO, numero.toString()));
                continue;
            }

            // Identificar operadores
            String operador = String.valueOf(caractereAtual);
            if (OPERADORES.contains(operador)) {
                tokens.add(new Token(TipoToken.OPERADOR, operador));
                i++;
                continue;
            }

            // Identificar símbolos
            if (SIMBOLOS.contains(caractereAtual)) {
                tokens.add(new Token(TipoToken.SIMBOLO, String.valueOf(caractereAtual)));
                i++;
                continue;
            }

            // Token desconhecido
            tokens.add(new Token(TipoToken.DESCONHECIDO, String.valueOf(caractereAtual)));
            i++;
        }

        return tokens;
    }
}
