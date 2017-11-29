package au.ltl.domain;

import java.util.Vector;

public class Trace {
	
	private String id; // Unique ID of the trace for internal use (e.g., Trace#1, ..., Trace#N).
					   // --- This value is STABLE after the transition between the TracesPerspective panel and the ConstraintsPerspective panel.
	
	private Vector<String> trace_alphabet_vector; // Alphabet of the activities of the trace (e.g., a,b,c,d, ... ecc.).
	
	private Vector<String> original_trace_content_vector; // Original and ordered content of the trace (e.g., a,b,a,c,d,a, ... ecc.).
	
	private Vector<String> original_transaction_id; // original id of the task in the BPMN model
	
	public Trace(String trace_id) {		
		id = trace_id;
		trace_alphabet_vector = new Vector<String>();
		original_trace_content_vector = new Vector<String>();
		original_transaction_id = new Vector<String>();
	}
	
	public Vector<String> getOriginal_transaction_id() {
		return original_transaction_id;
	}

	public void setOriginal_transaction_id(Vector<String> original_transaction_id) {
		this.original_transaction_id = original_transaction_id;
	}

	public String getTraceID() {
		return id;
	}
	
	public void setTraceID(String trace_ID) {
		id = trace_ID;
	}
	
	public Vector<String> getTraceAlphabet_vector() {
		return trace_alphabet_vector;
	}

	public void setTraceAlphabet_vector(Vector<String> tr_alphabet_vector) {
		this.trace_alphabet_vector = tr_alphabet_vector;
	}	
	
	public Vector<String> getOriginalTraceContent_vector() {
		return original_trace_content_vector;
	}



	public void setOriginalTraceContent_vector(Vector<String> trace_content) {
		original_trace_content_vector = trace_content;
	}

	public String getTraceNumber() {
		
		String[] split = this.getTraceID().split("#");
		return split[1];
	}

}
