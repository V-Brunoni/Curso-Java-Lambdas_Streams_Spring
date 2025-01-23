package br.com.alura.screenmatch.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true) // Ignora tudo o que não for title, totalSeasons e imdbRating
public record DadosSerie(@JsonAlias("Title") String titulo, // Informa um apelido 'title' para a variável titulo, le o title do json e escreve o titulo
                         @JsonAlias("totalSeasons") int totalTemporadas,
                         @JsonAlias("imdbRating") String avaliacao) {
}
