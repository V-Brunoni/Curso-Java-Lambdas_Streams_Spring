package br.com.alura.screenmatch.service;

public interface IConverteDados {

    <T> T obterDados(String json, Class<T> classe);
    // Metodo genérico sem nenhum tipo especificado, recebe uma String json e uma classe sem tipo



}
