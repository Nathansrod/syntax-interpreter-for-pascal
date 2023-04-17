package com.syntatic;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

public class LexicoAlt{

    private static final int EOF = 65535;
    private String nomeArquivo;
    private BufferedReader br;
    private String caminhoArquivo;
    private int linha = 1;
    private int coluna = 1;
    private char c;
    private String[] palavrasReservadas = {"and", "array", "begin", "case", "const", "div", "do", "downto", "else", "end", "file", "for", "function", "goto", "if", "in", "integer", "label", "mod", "nil", "not", "of", "or", "packed", "procedure", "program", "read", "real", "record", "repeat", "read", "set", "then", "to", "type", "until", "var", "while", "with", "write", "writeln"};
    

    public LexicoAlt(String nomeArquivo){
        this.nomeArquivo = nomeArquivo;
        this.caminhoArquivo = Paths.get(nomeArquivo).toAbsolutePath().toString();
        try{
            this.br = new BufferedReader(new FileReader(caminhoArquivo, StandardCharsets.UTF_8));
            this.c = (char) br.read();
        }
        catch(IOException e){
            System.err.println("Não foi possível abrir o arquivo ou ler do arquivo: " + nomeArquivo);
            e.printStackTrace();
        }
    }
    
    //cReal, cPalRes, 
    public Token getToken(){
        int colunaInicial = 0;
        try{
            while(this.c != EOF){
                StringBuilder lexema = new StringBuilder();
                if(Character.isWhitespace(this.c) && this.c != EOF){
                    while(Character.isWhitespace(this.c)){
                        if(this.c == '\n'){
                            this.linha += 1;
                            this.coluna = 0;
                        }
                        this.c = (char)this.br.read();
                        this.coluna += 1;
                    }
                }
                else if(Character.isLetter(this.c)){
                    colunaInicial = this.coluna;
                    while(Character.isLetter(this.c) || Character.isDigit(this.c)){
                        lexema.append(this.c);
                        this.c = (char)this.br.read();
                        this.coluna += 1;
                    }
                    Token token = new Token();
                    token.setClasse(Classe.cIdent);
                    if(verificarPalavraReservada(lexema.toString())){
                        token.setClasse(Classe.cPalRes);
                    }
                    token.setValor(new Valor(lexema.toString()));
                    token.setLinha(this.linha);
                    token.setColuna(colunaInicial);
                    return token;
                }
                else if(Character.isDigit(this.c)){
                    colunaInicial = this.coluna;
                    while(Character.isDigit(this.c)){
                        lexema.append(this.c);
                        this.c = (char)this.br.read();
                        this.coluna += 1;
                    }
                    Token token = new Token();
                    token.setClasse(Classe.cInt);
                    token.setValor(new Valor(Integer.parseInt(lexema.toString())));
                    token.setLinha(this.linha);
                    token.setColuna(colunaInicial);
                    return token;
                }
                else if(Character.compare(this.c, ':') == 0){
                    colunaInicial = this.coluna;
                    lexema.append(this.c);
                    this.c = (char)this.br.read();
                    this.coluna += 1;
                    if(Character.compare(c, '=') == 0){
                        lexema.append(this.c);
                        this.c = (char)this.br.read();
                        this.coluna += 1;
                        Token token = new Token();
                        token.setClasse(Classe.cAtrib);
                        token.setLinha(this.linha);
                        token.setColuna(colunaInicial);
                        return token;
                    }
                    else{
                        Token token = new Token();
                        token.setClasse(Classe.cDoisPontos);
                        token.setLinha(this.linha);
                        token.setColuna(colunaInicial);
                        return token;
                    }
                }
                else if(Character.compare(this.c, '=') == 0){
                    colunaInicial = this.coluna;
                    lexema.append(this.c);
                    this.c = (char)this.br.read();
                    this.coluna += 1;
                    Token token = new Token();
                    token.setClasse(Classe.cIgual);
                    token.setLinha(this.linha);
                    token.setColuna(colunaInicial);
                    return token;
                }
                else if(Character.compare(this.c, '+') == 0){
                    colunaInicial = this.coluna;
                    lexema.append(this.c);
                    this.c = (char)this.br.read();
                    this.coluna += 1;
                    Token token = new Token();
                    token.setClasse(Classe.cSoma);
                    token.setLinha(this.linha);
                    token.setColuna(colunaInicial);
                    return token;
                }
                else if(Character.compare(this.c, '-') == 0){
                    colunaInicial = this.coluna;
                    lexema.append(this.c);
                    this.c = (char)this.br.read();
                    this.coluna += 1;
                    Token token = new Token();
                    token.setClasse(Classe.cSub);
                    token.setLinha(this.linha);
                    token.setColuna(colunaInicial);
                    return token;
                }
                else if(Character.compare(this.c, '/') == 0){
                    colunaInicial = this.coluna;
                    lexema.append(this.c);
                    this.c = (char)this.br.read();
                    this.coluna += 1;
                    Token token = new Token();
                    token.setClasse(Classe.cDiv);
                    token.setLinha(this.linha);
                    token.setColuna(colunaInicial);
                    return token;
                }
                else if(Character.compare(this.c, '*') == 0){
                    colunaInicial = this.coluna;
                    lexema.append(this.c);
                    this.c = (char)this.br.read();
                    this.coluna += 1;
                    Token token = new Token();
                    token.setClasse(Classe.cMult);
                    token.setLinha(this.linha);
                    token.setColuna(colunaInicial);
                    return token;
                }
                else if(Character.compare(this.c, '>') == 0){
                    colunaInicial = this.coluna;
                    lexema.append(this.c);
                    this.c = (char)this.br.read();
                    this.coluna += 1;
                    if(Character.compare(c, '=') == 0){
                        lexema.append(this.c);
                        this.c = (char)this.br.read();
                        this.coluna += 1;
                        Token token = new Token();
                        token.setClasse(Classe.cMaiorIgual);
                        token.setLinha(this.linha);
                        token.setColuna(colunaInicial);
                        return token;
                    }
                    else{
                        Token token = new Token();
                        token.setClasse(Classe.cMaior);
                        token.setLinha(this.linha);
                        token.setColuna(colunaInicial);
                        return token;
                    }
                }
                else if(Character.compare(this.c, '<') == 0){
                    colunaInicial = this.coluna;
                    lexema.append(this.c);
                    this.c = (char)this.br.read();
                    this.coluna += 1;
                    if(Character.compare(c, '=') == 0){
                        lexema.append(this.c);
                        this.c = (char)this.br.read();
                        this.coluna += 1;
                        Token token = new Token();
                        token.setClasse(Classe.cMenorIgual);
                        token.setLinha(this.linha);
                        token.setColuna(colunaInicial);
                        return token;
                    }
                    else if(Character.compare(this.c, '>') == 0){
                        lexema.append(this.c);
                        this.c = (char)this.br.read();
                        this.coluna += 1;
                        Token token = new Token();
                        token.setClasse(Classe.cDiferente);
                        token.setLinha(this.linha);
                        token.setColuna(colunaInicial);
                        return token;
                    }
                    else{
                        Token token = new Token();
                        token.setClasse(Classe.cMenor);
                        token.setLinha(this.linha);
                        token.setColuna(colunaInicial);
                        return token;
                    }
                }
                else if(Character.compare(this.c, ',') == 0){
                    colunaInicial = this.coluna;
                    lexema.append(this.c);
                    this.c = (char)this.br.read();
                    this.coluna += 1;
                    Token token = new Token();
                    token.setClasse(Classe.cVirgula);
                    token.setLinha(this.linha);
                    token.setColuna(colunaInicial);
                    return token; 
                }
                else if(Character.compare(this.c, ';') == 0){
                    colunaInicial = this.coluna;
                    lexema.append(this.c);
                    this.c = (char)this.br.read();
                    this.coluna += 1;
                    Token token = new Token();
                    token.setClasse(Classe.cPontoVirg);
                    token.setLinha(this.linha);
                    token.setColuna(colunaInicial);
                    return token; 
                }
                else if(Character.compare(this.c, '.') == 0){
                    colunaInicial = this.coluna;
                    lexema.append(this.c);
                    this.c = (char)this.br.read();
                    this.coluna += 1;
                    Token token = new Token();
                    token.setClasse(Classe.cPonto);
                    token.setLinha(this.linha);
                    token.setColuna(colunaInicial);
                    return token; 
                }
                else if(Character.compare(this.c, '(') == 0){
                    colunaInicial = this.coluna;
                    lexema.append(this.c);
                    this.c = (char)this.br.read();
                    this.coluna += 1;
                    Token token = new Token();
                    token.setClasse(Classe.cParEsq);
                    token.setLinha(this.linha);
                    token.setColuna(colunaInicial);
                    return token; 
                }
                else if(Character.compare(this.c, ')') == 0){
                    colunaInicial = this.coluna;
                    lexema.append(this.c);
                    this.c = (char)this.br.read();
                    this.coluna += 1;
                    Token token = new Token();
                    token.setClasse(Classe.cParDir);
                    token.setLinha(this.linha);
                    token.setColuna(colunaInicial);
                    return token; 
                }
                else if(Character.compare(this.c, '\'') == 0){
                    lexema.append(this.c);
                    colunaInicial = this.coluna;
                    this.c = (char)this.br.read();
                    this.coluna += 1;
                    while(Character.compare(this.c, '\'') != 0){
                        lexema.append(this.c);
                        this.c = (char)this.br.read();
                        this.coluna += 1;
                    }
                    lexema.append(this.c);
                    this.c = (char)this.br.read();
                    this.coluna += 1;
                    Token token = new Token();
                    token.setClasse(Classe.cString);
                    token.setValor(new Valor(lexema.toString()));
                    token.setLinha(this.linha);
                    token.setColuna(colunaInicial);
                    return token;
                }
                else if(Character.compare(this.c, '{') == 0){
                    lexema.append(this.c);
                    colunaInicial = this.coluna;
                    this.c = (char)this.br.read();
                    this.coluna += 1;
                    while(Character.compare(this.c, '}') != 0){
                        lexema.append(this.c);
                        this.c = (char)this.br.read();
                        this.coluna += 1;
                    }
                    lexema.append(this.c);
                    this.c = (char)this.br.read();
                    this.coluna += 1;
                }
                else{
                    System.err.println("Erro lexico!");
                    return null;
                }
            }
            Token token = new Token();
            token.setClasse(Classe.cEOF);
            token.setLinha(this.linha);
            token.setColuna(this.coluna);
            return token;
        }
        catch(IOException e){
            System.err.println("Erro: " + e.getMessage());
            return null;
        }
    }

    private boolean verificarPalavraReservada(String valor){
        for(String palRes : palavrasReservadas){
            valor = valor.toLowerCase();
            if(valor.equals(palRes)){
                return true;
            }
        }
        return false;
    }

}