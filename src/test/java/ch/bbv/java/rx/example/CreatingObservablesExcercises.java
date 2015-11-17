package ch.bbv.java.rx.example;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import rx.Observable;
import rx.observers.TestObserver;
import rx.subjects.BehaviorSubject;

public class CreatingObservablesExcercises extends RxTest {
	
	
	@Test
	public void exercise_UsingFactoryMethod() {
		
		
        // The preferred way of creating production observables is through combinators
        // (i.e. Return, Never, Interval, ...). In tests predefined sequences are predominant.
        // For more complex scenarios there is Observable.create(...)
//        Random random = new Random(7);

        // TODO: Create an infinite observable generating integers at random times but at least every 100 ticks
        //////////////////// UNRESOVED /////////////////////////////////////////
//        Observable<Long> observable = Observable.never();
        //////////////////// RESOLVED //////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////
        

        
        // check it

        // Extremely poor mans randomness test, for better variants see Knuth's TAOCP
//        var result = scheduler.Start(() => observable, 1000);
//        var timings = result.Messages.Select(m => m.Time).Distinct();
//        timings.Count().Should().BeGreaterThan(1);
		
		
        
	}

	@Test
	public void exerciseFromInterval() {
		
		Collection<Long> result = new ArrayList<>();
		
		// TODO: Create an observable emitting numbers starting from 0...
		//       every 150ms, but only for one second (i.e. ends with 5).
		//////////////////// UNRESOVED /////////////////////////////////////////
//		Observable<Losng> observable = Observable.empty();
		//////////////////// RESOLVED //////////////////////////////////////////
		Observable<Long> observable = Observable.interval(150, TimeUnit.MILLISECONDS).take(1, TimeUnit.SECONDS);
		////////////////////////////////////////////////////////////////////////
		observable.toBlocking().toIterable().forEach(result::add);
		
		// verify
		Collection<Long> expected = Arrays.asList(0L, 1L, 2L, 3L, 4L, 5L);
		assertEquals(expected, result);

	}
	
	@Test
	public void exerciseFromSubjects() {

        // Subjects are IObservable and IObserver at the same time.
        // Being the mutable variable equivalent of observables they can be used
        // to imperatively send events (e.g. for reactive properties, replacing IPropertyChanged).
        // For a detailed discussion on when subjects should be avoided
        // see http://davesexton.com/blog/post/To-Use-Subject-Or-Not-To-Use-Subject.aspx.
		

        BehaviorSubject<Integer> subject = BehaviorSubject.create();
        TestObserver<Integer> testObserver = new TestObserver<>();
        subject.subscribe(testObserver);
        
        //// TODO: send some notifications
		//////////////////// UNRESOVED /////////////////////////////////////////
		//////////////////// RESOLVED //////////////////////////////////////////
        subject.onNext(34);
        subject.onNext(29);
        subject.onNext(93);
        subject.onNext(45);
        subject.onNext(13);
        subject.onCompleted();
		////////////////////////////////////////////////////////////////////////

		// check it
        assertTrue("Error Event received!", testObserver.getOnErrorEvents().isEmpty());
        assertTrue("No Completed Event received!", testObserver.getOnCompletedEvents().size()==1);
        testObserver.assertReceivedOnNext(Arrays.asList(34, 29, 93, 45, 13));
        
	}
		
}
