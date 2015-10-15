package ch.bbv.java.rx.example;

import java.util.Arrays;
import java.util.Collection;

import rx.Observable;
import rx.schedulers.Schedulers;

public class SimpleRx {
	
	private static final Long INTERVAL = 300L;

	private static final Collection<String> DATA = Arrays.asList((new StringBuilder())
			.append("In computing, reactive programming is a programming paradigm oriented ")
			.append("around data flows and the propagation of change. This means that it ")
			.append("should be possible to express static or dynamic data flows with ease ")
			.append("in the programming languages used, and that the underlying execution ")
			.append("model will automatically propagate changes through the data flow.")
			.toString().split(" "));

	public Collection<String> getElements() {
		return DATA;
	}
	
	public Observable<String> getElementsRx() {
		return Observable.from(DATA);		
	}
	
	public Observable<String> getElementsRxSlow () {
		return Observable.from(DATA).map(s -> someExpensiveStringOperation(s));
	}
	
	private String toUpperCase(final String in) {
		return in.toUpperCase();
	}
	
	private Observable<String> toUpperCase(final Observable<String> in) {
		return in.map(s -> s.toUpperCase());
	}
	
	private String someExpensiveStringOperation(final String in) {
		try {
			Thread.sleep(INTERVAL);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return String.format("converted in thread:%s(%d)   Value:(%s)", Thread.currentThread().getName(), Thread.currentThread().getId(), in);
	}
	
}
