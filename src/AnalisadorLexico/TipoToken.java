package AnalisadorLexico;

public enum TipoToken {
    MAIN,
    END,
    INTEIRO,
    BOOLEANO,
    PROCEDIMENTO,
    FUNCAO,
    RETORNO,
    WHILE,
    SCANF,
    PRINTF,
    BREAK,
    CONTINUE,
    VERDADEIRO,
    FALSO,
    PALAVRA_CHAVE,   // Ex: while, return
    ABRE_PAREN,      // Ex: (
    FECHA_PAREN,     // Ex: )
    ABRE_CHAVES,     // Ex: {
    FECHA_CHAVES,    // Ex: }
    ASPAS,           // Ex: "
    LOGICO,          // Ex: and, or
    IF,              // Ex: if
    ELSE,            // Ex: else
    IDENTIFICADOR,   // Ex: variáveis e nomes de funções
    NUMERO,          // Ex: 123, 456
    OPERADOR,        // Ex: +, -, *, /
    DELIMITADOR,     // Ex: , ;
    DESCONHECIDO     // Qualquer token que ão é reconhecido
}
