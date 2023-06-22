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

    //<programa> ::= program <id> {A1} ; <corpo> • {A45}
    public void programa() {
            if (testarPalavraReservada("program")) {
            token = lexico.getToken();
            id();
            //{A1}
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
        } else {
            mostrarMensagemErro("<programa> Faltou começar o programa com 'program'");
        }
    }

    //<corpo> ::= <declara> {A44} begin <sentencas> end {A46}
    public void corpo() {
        declara();
        //A{44}
        if(testarPalavraReservada("begin")) {
            token = lexico.getToken();
            sentencas();
            if(testarPalavraReservada("end")) {
                token = lexico.getToken();
                //{A46}
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
            tipo_var();
            //{A2}
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
        id();
        //{A3};
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
            mostrarMensagemErro("<mais_sentencas> Faltou ';'");
        }
    }

    //<cont_sentencas> ::= <sentencas> | vazio
    public void cont_sentencas() {
        //read, write, for, repeat, while, if
        if(testarPalavraReservada("read") ||
           testarPalavraReservada("write") ||
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
        id();
        //{A8};
        mais_var_read();
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
            id();
            //{A9}
            mais_exp_write();
        }
        else if(token.getClasse().equals(Classe.cString)) {
            string();
            //{A59}
            mais_exp_write();
        }
        else if(token.getClasse().equals(Classe.cInt)) {
            intnum();
            //{A43}
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
            repeat {A14} <sentencas> until ( <condicao> ) {A15} |
            while {A16} ( <condicao> ) {A17} do begin <sentencas> end {A18} |
            if ( <condicao> ) {A19} then begin <sentencas> end {A20} <pfalsa> {A21} |
            <id> {A49} := <expressao> {A22}*/
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
                    //{A61};
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
            id();
            //{A57}
            if(token.getClasse().equals(Classe.cAtrib)) {
                token = lexico.getToken();
                expressao();
                //{A11}
                if(testarPalavraReservada("to")) {
                    expressao();
                    //{A12}
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
            //{A14}
            sentencas();
            if(testarPalavraReservada("until")) {
                if(token.getClasse().equals(Classe.cParEsq)) {
                    token = lexico.getToken();
                    condicao();
                    if(token.getClasse().equals(Classe.cParDir)) {
                        token = lexico.getToken();
                        //{A15}
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
            //{A16};
            if(token.getClasse().equals(Classe.cParEsq)) {
                token = lexico.getToken();
                condicao();
                if(token.getClasse().equals(Classe.cParDir)) {
                    token = lexico.getToken();
                    //{A17}
                    if(testarPalavraReservada("do")) {
                        token = lexico.getToken();
                        if(testarPalavraReservada("begin")) {
                            token = lexico.getToken();
                            sentencas();
                            if(testarPalavraReservada("end")) {
                                token = lexico.getToken();
                                //{A18};
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
                condicao();
                if(token.getClasse().equals(Classe.cParDir)) {
                    token = lexico.getToken();
                    //{A19};
                    if(testarPalavraReservada("then")) {
                        token = lexico.getToken();
                        if(testarPalavraReservada("begin")) {
                            sentencas();
                            if(testarPalavraReservada("end")) {
                                //{A20};
                                pfalsa();
                                //{A21};
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
        else {
            id();
            //A13();
            if(token.getClasse().equals(Classe.cAtrib)) {
                token = lexico.getToken();
                expressao();
                //A14();
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
            //{A25}
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
            termo_logico();
            mais_expr_logica();
            //{A26}
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
            fator_logico();
            mais_termo_logico();
            //{A27}
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
            fator_logico();
            //{A28}
        }
        else if(testarPalavraReservada("true")) {
            //{A29}
        }
        else if(testarPalavraReservada("false")) {
            //{A30}
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
            expressao();
            //{A31}
        }
        else if(token.getClasse().equals(Classe.cMaior)) {
            token = lexico.getToken();
            expressao();
            //{A32}
        }
        else if(token.getClasse().equals(Classe.cMaiorIgual)) {
            token = lexico.getToken();
            expressao();
            //{A33}
        }
        else if(token.getClasse().equals(Classe.cMenor)) {
            token = lexico.getToken();
            expressao();
            //{A34}
        }
        else if(token.getClasse().equals(Classe.cMenorIgual)) {
            token = lexico.getToken();
            expressao();
            //{A35}
        }
        else if(token.getClasse().equals(Classe.cDiferente)) {
            token = lexico.getToken();
            expressao();
            //{A36}
        }
    }

    /*<mais_expressao> ::= + <termo> <mais_expressao> {A37} |
                     - <termo> <mais_expressao> {A38} | vazio*/
    public void mais_expressao() {
        if(token.getClasse().equals(Classe.cSoma)) {
            termo();
            mais_expressao();
            //{A37}
        }
        else if(token.getClasse().equals(Classe.cSub)) {
            termo();
            mais_expressao();
            //{A38}
        }
    }
    
    //<condicao> ::= <expressao> <relacao> {A15} <expressao> {A16}
    public void condicao() {
        expressao();
        relacao();
        //{A15};
        expressao();
        //{A16};
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
            mostrarMensagemErro("<relacao> Operador relacional invalido");
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
            mostrarMensagemErro("<op_ad> Faltou + ou -");
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
            fator();
            mais_termo();
            //{A39}
        }
        else if(token.getClasse().equals(Classe.cDiv)) {
            fator();
            mais_termo();
            //{A40}
        }
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
            mostrarMensagemErro("<op_mul> Faltou / ou *");
        }
    }

    //<fator> ::= <id> {A55} | <intnum> {A41} | ( <expressao> )
    public void fator() {
        if(token.getClasse().equals(Classe.cIdent)) {
            id();
            //{A55}
        }
        else if(token.getClasse().equals(Classe.cInt)) {
            intnum();
            //{A41}
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

    private void mostrarMensagemErro(String mensagem) {
        System.out.println("Linha: " + token.getLinha() + 
        ", Coluna: " + token.getColuna() +
         ". " + mensagem);
    }

    private boolean testarPalavraReservada(String palavra) {
        return token.getClasse() == Classe.cPalRes &&
            token.getValor().getValorIdentificador().toLowerCase().equals(palavra);
    }

}
