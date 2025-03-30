import AnalisadorSemantico.AnalisadorSemantico;
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

        // inicializa o Lexer e realiza a análise léxica
        Lexer analisadorLexico = new Lexer(codigoFonte.toString());
        List<Token> tokens = analisadorLexico.analisar();

        // análise sintática
        TabelaSimbolos tabelaSimbolos = new TabelaSimbolos();
        Parser analisadorSintatico = new Parser(tokens, tabelaSimbolos);
        analisadorSintatico.parse();

        AnalisadorSemantico analisadorSemantico = new AnalisadorSemantico(tabelaSimbolos);
        analisadorSemantico.verificarDeclaracoesDuplicadas();
        analisadorSemantico.verificarVariaveisUtilizadas();

        if (analisadorSemantico.temErros()) {
            System.out.println("\nErros semânticos encontrados:");
            for (String erro : analisadorSemantico.getErrosSemanticos()) {
                System.out.println(erro);
            }
        } else {
            System.out.println("\nNenhum erro semântico encontrado.");
        }


        System.out.println("\nTabela de Símbolos:");
        System.out.println(tabelaSimbolos);

    }
}
