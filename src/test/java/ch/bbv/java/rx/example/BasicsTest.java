package ch.bbv.java.rx.example;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Test;

import rx.Observable;
import rx.observers.TestObserver;
import rx.observers.TestSubscriber;
import rx.schedulers.TestScheduler;
import rx.subjects.PublishSubject;

// HOW TO USE THIS WORKSHOP
// Goal: make all test pass by just implementing the missing parts indicated with TODOs
// The code you should change or add is always right below the TODOs.
// Try starting at the TODOs and only then read the rest of code. Normally it's not needed
// to know the details of the test arrangement before you know the task (sometimes even not at all).
// Try solving first without looking at the test output. Debugging should not be needed.
// -

public class BasicsTest extends RxTest {
	
	@Test
	public void exerciseObservableAndObserver() {
		
		// TODO: create an 'Observable' that 'return's an integer
		Observable<Integer> observable = Observable.empty();
		
		TestObserver<Integer> observer = new TestObserver<>();
		
		//// TODO: subscribe to the observable

		// check it
		observer.assertReceivedOnNext(Arrays.asList(42));
		observer.assertTerminalEvent();
		
	}

	
	@Test
	public void exerciseObsertvableAndAction() {
		Observable<Double> observable = Observable.create(subscriber -> subscriber.onError(new Exception()));
		final AtomicBoolean errorHappened = new AtomicBoolean(false);

        // Instead of having an observer, we can also supply
        // a method to be called when something happens.
        // TODO extend the subscription
        observable.subscribe();
        
        // check it
        assertTrue(errorHappened.get());
    }
	
	@Test
    public void exerciseFromCollection()
    {
        // Note that this should only be used
        // - for tests
        // - when an external API is reactive-only
        // as hiding the synchronous character of collections is considered bad practice
		List<String> collection = Arrays.asList("Hello", "world");

        // TODO: create an observable from the collection
		Observable<String> observable = Observable.empty();

		// verify
		TestSubscriber<String> testObserver = new TestSubscriber<>();
		observable.subscribe(testObserver);
		testObserver.assertCompleted();
		testObserver.assertNoErrors();
		testObserver.assertReceivedOnNext(collection);
    }
	
	@Test
    public void exerciseObservableToCollection()
    {
        // Leaving the monad* (the world of observables) is mostly useful
        // when testing or building non-reactive datastructures for passing
        // them on to non-reactive APIs.
		Observable<String> observable = Observable.just("FooBar").repeat(3);

        // TODO: create a collection from the observable
		Collection<String> collection = new ArrayList<>();

		// verify
		assertTrue(collection.equals(Arrays.asList("FooBar","FooBar","FooBar")));
    }

	@Test
    public void noteErrorsShouldTerminateObservables()
    {
		List<Long> testData = Arrays.asList(1000L, 2000L, 3000L);
		PublishSubject<Long> observable = PublishSubject.create();
		TestObserver<Long> testObserver = new TestObserver<>();
		observable.subscribe(testObserver);
		
		testData.stream().forEach(observable::onNext);
		observable.onError(new Exception("some error ocurred"));
		// observable refuses to emit any further elements
		observable.onNext(5000L); 
		observable.onNext(6000L);
		observable.onNext(7000L);
		observable.onCompleted();

		testObserver.assertReceivedOnNext(testData);
		assertTrue(testObserver.getOnNextEvents().equals(testData));
		assertTrue(testObserver.getOnErrorEvents().size()==1);
		assertTrue(testObserver.getOnCompletedEvents().isEmpty());

    }
	
	@Test
	public void noteColdObservables() {
		
		Observable<Long> observable = Observable.interval(1, TimeUnit.SECONDS, testScheduler);
		
		// first subscription:
		observable.take(6).subscribe(l -> log(String.format("Subscription A recieved %d", l)));
		
		testScheduler.advanceTimeBy(2001, TimeUnit.MILLISECONDS);
		
		// second subscription:
		observable.take(3).subscribe(l -> log(String.format("      Subscription B recieved %d", l)));
		
		testScheduler.advanceTimeBy(10, TimeUnit.SECONDS);

	}

	@Test
	public void noteHotObservables() {
		
		TestScheduler scheduler = new TestScheduler();
		PublishSubject<Long> observable = PublishSubject.create();
		observable.observeOn(scheduler);
		
		// first subscription:
		observable.take(6).subscribe(l -> log(String.format("Subscription A recieved %d", l)));
		
		// emitting plan
		for (long i=1; i<=20; ++i) {
			// Hot Observable: we can emit signals, no matter whether
			// there are subscriptions or not.
			observable.onNext(i);
			scheduler.advanceTimeBy( 1, TimeUnit.SECONDS);
			if(i==2) {
				// second subscription after two seconds:
				observable.take(3).subscribe(l -> log(String.format("      Subscription B recieved %d", l)));				
			}
		}
		observable.onCompleted();
		
	}
}
