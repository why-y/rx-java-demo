package ch.bbv.java.rx.example;

import org.junit.Test;

import rx.Observable;
import rx.observers.TestSubscriber;

/**
 * @author yvesgross
 *
 */
public class Interfacing extends RxTest {
	
	@Test
	public void noteObservablesDoNotSubscribeUnnecessarily() {
		
		Observable.create(subscriber -> {
			subscriber.onNext("Message A");
			subscriber.onNext("Message B");
			subscriber.onNext("Message C");
			subscriber.onCompleted();
		});
		
		TestSubscriber<String> subscriber = new TestSubscriber<>();
//		observable.subscribe(subscriber);
		
		// check it
		subscriber.assertCompleted();
		subscriber.assertNoErrors();
		subscriber.assertNoValues();
		
	}

	@Test
	public void exerciseFromTasks() {
		
		// check it
		
	}

	@Test
	public void exercise() {
		
		// check it
		
	}

}
