import lexer.*;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Exemplo de código-fonte para teste
        String codigoFonte = """
            se (x == 10) {
                retorne x + 5;
            }
        """;

        // Inicializa o Lexer e realiza a análise léxica
        Lexer analisadorLexico = new Lexer(codigoFonte);
        List<Token> tokens = analisadorLexico.analisar();

        // Exibe os tokens gerados
        System.out.println("Tokens gerados:");
        tokens.forEach(System.out::println);
    }
}
