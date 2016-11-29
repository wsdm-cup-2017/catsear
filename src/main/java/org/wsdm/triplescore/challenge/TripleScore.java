package org.wsdm.triplescore.challenge;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.aksw.kbox.kibe.KBox;
import org.aksw.openqa.nlp.text.TextNLP2.Filter;
import org.aksw.smart.starpath.indexbuilder.xingu.SCCFilter;
import org.aksw.smart.starpath.indexbuilder.xingu.URIPatternFilter;
import org.aksw.smart.starpath.indexbuilder.xingu.scorer.JaccardLabelStarpathSCCScorer;
import org.aksw.smart.starpath.xingu.qald.benchmark.SCCFilterList;
import org.dbtrends.scc.Knowledgebase;
import org.dbtrends.scc.Literal;
import org.dbtrends.scc.Property;
import org.dbtrends.scc.SCC;
import org.dbtrends.scc.URIObject;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.RDFNode;

/**
 * 
 * @author emarx
 *
 */
public class TripleScore {
	
	private final static String DBPEDIA_NAMESPACE = "http://dbpedia.org/resource";
	private final static String RDF_TYPE = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
	
	private final static String DBP_OCCUPATION = "http://dbpedia.org/property/occupation";
	
	private final static String DBP_BIRTHPLACE = "http://dbpedia.org/property/birthPlace";
	private final static String DBO_BIRTHPLACE = "http://dbpedia.org/ontology/birthPlace";
	private final static String DBP_CITZENSHIP = "http://dbpedia.org/property/citizenship";
	
	private final static String DBP_COMMON_NAME = "http://dbpedia.org/property/commonName";
	private final static String FOAF_NAME = "http://xmlns.com/foaf/0.1/name";
	private final static String DBO_COUNTRY = "http://dbpedia.org/ontology/country";
	private final static String DBO_PROFESSION = "http://dbpedia.org/ontology/profession";
	private final static String DBP_PROFESSION = "http://dbpedia.org/property/profession";
	
