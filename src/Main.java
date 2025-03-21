import AnalisadorSintatico.*;
import AnalisadorLexico.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String caminhoArquivo = "ExemploCodigo/calcular.txt";

        // lê o conteúdo do arquivo
        StringBuilder codigoFonte = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(caminhoArquivo))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                codigoFonte.append(linha).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        System.out.println(codigoFonte);

        // Inicializa o Lexer e realiza a análise léxica
        Lexer analisadorLexico = new Lexer(codigoFonte.toString());
        List<Token> tokens = analisadorLexico.analisar();

        // Exibe os tokens gerados
//        System.out.println("\nTokens gerados:");
//        tokens.forEach(System.out::println);

       // System.out.println("\nLista de tokens antes do parse:"); 
       // tokens.forEach(token -> System.out.println(token.getTipo() + " " + token.getValor()));

        //Inicializa o Parser e realiza a análise sintática
        TabelaSimbolos tabelaSimbolos = new TabelaSimbolos();
        Parser analisadorSintatico = new Parser(tokens, tabelaSimbolos);
        analisadorSintatico.parse();

        System.out.println("\nTabela de Símbolos:");
        System.out.println(tabelaSimbolos);

    }
}
