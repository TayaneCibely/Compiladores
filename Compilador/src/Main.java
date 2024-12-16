import lexer.*;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Exemplo de código-fonte para teste
        String codigoFonte = """
           main meuPrograma1(){
            int i = 1;
            while(i<=10){
                if(i % 2 == 0){
                    print(“i”);
                }
            }
           }end
        """;

        // Inicializa o Lexer e realiza a análise léxica
        Lexer analisadorLexico = new Lexer(codigoFonte);
        List<Token> tokens = analisadorLexico.analisar();

        // Exibe os tokens gerados
        System.out.println("Tokens gerados:");
        tokens.forEach(System.out::println);
    }
}
