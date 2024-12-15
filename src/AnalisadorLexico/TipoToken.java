package AnalisadorLexico;

/**
 * Enumeração para os diferentes tipos de tokens reconhecidos pelo Lexer.
 */
public enum TipoToken {
    PALAVRA_CHAVE,   // Ex: while, return
    LOGICO,          // Ex: and, or
    CONDICIONAIS,    // Ex: if, else
    IDENTIFICADOR,   // Ex: variáveis e nomes de funções
    NUMERO,          // Ex: 123, 456
    OPERADOR,        // Ex: +, -, *, /
    DELIMITADOR,     // Ex: , ;
    DESCONHECIDO     // Qnualquer token que ão é reconhecido

}
