package ch.bbv.java.rx.example;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import rx.Observable;
import rx.Observer;
import rx.observers.TestObserver;
import rx.subjects.PublishSubject;

/**
 * @author yvesgross
 * @param <T>
 *
 */
public class CombinatorsExcercises<T> {

	@Test
    public void exerciseFiltering()
    {
		TestObserver<String> observer = new TestObserver<>();
		
		Observable<String> subject = PublishSubject.create(s -> {
			s.onNext("This");
            s.onNext("is yet");
            s.onNext("another"); // length = 7
            s.onNext("way of");
            s.onNext("constructing");
            s.onNext("observables");
            s.onCompleted();});
		

        // TODO: Use observable operators to return only the terms where the length is greater than 7
        // HINT: http://reactivex.io/documentation/operators/filter.html

		//////////////////// UNRESOVED /////////////////////////////////////////
//		Observable<String> observable = Observable.empty();
		//////////////////// RESOLVED //////////////////////////////////////////
		Observable<String> observable = subject.filter(s -> s.length()>7);
		////////////////////////////////////////////////////////////////////////
		
		// verify:
		observable.subscribe(observer);
		observer.assertReceivedOnNext(Arrays.asList("constructing", "observables"));
    }

	@Test
    public void exerciseMerging() throws InterruptedException
    {
		List<String> valuesA = Arrays.asList("A", "B", "C");
		List<String> valuesB = Arrays.asList("X", "Y", "Z");

		CountDownLatch cdl = new CountDownLatch(1);
		
		Observable<String> observableA = Observable
				.interval(0L, 100L, TimeUnit.MILLISECONDS)
				.take(3)
				.map(i -> valuesA.get(i.intValue()));
		
		Observable<String> observableB = Observable
				.interval(20L, 50L, TimeUnit.MILLISECONDS)
				.take(3)
				.map(i -> valuesB.get(i.intValue()));
		
		List<String> result = new ArrayList<>();
		Observable
			.merge(observableA, observableB)
			.subscribe(
					result::add,
					e -> e.printStackTrace(),
					()-> cdl.countDown());
				
		// verify

		// wait until the observable has been completed
		cdl.await(); 
		
		// TODO: Write the expected values with the correct order into the next statement
        // HINT: http://reactivex.io/documentation/operators/merge.html	
		//////////////////// UNRESOVED /////////////////////////////////////////
//		List<String> expected = Arrays.asList("??");
		//////////////////// RESOLVED //////////////////////////////////////////
		List<String> expected = Arrays.asList("A", "X", "Y", "B", "Z", "C");
		////////////////////////////////////////////////////////////////////////
		assertEquals(expected, result);
		
    }

	@Test
	public void exerciseConcatenate() throws InterruptedException {
	
		List<String> valuesA = Arrays.asList("A", "B", "C");
		List<String> valuesB = Arrays.asList("X", "Y", "Z");

		CountDownLatch cdl = new CountDownLatch(1);
		
		Observable<String> observableA = Observable
				.interval(0L, 100L, TimeUnit.MILLISECONDS)
				.take(3)
				.map(i -> valuesA.get(i.intValue()));
		
		Observable<String> observableB = Observable
				.interval(20L, 50L, TimeUnit.MILLISECONDS)
				.take(3)
				.map(i -> valuesB.get(i.intValue()));
		
		List<String> result = new ArrayList<>();
		Observable
			.concat(observableA, observableB)
			.subscribe(
					result::add,
					e -> e.printStackTrace(),
					()-> cdl.countDown());
		
		// verify

		// wait until the observable has been completed
		cdl.await(); 
		
		// TODO: Write the expected values with the correct order into the next statement
        // HINT: http://reactivex.io/documentation/operators/merge.html	
		//////////////////// UNRESOVED /////////////////////////////////////////
//		List<String> expected = Arrays.asList("??");
		//////////////////// RESOLVED //////////////////////////////////////////
		List<String> expected = Arrays.asList("A", "B", "C", "X", "Y", "Z");
		////////////////////////////////////////////////////////////////////////
		assertEquals(expected, result);
		
	}

	@Test
	public void exerciseDebounce() throws InterruptedException {

		CountDownLatch cdl = new CountDownLatch(1);
		
		List<Integer> result = new ArrayList<>();
		Observer<Integer> testObserver = new Observer<Integer>() {

			@Override
			public void onCompleted() {
				cdl.countDown();
			}

			@Override
			public void onError(Throwable e) {
				e.printStackTrace();
			}

			@Override
			public void onNext(Integer t) {
				result.add(t);
			}
		};
				
		PublishSubject<Integer> input = PublishSubject.create();
		
		
		// TODO: ignore input elements preceeding another in less than or equal 100 ticks
		// HINT 1: http://reactivex.io/documentation/operators/debounce.html
		
		//////////////////// UNRESOVED /////////////////////////////////////////
//		Observable<Integer> testObservable = input.empty();
		//////////////////// RESOLVED //////////////////////////////////////////
		Observable<Integer> observable = input.debounce(110, TimeUnit.MILLISECONDS);
		////////////////////////////////////////////////////////////////////////

		observable.subscribe(testObserver);
		
		// start test sequence
		input.onNext(1);
		Thread.sleep(50);
		input.onNext(2);
		Thread.sleep(100);
		input.onNext(3);
		Thread.sleep(150);
		input.onNext(4);
		input.onCompleted();
				
		// wait for completion
		cdl.await();
		
		assertEquals(Arrays.asList(3,4), result);

	}

		
	@Test
	public void test1() {
		
		//////////////////// UNRESOVED /////////////////////////////////////////
		//////////////////// RESOLVED //////////////////////////////////////////
		////////////////////////////////////////////////////////////////////////
		

		// verify
		
	}
	
}