	private final static Integer SOME_EVIDENCE = 5; // Range from 3 to 7
	private final static Integer FULL_EVIDENCE = 7; // Range from 3 to 7
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		if(args.length != 2) {
			System.out.println("use java -jar triplescore <inputFile> <outputFile>");
		} else {
			String inputFilePath = args[0];
			String outputFilePath = args[1];
			score(inputFilePath, outputFilePath);
		}
	}
	
	public static void score(String inputFilePath, String outputFilePath) throws FileNotFoundException, IOException {
		File inputFile = new File(inputFilePath);
		File outputFile = new File(outputFilePath);
		FileReader inputReader = new FileReader(inputFile);
		PrintStream ps = new PrintStream(outputFile);
		try (BufferedReader br = new BufferedReader(inputReader)) {
		    String line = null;
		    Entry lastEntry = null;
		    List<Entry> entries = new ArrayList<Entry>();
		    while ((line = br.readLine()) != null) {
		    	try {
					Entry entry = parseLine(line);
					if(lastEntry!=null &&
							!lastEntry.getSubject().equals(entry.getSubject())) {
						// start scoring with starpath
						processEntries(entries);						
						print(ps, entries); // printing output
						entries.clear();
					}
					entries.add(entry);
					lastEntry = entry;					
				} catch (LineParseException e) {
					e.printStackTrace();
				}
		    }
			processEntries(entries);
			print(ps, entries); // printing output
		}
	}
	
	private static void processEntries(List<Entry> entries) {
		processStarpathEntries(entries);
		processMommys(entries);
	}
	
	private static void processMommys(List<Entry> entries) {
		// TODO: score with remaining not scored entries
		// TODO: tommaso's score goes here
	}
	
	private static void processStarpathEntries(List<Entry> entries) {
		for(Entry e : entries) {
			Integer score = null;
			try {
				score = getProfessionScore(e); // we have to try either Profession or Nationality
			} catch (Exception e2) {
				e2.printStackTrace();
			}							
			if(score == null) { // if score is null, maybe is nationality
				try {
					score = getNationalityScore(e);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			e.setScore(score);
		}
	}
	
	private static Integer getProfessionScore(Entry entry) throws Exception {
		String entryURL = DBPEDIA_NAMESPACE + "/" + entry.getURLSubject();
		Filter[] filters = new Filter[] { Filter.LOWER_CASE,
				Filter.ACCENT,
				Filter.STEM,
				Filter.YAGO,
				Filter.UNDERSCORE};
		String[] properties = new String[] {RDF_TYPE,
				DBP_OCCUPATION,
				DBO_PROFESSION,
				DBP_PROFESSION};
		return score(entryURL, entry.getObject(), filters, properties);
	}
	
	private static Integer getNationalityScore(Entry entry) throws Exception {
		String entryURL = DBPEDIA_NAMESPACE + "/" + entry.getURLSubject();		
		
		Filter[] filters = new Filter[] { Filter.LOWER_CASE,
				Filter.ACCENT,
				Filter.STEM};
		String[] properties = new String[] {DBP_BIRTHPLACE, DBO_BIRTHPLACE, DBP_CITZENSHIP};
		Integer firstSpamScore = score(entryURL, entry.getObject(), filters, properties);
		
		if(firstSpamScore != null && firstSpamScore == 7) { // full match
			return FULL_EVIDENCE;
		}
		
		SCC entrySCC = Knowledgebase.DBpedia39.getSCC(entryURL);
		List<Property> validProperties = new ArrayList<Property>();
		try {
			validProperties.addAll(entrySCC.getProperties(DBP_BIRTHPLACE));			
		} catch (Exception e) {
		}
		try {
			validProperties.addAll(entrySCC.getProperties(DBO_BIRTHPLACE));
		} catch (Exception e) {
		}
		try {
			validProperties.addAll(entrySCC.getProperties(DBP_CITZENSHIP));
		} catch (Exception e) {
		}
		properties = new String[] {DBP_COMMON_NAME, FOAF_NAME, DBO_COUNTRY};
		for(Property p : validProperties) {
			if(p != null) {
				if(!p.getValue().isLiteral()) {
					URIObject uriObject = (URIObject) p.getValue();
					Integer score = score(uriObject.getURI(),
							entry.getObject(),
							filters,
							properties);
					if(score != null) {
						if(score == 7) { // full match
							return FULL_EVIDENCE;
						} else if(firstSpamScore == null || score > firstSpamScore) {
							firstSpamScore = score;
						}
					}
				}
			}
		}	
		return firstSpamScore;
	}
	
	private static Integer score(String entityURL,
			String object,
			Filter[] filters,
			String[] properties) throws Exception {		
		JaccardLabelStarpathSCCScorer scorer = new JaccardLabelStarpathSCCScorer();
		
		String[] labels = new String[] {"http://www.w3.org/2000/01/rdf-schema#label"};
		String[] types = new String[] { "http://www.w3.org/1999/02/22-rdf-syntax-ns#type" };
		String[] langs = new String[] { null, "en" };
		
		SCCFilter entityFilter = new SCCFilterList(
				new String[] { "http://dbpedia.org/resource"});
		
		URIPatternFilter propertyFilter = new URIPatternFilter(
				properties);
		
		SCC scc = Knowledgebase.DBpedia39.getSCC(entityURL);
		if(scc == null) {
			return null; // can't be found
		}
		List<String> entityTypes = getTypes(entityURL);
		for(String entityType : entityTypes) {
			Property p = new Property("http://www.w3.org/1999/02/22-rdf-syntax-ns#type", 
					null,
					entityType,
					new ArrayList<Literal>());
			scc.addProperty(p);
		}
		double score = scorer.compute(object,
				entityFilter,
				propertyFilter,
				filters,
				scc,
				0,
				langs,
				labels,
				types);		
		if(score >= 1) { // full match
			return FULL_EVIDENCE;
		} else if(score > 0) { // partial match
			return SOME_EVIDENCE;
		}
		return null; // don't know
	}
	
	private static List<String> getTypes(String resourceURI) throws MalformedURLException, Exception {
		List<String> types = new ArrayList<String>();
		com.hp.hpl.jena.query.ResultSet rs = KBox.query("Select ?type where {<" + resourceURI + ">"
		  + " <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>"
		  +  " ?type }", true,
		  new URL[]{new URL("http://dbpedia.org/2015_10/yago_types")});
		
		while(rs.hasNext()) {
			QuerySolution qs = rs.nextSolution();
			RDFNode type = qs.get("type");
			types.add(type.toString());
		}
		
		return types;
	}
	
	private static void print(PrintStream ps, List<Entry> entries) {
		for(Entry e : entries) {
			ps.println(e.toString());
		}
	}

	public static Entry parseLine(String line) throws LineParseException {
		String[] params = line.split("\\t");
		if(params.length >= 2) {
			Entry entry = new Entry();
			entry.setSubject(params[0]);
			entry.setObject(params[1]);
			return entry;
		}
		throw new LineParseException("Error parsing line " + line);
	}
} 
