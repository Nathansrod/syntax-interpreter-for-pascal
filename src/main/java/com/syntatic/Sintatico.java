package com.syntatic;

public class Sintatico {
    
    private LexicoAlt lexico;
    private Token token;
    private String nomeArquivo;

    public Sintatico(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public void analisar() {
        lexico = new LexicoAlt(nomeArquivo);
        token = lexico.getToken();
        programa();
    }

    // <programa> ::= program <id> {A1} <corpo> • {A30}
    public void programa() {
        if (testarPalavraReservada("program")) {
            token = lexico.getToken();
            id();
            //{A1}
            corpo();
            if (token.getClasse() == Classe.cPonto) {
                token = lexico.getToken();
                //{A30}
            } else {
                mostrarMensagemErro("Faltou ponto final no PROGRAM");
            }
        } else {
            mostrarMensagemErro("Faltou começar o programa com PROGRAM");
        }
    }

    //<corpo> ::= <declara> begin <sentencas> end
    public void corpo() {
        declara();
        if(testarPalavraReservada("begin")) {
            token = lexico.getToken();
            sentencas();
            if(testarPalavraReservada("end")) {
                token = lexico.getToken();
            }
            else {
                mostrarMensagemErro("Faltou 'end'");
            }
        }
        else {
            mostrarMensagemErro("Faltou 'begin'");
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
            mostrarMensagemErro("Faltou ';'");
        }
    }

    //<cont_dc> ::= <dvar> <mais_dc> | vazio
    public void cont_dc() {
        if(token.getClasse().equals(Classe.cIdent)){
            dvar();
            mais_dc();
        }
    }

    //<dvar> ::= <variaveis> : <tipo_var>
    public void dvar() {
        variaveis();
        if(token.getClasse().equals(Classe.cDoisPontos)) {
            token = lexico.getToken();
            tipo_var();
        }
        else {
            mostrarMensagemErro("Faltou ':'");
        }
    }

    //<tipo_var> ::= integer
    public void tipo_var() {
        if(testarPalavraReservada("integer")) {
            token = lexico.getToken();
        }
        else {
            mostrarMensagemErro("Tipo de variavel invalido");
        }
    }

    //<variaveis> ::= <id> {A2} <mais_var>
    public void variaveis() {
        id();
        //A2();
        mais_var();
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
            mostrarMensagemErro("Faltou ';'");
        }
    }

    //<cont_sentencas> ::= <sentencas> | vazio
    public void cont_sentencas() {
        sentencas();
    }

    //<var_read> ::= <id> {A5} <mais_var_read>
    public void var_read() {
        id();
        //A5();
        mais_var_read();
    }

    //<mais_var_read> ::=  ,  <var_read> | vazio
    public void mais_var_read() {
        if(token.getClasse().equals(Classe.cVirgula)) {
            token = lexico.getToken();
            var_read();
        }
    }
    //<var_write> ::= <id> {A6} <mais_var_write>
    public void var_write() {
        id();
        //A6();
        mais_var_write();
    }
    
    //<mais_var_write> ::=  ,  <var_write> | vazio
    public void mais_var_write() {
        if(token.getClasse().equals(Classe.cVirgula)) {
            token = lexico.getToken();
            var_write();
        }
    }

