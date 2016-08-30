package br.ufsc.silq.core.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import br.ufsc.silq.core.data.AvaliacaoStats.ContadorConceitos;
import br.ufsc.silq.core.data.AvaliacaoStats.TotalizadorConceito;
import br.ufsc.silq.core.parser.dto.Artigo;
import br.ufsc.silq.core.parser.dto.Conceito;
import br.ufsc.silq.core.parser.dto.Trabalho;

public class AvaliacaoStatsTest {

	private AvaliacaoStats stats;
	private List<Artigo> artigos;
	private List<Trabalho> trabalhos;

	@Before
	public void setup() {
		this.artigos = new ArrayList<>();
		this.trabalhos = new ArrayList<>();

		this.artigos.add(new Artigo("Artigo 1", 2014, "Veículo 1", "1111-111X"));
		this.artigos.add(new Artigo("Artigo 2", 2000, "Veículo 2", "2222-222X"));
		this.artigos.add(new Artigo("Artigo 3", 2016, "Veículo 1", "3333-333X"));
		Artigo artigo4 = new Artigo("Artigo 4", 2016, "Veículo 1", "4444-444X");
		artigo4.addConceito(new Conceito(1L, "Veículo quase 1", "B3", NivelSimilaridade.NORMAL, 2016));
		artigo4.addConceito(new Conceito(2L, "Veículo 1", "A2", new NivelSimilaridade(0.99f), 2016));
		artigo4.addConceito(new Conceito(3L, "Veículo 1", "A3", NivelSimilaridade.ALTO, 2016));
		this.artigos.add(artigo4);

		this.trabalhos.add(new Trabalho("Trabalho 4", 1999, "Veículo 3"));
		this.trabalhos.add(new Trabalho("Trabalho 5", 2003, "Veículo 2"));
		Trabalho trabalho6 = new Trabalho("Trabalho 6", 1995, "Veículo 2");
		trabalho6.addConceito(new Conceito(4L, "Veículo quase 2", "A2", new NivelSimilaridade(0.467f), 1995));
		trabalho6.addConceito(new Conceito(5L, "Veículo 2", "A1", new NivelSimilaridade(1f), 1995));
		trabalho6.addConceito(new Conceito(6L, "Veículo quase 2", "A3", NivelSimilaridade.BAIXO, 1996));
		this.trabalhos.add(trabalho6);

		this.stats = new AvaliacaoStats(this.artigos, this.trabalhos);
	}

	@Test
	public void testAnoPrimeiraPublicacao() {
		Assertions.assertThat(this.stats.getAnoPrimeiraPublicacao()).isEqualTo(1995);
	}

	@Test
	public void testAnoUltimaPublicacao() {
		Assertions.assertThat(this.stats.getAnoUltimaPublicacao()).isEqualTo(2016);
	}

	@Test
	public void testGetQtdeArtigosPorAno() {
		Map<Integer, ContadorConceitos> map = this.stats.getQtdeArtigosPorAno();
		Assertions.assertThat(map).containsOnlyKeys(2000, 2014, 2016);

		Assertions.assertThat(map.get(2016).get(ContadorConceitos.TOTAL)).isEqualTo(2);
		Assertions.assertThat(map.get(2016).get(ContadorConceitos.SEM_CONCEITO)).isEqualTo(1);
		Assertions.assertThat(map.get(2016).get("A2")).isEqualTo(1);
		Assertions.assertThat(map.get(2016).get("A3")).isNull();
		Assertions.assertThat(map.get(2016).get("B3")).isNull();

		Assertions.assertThat(map.get(2000).get(ContadorConceitos.TOTAL)).isEqualTo(1);
		Assertions.assertThat(map.get(2000).get(ContadorConceitos.SEM_CONCEITO)).isEqualTo(1);
	}

