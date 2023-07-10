package com.syntatic;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
 
public class App {
 
    public static void main(String[] args) {

        /*if (args.length == 0) {
            System.out.println("Modo de usar: java -jar NomePrograma NomeArquivoCodigo");
            return;
        }*/

        // Teste único
        /*String nomeArquivo = "teste10";
 
        substituirTabulacao(nomeArquivo);
 
        Sintatico sintatico = new Sintatico(nomeArquivo);
        sintatico.analisar();*/

        // Vários testes
        String[] arquivosDeTeste = {
            "teste1salario",
            "teste2atribuicao",
            "teste3expressao",
            "teste4leitura",
            "teste5condicional",
            "teste6enquanto",
            "teste7repete",
            "teste8para",
            "teste9elogico",
            "teste10"
        };

        for (String teste : arquivosDeTeste) {
            substituirTabulacao(teste);
            Sintatico sintatico = new Sintatico(teste);
            sintatico.analisar();
        }
    }
 
    public static void substituirTabulacao(String nomeArquivo) {
        Path caminhoArquivo = Paths.get(nomeArquivo);
        int numeroEspacosPorTab = 4;
        StringBuilder juntando = new StringBuilder();
        String espacos;
 
        for (int cont = 0; cont < numeroEspacosPorTab; cont++) {
            juntando.append(" ");
        }
        espacos = juntando.toString();
 
        String conteudo;
        try {
            conteudo = new String(Files.readAllBytes(caminhoArquivo), StandardCharsets.UTF_8);
            conteudo = conteudo.replace("\t", espacos);
            Files.write(caminhoArquivo, conteudo.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}