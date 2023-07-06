package com.syntatic;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Sintatico {
    
    private LexicoAlt lexico;
    private Token token;
    private String nomeArquivo;

    private TabelaSimbolos tabela;
    private String nomeArquivoSaida;
	private String caminhoArquivoSaida;
	private BufferedWriter bw;
	private FileWriter fw;
    private String instrucoes;
    private List<Registro> ultimasVariaveisDeclaradas = new ArrayList<>();
    private String varDoFor;

    public Sintatico(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public void analisar() {
        lexico = new LexicoAlt(nomeArquivo);
        token = lexico.getToken();
		nomeArquivoSaida = "../output/codigo.c";
		caminhoArquivoSaida = Paths.get(nomeArquivoSaida).toAbsolutePath().toString();
		bw = null;
		fw = null;
		try {
			fw = new FileWriter(caminhoArquivoSaida, Charset.forName("UTF-8"));
			bw = new BufferedWriter(fw);
			programa();
			bw.close();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("== TABELA DE SIMBOLOS ==");
		System.out.println(tabela);
    }    

    private void gerarCodigo(String instrucoes) {
		try {
			bw.write(instrucoes + "\n");
            this.instrucoes = "";
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    //<programa> ::= program <id> {A1} ; <corpo> • {A45}
    public void programa() {
            if (testarPalavraReservada("program")) {
            token = lexico.getToken();
            if (token.getClasse().equals(Classe.cIdent)) {
                A1();
                id();
                if (token.getClasse().equals(Classe.cPontoVirg)) {
                    token = lexico.getToken();
                    corpo();
                    if (token.getClasse() == Classe.cPonto) {
                        token = lexico.getToken();
                        //{A45}
                    } else {
                        mostrarMensagemErro("<programa> Faltou ponto final no 'program'");
                    }
                }
                else {
                    mostrarMensagemErro("<programa> Faltou ; depois do nome");
                }
            }
            else {
                mostrarMensagemErro("<programa> Faltou identificador depois de program");
            }
        } else {
            mostrarMensagemErro("<programa> Faltou começar o programa com 'program'");
        }
    }

    //<corpo> ::= <declara> {A44}->n precisa begin <sentencas> end {A46}
    public void corpo() {
        declara();
        if(testarPalavraReservada("begin")) {
            token = lexico.getToken();
            sentencas();
            if(testarPalavraReservada("end")) {
                token = lexico.getToken();
                A46();
            }
            else {
                mostrarMensagemErro("<corpo/begin> Faltou 'end'");
            }
        }
        else {
            mostrarMensagemErro("<corpo> Faltou 'begin'");
        }
    }

    //<declara> ::= var <dvar> <mais_dc> | vazio
    public void declara() {
        if(testarPalavraReservada("var")) {
            token = lexico.getToken();
            dvar();
            mais_dc();
        }
    }

    //<mais_dc> ::=  ; <cont_dc>
    public void mais_dc() {
        if(token.getClasse().equals(Classe.cPontoVirg)){
            token = lexico.getToken();
            cont_dc();
        }
        else{
            mostrarMensagemErro("<mais_dc> Faltou ';'");
        }
    }

    //<cont_dc> ::= <dvar> <mais_dc> | vazio
    public void cont_dc() {
        if(token.getClasse().equals(Classe.cIdent)){
            dvar();
            mais_dc();
        }
    }

    //<dvar> ::= <variaveis> : <tipo_var> {A2}
    public void dvar() {
        variaveis();
        if(token.getClasse().equals(Classe.cDoisPontos)) {
            token = lexico.getToken();
            if(testarPalavraReservada("integer")) {
                A2();
                tipo_var();
            }
            else {
                mostrarMensagemErro("<dvar> Tipo de variavel invalido");
            }
        }
        else {
            mostrarMensagemErro("<dvar> Faltou ':'");
        }
    }

    //<tipo_var> ::= integer
    public void tipo_var() {
        if(testarPalavraReservada("integer")) {
            token = lexico.getToken();
        }
        else {
            mostrarMensagemErro("<tipo_var> Tipo de variavel invalido");
        }
    }

    //<variaveis> ::= <id> {A3} <mais_var>
    public void variaveis() {
        if (token.getClasse().equals(Classe.cIdent)) {
            A3();
            id();
            mais_var();
        }
        else {
            mostrarMensagemErro("<variaveis> Faltou identificador");
        }
    }

    //<mais_var> ::=  ,  <variaveis> | vazio
    public void mais_var() {
        if(token.getClasse().equals(Classe.cVirgula)) {
            token = lexico.getToken();
            variaveis();
        }
    }

    //<sentencas> ::= <comando> <mais_sentencas>
    public void sentencas() {
        comando();
        mais_sentencas();
    }

    //<mais_sentencas> ::=  ; <cont_sentencas>
    public void mais_sentencas() {
        if(token.getClasse().equals(Classe.cPontoVirg)){
            token = lexico.getToken();
            cont_sentencas();
        }
        else {
            mostrarMensagemErro("<mais_sentencas> Faltou ';'");
        }
    }

    //<cont_sentencas> ::= <sentencas> | vazio
    public void cont_sentencas() {
        //read, write, for, repeat, while, if
        if(testarPalavraReservada("read") ||
           testarPalavraReservada("write") ||
           testarPalavraReservada("writeln") ||
           testarPalavraReservada("for") ||
           testarPalavraReservada("repeat") ||
           testarPalavraReservada("while") ||
           testarPalavraReservada("if") ||
           token.getClasse().equals(Classe.cIdent)) {
            sentencas();
        }
    }

    //<var_read> ::= <id> {A8} <mais_var_read>
    public void var_read() {
        if (token.getClasse().equals(Classe.cIdent)) {
            A8();
            id();
            mais_var_read();
        }
        else {
            mostrarMensagemErro("<var_read> Faltou identificador");
        }
    }

    //<mais_var_read> ::=  ,  <var_read> | vazio
    public void mais_var_read() {
        if(token.getClasse().equals(Classe.cVirgula)) {
            token = lexico.getToken();
            var_read();
        }
    }

    /*<exp_write> ::= <id> {A09} <mais_exp_write> |
                <string> {A59} <mais_exp_write> |
                <intnum> {A43} <mais_exp_write>*/
    public void exp_write() {
        if(token.getClasse().equals(Classe.cIdent)) {
            A9();            
            id();
            mais_exp_write();
        }
        else if(token.getClasse().equals(Classe.cString)) {
            A59();
            string();
            mais_exp_write();
        }
        else if(token.getClasse().equals(Classe.cInt)) {
            A43();
            intnum();
            mais_exp_write();
        }
    }

    //<mais_exp_write> ::=  ,  <exp_write> | vazio
    public void mais_exp_write() {
        if(token.getClasse().equals(Classe.cVirgula)) {
            token = lexico.getToken();
            exp_write();
        }
    }

    /*<comando> ::= 
            read ( <var_read> ) |
            write ( <exp_write> ) |
            writeln ( <exp_write ) {A61}|
            for <id> {A57} := <expressao> {A11} to <expressao> {A12} 
            do begin <sentencas> end {A13} |
            repeat {A14} <sentencas> until ( <expressao_logica> ) {A15} |
            while {A16} ( <expressao_logica> ) {A17} do begin <sentencas> end {A18} |
            if ( <expressao_logica> ) {A19} then begin <sentencas> end {A20} <pfalsa> {A21} |
            <id> {A49} := <expressao> {A22} | vazio*/
    public void comando() {
        if(testarPalavraReservada("read")) {
            token = lexico.getToken();
            var_read();
        }
        else if(testarPalavraReservada("write")) {
            token = lexico.getToken();
            if (token.getClasse().equals(Classe.cParEsq)) {
                token = lexico.getToken();
                exp_write();
                if (token.getClasse().equals(Classe.cParDir)) {
                    token = lexico.getToken();
                }
                else {
                    mostrarMensagemErro("<comando/write> faltou ')'");    
                }
            }            
            else {
                mostrarMensagemErro("<comando/write> faltou '('");
            }
        }
        else if(testarPalavraReservada("writeln")) {
            token = lexico.getToken();
            if (token.getClasse().equals(Classe.cParEsq)) {
                token = lexico.getToken();
                exp_write();
                if (token.getClasse().equals(Classe.cParDir)) {
                    token = lexico.getToken();
                    A61();
                }
                else {
                    mostrarMensagemErro("<comando/writeln> faltou ')'");    
                }
            }            
            else {
                mostrarMensagemErro("<comando/writeln> faltou '('");
            }
        }
        else if(testarPalavraReservada("for")) {
            token = lexico.getToken();
            A57();
            id();
            if(token.getClasse().equals(Classe.cAtrib)) {
                token = lexico.getToken();
                A11();
                expressao();  
                if(testarPalavraReservada("to")) {
                    token = lexico.getToken();
                    A12();
                    expressao();
                    if(testarPalavraReservada("do")) {
                        token = lexico.getToken();
                        if(testarPalavraReservada("begin")) {
                            token = lexico.getToken();
                            sentencas();
                            if(testarPalavraReservada("end")) {
                                token = lexico.getToken();
                                A13();
                            }
                        }
                    }
                }
                else {
                    mostrarMensagemErro("<comando/for> Faltou 'to'");
                }
            }
            else {
                mostrarMensagemErro("<comando/for> Faltou operador ':='");
            }
        }
        else if(testarPalavraReservada("repeat")) {
            token = lexico.getToken();
            A14();
            sentencas();
            if(testarPalavraReservada("until")) {
                token = lexico.getToken();
                if(token.getClasse().equals(Classe.cParEsq)) {
                    token = lexico.getToken();
                    expressao_logica();
                    if(token.getClasse().equals(Classe.cParDir)) {
                        token = lexico.getToken();
                        A15();
                    }
                    else {
                        mostrarMensagemErro("<comando/until> Faltou ')'");
                    }
                }
                else {
                    mostrarMensagemErro("<comando/until> Faltou '('");
                }
            }
            else {
                mostrarMensagemErro("<comando/repeat> Faltou 'until'");
            }
        }
        else if(testarPalavraReservada("while")) {
            token = lexico.getToken();
            A16();
            if(token.getClasse().equals(Classe.cParEsq)) {
                token = lexico.getToken();
                expressao_logica();
                if(token.getClasse().equals(Classe.cParDir)) {
                    token = lexico.getToken();
                    A17();
                    if(testarPalavraReservada("do")) {
                        token = lexico.getToken();
                        if(testarPalavraReservada("begin")) {
                            token = lexico.getToken();
                            sentencas();
                            if(testarPalavraReservada("end")) {
                                token = lexico.getToken();
                                A18();
                            }
                            else {
                                mostrarMensagemErro("<comando/while> Faltou 'end'");
                            }
                        }
                        else {
                            mostrarMensagemErro("<comando/while> Faltou 'begin'");
                        }
                    }
                    else {
                        mostrarMensagemErro("<comando/while> Faltou 'do'");
                    }

                }
                else {
                    mostrarMensagemErro("<comando/while> Faltou ')'");
                }
            }
            else {
                mostrarMensagemErro("<comando/while> Faltou '('");
            }
        }
        else if(testarPalavraReservada("if")) {
            token = lexico.getToken();
            if(token.getClasse().equals(Classe.cParEsq)) {
                token = lexico.getToken();
                A62(); // Gera o começo do if
                expressao_logica();
                if(token.getClasse().equals(Classe.cParDir)) {
                    token = lexico.getToken();
                    A19();
                    if(testarPalavraReservada("then")) {
                        token = lexico.getToken();
                        if(testarPalavraReservada("begin")) {
                            token = lexico.getToken();
                            sentencas();
                            if(testarPalavraReservada("end")) {
                                token = lexico.getToken();
                                A20();
                                pfalsa();
                                A21();
                            }
                            else {
                                mostrarMensagemErro("<comando/if> Faltou 'end'");
                            }
                        }
                        else {
                            mostrarMensagemErro("<comando/if> Faltou 'begin'");
                        }
                    }
                }
                else {
                    mostrarMensagemErro("<comando/if> Faltou '('");
                }
            }
            else {
                mostrarMensagemErro("<comando/if> Faltou ')'");
            }
        }
        else if(token.getClasse().equals(Classe.cIdent)){
            A49();
            id();
            if(token.getClasse().equals(Classe.cAtrib)) {
                token = lexico.getToken();
                expressao();
                A22();
            }
            else {
                mostrarMensagemErro("<comando> Faltou operador ':='");
            }
        }
    }

    //<pfalsa> ::= else {A25} begin <sentencas> end | vazio
    public void pfalsa () {
        if(testarPalavraReservada("else")) {
            token = lexico.getToken();
            A25();
            if(testarPalavraReservada("begin")) {
                token = lexico.getToken();
                sentencas();
                if(testarPalavraReservada("end")) {
                    token = lexico.getToken();
                }
                else {
                    mostrarMensagemErro("<pfalsa> Faltou 'end'");
                }
            }
            else {
                mostrarMensagemErro("<pfalsa> Faltou 'begin'");
            }
        }
    }

    //<expressao_logica> ::= <termo_logico> <mais_expr_logica>
    public void expressao_logica() {
        termo_logico();
        mais_expr_logica();
    }

    //<mais_expr_logica> ::= or <termo_logico> <mais_expr_logica> {A26} | vazio
    public void mais_expr_logica() {
        if(testarPalavraReservada("or")) {
            token = lexico.getToken();
            A26();
            termo_logico();
            mais_expr_logica();
        }
    }

    //<termo_logico> ::= <fator_logico> <mais_termo_logico>
    public void termo_logico() {
        fator_logico();
        mais_termo_logico();
    }

    //<mais_termo_logico> ::= and <fator_logico> <mais_termo_logico> {A27} | vazio
    public void mais_termo_logico() {
        if(testarPalavraReservada("and")) {
            A27();
            fator_logico();
            mais_termo_logico();
        }
    }

    /*<fator_logico> ::= <relacional> |
                   ( <expressao_logica> ) |
                   not <fator_logico> {A28} |
                   true {A29} |
                   false {A30}*/
    public void fator_logico() {
        if(token.getClasse().equals(Classe.cParEsq)) {
            token = lexico.getToken();
            expressao_logica();
            if(token.getClasse().equals(Classe.cParDir)) {
                token = lexico.getToken();
            }
            else {
                mostrarMensagemErro("<fator_logico> Faltou ')'");
            }
        }
        else if(testarPalavraReservada("not")) {
            token = lexico.getToken();
            A28();
            fator_logico();
        }
        else if(testarPalavraReservada("true")) {
            A29();
        }
        else if(testarPalavraReservada("false")) {
            A30();
        }
        else {
            relacional();
        }
    }

    /*<relacional> ::= <expressao> =  <expressao> {A31} |
                 <expressao> >  <expressao> {A32} |
                 <expressao> >= <expressao> {A33} |
                 <expressao> <  <expressao> {A34} |
                 <expressao> <= <expressao> {A35} |
                 <expressao> <> <expressao> {A36}*/
    public void relacional() {
        expressao();
        if(token.getClasse().equals(Classe.cIgual)) {
            token = lexico.getToken();
            A31();
            expressao();
        }
        else if(token.getClasse().equals(Classe.cMaior)) {
            token = lexico.getToken();
            A32();
            expressao();
        }
        else if(token.getClasse().equals(Classe.cMaiorIgual)) {
            token = lexico.getToken();
            A33();
            expressao();
        }
        else if(token.getClasse().equals(Classe.cMenor)) {
            token = lexico.getToken();
            A34();
            expressao();
        }
        else if(token.getClasse().equals(Classe.cMenorIgual)) {
            token = lexico.getToken();
            A35();
            expressao();
        }
        else if(token.getClasse().equals(Classe.cDiferente)) {
            token = lexico.getToken();
            A36();
            expressao();
        }
    }

    //<expressao> ::= <termo> <outros_termos>
    public void expressao() {
        termo();
        mais_expressao();
    }

    /*<mais_expressao> ::= + <termo> <mais_expressao> {A37} |
                     - <termo> <mais_expressao> {A38} | vazio*/
    public void mais_expressao() {
        if(token.getClasse().equals(Classe.cSoma)) {
            token = lexico.getToken();
            A37();
            termo();
            mais_expressao();
        }
        else if(token.getClasse().equals(Classe.cSub)) {
            token = lexico.getToken();
            A38();
            termo();
            mais_expressao();
        }
    }

    //<termo> ::= <fator> <mais_termo>
    public void termo() {
        fator();
        mais_termo();
    }

    /*<mais_termo> ::= * <fator> <mais_termo> {A39} |
                 / <fator> <mais_termo> {A40} | vazio */
    public void mais_termo() {
        if(token.getClasse().equals(Classe.cMult)) {
            token = lexico.getToken();
            A39();
            fator();
            mais_termo();
        }
        else if(token.getClasse().equals(Classe.cDiv)) {
            token = lexico.getToken();
            A40();
            fator();
            mais_termo();
        }
    }

    //<fator> ::= <id> {A55} | <intnum> {A41} | ( <expressao> )
    public void fator() {
        if(token.getClasse().equals(Classe.cIdent)) {
            A55();
            id();
        }
        else if(token.getClasse().equals(Classe.cInt)) {
            A41();
            intnum();
        }
        else if(token.getClasse().equals(Classe.cParEsq)) {
            token = lexico.getToken();
            expressao();
            if(token.getClasse().equals(Classe.cParDir)) {
                token = lexico.getToken();
            }
            else {
                mostrarMensagemErro("<fator> Faltou ')'");
            }
        }
        else {
            mostrarMensagemErro("<fator> Faltou '('");
        }
    }

    //<id> ::= letra (letra | digito)*
    public void id() {
        if(token.getClasse().equals(Classe.cIdent)) {
            token = lexico.getToken();
        }
        else {
            mostrarMensagemErro("<id> Faltou identificador");
        }
    }

    //<intnum> ::= digito+
    public void intnum() {
        if(token.getClasse().equals(Classe.cInt)) {
            token = lexico.getToken();
        }
        else {
            mostrarMensagemErro("<intnum> Faltou um inteiro/digito");
        }
    }

    //<string> ::= '\'' QQCOISA* '\''
    public void string() {
        if(token.getClasse().equals(Classe.cString)) {
            token = lexico.getToken();
        }
    }

    //Auxiliares
    private void mostrarMensagemErro(String mensagem) {
        System.out.println("Linha: " + token.getLinha() + 
        ", Coluna: " + token.getColuna() +
         ". " + mensagem);
    }

    private boolean testarPalavraReservada(String palavra) {
        return token.getClasse() == Classe.cPalRes &&
            token.getValor().getValorIdentificador().toLowerCase().equals(palavra);
    }

    //Ações
    public void A1() {
        tabela = new TabelaSimbolos();
        tabela.setTabelaPai(null);
        Registro registro = new Registro();
        registro.setNome(token.getValor().getValorIdentificador());
        registro.setCategoria(Categoria.PROGRAMAPRINCIPAL);
        tabela.inserirRegistro(registro);
        instrucoes += "#include <stdio.h>\n" +
                     "#include <stlib.h>\n\n" +
                     "int main(){";
        gerarCodigo(instrucoes);
    }

    public void A2() {
        if (testarPalavraReservada("integer")) {
            instrucoes += "int ";
            while (ultimasVariaveisDeclaradas.size() > 0) {
                ultimasVariaveisDeclaradas.get(0).setTipo(Tipo.integer);
                instrucoes += ultimasVariaveisDeclaradas.get(0).getNome();
                ultimasVariaveisDeclaradas.remove(0);
                if (ultimasVariaveisDeclaradas.size() > 0) {
                    instrucoes += ", ";
                }
            }
            instrucoes += ";";
            gerarCodigo(instrucoes);
        }
    }

    public void A3() {
        if (tabela.jaTemIdentificador(token)) {
            mostrarMensagemErro("INFO: Identificador ja existe");
        }
        else {
            Registro registro = new Registro();
            registro.setNome(token.getValor().getValorIdentificador());
            registro.setCategoria(Categoria.VARIAVEL);
            tabela.inserirRegistro(registro);
            ultimasVariaveisDeclaradas.add(registro);
        }
    }

    public void A8() {
        if (tabela.jaTemIdentificadorRecursiva(token)) {
            Registro registro = tabela.getIdentificadorRecursiva(token);
            if (registro.getCategoria().equals(Categoria.VARIAVEL) || registro.getCategoria().equals(Categoria.PARAMETRO)) {
                instrucoes += "scanf(\"%d\", &" + registro.getNome() + ");";
                gerarCodigo(instrucoes); 
            }
            else {
                mostrarMensagemErro("Erro A8: identificador nao e uma variavel");
            }
        }
        else {
            mostrarMensagemErro("Erro A8: variavel nao declada " + token.getValor().getValorIdentificador());
        }
    }
    
    public void A9() {
        if (tabela.jaTemIdentificadorRecursiva(token)) {
            Registro registro = tabela.getIdentificadorRecursiva(token);
            if (registro.getCategoria().equals(Categoria.VARIAVEL) || registro.getCategoria().equals(Categoria.PARAMETRO)) {
                instrucoes += "printf(\"%d\", " + registro.getNome() + ");";
                gerarCodigo(instrucoes); 
            }
            else {
                mostrarMensagemErro("Erro A9: identificador nao e uma variavel");
            }
        }
        else {
            mostrarMensagemErro("Erro A9: variavel nao declada " + token.getValor().getValorIdentificador());
        }
    }

    public void A11() {
        instrucoes += token.getValor().getValorInteiro() + ";";
    }

    public void A12() {
        instrucoes += varDoFor + "<" + token.getValor().getValorInteiro() + ";" + varDoFor + "++){";
        gerarCodigo(instrucoes);
    }

    public void A13() { // Fim do for
        instrucoes += "}";
        gerarCodigo(instrucoes);
    }

    public void A14() {
        instrucoes += "do {";
        gerarCodigo(instrucoes);
    }

    public void A15() {
        String sentenca_logica = instrucoes;
        instrucoes += "}while(" + sentenca_logica + ");";
        gerarCodigo(instrucoes);
    }

    public void A16() {
        instrucoes += "while(";
    }

    public void A17() {
        instrucoes += "){";
        gerarCodigo(instrucoes);
    }

    public void A18() {
        instrucoes += "}";
        gerarCodigo(instrucoes);
    }

    public void A19() {
        instrucoes += "){";
        gerarCodigo(instrucoes);
    }

    public void A20() {
        instrucoes += "}";
        gerarCodigo(instrucoes);
    }

    public void A21() {
        instrucoes += "}";
        gerarCodigo(instrucoes);
    }

    public void A22() {
        instrucoes += ";";
        gerarCodigo(instrucoes);
    }

    public void A25() {
        instrucoes += "else{";
        gerarCodigo(instrucoes);
    }

    public void A26() {
        instrucoes += " || ";
    }

    public void A27() {
        instrucoes += " && ";
    }

    public void A28() {
        instrucoes += " !";
    }

    public void A29() {
        instrucoes += " true";
    }

    public void A30() {
        instrucoes += " false";
    }

    public void A31() {
        instrucoes += " == ";
    }

    public void A32() {
        instrucoes += " > ";
    }

    public void A33() {
        instrucoes += " >= ";
    }

    public void A34() {
        instrucoes += " < ";
    }

    public void A35() {
        instrucoes += " <= ";
    }

    public void A36() {
        instrucoes += " != ";
    }

    public void A37() {
        instrucoes += " + ";
    }

    public void A38() {
        instrucoes += " - ";
    }

    public void A39() {
        instrucoes += " * ";
    }

    public void A40() {
        instrucoes += " / ";
    }

    public void A41() {
        instrucoes += token.getValor().getValorInteiro();
    }

    public void A43() {
        instrucoes += "printf(\"" + token.getValor().getValorInteiro() + "\");";
        gerarCodigo(instrucoes); 
    }

    public void A46() {
        instrucoes += "return 0;\n}";
        gerarCodigo(instrucoes);
    }

    public void A49() {
        if (tabela.jaTemIdentificadorRecursiva(token)) {
            Registro registro = tabela.getIdentificador(token);
            if (registro.getCategoria().equals(Categoria.VARIAVEL)
                || registro.getCategoria().equals(Categoria.PARAMETRO)) {
                instrucoes += token.getValor().getValorIdentificador() + " = ";
            }
        }
        else {
            mostrarMensagemErro("Erro A49: elemento nao declarado " + token.getValor().getValorIdentificador());
        }
    }

    public void A55() {
        instrucoes += token.getValor().getValorIdentificador();
    }

    public void A57() {
        if (tabela.jaTemIdentificadorRecursiva(token)) {
            Registro registro = tabela.getIdentificadorRecursiva(token);
            if (registro.getCategoria().equals(Categoria.VARIAVEL)
                || registro.getCategoria().equals(Categoria.PARAMETRO)
                || registro.getCategoria().equals(Categoria.FUNCAO)) {
                varDoFor = token.getValor().getValorIdentificador();
                instrucoes += "for(" + varDoFor + "=";
            }
            else {
                mostrarMensagemErro("Erro A57: identificador nao e uma variavel");
            }
        }
        else {
            mostrarMensagemErro("Erro A57: variavel nao declada " + token.getValor().getValorIdentificador());
        }
    }

    public void A59() {
        instrucoes += "printf(\"" + token.getValor().getValorIdentificador() + "\");";
        gerarCodigo(instrucoes); 
    }

    public void A61() {
        instrucoes += "printf(\"\\n\");";
        gerarCodigo(instrucoes);
    }

    public void A62() {
        instrucoes += "if(";
    }
}
