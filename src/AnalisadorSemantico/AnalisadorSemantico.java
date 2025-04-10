package AnalisadorSemantico;

import AnalisadorSintatico.*;
import AnalisadorSintatico.Simbolo;

import java.util.*;

public class AnalisadorSemantico {
    private TabelaSimbolos tabelaSimbolos;
    private List<String> errosSemanticos;

    public AnalisadorSemantico(TabelaSimbolos tabelaSimbolos) {
        this.tabelaSimbolos = tabelaSimbolos;
        this.errosSemanticos = new ArrayList<>();
    }

    public boolean analisar() {
        verificarDeclaracoesDuplicadas();
        verificarVariaveisUtilizadas();
        verificarTiposCompativeis();
        verificarRetornoFuncoes();

        if (!errosSemanticos.isEmpty()) {
            for (String erro : errosSemanticos) {
                System.err.println(erro);
            }
            return false;
        }

        return true;
    }

    //  verificações semânticas
    //  verificar se existe mais de uma variável com o mesmo nome no mesmo escopo
    public void verificarDeclaracoesDuplicadas() {
        Map<Integer, List<String>> duplicacoes = tabelaSimbolos.getDuplicacoesDetectadas();

        for (Map.Entry<Integer, List<String>> entry : duplicacoes.entrySet()) {
            List<String> mensagensErro = entry.getValue();

            for (String mensagem : mensagensErro) {
                errosSemanticos.add("Erro: " + mensagem);
            }
        }
    }

    // verificar se a variável foi declarada no código
    public void verificarVariaveisUtilizadas() {
        Map<String, List<Integer>> variaveisUsadas = tabelaSimbolos.getVariaveisUtilizadas();

        for (Map.Entry<String, List<Integer>> entry : variaveisUsadas.entrySet()) {
            String identificador = entry.getKey();
            List<Integer> linhas = entry.getValue();

            // ignorar valores literais
            if (identificador.matches("\\d+") ||
                    identificador.equals("true") ||
                    identificador.equals("false") ||
                    identificador.equals("return")) {
                continue;
            }

            if (tabelaSimbolos.buscarSimbolo(identificador) == null) {
                for (int linha : linhas) {
                    errosSemanticos.add("Erro: Variável '" + identificador +
                            "' utilizada na linha " + linha +
                            " não foi declarada.");
                }
            }
        }
    }

    public void verificarTiposCompativeis() {
        List<Simbolo> simbolos = tabelaSimbolos.getSimbolos();

        for (Simbolo simbolo : simbolos) {
            // ignorar símbolos do tipo "return"
            if (simbolo.getTipo() != null && simbolo.getTipo().equals("return")) {
                continue;
            }

            if (simbolo.getValor() != null) {
                String tipoVariavel = simbolo.getTipo();
                String valor = simbolo.getValor();
                String tipoExpressao = simbolo.getTipoExpressao();

                if (tipoExpressao != null && !tipoExpressao.equals(tipoVariavel)) {
                    errosSemanticos.add("Erro: Incompatibilidade de tipos na linha " +
                            simbolo.getLinha() + ". Não é possível atribuir " +
                            "um valor do tipo '" + tipoExpressao + "' a uma variável de tipo '" +
                            tipoVariavel + "'.");
                }
                // verificações baseadas no valor
                else if (tipoExpressao == null) {
                    // numeros
                    if (valor.matches("\\d+") && !tipoVariavel.equals("int")) {
                        errosSemanticos.add("Erro: Incompatibilidade de tipos na linha " +
                                simbolo.getLinha() + ". Não é possível atribuir " +
                                "um valor inteiro a uma variável de tipo '" + tipoVariavel + "'.");
                    } else if ((valor.equals("true") || valor.equals("false")) && !tipoVariavel.equals("bool")) {
                        errosSemanticos.add("Erro: Incompatibilidade de tipos na linha " +
                                simbolo.getLinha() + ". Não é possível atribuir " +
                                "um valor booleano a uma variável de tipo '" + tipoVariavel + "'.");
                    }
                    // Verificação para strings
                    else if ((valor.startsWith("\"") && valor.endsWith("\"")) && !tipoVariavel.equals("string")) {
                        errosSemanticos.add("Erro: Incompatibilidade de tipos na linha " +
                                simbolo.getLinha() + ". Não é possível atribuir " +
                                "uma string a uma variável de tipo '" + tipoVariavel + "'.");
                    }
                }
            }
        }
    }

    private void verificarRetornoFuncoes() {
        List<Simbolo> simbolos = tabelaSimbolos.getSimbolos();

        // identificar funções
        Map<String, String> funcoes = new HashMap<>();
        for (Simbolo simbolo : simbolos) {
            if (simbolo.getTipo() != null &&
                    !simbolo.getTipo().equals("procedure") &&
                    !simbolo.getIdentificador().equals("main") &&
                    simbolo.getValor() == null &&
                    tabelaSimbolos.getEscopoDoSimbolo(simbolo.getIdentificador()) == 0) {
                funcoes.put(simbolo.getIdentificador(), simbolo.getTipo());
            }
        }

        // agrupar os returns por função
        Map<String, List<Simbolo>> returnsAgrupados = new HashMap<>();

        // inicializar a lista para cada função
        for (String nomeFuncao : funcoes.keySet()) {
            returnsAgrupados.put(nomeFuncao, new ArrayList<>());
        }

        // associar cada return à sua função pai
        for (Simbolo simbolo : simbolos) {
            if (simbolo.getIdentificador() != null &&
                    simbolo.getIdentificador().equals("return") &&
                    simbolo.getFuncaoPai() != null) {

                String funcaoPai = simbolo.getFuncaoPai();
                if (funcoes.containsKey(funcaoPai)) {
                    returnsAgrupados.get(funcaoPai).add(simbolo);
                }
            }
        }

        // verificar cada função e  returns
        for (Map.Entry<String, String> entry : funcoes.entrySet()) {
            String nomeFuncao = entry.getKey();
            String tipoFuncao = entry.getValue();
            List<Simbolo> returns = returnsAgrupados.get(nomeFuncao);

            if (returns.isEmpty()) {
                errosSemanticos.add("Erro: Função '" + nomeFuncao +
                        "' do tipo '" + tipoFuncao +
                        "' não possui comando de retorno.");
                continue;
            }

            for (Simbolo returnSimbolo : returns) {
                String valorRetorno = returnSimbolo.getValor();
                String tipoRetorno = determinarTipoValor(valorRetorno);

                if (tipoRetorno != null && !tipoRetorno.equals(tipoFuncao)) {
                    errosSemanticos.add("Erro: Incompatibilidade de tipos no retorno da função '" +
                            nomeFuncao + "'. Esperado '" + tipoFuncao +
                            "', encontrado '" + tipoRetorno + "' na linha " +
                            returnSimbolo.getLinha() + ".");
                }
            }
        }
    }

    private String determinarTipoValor(String valor) {
        if (valor.matches("\\d+")) {
            return "int";
        } else if (valor.equals("true") || valor.equals("false")) {
            return "bool";
        } else if (valor.startsWith("\"") && valor.endsWith("\"")) {
            return "string";
        } else {
            Simbolo varRetorno = tabelaSimbolos.buscarSimbolo(valor);
            if (varRetorno != null) {
                return varRetorno.getTipo();
            }
        }
        return null;
    }

    public List<String> getErrosSemanticos() {
        return errosSemanticos;
    }

    public boolean temErros() {
        return !errosSemanticos.isEmpty();
    }
}