    /*<comando> ::= 
            read ( <var_read> ) |
            write ( <var_write> ) |
            for <id> {A25} := <expressao> {A26} to {A27} <expressao> {A28} 
            do begin <sentencas> end {A29} |
            repeat {A23} <sentencas> until ( <condicao> ) {A24} |
            while {A20} ( <condicao> ) {A21} do begin <sentencas> end {A22} |
            if ( <condicao> ) {A17} then begin <sentencas> end {A18} 
            <pfalsa> {A19} |
            <id> {A13} := <expressao> {A14} */
    public void comando() {
        if(testarPalavraReservada("read")) {
            token = lexico.getToken();
            var_read();
        }
        else if(testarPalavraReservada("write")) {
            token = lexico.getToken();
            var_write();
        }
        else if(testarPalavraReservada("for")) {
            token = lexico.getToken();
            id();
            //A25();
            if(token.getClasse().equals(Classe.cAtrib)) {
                token = lexico.getToken();
                expressao();
                //A26();
                if(testarPalavraReservada("to")) {
                    //A27();
                    expressao();
                    //A28();
                }
                else {
                    mostrarMensagemErro("Faltou 'to'");
                }
            }
            else {
                mostrarMensagemErro("Faltou operador ':='");
            }
        }
        else if(testarPalavraReservada("repeat")) {
            token = lexico.getToken();
            //A23();
            sentencas();
            if(testarPalavraReservada("until")) {
                if(token.getClasse().equals(Classe.cParEsq)) {
                    token = lexico.getToken();
                    condicao();
                    if(token.getClasse().equals(Classe.cParDir)) {
                        token = lexico.getToken();
                        //A24();
                    }
                    else {
                        mostrarMensagemErro("Faltou ')'");
                    }
                }
                else {
                    mostrarMensagemErro("Faltou '('");
                }
            }
            else {
                mostrarMensagemErro("Faltou 'until'");
            }
        }
        else if(testarPalavraReservada("while")) {
            token = lexico.getToken();
            //A20();
            if(token.getClasse().equals(Classe.cParEsq)) {
                token = lexico.getToken();
                condicao();
                if(token.getClasse().equals(Classe.cParDir)) {
                    token = lexico.getToken();
                    //A21();
                    if(testarPalavraReservada("do")) {
                        token = lexico.getToken();
                        if(testarPalavraReservada("begin")) {
                            token = lexico.getToken();
                            sentencas();
                            if(testarPalavraReservada("end")) {
                                token = lexico.getToken();
                                //A22();
                            }
                            else {
                                mostrarMensagemErro("Faltou 'end'");
                            }
                        }
                        else {
                            mostrarMensagemErro("Faltou 'begin'");
                        }
                    }
                    else {
                        mostrarMensagemErro("Faltou 'do'");
                    }

                }
                else {
                    mostrarMensagemErro("Faltou ')'");
                }
            }
            else {
                mostrarMensagemErro("Faltou '('");
            }
        }
        else if(testarPalavraReservada("if")) {
            token = lexico.getToken();
            if(token.getClasse().equals(Classe.cParEsq)) {
                token = lexico.getToken();
                condicao();
                if(token.getClasse().equals(Classe.cParDir)) {
                    token = lexico.getToken();
                    //A17();
                    if(testarPalavraReservada("then")) {
                        token = lexico.getToken();
                        if(testarPalavraReservada("begin")) {
                            sentencas();
                            if(testarPalavraReservada("end")) {
                                //A18();
                                pfalsa();
                                //A19();
                            }
                            else {
                                mostrarMensagemErro("Faltou 'end'");
                            }
                        }
                        else {
                            mostrarMensagemErro("Faltou 'begin'");
                        }
                    }
                }
                else {
                    mostrarMensagemErro("Faltou '('");
                }
            }
            else {
                mostrarMensagemErro("Faltou ')'");
            }
        }
        else {
            id();
            //A13();
            if(token.getClasse().equals(Classe.cAtrib)) {
                token = lexico.getToken();
                expressao();
                //A14();
            }
            else {
                mostrarMensagemErro("Faltou operador ':='");
            }
        }
    }

    //<condicao> ::= <expressao> <relacao> {A15} <expressao> {A16}
    public void condicao() {
        expressao();
        relacao();
        //A15();
        expressao();
        //A16();
    }

