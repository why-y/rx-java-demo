package ch.bbv.java.rx.example;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

/**
 * @author yvesgross
 *
 */
public class MiscTest {
	
	@Test
	public void exerciseObservablesAreCollectionlike() {
		
		final double precision = 0.01;
		
		// We want to approximate the largest value X, 
		// so that 0 < X < Math.PI and cos(X) > 0		
		List<Double> halfUnitCircle = new ArrayList<>();
		for(double d=0.0; d<=Math.PI; d+=precision) {	
			halfUnitCircle.add(new Double(d));
		}
		Observable<Double> observable = Observable.from(halfUnitCircle);
		
		// TODO: write an observable filtering for x where Math.Cos(x) > 0
		//////////////////// UNRESOVED /////////////////////////////////////////
//		observable = observable;
		//////////////////// RESOLVED //////////////////////////////////////////
		observable = observable.filter(d -> Math.cos(d) > 0);
		////////////////////////////////////////////////////////////////////////
		
		// TODO: get the last value
		//////////////////// UNRESOVED /////////////////////////////////////////
//		observable = observable;
		//////////////////// RESOLVED //////////////////////////////////////////
		observable = observable.last();
		////////////////////////////////////////////////////////////////////////
		
		// checkit
		TestSubscriber<Double> subscriber = new TestSubscriber<>();
		observable.subscribe(subscriber);

		subscriber.assertCompleted();
		subscriber.assertNoErrors();
		
		System.out.println(String.format("Result: %s", subscriber.getOnNextEvents()));
		
		subscriber.assertValueCount(1);
		
		double lastBeforeZero = subscriber.getOnNextEvents().get(0);
		double diffToPiHalf = Math.abs(lastBeforeZero-Math.PI/2);
		assertTrue("Result is not close to PI/2!", diffToPiHalf < precision);
		
	}

	
	@Test
	public void exerciseObservablesAndSchedulers() {
		final String OBSERVABLE = "OBSERVABLE";
		final String OBSERVER = "OBSERVER";
		
        Observable<Integer> observable = Observable.just(1983);
        
        final Map<String, Long> threads = new HashMap<>();
        threads.put(OBSERVABLE, new Long(Thread.currentThread().getId()));
        

        // TODO: find a way to schedule observations on a different thread
        // HINT: might also be done using an overload when creating the observable
		//////////////////// UNRESOVED /////////////////////////////////////////
//        observable = observable;
		//////////////////// RESOLVED //////////////////////////////////////////
        observable = observable.observeOn(Schedulers.computation());
		////////////////////////////////////////////////////////////////////////
        
        // checkit
        
        observable.subscribe(val -> threads.put(OBSERVER, new Long(Thread.currentThread().getId())));

        try {
			Thread.sleep(10);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
        
        System.out.println(String.format("ObservableThreadId: %d   ObserverThreadId: %d", threads.get(OBSERVABLE), threads.get(OBSERVER)));
        assertNotEquals(threads.get(OBSERVABLE), threads.get(OBSERVER));
        
    }
	
	@Test
	public void noteTestingObservables() {
		
        // Testing Rx components is simple in principle (as you've seen) but can be complex for
        // more advanced use cases, including threading, asynchronism, timing, schedulers and so forth.
        // Here, we'll only have a glimpse of how this might be done... Basically, we have a scheduler
        // running arbitrarily fast by always setting its internal clock to the next event.
        // This allows for almost immediate occurrence of future events and replaying of history.
		
		LocalDate birthDay = LocalDate.of(1950, 10, 23);
		LocalDate weddingDay = LocalDate.of(1980, 6, 6);
		
		final Map<Month, String> presents = new HashMap<>();
		
		// Concurrency is only introduced using schedulers. All methods needing an IScheduler also have
		// overrides using the default scheduler. For testing, we need to provide a custom one.
		// TODO: pass 'scheduler' at the right place
		
		//////////////////// UNRESOVED /////////////////////////////////////////
		//////////////////// RESOLVED //////////////////////////////////////////
		TestScheduler scheduler = new TestScheduler();
		////////////////////////////////////////////////////////////////////////
		
		Observable.interval(1, TimeUnit.SECONDS, scheduler)
			.map(i -> Month.of(i.intValue()+1))
			.limit(12)
			.doOnNext(m -> System.out.println(m.toString()))
//			.observeOn(Schedulers.io())
			.subscribe(m -> {
				if (m.equals(birthDay.getMonth())){
					presents.put(m, "New Bike");
				}
				if (m.equals(weddingDay.getMonth())) {
					presents.put(m, "Breakfast at tiffany's");
				}
			});
		
		
		
		// check it
		
		// wait for completion ...
		scheduler.advanceTimeBy(8, TimeUnit.SECONDS);
		Map<Month, String> expectedPresens = new HashMap<>();
		expectedPresens.put(Month.JUNE, "Breakfast at tiffany's");
		assertEquals(expectedPresens, presents);
		
		scheduler.advanceTimeBy(4, TimeUnit.SECONDS);
		
		// .. takes forever
//		try {
//			Thread.sleep(Month.values().length * 1000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		System.out.println(presents);
		expectedPresens = new HashMap<>();
		expectedPresens.put(Month.JUNE, "Breakfast at tiffany's");
		expectedPresens.put(Month.OCTOBER, "New Bike");
		assertEquals(expectedPresens, presents);
		
	}	
	
}

