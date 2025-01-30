package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.DadosEpisodio;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.service.ConsumoAPI;
import br.com.alura.screenmatch.service.ConverteDados;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    private Scanner sc = new Scanner(System.in);

    private ConsumoAPI consumo = new ConsumoAPI();

    private ConverteDados conversor = new ConverteDados();

    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    // Declarando ENDERECO como uma variável constante, não pode ter seu valor alterado após a atribuição inicial
    // Já que o valor é constante deve-se iniciar com um valor
    private final String API_KEY = "&apikey=d72c4101";


    public void exibeMenu(){
        System.out.println("Informe o nome da série que deseja buscar: ");
        var nomeSerie = sc.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dadosSerie = conversor.obterDados(json, DadosSerie.class);
        System.out.println(dadosSerie);

        List<DadosTemporada> temporadas = new ArrayList<>();

        // Listar todas as temporadas e seus dados
		for (int i = 1; i <= dadosSerie.totalTemporadas(); i++){
            json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + "&season=" + i + API_KEY);
            DadosTemporada dadosTemp = conversor.obterDados(json, DadosTemporada.class);
			temporadas.add(dadosTemp);
		}
		temporadas.forEach(System.out::println);

//        // Listar todos os episódios de todas as temporadas
//        for (int i = 0; i < dadosSerie.totalTemporadas(); i++){
//            List<DadosEpisodio> episodiosTemp = temporadas.get(i).episodios();
//            for (int j = 0; j < episodiosTemp.size(); j++) {
//                System.out.println(episodiosTemp.get(j).titulo());
//            }
//        }

        // Mesma logica dos dois for acima, porém de uma forma mais concisa
        temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));
        // Lambda ->

        //EXEMPLO DE STREAM
//        List<String> nomes = Arrays.asList("Jacque", "Iasmin", "Paulo", "Rodrigo", "Nico");
//        nomes.stream()
//                .sorted() // Operação intermediária, ordena em ordem alfabética
//                .limit(3) // Limite para exibir somente 3 nomes
//                .filter(n -> n.startsWith("N")) // Filtra somente nomes que começam com a letra 'N'
//                .map(n -> n.toUpperCase()) // Pega os nomes que começam com N e colocam eles em Maiúsculo
//                .forEach(System.out::println); // Operação final, imprime cada elemento da lista


        List<DadosEpisodio> dadosEpisodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream())
                .collect(Collectors.toList()); // Coleta para uma lista que pode ser alterada
                //.toList(); manda para uma lista imutável

//        System.out.println("\nTOP 10 Episódios: ");
//        dadosEpisodios.stream()
//                .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
//                .peek(e -> System.out.println("Primeiro filtro (N/A) " + e)) // peek = espiar oque esta acontecendo dentro do stream
//                .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
//                .peek(e -> System.out.println("Ordenação " + e))
//                .limit(10)
//                .peek(e -> System.out.println("Limite " + e))
//                .map(e -> e.titulo().toUpperCase())
//                .peek(e -> System.out.println("Mapeamento " + e))
//                .forEach(System.out::println);

        List<Episodio> episodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream()
                        .map(d -> new Episodio(t.numeroTemp(), d))
                ).collect(Collectors.toList());

        System.out.println("\n");
        episodios.forEach(System.out::println);

//        System.out.println("Digite o nome do episódio: ");
//        var trechoTitulo = sc.nextLine();
//        Optional<Episodio> episodioBuscado = episodios.stream()
//                .filter(e -> e.getTitulo().toUpperCase().contains(trechoTitulo.toUpperCase()))
//                .findFirst();
//        if (episodioBuscado.isPresent()){
//            System.out.println("Episódio encontrado!");
//            System.out.println(episodioBuscado.get());
//        } else {
//            System.out.println("Episódio não encontrado!");
//        }


//        System.out.println("\nA partir de que ano você deseja ver os episódios ?");
//        var ano = sc.nextInt();
//        sc.nextLine();
//
//        LocalDate dataBusca = LocalDate.of(ano, 1,1);
//
//        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy"); // Formada a data no padrão BR
//
//        episodios.stream()
//                .filter(e -> e.getDataLancamento() != null && e.getDataLancamento().isAfter(dataBusca))
//                .forEach(e -> System.out.println(
//                        "\nTemporada: " + e.getTemporada() +
//                                ", Episódio: " + e.getTitulo() +
//                                ", Data lançamento: " + e.getDataLancamento().format(formatador)
//                ));


        Map<Integer, Double> avaliacoesTemp = episodios.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.groupingBy(Episodio::getTemporada,
                        Collectors.averagingDouble(Episodio::getAvaliacao)));
        System.out.println("\nMédia de Avaliações por Temporada: ");
        System.out.println(avaliacoesTemp);

        DoubleSummaryStatistics est = episodios.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.summarizingDouble(Episodio::getAvaliacao));
        System.out.println("-- Estatísticas Gerais dos Episódios --");
        System.out.println("Média: " + est.getAverage());
        System.out.println("Melhor avaliado: " + est.getMax());
        System.out.println("Pior avaliado: " + est.getMin());
        System.out.println("Quantidade de episódios avaliados: " + est.getCount());



    }
}
