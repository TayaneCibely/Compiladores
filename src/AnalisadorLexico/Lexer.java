package AnalisadorLexico;

import java.util.*;
import java.util.regex.*;

public class Lexer {
    // Palavras-chave suportadas pela linguagem
    private static final Set<String> PALAVRAS_CHAVE = Set.of();

    private static final Set<String> MAIN = Set.of("main");

    private static final Set<String> END = Set.of("end");

    private static final Set<String> INTEIRO = Set.of("int");

    private static final Set<String> BOOLEANO = Set.of("bool");

    private static final Set<String> PROCEDIMENTO = Set.of("procedure");

    private static final Set<String> FUNCAO = Set.of("function");

    private static final Set<String> RETORNO = Set.of("return");

    private static final Set<String> WHILE = Set.of("while");

    private static final Set<String> SCANF = Set.of("scanf");

    private static final Set<String> PRINTF = Set.of("printf");

    private static final Set<String> BREAK = Set.of("break");

    private static final Set<String> CONTINUE = Set.of("continue");

    private static final Set<String> LOGICOS = Set.of("or", "not", "and");

    private static final Set<String> VERDADEIRO = Set.of("true");

    private static final Set<String> FALSO = Set.of("false");

    private static final Set<String> IF = Set.of("if");

    private static final Set<String> ELSE = Set.of("else");

    private static final Set<String> OPE_ARIT = Set.of("*", "+", "-", "/", "=", "%");

    private static final Set<String> OPE_ATRI = Set.of("=");
    
    private static final Set<String> OPE_REL = Set.of("==", "<", ">", ">=", "<=");

    private static final Set<String> VIRGULA = Set.of(",");

    private static final Set<String> PON_VIR = Set.of(";");

    private static final Set<String> ABRE_PAREN = Set.of("(");

    private static final Set<String> FECHA_PAREN = Set.of(")");

    private static final Set<String> ABRE_CHAVES = Set.of("{");

    private static final Set<String> FECHA_CHAVES = Set.of("}");

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
                } else if (MAIN.contains(identificador)) {
                    tokens.add(new Token(TipoToken.MAIN, identificador, linhaAtual));
                } else if (END.contains(identificador)) {
                    tokens.add(new Token(TipoToken.END, identificador, linhaAtual));
                } else if (PROCEDIMENTO.contains(identificador)) {
                    tokens.add(new Token(TipoToken.PROCEDIMENTO, identificador, linhaAtual));
                } else if (FUNCAO.contains(identificador)) {
                    tokens.add(new Token(TipoToken.FUNCAO, identificador, linhaAtual));
                } else if (INTEIRO.contains(identificador)) {
                    tokens.add(new Token(TipoToken.INTEIRO, identificador, linhaAtual));
                } else if (RETORNO.contains(identificador)) {
                    tokens.add(new Token(TipoToken.RETORNO, identificador, linhaAtual));
                } else if (WHILE.contains(identificador)) {
                    tokens.add(new Token(TipoToken.WHILE, identificador, linhaAtual));
                } else if (SCANF.contains(identificador)) {
                    tokens.add(new Token(TipoToken.SCANF, identificador, linhaAtual));
                } else if (PRINTF.contains(identificador)) {
                    tokens.add(new Token(TipoToken.PRINTF, identificador, linhaAtual));
                } else if (BREAK.contains(identificador)) {
                    tokens.add(new Token(TipoToken.BREAK, identificador, linhaAtual));
                } else if (CONTINUE.contains(identificador)) {
                    tokens.add(new Token(TipoToken.CONTINUE, identificador, linhaAtual));
                } else if (BOOLEANO.contains(identificador)) {
                    tokens.add(new Token(TipoToken.BOOLEANO, identificador, linhaAtual));
                } else if (IF.contains(identificador)) {
                    tokens.add(new Token(TipoToken.IF, identificador, linhaAtual));
                }  else if (ELSE.contains(identificador)) {
                    tokens.add(new Token(TipoToken.ELSE, identificador, linhaAtual));
                } else if (LOGICOS.contains(identificador)) {
                    tokens.add(new Token(TipoToken.LOGICO, identificador, linhaAtual));
                } else if (VERDADEIRO.contains(identificador)) {
                    tokens.add(new Token(TipoToken.VERDADEIRO, identificador, linhaAtual));
                } else if (FALSO.contains(identificador)) {
                    tokens.add(new Token(TipoToken.FALSO, identificador, linhaAtual));
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

            if (OPE_ATRI.contains(String.valueOf(currentChar))) {
                tokens.add(new Token(TipoToken.OPE_ATRI, String.valueOf(currentChar), linhaAtual));
                pos++;
                continue;
            }

            if (OPE_ARIT.contains(String.valueOf(currentChar))) {
                tokens.add(new Token(TipoToken.OPE_ARIT, String.valueOf(currentChar), linhaAtual));
                pos++;
                continue;
            }

            if (OPE_REL.contains(String.valueOf(currentChar))) {
                tokens.add(new Token(TipoToken.OPE_REL, String.valueOf(currentChar), linhaAtual));
                pos++;
                continue;
            }

            if (PON_VIR.contains(String.valueOf(currentChar))) {
                tokens.add(new Token(TipoToken.PON_VIR, String.valueOf(currentChar), linhaAtual));
                pos++;
                continue;
            }

            if (VIRGULA.contains(String.valueOf(currentChar))) {
                tokens.add(new Token(TipoToken.VIRGULA, String.valueOf(currentChar), linhaAtual));
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

            if (ABRE_CHAVES.contains(String.valueOf(currentChar))) {
                tokens.add(new Token(TipoToken.ABRE_CHAVES, "{", linhaAtual));
                pos++;
                continue;
            }

            if (FECHA_CHAVES.contains(String.valueOf(currentChar))) {
                tokens.add(new Token(TipoToken.FECHA_CHAVES, "}", linhaAtual));
                pos++;
                continue;
            }

            if(currentChar == '"'){
                tokens.add(new Token(TipoToken.ASPAS, String.valueOf(currentChar), linhaAtual));
                pos++;
                continue;
            }

            throw new RuntimeException("Erro: Caractere inesperado: " + currentChar + " na linha: " + linhaAtual);
        }

        return tokens;
    }
}
