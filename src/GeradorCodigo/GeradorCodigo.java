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
        if (instrucaoStr.startsWith("# Início do programa")) {
            return "# início do programa";
        } else if (instrucaoStr.startsWith("# Fim do programa")) {
            return "# fim do programa";
        } else if (instrucaoStr.endsWith(":")) {
            String labelName = instrucaoStr.substring(0, instrucaoStr.length() - 1);
            if (labelName.startsWith("inicio_while_") || labelName.startsWith("cond_while_") || 
                labelName.startsWith("fim_while_") || labelName.startsWith("else_") || 
                labelName.startsWith("fim_if_")) {
                return "L" + mapearLabel(labelName) + ":";
            }
            return labelName + ":";
        } else if (instrucaoStr.startsWith("goto ")) {
            String labelName = instrucaoStr.substring(5);
            if (labelName.startsWith("inicio_while_") || labelName.startsWith("cond_while_") || 
                labelName.startsWith("fim_while_") || labelName.startsWith("else_") || 
                labelName.startsWith("fim_if_")) {
                return "goto L" + mapearLabel(labelName);
            }
            return "goto " + labelName;
        } else if (instrucaoStr.startsWith("if (")) {
            int posGoto = instrucaoStr.indexOf("goto ");
            String cond = instrucaoStr.substring(4, instrucaoStr.indexOf(")"));
            String label = instrucaoStr.substring(posGoto + 5);
            
            // Converter para o formato Dragon Book
            String varCond = cond.split(" == ")[0].trim();
            String valorCond = cond.contains("false") ? "0" : "1";
            
            if (label.startsWith("inicio_while_") || label.startsWith("cond_while_") || 
                label.startsWith("fim_while_") || label.startsWith("else_") || 
                label.startsWith("fim_if_")) {
                return "if " + mapearVariavel(varCond) + " = " + valorCond + " goto L" + mapearLabel(label);
            }
            return "if " + mapearVariavel(varCond) + " = " + valorCond + " goto " + label;
        } else if (instrucaoStr.startsWith("print ")) {
            String var = instrucaoStr.substring(6);
            if (var.startsWith("\"") && var.endsWith("\"")) {
                return "print " + var;
            } else {
                return "print " + mapearVariavel(var);
            }
        } else if (instrucaoStr.contains(" = ") && !instrucaoStr.contains(" == ")) {
            String[] partes = instrucaoStr.split(" = ", 2);
            String destino = partes[0];
            String valor = partes[1];
    
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
                    op = "%";
                    operandos = valor.split(" % ");
                } else if (valor.contains(" == ")) {
                    op = "=";  // Mudando para o formato Dragon Book
                    operandos = valor.split(" == ");
                } else if (valor.contains(" != ")) {
                    op = "!=";
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
                    return mapearVariavel(destino) + " := " + mapearVariavel(operandos[0]) + " " + op + " "
                            + mapearVariavel(operandos[1]);
                }
            }
    
            return mapearVariavel(destino) + " := " + mapearVariavel(valor);
        } else if (instrucaoStr.startsWith("call ")) {
            String resto = instrucaoStr.substring(5);
            String[] partes = resto.split(", ", 2);
            String funcao = partes[0];
            String numArgs = partes.length > 1 ? partes[1] : "0";
            return "call " + funcao + ", " + numArgs;
        } else if (instrucaoStr.contains(" = call ")) {
            String[] partes = instrucaoStr.split(" = call ", 2);
            String destino = partes[0];
            String[] funcaoArgs = partes[1].split(", ", 2);
            String funcao = funcaoArgs[0];
            String numArgs = funcaoArgs.length > 1 ? funcaoArgs[1] : "0";
            return mapearVariavel(destino) + " := call " + funcao + ", " + numArgs;
        } else if (instrucaoStr.startsWith("param ")) {
            String param = instrucaoStr.substring(6);
            return "param " + mapearVariavel(param);
        } else if (instrucaoStr.startsWith("return ")) {
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

        if (var.equals("true") || var.equals("false") ||
                var.matches("\\d+") || var.startsWith("\"")) {
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
        codigo.add("# Código de Três Endereços (Formato Dragon Book)");
        for (String instrucaoStr : instrucoesStrings) {
            adicionarInstrucaoFormatada(instrucaoStr);
        }
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