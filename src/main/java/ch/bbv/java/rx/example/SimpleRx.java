package ch.bbv.java.rx.example;

import java.util.Arrays;
import java.util.Collection;

import rx.Observable;

public class SimpleRx {

	private static final Collection<String> data = Arrays.asList((new StringBuilder())
			.append("In computing, reactive programming is a programming paradigm oriented ")
			.append("around data flows and the propagation of change. This means that it ")
			.append("should be possible to express static or dynamic data flows with ease ")
			.append("in the programming languages used, and that the underlying execution ")
			.append("model will automatically propagate changes through the data flow.")
			.toString().split(" "));

	public Collection<String> getElements() {
		return data;
	}
	
	public Observable<String> getElementsRx() {
		return Observable.from(data);		
	}
	
}
