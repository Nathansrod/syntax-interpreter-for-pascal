package com.syntatic;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

public class Lexico {
 
    private String nomeArquivo;
    private BufferedReader br;
    private String caminhoArquivo;
    private int linha = 1;
    private int coluna = 1;

    public Lexico(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
        this.caminhoArquivo = Paths.get(nomeArquivo).toAbsolutePath().toString();
        try{
            this.br = new BufferedReader(new FileReader(caminhoArquivo, StandardCharsets.UTF_8));
        }
        catch(IOException e){
            System.err.println("Não foi possível abrir o arquivo ou ler do arquivo: " + nomeArquivo);
            e.printStackTrace();
        }
    }
 
    public Token getToken() {
        int estado = 0;
        int c;
        StringBuilder lexema = new StringBuilder();
        char caractere;
        Token token = new Token();
 
        try{
            c = br.read();
            while (c != -1) { // -1 fim da stream
                caractere = (char) c;
                this.coluna++;
                switch(estado){
                    case 0:
                        if(Character.isLetter(caractere)){
                            lexema.append(caractere);
                            c = br.read();
                            estado = 1;
                        }
                        else if(Character.isDigit(caractere)){
                            lexema.append(caractere);
                            c = br.read();
                            estado = 2;
                        }
                        else if(!Character.isWhitespace(caractere)){
                            System.err.println("Erro: " + caractere);
                        }
                        else{
                            c = br.read();
                        }
                    break;
                    case 1:
                        if(Character.isLetter(caractere) || Character.isDigit(caractere)){
                            lexema.append(caractere);
                            c = br.read();
                            estado = 1;
                        }
                        else{
                            token.setClasse(Classe.cIdent);
                            token.setValor(new Valor(lexema.toString()));
                            token.setColuna(this.coluna - lexema.length());
                            token.setLinha(this.linha);
                            this.linha++;
                            return token;
                        }
                    break;
                    case 2:
                        if(Character.isDigit(caractere)){
                            lexema.append(caractere);
                            c = br.read();
                            estado = 2;
                        }
                        else{
                            token.setClasse(Classe.cInt);
                            token.setValor(new Valor(Integer.parseInt(lexema.toString())));
                            token.setColuna(this.coluna - lexema.length());
                            token.setLinha(this.linha);
                            this.linha++;
                            return token;
                        }
                    break;
                    default:
                    break;
                }
            }

            token.setClasse(Classe.cEOF);
            token.setColuna(this.coluna);
            token.setLinha(this.linha);
            return token;
        } catch (IOException e) {
            System.err.println("Não foi possível abrir o arquivo ou ler do arquivo: " + nomeArquivo);
            e.printStackTrace();
            return null;
        }
    }
 
}