	@Test
	public void testGetQtdeTrabalhosPorAno() {
		Map<Integer, ContadorConceitos> map = this.stats.getQtdeTrabalhosPorAno();
		Assertions.assertThat(map).containsOnlyKeys(1995, 1999, 2003);

		Assertions.assertThat(map.get(1995).get(ContadorConceitos.TOTAL)).isEqualTo(1);
		Assertions.assertThat(map.get(1995).get(ContadorConceitos.SEM_CONCEITO)).isEqualTo(0);
		Assertions.assertThat(map.get(1995).get("A1")).isEqualTo(1);
		Assertions.assertThat(map.get(1995).get("A2")).isNull();
		Assertions.assertThat(map.get(1995).get("A3")).isNull();

		Assertions.assertThat(map.get(2003).get(ContadorConceitos.TOTAL)).isEqualTo(1);
		Assertions.assertThat(map.get(2003).get(ContadorConceitos.SEM_CONCEITO)).isEqualTo(1);
	}

	@Test
	public void testGetPublicacoesPorAno() {
		Map<String, Map<Integer, ContadorConceitos>> map = this.stats.getPublicacoesPorAno();
		Assertions.assertThat(map).containsOnlyKeys("artigos", "trabalhos");
		Assertions.assertThat(map.get("artigos")).isEqualTo(this.stats.getQtdeArtigosPorAno());
		Assertions.assertThat(map.get("trabalhos")).isEqualTo(this.stats.getQtdeTrabalhosPorAno());
	}

	@Test
	public void testGetTotalizador() {
		Artigo artigo42 = new Artigo("Artigo 42", 2000, "Somethin", "4242-42XX");
		artigo42.addConceito(new Conceito(90L, "Veículo", "A2", NivelSimilaridade.NORMAL, 2000));
		this.artigos.add(artigo42);
		this.stats = new AvaliacaoStats(this.artigos, this.trabalhos);

		List<TotalizadorConceito> list = this.stats.getTotalizador();
		Assertions.assertThat(list).hasSize(2);
		Assertions.assertThat(list).contains(new TotalizadorConceito("A1", 1));
		Assertions.assertThat(list).contains(new TotalizadorConceito("A2", 2));
	}

	@Test
	public void testReduceWithEmptyStats() {
		AvaliacaoStats stats2 = new AvaliacaoStats();
		AvaliacaoStats reduced = this.stats.reduce(stats2);
		Assertions.assertThat(reduced.getArtigos()).isEqualTo(this.artigos);
		Assertions.assertThat(reduced.getTrabalhos()).isEqualTo(this.trabalhos);
	}

	private AvaliacaoStats createStats2() {
		List<Artigo> novosArtigos = new ArrayList<>();
		List<Trabalho> novosTrabalhos = new ArrayList<>();

		novosArtigos.add(new Artigo("Artigo99", 1999, "99' show of articles", "9999-999X"));
		novosArtigos.add(new Artigo("Artigo00", 2000, "00' show of articles", "0000-000X"));

		novosTrabalhos.add(new Trabalho("Trabs", 2030, "Mars"));

		AvaliacaoStats stats2 = new AvaliacaoStats(novosArtigos, novosTrabalhos);
		return stats2;
	}

	@Test
	public void testReduce() {
		AvaliacaoStats stats2 = this.createStats2();
		AvaliacaoStats reduced = this.stats.reduce(stats2);

		Assertions.assertThat(reduced.getArtigos()).hasSize(this.artigos.size() + stats2.getArtigos().size());
		Assertions.assertThat(reduced.getTrabalhos()).hasSize(this.trabalhos.size() + stats2.getTrabalhos().size());
	}

	public void testReduceNoSideEffects() {
		ArrayList<Artigo> artigosCopy = new ArrayList<>(this.stats.getArtigos());
		ArrayList<Trabalho> trabalhosCopy = new ArrayList<>(this.stats.getTrabalhos());
		this.stats.reduce(this.createStats2());
		Assertions.assertThat(this.stats.getArtigos()).isEqualTo(artigosCopy);
		Assertions.assertThat(this.stats.getTrabalhos()).isEqualTo(trabalhosCopy);
	}

	@Test
	public void testEqualityOfReduce() {
		AvaliacaoStats reduced1 = this.stats.reduce(this.createStats2());
		AvaliacaoStats reduced2 = this.createStats2().reduce(this.stats);
		Assertions.assertThat(reduced2.getArtigos()).containsOnlyElementsOf(reduced1.getArtigos());
		Assertions.assertThat(reduced2.getTrabalhos()).containsOnlyElementsOf(reduced1.getTrabalhos());
	}
}