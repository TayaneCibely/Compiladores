package GeradorCodigo;

import AnalisadorLexico.Token;
import AnalisadorLexico.TipoToken;
import AnalisadorSintatico.Simbolo;
import AnalisadorSintatico.TabelaSimbolos;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class GeradorCodigoTresEnderecos {
    private TabelaSimbolos tabelaSimbolos;
    private List<Instrucao> codigo;
    private int tempCounter;
    private int labelCounter;
    private Map<String, String> variaveisTemporarias;
    private GeradorCodigo geradorCodigoDragon;

    public GeradorCodigoTresEnderecos(TabelaSimbolos tabelaSimbolos) {
        this.tabelaSimbolos = tabelaSimbolos;
        this.codigo = new ArrayList<>();
        this.tempCounter = 0;
        this.labelCounter = 0;
        this.variaveisTemporarias = new HashMap<>();
        this.geradorCodigoDragon = new GeradorCodigo();
    }

    public void gerarCodigo(List<Token> tokens) {
        try {
            System.out.println("Iniciando geração de código de três endereços...");
            codigo.add(new Instrucao(null, "inicio", null, null, null));
            processarTokens(tokens);
            codigo.add(new Instrucao(null, "fim", null, null, null));
            System.out.println("Geração de código concluída. Tamanho: " + codigo.size());

        } catch (Exception e) {
            System.err.println("Erro durante a geração de código: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void processarTokens(List<Token> tokens) {
        int pos = 0;
        while (pos < tokens.size()) {
            if (tokens.get(pos).getTipo() == TipoToken.INTEIRO ||
                    tokens.get(pos).getTipo() == TipoToken.BOOLEANO) {
                pos = processarDeclaracaoVariavel(tokens, pos);
            } else if (tokens.get(pos).getTipo() == TipoToken.PROCEDIMENTO) {
                pos = processarProcedimento(tokens, pos);
            } else if (tokens.get(pos).getTipo() == TipoToken.FUNCAO) {
                pos = processarFuncao(tokens, pos);
            } else if (tokens.get(pos).getTipo() == TipoToken.MAIN) {
                codigo.add(new Instrucao(null, "label", "main", null, null));
                pos += 3;
                pos = processarBloco(tokens, pos);
            } else {
                pos++;
            }
        }
    }

    private int processarDeclaracaoVariavel(List<Token> tokens, int pos) {
        String tipo = tokens.get(pos).getValor();
        pos++;
        while (pos < tokens.size() && tokens.get(pos).getTipo() != TipoToken.PON_VIR) {
            if (tokens.get(pos).getTipo() == TipoToken.IDENTIFICADOR) {
                String id = tokens.get(pos).getValor();
                pos++;
                if (pos < tokens.size() && tokens.get(pos).getTipo() == TipoToken.OPE_ATRI) {
                    pos++;
                    String resultado = processarExpressao(tokens, pos);
                    codigo.add(new Instrucao(null, "atrib", id, resultado, null));
                    while (pos < tokens.size() &&
                            tokens.get(pos).getTipo() != TipoToken.VIRGULA &&
                            tokens.get(pos).getTipo() != TipoToken.PON_VIR) {
                        pos++;
                    }
                }
            }
            if (pos < tokens.size() && tokens.get(pos).getTipo() == TipoToken.VIRGULA) {
                pos++;
            }
        }
        if (pos < tokens.size() && tokens.get(pos).getTipo() == TipoToken.PON_VIR) {
            pos++;
        }
        return pos;
    }

    private int processarProcedimento(List<Token> tokens, int pos) {
        pos++;
        if (pos < tokens.size() && tokens.get(pos).getTipo() == TipoToken.IDENTIFICADOR) {
            String nomeProcedimento = tokens.get(pos).getValor();
            codigo.add(new Instrucao(null, "label", nomeProcedimento, null, null));
            pos++;
            if (pos < tokens.size() && tokens.get(pos).getTipo() == TipoToken.ABRE_PAREN) {
                pos++;
                while (pos < tokens.size() && tokens.get(pos).getTipo() != TipoToken.FECHA_PAREN) {
                    pos++;
                }
                pos++;
            }
            if (pos < tokens.size() && tokens.get(pos).getTipo() == TipoToken.ABRE_CHAVES) {
                pos++;
                pos = processarBloco(tokens, pos);
                if (pos < tokens.size() && tokens.get(pos).getTipo() == TipoToken.FECHA_CHAVES) {
                    pos++;
                }
            }
            codigo.add(new Instrucao(null, "retorno", null, null, null));
        }
        return pos;
    }

    private int processarFuncao(List<Token> tokens, int pos) {
        pos++;
        String tipoRetorno = "";
        if (pos < tokens.size() &&
                (tokens.get(pos).getTipo() == TipoToken.INTEIRO ||
                        tokens.get(pos).getTipo() == TipoToken.BOOLEANO)) {
            tipoRetorno = tokens.get(pos).getValor();
            pos++;
        }
        if (pos < tokens.size() && tokens.get(pos).getTipo() == TipoToken.IDENTIFICADOR) {
            String nomeFuncao = tokens.get(pos).getValor();
            codigo.add(new Instrucao(null, "label", nomeFuncao, null, null));
            pos++;
            if (pos < tokens.size() && tokens.get(pos).getTipo() == TipoToken.ABRE_PAREN) {
                pos++;
                while (pos < tokens.size() && tokens.get(pos).getTipo() != TipoToken.FECHA_PAREN) {
                    pos++;
                }
                pos++;
            }
            if (pos < tokens.size() && tokens.get(pos).getTipo() == TipoToken.ABRE_CHAVES) {
                pos++;
                pos = processarBloco(tokens, pos);
                if (pos < tokens.size() && tokens.get(pos).getTipo() == TipoToken.FECHA_CHAVES) {
                    pos++;
                }
            }
            codigo.add(new Instrucao(null, "retorno", null, null, null)); 
                                                                    
        }
        return pos;
    }

    private int processarBloco(List<Token> tokens, int pos) {
        while (pos < tokens.size() && tokens.get(pos).getTipo() != TipoToken.FECHA_CHAVES) {
            if (tokens.get(pos).getTipo() == TipoToken.INTEIRO ||
                    tokens.get(pos).getTipo() == TipoToken.BOOLEANO) {
                pos = processarDeclaracaoVariavel(tokens, pos);
            } else if (tokens.get(pos).getTipo() == TipoToken.IF) {
                pos = processarIf(tokens, pos);
            } else if (tokens.get(pos).getTipo() == TipoToken.WHILE) {
                pos = processarWhile(tokens, pos);
            } else if (tokens.get(pos).getTipo() == TipoToken.PRINT) {
                pos = processarPrint(tokens, pos);
            } else if (tokens.get(pos).getTipo() == TipoToken.IDENTIFICADOR) {
                if (pos + 1 < tokens.size() && tokens.get(pos + 1).getTipo() == TipoToken.OPE_ATRI) {
                    pos = processarAtribuicao(tokens, pos);
                } else if (pos + 1 < tokens.size() && tokens.get(pos + 1).getTipo() == TipoToken.ABRE_PAREN) {
                    pos = processarChamadaFuncao(tokens, pos);
                } else {
                    pos++;
                }
            } else if (tokens.get(pos).getTipo() == TipoToken.RETORNO) {
                pos = processarReturn(tokens, pos);
            } else if (tokens.get(pos).getTipo() == TipoToken.BREAK) {
                codigo.add(new Instrucao(null, "goto", "fim_loop", null, null));
                pos += 2;
            } else if (tokens.get(pos).getTipo() == TipoToken.CONTINUE) {
                codigo.add(new Instrucao(null, "goto", "cond_loop", null, null));
                pos += 2;
            } else {
                pos++;
            }
        }
        return pos;
    }

    private int processarIf(List<Token> tokens, int pos) {
        pos++;
        if (pos < tokens.size() && tokens.get(pos).getTipo() == TipoToken.ABRE_PAREN) {
            pos++;
        }
        String condicao = null;
        int condStartPos = pos;
        while (pos < tokens.size() && tokens.get(pos).getTipo() != TipoToken.FECHA_PAREN) {
            pos++;
        }
        condicao = gerarCodigoCondicao(tokens, condStartPos, pos);
        String labelElse = novoLabel("else");
        String labelFimIf = novoLabel("fim_if");
        codigo.add(new Instrucao(null, "if_false", condicao, labelElse, null));
        if (pos < tokens.size() && tokens.get(pos).getTipo() == TipoToken.FECHA_PAREN) {
            pos++;
        }
        if (pos < tokens.size() && tokens.get(pos).getTipo() == TipoToken.ABRE_CHAVES) {
            pos++;
        }
        pos = processarBloco(tokens, pos);
        if (pos < tokens.size() && tokens.get(pos).getTipo() == TipoToken.FECHA_CHAVES) {
            pos++;
        }
        if (pos < tokens.size() && tokens.get(pos).getTipo() == TipoToken.ELSE) {
            pos++;
            codigo.add(new Instrucao(null, "goto", labelFimIf, null, null));
            codigo.add(new Instrucao(null, "label", labelElse, null, null));
            if (pos < tokens.size() && tokens.get(pos).getTipo() == TipoToken.ABRE_CHAVES) {
                pos++;
            }
            pos = processarBloco(tokens, pos);
            if (pos < tokens.size() && tokens.get(pos).getTipo() == TipoToken.FECHA_CHAVES) {
                pos++;
            }
            codigo.add(new Instrucao(null, "label", labelFimIf, null, null));
        } else {
            codigo.add(new Instrucao(null, "label", labelElse, null, null));
        }
        return pos;
    }

    private int processarWhile(List<Token> tokens, int pos) {
        pos++;
        String labelInicio = novoLabel("inicio_while");
        String labelCondicao = novoLabel("cond_while");
        String labelFim = novoLabel("fim_while");
        codigo.add(new Instrucao(null, "label", labelInicio, null, null));
        if (pos < tokens.size() && tokens.get(pos).getTipo() == TipoToken.ABRE_PAREN) {
            pos++;
        }
        codigo.add(new Instrucao(null, "label", labelCondicao, null, null));
        String condicao = null;
        int condStartPos = pos;
        while (pos < tokens.size() && tokens.get(pos).getTipo() != TipoToken.FECHA_PAREN) {
            pos++;
        }
        condicao = gerarCodigoCondicao(tokens, condStartPos, pos);
        codigo.add(new Instrucao(null, "if_false", condicao, labelFim, null));
        if (pos < tokens.size() && tokens.get(pos).getTipo() == TipoToken.FECHA_PAREN) {
            pos++;
        }
        if (pos < tokens.size() && tokens.get(pos).getTipo() == TipoToken.ABRE_CHAVES) {
            pos++;
        }
        pos = processarBloco(tokens, pos);
        if (pos < tokens.size() && tokens.get(pos).getTipo() == TipoToken.FECHA_CHAVES) {
            pos++;
        }
        codigo.add(new Instrucao(null, "goto", labelInicio, null, null));
        codigo.add(new Instrucao(null, "label", labelFim, null, null));
        return pos;
    }

    private int processarPrint(List<Token> tokens, int pos) {
        pos++;
        if (pos < tokens.size() && tokens.get(pos).getTipo() == TipoToken.ABRE_PAREN) {
            pos++;
        }
        String arg = null;
        if (pos < tokens.size()) {
            if (tokens.get(pos).getTipo() == TipoToken.STRING) {
                arg = tokens.get(pos).getValor();
                pos++;
            } else if (tokens.get(pos).getTipo() == TipoToken.IDENTIFICADOR) {
                arg = tokens.get(pos).getValor();
                pos++;
            }
        }
        codigo.add(new Instrucao(null, "print", arg, null, null));
        if (pos < tokens.size() && tokens.get(pos).getTipo() == TipoToken.FECHA_PAREN) {
            pos++;
        }
        if (pos < tokens.size() && tokens.get(pos).getTipo() == TipoToken.PON_VIR) {
            pos++;
        }
        return pos;
    }

    private int processarAtribuicao(List<Token> tokens, int pos) {
        String id = tokens.get(pos).getValor();
        pos++;
        pos++;
        int exprStartPos = pos;
        while (pos < tokens.size() && tokens.get(pos).getTipo() != TipoToken.PON_VIR) {
            pos++;
        }
        String resultado = processarExpressao(tokens, exprStartPos);
        codigo.add(new Instrucao(null, "atrib", id, resultado, null));
        if (pos < tokens.size() && tokens.get(pos).getTipo() == TipoToken.PON_VIR) {
            pos++;
        }
        return pos;
    }

    private int processarChamadaFuncao(List<Token> tokens, int pos) {
        String nomeFuncao = tokens.get(pos).getValor();
        pos++;
        if (pos < tokens.size() && tokens.get(pos).getTipo() == TipoToken.ABRE_PAREN) {
            pos++;
        }
        List<String> args = new ArrayList<>();
        while (pos < tokens.size() && tokens.get(pos).getTipo() != TipoToken.FECHA_PAREN) {
            int argStartPos = pos;
            while (pos < tokens.size() &&
                    tokens.get(pos).getTipo() != TipoToken.VIRGULA &&
                    tokens.get(pos).getTipo() != TipoToken.FECHA_PAREN) {
                pos++;
            }
            String arg = processarExpressao(tokens, argStartPos);
            args.add(arg);
            if (pos < tokens.size() && tokens.get(pos).getTipo() == TipoToken.VIRGULA) {
                pos++;
            }
        }
        for (int i = 0; i < args.size(); i++) {
            codigo.add(new Instrucao(null, "param", args.get(i), null, null));
        }
        String temp = novaTemporaria();
        codigo.add(new Instrucao(temp, "call", nomeFuncao, String.valueOf(args.size()), null));
        codigo.add(new Instrucao(null, "atrib", "resultado", temp, null));
        if (pos < tokens.size() && tokens.get(pos).getTipo() == TipoToken.FECHA_PAREN) {
            pos++;
        }
        if (pos < tokens.size() && tokens.get(pos).getTipo() == TipoToken.PON_VIR) {
            pos++;
        }
        return pos;
    }

    private int processarReturn(List<Token> tokens, int pos) {
        pos++;
        String resultado = null;
        if (pos < tokens.size() && tokens.get(pos).getTipo() != TipoToken.PON_VIR) {
            if (tokens.get(pos).getTipo() == TipoToken.VERDADEIRO ||
                    tokens.get(pos).getTipo() == TipoToken.FALSO ||
                    tokens.get(pos).getTipo() == TipoToken.NUMERO ||
                    tokens.get(pos).getTipo() == TipoToken.STRING) {
                resultado = tokens.get(pos).getValor();
                pos++;
            } else {
                int exprStartPos = pos;
                while (pos < tokens.size() && tokens.get(pos).getTipo() != TipoToken.PON_VIR) {
                    pos++;
                }
                resultado = processarExpressao(tokens, exprStartPos);
            }
        }
        if (resultado != null) {
            codigo.add(new Instrucao(null, "retorno", resultado, null, null));
        } else {
            codigo.add(new Instrucao(null, "retorno", null, null, null));
        }
        if (pos < tokens.size() && tokens.get(pos).getTipo() == TipoToken.PON_VIR) {
            pos++;
        }
        return pos;
    }

    private String processarExpressao(List<Token> tokens, int startPos) {
        Stack<String> operandos = new Stack<>();
        Stack<Token> operadores = new Stack<>();

        int pos = startPos;
        while (pos < tokens.size()) {
            Token token = tokens.get(pos);

            if (token.getTipo() == TipoToken.PON_VIR ||
                    token.getTipo() == TipoToken.VIRGULA ||
                    token.getTipo() == TipoToken.FECHA_PAREN) {
                break;
            }

            if (token.getTipo() == TipoToken.IDENTIFICADOR ||
                    token.getTipo() == TipoToken.NUMERO ||
                    token.getTipo() == TipoToken.STRING ||
                    token.getTipo() == TipoToken.VERDADEIRO ||
                    token.getTipo() == TipoToken.FALSO) {
                operandos.push(token.getValor());
            } else if (token.getTipo() == TipoToken.OPE_ARIT ||
                    token.getTipo() == TipoToken.OPE_REL) {

                while (!operadores.isEmpty() &&
                        precedencia(operadores.peek()) >= precedencia(token)) {
                    gerarCodigoOperador(operandos, operadores);
                }
                operadores.push(token);
            } else if (token.getTipo() == TipoToken.ABRE_PAREN) {
                operadores.push(token);
            } else if (token.getTipo() == TipoToken.FECHA_PAREN) {
                while (!operadores.isEmpty() &&
                        operadores.peek().getTipo() != TipoToken.ABRE_PAREN) {
                    gerarCodigoOperador(operandos, operadores);
                }

                if (!operadores.isEmpty()) {
                    operadores.pop();
                }
            }

            pos++;
        }

        while (!operadores.isEmpty()) {
            gerarCodigoOperador(operandos, operadores);
        }
        return operandos.isEmpty() ? null : operandos.pop();
    }

    private void gerarCodigoOperador(Stack<String> operandos, Stack<Token> operadores) {
        Token operador = operadores.pop();
        String op = operador.getValor();

        if (operador.getTipo() == TipoToken.ABRE_PAREN ||
                operador.getTipo() == TipoToken.FECHA_PAREN) {
            return;
        }

        String op2 = operandos.pop();
        String op1 = operandos.pop();
        String temp = novaTemporaria();

        codigo.add(new Instrucao(temp, op, op1, op2, null));
        operandos.push(temp);
    }

    private int precedencia(Token token) {
        if (token.getTipo() == TipoToken.ABRE_PAREN ||
                token.getTipo() == TipoToken.FECHA_PAREN) {
            return 0;
        }

        String op = token.getValor();
        if (op.equals("*") || op.equals("/") || op.equals("%")) {
            return 3;
        } else if (op.equals("+") || op.equals("-")) {
            return 2;
        } else if (op.equals("==") || op.equals("!=") ||
                op.equals("<") || op.equals("<=") ||
                op.equals(">") || op.equals(">=")) {
            return 1;
        }
        return 0;
    }

    private String gerarCodigoCondicao(List<Token> tokens, int startPos, int endPos) {
        String resultado = null;
        Stack<String> operandos = new Stack<>();
        Stack<Token> operadores = new Stack<>();

        for (int i = startPos; i < endPos; i++) {
            Token token = tokens.get(i);

            if (token.getTipo() == TipoToken.IDENTIFICADOR ||
                    token.getTipo() == TipoToken.NUMERO ||
                    token.getTipo() == TipoToken.VERDADEIRO ||
                    token.getTipo() == TipoToken.FALSO) {
                operandos.push(token.getValor());
            } else if (token.getTipo() == TipoToken.OPE_ARIT ||
                    token.getTipo() == TipoToken.OPE_REL) {

                while (!operadores.isEmpty() &&
                        precedencia(operadores.peek()) >= precedencia(token)) {
                    gerarCodigoOperador(operandos, operadores);
                }
                operadores.push(token);
            } else if (token.getTipo() == TipoToken.ABRE_PAREN) {
                operadores.push(token);
            } else if (token.getTipo() == TipoToken.FECHA_PAREN) {
                while (!operadores.isEmpty() &&
                        operadores.peek().getTipo() != TipoToken.ABRE_PAREN) {
                    gerarCodigoOperador(operandos, operadores);
                }

                if (!operadores.isEmpty()) {
                    operadores.pop();
                }
            }
        }

        while (!operadores.isEmpty()) {
            gerarCodigoOperador(operandos, operadores);
        }

        return operandos.isEmpty() ? null : operandos.pop();
    }

    private String novaTemporaria() {
        return "t" + (tempCounter++);
    }

    private String novoLabel(String prefixo) {
        return prefixo + "_" + (labelCounter++);
    }


    public static class Instrucao {
        private String resultado;
        private String operacao;
        private String op1;
        private String op2;
        private String comentario;

        public Instrucao(String resultado, String operacao, String op1, String op2, String comentario) {
            this.resultado = resultado;
            this.operacao = operacao;
            this.op1 = op1;
            this.op2 = op2;
            this.comentario = comentario;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            switch (operacao) {
                case "inicio":
                    sb.append("# Início do programa");
                    break;
                case "fim":
                    sb.append("# Fim do programa");
                    break;
                case "label":
                    sb.append(op1).append(":");
                    break;
                case "goto":
                    sb.append("goto ").append(op1);
                    break;
                case "if_false":
                    sb.append("if NOT(").append(op1).append(") goto ").append(op2);
                    break;
                case "print":
                    sb.append("print ").append(op1);
                    break;
                case "atrib":
                    sb.append(op1).append(" = ").append(op2);
                    break;
                case "call":
                    if (resultado != null) {
                        sb.append(resultado).append(" = call ").append(op1).append(", ").append(op2);
                    } else {
                        sb.append("call ").append(op1).append(", ").append(op2);
                    }
                    break;
                case "param":
                    sb.append("param ").append(op1);
                    break;
                case "retorno":
                    if (op1 != null) {
                        sb.append("return ").append(op1);
                    } else {
                        sb.append("return");
                    }
                    break;
                default:
                    sb.append(resultado).append(" = ").append(op1).append(" ").append(operacao).append(" ").append(op2);
                    break;
            }
            if (comentario != null && !comentario.isEmpty()) {
                sb.append(" # ").append(comentario);
            }
            return sb.toString();
        }
    }

    public void imprimirCodigo() {
        System.out.println("\n=== Código de Três Endereços ===");
        List<String> codigoString = new ArrayList<>();
        for (Instrucao instr : codigo) {
            System.out.println(instr);
        }
        List<String> codigoDragon = geradorCodigoDragon.converterCodigoExistente(codigoString);
        System.out.println("\n===== Código de Três Endereços (Formato Dragon Book) =====");
        for (String linha : codigoDragon) {
            System.out.println(linha);
        }
    }
}