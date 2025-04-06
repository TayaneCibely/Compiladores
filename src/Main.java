import AnalisadorSemantico.AnalisadorSemantico;
import AnalisadorSintatico.*;
import GeradorCodigo.GeradorCodigo;
import GeradorCodigo.GeradorCodigoTresEnderecos;
import AnalisadorLexico.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String caminhoArquivo = "ExemploCodigo/teste.txt";

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

        System.out.println("\n==== Código Fonte ====\n");
        System.out.println(codigoFonte);
        System.out.println("=====================\n");

        try {
            // Análise Léxica
            System.out.println("Realizando análise léxica...");
            Lexer analisadorLexico = new Lexer(codigoFonte.toString());
            List<Token> tokens = analisadorLexico.analisar();


            // Análise Sintática
            System.out.println("Realizando análise sintática...");
            TabelaSimbolos tabelaSimbolos = new TabelaSimbolos();
            GeradorCodigo geradorCodigoBasico = new GeradorCodigo();
            Parser analisadorSintatico = new Parser(tokens, tabelaSimbolos, geradorCodigoBasico);
            analisadorSintatico.parse();

            // Análise Semântica
            System.out.println("Realizando análise semântica...");
            AnalisadorSemantico analisadorSemantico = new AnalisadorSemantico(tabelaSimbolos);
            boolean analiseSemanticaSucesso = analisadorSemantico.analisar();

            if (!analiseSemanticaSucesso) {
                System.out.println("\n==== Erros Semânticos ====");
                for (String erro : analisadorSemantico.getErrosSemanticos()) {
                    System.out.println(erro);
                }
                System.out.println("=======================");
            } else {
                System.out.println("\n✓ Nenhum erro semântico encontrado.");
            }

            System.out.println("\n==== Tabela de Símbolos ====");
            System.out.println(tabelaSimbolos);
            System.out.println("=========================");

            // Geração do Código de Três Endereços
            System.out.println("Gerando o código de três endereços...");
            GeradorCodigoTresEnderecos gerador = new GeradorCodigoTresEnderecos(tabelaSimbolos);
            gerador.gerarCodigo(tokens);
            gerador.imprimirCodigo();
            
            System.out.println("===============================");

        } catch (Exception e) {
            System.err.println("Erro durante o processo de compilação: " + e.getMessage());
            e.printStackTrace();
        }
    }
}