    //<pfalsa> ::= else begin <sentencas> end | vazio
    public void pfalsa () {
        if(testarPalavraReservada("else")) {
            token = lexico.getToken();
            if(testarPalavraReservada("begin")) {
                token = lexico.getToken();
                sentencas();
                if(testarPalavraReservada("end")) {
                    token = lexico.getToken();
                }
                else {
                    mostrarMensagemErro("Faltou 'end'");
                }
            }
            else {
                mostrarMensagemErro("Faltou 'begin'");
            }
        }
    }

    //<relacao> ::= = | > | < | >= | <= | <>
    public void relacao() {
        if(token.getClasse().equals(Classe.cIgual)
        || token.getClasse().equals(Classe.cMaior)
        || token.getClasse().equals(Classe.cMenor)
        || token.getClasse().equals(Classe.cMaiorIgual)
        || token.getClasse().equals(Classe.cMenorIgual)
        || token.getClasse().equals(Classe.cDiferente)) {
            token = lexico.getToken();
        }
        else {
            mostrarMensagemErro("Operador relacional invalido");
        }
    }

    //<expressao> ::= <termo> <outros_termos>
    public void expressao() {
        termo();
        outros_termos();
    }

    //<outros_termos> ::= <op_ad> {A9} <termo> {A10} <outros_termos>  | vazio
    public void outros_termos() {
        if(token.getClasse().equals(Classe.cSoma) || token.getClasse().equals(Classe.cSub)) {
            op_ad();
            //A9();
            termo();
            //A10();
            outros_termos();
        }
    }

    //<op_ad> ::= + | -
    public void op_ad() {
        if(token.getClasse().equals(Classe.cSoma) || token.getClasse().equals(Classe.cSub)) {
            token = lexico.getToken();
        }
        else {
            mostrarMensagemErro("Faltou + ou -");
        }
    }

    //<termo> ::= <fator> <mais_fatores>
    public void termo() {
        fator();
        mais_fatores();
    }

    //<mais_fatores> ::= <op_mul> {A11} <fator> {A12} <mais_fatores> | vazio
    public void mais_fatores() {
        if(token.getClasse().equals(Classe.cMult) || token.getClasse().equals(Classe.cDiv)) {
            op_mul();
            //A11();
            fator();
            //A12();
            mais_fatores();
        }
    }

    //<op_mul> ::= * | /
    public void op_mul() {
        if(token.getClasse().equals(Classe.cMult) || token.getClasse().equals(Classe.cDiv)) {
            token = lexico.getToken();
        }
        else {
            mostrarMensagemErro("Faltou / ou *");
        }
    }

    //<fator> ::= <id_var> {A7} | <intnum> {A8} | (<expressao>)
    public void fator() {
        id();
        //A7();
        intnum();
        //A8();
        if(token.getClasse().equals(Classe.cParEsq)) {
            token = lexico.getToken();
            expressao();
            if(token.getClasse().equals(Classe.cParDir)) {
                token = lexico.getToken();
            }
            else {
                mostrarMensagemErro("Faltou ')'");
            }
        }
        else {
            mostrarMensagemErro("Faltou '('");
        }
    }

    //<id> ::= letra (letra | digito)* {A3} 
    public void id() {
        if(token.getClasse().equals(Classe.cIdent)) {
            token = lexico.getToken();
            //A3();
        }
        else {
            mostrarMensagemErro("Faltou identificador");
        }
    }

    //<intnum> ::= digito {A4}
    public void intnum() {
        if(token.getClasse().equals(Classe.cInt)) {
            token = lexico.getToken();
            //A4();
        }
        else {
            mostrarMensagemErro("Faltou um inteiro/digito");
        }
    }

    private void mostrarMensagemErro(String mensagem) {
        System.out.println("Linha: " + token.getLinha() + 
        ", Coluna: " + token.getColuna() +
         ". " + mensagem);
    }

    private boolean testarPalavraReservada(String palavra) {
        return token.getClasse() == Classe.cPalRes &&
            token.getValor().getValorIdentificador().equals(palavra);
    }

}
