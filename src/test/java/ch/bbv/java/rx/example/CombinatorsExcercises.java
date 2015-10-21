package ch.bbv.java.rx.example;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import rx.Observable;
import rx.Observer;
import rx.observers.TestObserver;
import rx.subjects.PublishSubject;

/**
 * @author yvesgross
 *
 */
public class CombinatorsExcercises {

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

				
		PublishSubject<Integer> input = PublishSubject.create();
		
		// TODO: ignore input elements preceeding another in less than or equal 100 ticks
		// HINT 1: http://reactivex.io/documentation/operators/debounce.html
		
		//////////////////// UNRESOVED /////////////////////////////////////////
//		Observable<Integer> testObservable = input.empty();
		//////////////////// RESOLVED //////////////////////////////////////////
		Observable<Integer> observable = input.debounce(110, TimeUnit.MILLISECONDS);
		////////////////////////////////////////////////////////////////////////

		CountDownLatch cdl = new CountDownLatch(1);
		List<Integer> result = new ArrayList<>();
		observable.subscribe(result::add, 
				e -> e.printStackTrace(),
				() -> cdl.countDown());
		
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
	public void exerciseChangesOnly() {
		
		List<Integer> input = Arrays.asList(20, 20, 21, 22, 22, 21, 24, 24, 24, 23);
		
        // As with records from a database, we might only be interested in distinct notifications.
        // For reactive streams, having strictly distinct values implies waiting for completion and
        // caching all intermediate results. A more common use case is to only let through values that
        // changed relatively to the previous one.
        // TODO: only return changed temperature values (includes the first, new one)
        // HINT: http://reactivex.io/documentation/operators/distinct.html (almost)
		
		//////////////////// UNRESOVED /////////////////////////////////////////
//		Observable<Integer> observable = Observable.empty();
		//////////////////// RESOLVED //////////////////////////////////////////
		Observable<Integer> observable = Observable.from(input).distinctUntilChanged();
		////////////////////////////////////////////////////////////////////////
		

		// verify
		List<Integer> expected = Arrays.asList(20, 21, 22, 21, 24, 23);
		TestObserver<Integer> testObserver = new TestObserver<>();
		observable.subscribe(testObserver);
		testObserver.getOnErrorEvents().isEmpty();
		testObserver.assertTerminalEvent();
		testObserver.assertReceivedOnNext(expected);
		
	}

	
	
	@Test
	public void exerciseBatching() throws InterruptedException {
		
		PublishSubject<String> input = PublishSubject.create();
		
        // - The cable car leaves: 
        //   a) when there are 2 persons in the car
        //   b) when 120 ticks passed
        // - The cable car should only leave, when tourists are present
        // TODO: Split the tourists into suitable batches
        // HINT 1: http://reactivex.io/documentation/operators/buffer.html (suitable overload)
        // HINT 2: filtering
		
		//////////////////// UNRESOVED /////////////////////////////////////////
//		Observable<List<String>> observable = input.empty();
		//////////////////// RESOLVED //////////////////////////////////////////
		Observable<List<String>> observable = input.buffer(120, TimeUnit.MILLISECONDS, 2).filter(l -> !l.isEmpty());
		////////////////////////////////////////////////////////////////////////
		
		CountDownLatch cdl = new CountDownLatch(1);
		List<List<String>> result = new ArrayList<>();
		observable.subscribe(
				result::add,
				e -> e.printStackTrace(),
				() -> cdl.countDown());
		
		// start test sequence
		Thread.sleep(300);
		input.onNext("Alice");
		Thread.sleep(130);
		input.onNext("Bob");
		Thread.sleep(51);
		input.onNext("Carol");
		Thread.sleep(20);
		input.onNext("Dave");
		Thread.sleep(4);
		input.onNext("Eric");
		input.onNext("Floyd");
		input.onCompleted();
		
		// wait for completion
		cdl.await();
		
		assertEquals(Arrays.asList(
				Arrays.asList("Alice"),
				Arrays.asList("Bob"),
				Arrays.asList("Carol", "Dave"),
				Arrays.asList("Eric", "Floyd")), 
				result);
		
	}
		
	@Test
	public void exerciseMovingAverage() {

		// TODO: calculate the moving (every second) average (over one minute) of the temperature readings.
        // HINT: use a suitable overload of the same method used in "Batching"
        // and then calculate the average of each batch using LINQ
        // HINT: don't forget to pass the scheduler

		//////////// ?????????????????????????????
		//////////////////// UNRESOVED /////////////////////////////////////////
		//////////////////// RESOLVED //////////////////////////////////////////
		////////////////////////////////////////////////////////////////////////

		// verify
		
	}
	
	@Test
	public void exerciseInterruptible() {
        
		// For a news site offering articles about trending topics we want to keep reading about
        // a topic until a new trend appears, when we want to hear about the latest news there.
        // For the sake of simplicity, we assume fixed rates for topics and articles:

		Map<String, List<String>> articlesInTopic = new HashMap<>();
		articlesInTopic.put("Topic A", Arrays.asList("Article 000","Article 002","Article 003"));
		articlesInTopic.put("Topic B", Arrays.asList("Article 100","Article 102","Article 103"));
		articlesInTopic.put("Topic C", Arrays.asList("Article 400","Article 402","Article 403"));
		
//		articlesInTopics = Observable.interval(100, TimeUnit.MILLISECONDS)
		
		
		
        // TODO: always switch to the latest topic
        // HINT: http://reactivex.io/documentation/operators/switch.html

		
		
		//////////////////// UNRESOVED /////////////////////////////////////////
		//////////////////// RESOLVED //////////////////////////////////////////
		////////////////////////////////////////////////////////////////////////

		// verify
		
	}
	
}
