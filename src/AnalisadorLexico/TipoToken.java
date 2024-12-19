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
    PRINT,
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
    STRING,
    NUMERO,          // Ex: 123, 456
    OPE_ARIT,        // Ex: +, -, *, /
    OPE_ATRI,        // Ex: =
    OPE_REL,        // Ex: ==, <, >, <=, >=
    PON_VIR,         // Ex: ;
    VIRGULA,     // Ex: ,
    DESCONHECIDO     // Qualquer token que ão é reconhecido
}
