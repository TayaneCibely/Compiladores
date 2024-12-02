package lexer;

/**
 * Enumeração para os diferentes tipos de tokens reconhecidos pelo Lexer.
 */
public enum TipoToken {
    PALAVRA_CHAVE,   // Exemplo: if, while, return
    IDENTIFICADOR,   // Exemplo: variáveis e nomes de funções
    NUMERO,          // Exemplo: 123, 456
    OPERADOR,        // Exemplo: +, -, *, /
    SIMBOLO,         // Exemplo: (, ), {, }
    DESCONHECIDO     // Qualquer token que não é reconhecido
}
