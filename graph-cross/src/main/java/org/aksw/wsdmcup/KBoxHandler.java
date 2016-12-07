package org.aksw.wsdmcup;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

import org.aksw.kbox.kibe.KBox;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class KBoxHandler {
	
	private static URL graph;
	private static final String NS = "http://catsear.wsdm-cup-2017.org/";
	private static final int NS_SIZE = NS.length();
	private static final int NS_PROP_SIZE = (NS + "property/type/").length();

	public KBoxHandler() throws MalformedURLException {
		super();
		graph = new URL("https://concept.research.microsoft.com/");
	}

	public Set<String> getObjects(String subject) throws Exception {
		
		Set<String> set = new TreeSet<>();
		
		String q = "select ?o where { <" + encode(subject) + "> ?p ?o }";
		System.out.println(q);
		ResultSet rs = KBox.query(q, graph);
		
		while(rs.hasNext()) {
			String uri = rs.next().get("o").asResource().getURI();
			set.add(decode(uri));
		}
		
		return set;
	}

	public KBObject getEverything(String subject) throws Exception {
		
		HashMap<String, Integer> objects = new HashMap<>();
		String q = "select ?p ?o where { <" + encode(subject) + "> ?p ?o }";
		System.out.println(q);
		ResultSet rs = KBox.query(q, graph);
		
		while(rs.hasNext()) {
			QuerySolution qs = rs.next();
			String p = qs.get("p").asResource().getURI();
			String o = qs.get("o").asResource().getURI();
			Integer pInt = Integer.parseInt(p.substring(NS_PROP_SIZE));
//			String obj = o.substring(NS_SIZE);
			String obj = decode(o);
			objects.put(obj, pInt);
		}
		
		return new KBObject(subject, objects);
	}
	
	private String encode(String s) throws UnsupportedEncodingException {
		return NS + URLEncoder.encode(s.toLowerCase(), "UTF-8").replaceAll("\\+", "%20");
	}

	private String decode(String s) throws UnsupportedEncodingException {
		return URLDecoder.decode(s.substring(NS_SIZE), "UTF-8");
	}

}
