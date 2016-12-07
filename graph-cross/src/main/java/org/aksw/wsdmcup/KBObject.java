package org.aksw.wsdmcup;

import java.util.HashMap;
import java.util.Set;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class KBObject {
	
	private String subject;
	private HashMap<String, Integer> objects;
	private Integer max;
	
	public KBObject(String subject, HashMap<String, Integer> objects) {
		this.subject = subject;
		this.objects = objects;
		
//		Integer max = Integer.MIN_VALUE;
//		for(Integer i : objects.values())
//			if(i > max)
//				max = i;
//		this.max = max;
	}
	
	public boolean isEmpty() {
		return objects.isEmpty();
	}
	
	public Integer getScore(String object) {
		return objects.get(object);
	}
	
	public Set<String> getObjects() {
		return objects.keySet();
	}

	public String getSubject() {
		return subject;
	}

	public HashMap<String, Integer> getObjectsMap() {
		return objects;
	}

	public Integer getMax() {
		return max;
	}
	
}