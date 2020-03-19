package com.candy.commons.sequence.db;

import com.candy.commons.sequence.Sequence;

import java.util.Map;

public class SequenceUtil {
	
	private Map<String, Sequence> sequenceMap;
	private Sequence defaultSequence;

	public String get(String name) {
		Sequence sequence = null;
		if (this.sequenceMap != null) {
			sequence = this.sequenceMap.get(name);
		}
		if (sequence == null) {
			if (this.defaultSequence != null) {
				return this.defaultSequence.get(name);
			}
			throw new RuntimeException("sequence " + name + " undefined!");
		}
		return sequence.get(name);
	}	
	
	public void setDefaultSequence(Sequence defaultSequence) {
		this.defaultSequence = defaultSequence;
	}

	public void setSequenceMap(Map<String, Sequence> sequenceMap) {
		this.sequenceMap = sequenceMap;
	}
}