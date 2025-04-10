package GeradorCodigo;

import AnalisadorLexico.Token;
import AnalisadorSemantico.AnalisadorSemantico;
import AnalisadorSintatico.TabelaSimbolos;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeradorCodigo {
    private List<String> codigo;
    private TabelaSimbolos tabelaSimbolos;
    private AnalisadorSemantico analisadorSemantico;
    private int tempCounter;
    private int labelCounter;
    private Map<String, Integer> variaveisMapeadas;

    public GeradorCodigo() {
        this.codigo = new ArrayList<>();
        this.tempCounter = 1;
        this.labelCounter = 1;
        this.variaveisMapeadas = new HashMap<>();
    }

    public GeradorCodigo(TabelaSimbolos tabelaSimbolos, AnalisadorSemantico analisadorSemantico) {
        this.codigo = new ArrayList<>();
        this.tabelaSimbolos = tabelaSimbolos;
        this.analisadorSemantico = analisadorSemantico;
        this.tempCounter = 1;
        this.labelCounter = 1;
        this.variaveisMapeadas = new HashMap<>();
    }

    public void adicionarInstrucaoFormatada(String instrucaoStr) {
        String formatada = formatarInstrucao(instrucaoStr);
        codigo.add(formatada);
    }

    private String formatarInstrucao(String instrucaoStr) {
        // Comentários
        if (instrucaoStr.startsWith("# Início do programa")) {
            return "# início do programa";
        } else if (instrucaoStr.startsWith("# Fim do programa")) {
            return "# fim do programa";
        }
        // Labels
        else if (instrucaoStr.endsWith(":")) {
            String labelName = instrucaoStr.substring(0, instrucaoStr.length() - 1);
            if (labelName.startsWith("inicio_while_") || labelName.startsWith("cond_while_") ||
                    labelName.startsWith("fim_while_") || labelName.startsWith("else_") ||
                    labelName.startsWith("fim_if_")) {
                return "L" + mapearLabel(labelName) + ":";
            }
            return labelName + ":";
        }
        // Goto
        else if (instrucaoStr.startsWith("goto ")) {
            String labelName = instrucaoStr.substring(5);
            if (labelName.startsWith("inicio_while_") || labelName.startsWith("cond_while_") ||
                    labelName.startsWith("fim_while_") || labelName.startsWith("else_") ||
                    labelName.startsWith("fim_if_")) {
                return "goto L" + mapearLabel(labelName);
            }
            return "goto " + labelName;
        }
        // If
        else if (instrucaoStr.startsWith("if (")) {
            int posGoto = instrucaoStr.indexOf("goto ");
            String cond = instrucaoStr.substring(4, instrucaoStr.indexOf(")"));
            String label = instrucaoStr.substring(posGoto + 5);

            if (cond.contains(" == false")) {
                // Extrair a condição dentro do "NOT"
                String varName = cond.split(" ==")[0].trim();
                String targetLabel = label.startsWith("inicio_while_") || label.startsWith("cond_while_") ||
                        label.startsWith("fim_while_") || label.startsWith("else_") ||
                        label.startsWith("fim_if_") ? "L" + mapearLabel(label) : label;

                return "if NOT(" + mapearVariavel(varName) + ") goto " + targetLabel;
            } else {
                String[] partes;
                String op;
                String esquerda;
                String direita;

                // Determinar o operador e inverter se necessário
                if (cond.contains(" == ")) {
                    partes = cond.split(" == ");
                    op = "==";
                } else if (cond.contains(" != ")) {
                    partes = cond.split(" != ");
                    op = "!=";
                } else if (cond.contains(" <= ")) {
                    partes = cond.split(" <= ");
                    op = "<=";
                } else if (cond.contains(" >= ")) {
                    partes = cond.split(" >= ");
                    op = ">=";
                } else if (cond.contains(" < ")) {
                    partes = cond.split(" < ");
                    op = "<";
                } else if (cond.contains(" > ")) {
                    partes = cond.split(" > ");
                    op = ">";
                } else {
                    // Operador implícito (comparação com true/false)
                    partes = new String[] { cond, cond.contains("false") ? "false" : "true" };
                    op = "==";
                }

                esquerda = mapearVariavel(partes[0].trim());
                direita = mapearVariavel(partes[1].trim());

                String targetLabel = label.startsWith("inicio_while_") || label.startsWith("cond_while_") ||
                        label.startsWith("fim_while_") || label.startsWith("else_") ||
                        label.startsWith("fim_if_") ? "L" + mapearLabel(label) : label;

                String dragonOp;
                switch (op) {
                    case "==":
                        dragonOp = "=";
                        break;
                    case "!=":
                        dragonOp = "<>";
                        break;
                    default:
                        dragonOp = op;
                        break;
                }

                // Tratar caso especial de comparação com true/false
                if ((partes[1].trim().equals("true") || partes[1].trim().equals("false"))) {
                    if (partes[1].trim().equals("true")) {
                        direita = "1";
                    } else {
                        direita = "0";
                    }
                }

                return "if " + esquerda + " " + dragonOp + " " + direita + " goto " + targetLabel;
            }
        }
        // Print
        else if (instrucaoStr.startsWith("print ")) {
            String var = instrucaoStr.substring(6);
            if (var.startsWith("\"") && var.endsWith("\"")) {
                return "print " + var;
            } else {
                return "print " + mapearVariavel(var);
            }
        }
        // Atribuições
        else if (instrucaoStr.contains(" = ") && !instrucaoStr.contains(" == ")) {
            String[] partes = instrucaoStr.split(" = ", 2);
            String destino = partes[0].trim();
            String valor = partes[1].trim();

            // Operações binárias
            if (valor.contains(" + ") || valor.contains(" - ") ||
                    valor.contains(" * ") || valor.contains(" / ") ||
                    valor.contains(" % ") || valor.contains(" == ") ||
                    valor.contains(" != ") || valor.contains(" < ") ||
                    valor.contains(" > ") || valor.contains(" <= ") ||
                    valor.contains(" >= ")) {

                String op = "";
                String[] operandos = null;

                if (valor.contains(" + ")) {
                    op = "+";
                    operandos = valor.split(" \\+ ");
                } else if (valor.contains(" - ")) {
                    op = "-";
                    operandos = valor.split(" - ");
                } else if (valor.contains(" * ")) {
                    op = "*";
                    operandos = valor.split(" \\* ");
                } else if (valor.contains(" / ")) {
                    op = "/";
                    operandos = valor.split(" / ");
                } else if (valor.contains(" % ")) {
                    op = "mod";
                    operandos = valor.split(" % ");
                } else if (valor.contains(" == ")) {
                    op = "=";
                    operandos = valor.split(" == ");
                } else if (valor.contains(" != ")) {
                    op = "<>";
                    operandos = valor.split(" != ");
                } else if (valor.contains(" <= ")) {
                    op = "<=";
                    operandos = valor.split(" <= ");
                } else if (valor.contains(" >= ")) {
                    op = ">=";
                    operandos = valor.split(" >= ");
                } else if (valor.contains(" < ")) {
                    op = "<";
                    operandos = valor.split(" < ");
                } else if (valor.contains(" > ")) {
                    op = ">";
                    operandos = valor.split(" > ");
                }

                if (operandos != null && operandos.length == 2) {
                    String op1 = mapearVariavel(operandos[0].trim());
                    String op2 = mapearVariavel(operandos[1].trim());
                    return mapearVariavel(destino) + " := " + op1 + " " + op + " " + op2;
                }
            }

            return mapearVariavel(destino) + " := " + mapearVariavel(valor);
        }
        // Chamadas de função
        else if (instrucaoStr.startsWith("call ")) {
            String resto = instrucaoStr.substring(5);
            String[] partes = resto.split(", ", 2);
            String funcao = partes[0];
            String numArgs = partes.length > 1 ? partes[1] : "0";
            return "call " + funcao + ", " + numArgs;
        } else if (instrucaoStr.contains(" = call ")) {
            String[] partes = instrucaoStr.split(" = call ", 2);
            String destino = partes[0].trim();
            String resto = partes[1].trim();
            String[] funcaoArgs = resto.split(", ", 2);
            String funcao = funcaoArgs[0];
            String numArgs = funcaoArgs.length > 1 ? funcaoArgs[1] : "0";
            return mapearVariavel(destino) + " := call " + funcao + ", " + numArgs;
        }
        // Parâmetros
        else if (instrucaoStr.startsWith("param ")) {
            String param = instrucaoStr.substring(6);
            return "param " + mapearVariavel(param);
        }
        // Return
        else if (instrucaoStr.startsWith("return ")) {
            String valor = instrucaoStr.substring(7);
            return "return " + mapearVariavel(valor);
        } else if (instrucaoStr.equals("return")) {
            return "return";
        }

        return instrucaoStr;
    }

    private String mapearVariavel(String var) {
        if (var == null)
            return "_";
        var = var.trim();

        if (var.equals("true")) {
            return "1";
        } else if (var.equals("false")) {
            return "0";
        } else if (var.matches("\\d+") || var.startsWith("\"")) {
            return var;
        }

        if (var.startsWith("t")) {
            if (!variaveisMapeadas.containsKey(var)) {
                variaveisMapeadas.put(var, tempCounter++);
            }
            return "t" + variaveisMapeadas.get(var);
        }

        return var;
    }

    private String mapearLabel(String label) {
        if (!variaveisMapeadas.containsKey(label)) {
            variaveisMapeadas.put(label, labelCounter++);
        }
        return variaveisMapeadas.get(label).toString();
    }

    public void converterCodigo(List<String> instrucoesStrings) {
        codigo.clear();
        codigo.add("# Código de Três Endereços (Formato Dragon Book)");
        for (String instrucaoStr : instrucoesStrings) {
            adicionarInstrucaoFormatada(instrucaoStr);
        }
    }

    public List<String> converterCodigoExistente(List<String> codigoOriginal) {
        List<String> codigoConvertido = new ArrayList<>();

        for (String instrucao : codigoOriginal) {
            codigoConvertido.add(formatarInstrucao(instrucao));
        }

        return codigoConvertido;
    }

    public void imprimirCodigo() {
        System.out.println("\n===== Código de Três Endereços (Formato Dragon Book) =====");
        for (String linha : codigo) {
            System.out.println(linha);
        }
    }

    public List<String> getCodigo() {
        return codigo;
    }

    public void adicionarInstrucao(String instrucao) {
        codigo.add(instrucao);
    }
}