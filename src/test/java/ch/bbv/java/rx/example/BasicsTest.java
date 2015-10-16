package ch.bbv.java.rx.example;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import rx.Observable;
import rx.observers.TestObserver;
import rx.subjects.PublishSubject;

/**
 * @author yvesgross
 *
 */
public class BasicsTest {
	
	private static boolean errorHappened = false;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		errorHappened=false;
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void exerciseObservableAndObserver() {
		
		// TODO: create an 'Observable' that 'return's an integer
		//////////////////// UNRESOVED /////////////////////////////////////////
//		Observable<Integer> observable = Observable.empty();
		//////////////////// RESOLVED //////////////////////////////////////////
		Observable<Integer> observable = Observable.just(new Integer(42));
		////////////////////////////////////////////////////////////////////////
		
		TestObserver<Integer> observer = new TestObserver<>();
		//// TODO: subscribe to the observable
		//////////////////// UNRESOVED /////////////////////////////////////////
		//////////////////// RESOLVED //////////////////////////////////////////
		observable.subscribe(observer);
		////////////////////////////////////////////////////////////////////////

		// checkit
		observer.assertReceivedOnNext(Arrays.asList(42));
		observer.assertTerminalEvent();
		
	}

	
	@Test
	public void exerciseObsertvableAndAction() {
		Observable<Double> observable = Observable.create(subscriber -> subscriber.onError(new Exception()));

        // Instead of having an observer, we can also supply
        // a method to be called when something happens.
        // TODO extend the subscription
    	//////////////////// UNRESOVED /////////////////////////////////////////
//        observable.subscribe();
    	//////////////////// RESOLVED //////////////////////////////////////////
        observable.subscribe(
        		s -> System.out.println(s),
        		e -> errorHappened = true);
    	////////////////////////////////////////////////////////////////////////
        
        // checkit
        assertTrue(errorHappened);
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
    	//////////////////// UNRESOVED /////////////////////////////////////////
//		Observable<String> observable = Observable.empty();
    	//////////////////// RESOLVED //////////////////////////////////////////
		Observable<String> observable = Observable.from(collection);
    	////////////////////////////////////////////////////////////////////////

		// verify
		TestObserver<String> testObserver = new TestObserver<>();
		observable.subscribe(testObserver);
		testObserver.assertReceivedOnNext(collection);
		testObserver.assertTerminalEvent();
    }
	
	@Test
    public void exerciseObservableToCollection()
    {
        // Leaving the monad* (the world of observables) is mostly useful
        // when testing or building non-reactive datastructures for passing
        // them on to non-reactive APIs.
		Observable<String> observable = Observable.just("FooBar").repeat(3);

        //// TODO: create a collection from the observable
		Collection<String> collection = new ArrayList<>();
		//////////////////// UNRESOVED /////////////////////////////////////////
		//////////////////// RESOLVED //////////////////////////////////////////
		observable.toBlocking().toIterable().forEach(collection::add);
		////////////////////////////////////////////////////////////////////////

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

		testObserver.assertReceivedOnNext(testData);
		assertTrue(testObserver.getOnNextEvents().equals(testData));
		assertTrue(testObserver.getOnErrorEvents().size()==1);
		assertTrue(testObserver.getOnCompletedEvents().isEmpty());

    }
	
}
