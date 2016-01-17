package br.ufsc.silq.core.parser.attribute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import br.ufsc.silq.core.dto.parser.struct.Artigo;
import br.ufsc.silq.core.utils.parser.ConverterHelper;

public class ArtigoAttributeGetter {

	public static List<Artigo> iterateUntilArtigos(Node raizLocal) {
		NodeList qualisList = raizLocal.getChildNodes();
		List<Artigo> artigos = new ArrayList<>();

		if (raizLocal.getNodeName().toLowerCase().equals("artigo-publicado")) {
			artigos.add(ArtigoAttributeGetter.getArtigoInfo(raizLocal));
		} else if (qualisList.getLength() > 0) {
			for (int i = 0; i < qualisList.getLength(); i++) {
				Node nodoFilho = qualisList.item(i);
				artigos.addAll(iterateUntilArtigos(nodoFilho));
			}
		}

		return artigos;
	}

	private static Artigo getArtigoInfo(Node raizLocal) {
		NodeList filhos = raizLocal.getChildNodes();

		Artigo artigo = new Artigo();
		for (int i = 0; i < filhos.getLength(); i++) {

			Node filho = filhos.item(i);
			String nodeName = filho.getNodeName().toLowerCase();

			if (nodeName.equals("dados-basicos-do-artigo")) {
				List<String> result = getAttribute(filho, Arrays.asList("titulo-do-artigo", "ano-do-artigo"));
				artigo.setNomeArtigo(result.get(1));
				artigo.setAno(ConverterHelper.parseIntegerSafely(result.get(0)));
			} else if (nodeName.equals("detalhamento-do-artigo")) {
				List<String> result = getAttribute(filho, Arrays.asList("titulo-do-periodico-ou-revista", "issn"));
				artigo.setTituloPeriodico(result.get(1));
				artigo.setIssn(result.get(0));
			}
		}

		return artigo;
	}

	public static List<String> getAttribute(Node nodo, List<String> attrList) {
		NamedNodeMap attributes = nodo.getAttributes();
		List<String> resultado = new ArrayList<>();

		for (int g = 0; g < attributes.getLength(); g++) {
			Attr attribute = (Attr) attributes.item(g);
			if (attrList.contains(attribute.getName().toLowerCase())) {
				resultado.add(attribute.getValue());
			}
		}

		return resultado;
	}
}
