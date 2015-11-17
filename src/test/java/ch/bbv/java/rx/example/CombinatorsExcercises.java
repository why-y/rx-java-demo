package ch.bbv.java.rx.example;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import rx.Observable;
import rx.observers.TestObserver;
import rx.observers.TestSubscriber;
import rx.subjects.PublishSubject;

/**
 * @author yvesgross
 *
 */
public class CombinatorsExcercises extends RxTest{

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
    public void exerciseMerging()
    {
		List<String> valuesA = Arrays.asList("A", "B", "C");
		List<String> valuesB = Arrays.asList("X", "Y", "Z");

		Observable<String> observableA = Observable
				.interval(0L, 100L, TimeUnit.MILLISECONDS, testScheduler)
				.take(3)
				.map(i -> valuesA.get(i.intValue()));
		
		Observable<String> observableB = Observable
				.interval(20L, 50L, TimeUnit.MILLISECONDS, testScheduler)
				.take(3)
				.map(i -> valuesB.get(i.intValue()));
		
		List<String> result = new ArrayList<>();
		Observable
			.merge(observableA, observableB)
			.subscribe(result::add);
				
		// verify

		// let the time elapse until completion
		testScheduler.advanceTimeBy(201, TimeUnit.MILLISECONDS);
		
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
	public void exerciseConcatenate() {

		List<String> valuesA = Arrays.asList("A", "B", "C");
		List<String> valuesB = Arrays.asList("X", "Y", "Z");

		Observable<String> observableA = Observable
				.interval(0L, 100L, TimeUnit.MILLISECONDS, testScheduler)
				.take(3)
				.map(i -> valuesA.get(i.intValue()));
		
		Observable<String> observableB = Observable
				.interval(20L, 50L, TimeUnit.MILLISECONDS, testScheduler)
				.take(3)
				.map(i -> valuesB.get(i.intValue()));
		
		List<String> result = new ArrayList<>();
		Observable
			.concat(observableA, observableB)
			.subscribe(result::add);
		
		// verify
		
		// let the time elapse until completion
		testScheduler.advanceTimeBy(321, TimeUnit.MILLISECONDS);

		
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
	public void exerciseDebounce() {
		
		PublishSubject<Integer> input = PublishSubject.create();
		
		// TODO: ignore input elements preceeding another in less than or equal 100 ticks
		// HINT 1: http://reactivex.io/documentation/operators/debounce.html
		
		//////////////////// UNRESOVED /////////////////////////////////////////
//		Observable<Integer> testObservable = input.empty();
		//////////////////// RESOLVED //////////////////////////////////////////
		Observable<Integer> observable = input.debounce(110, TimeUnit.MILLISECONDS, testScheduler);
		////////////////////////////////////////////////////////////////////////

		List<Integer> result = new ArrayList<>();
		observable.subscribe(result::add);
		
		// start test sequence
		input.onNext(1);
		testScheduler.advanceTimeBy(50, TimeUnit.MILLISECONDS);
		input.onNext(2);
		testScheduler.advanceTimeBy(100, TimeUnit.MILLISECONDS);
		input.onNext(3);
		testScheduler.advanceTimeBy(150, TimeUnit.MILLISECONDS);
		input.onNext(4);
		input.onCompleted();
						
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
		TestSubscriber<Integer> tester = new TestSubscriber<>();
		observable.subscribe(tester);
		tester.assertCompleted();
		tester.assertNoErrors();
		tester.assertReceivedOnNext(expected);
		
	}
		
	@Test
	public void exerciseBatching() {
		
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
		Observable<List<String>> observable = input.buffer(120, TimeUnit.MILLISECONDS, 2, testScheduler).filter(l -> !l.isEmpty());
		////////////////////////////////////////////////////////////////////////
		
		// verify
		List<List<String>> result = new ArrayList<>();
		observable.subscribe(result::add);
		
		// start test sequence
		testScheduler.advanceTimeBy(300, TimeUnit.MILLISECONDS);
		input.onNext("Alice");
		testScheduler.advanceTimeBy(130, TimeUnit.MILLISECONDS);
		input.onNext("Bob");
		testScheduler.advanceTimeBy(51, TimeUnit.MILLISECONDS);
		input.onNext("Carol");
		testScheduler.advanceTimeBy(20, TimeUnit.MILLISECONDS);
		input.onNext("Dave");
		testScheduler.advanceTimeBy(4, TimeUnit.MILLISECONDS);
		input.onNext("Eric");
		input.onNext("Floyd");
		input.onCompleted();
		
		assertEquals(Arrays.asList(
				Arrays.asList("Alice"),
				Arrays.asList("Bob"),
				Arrays.asList("Carol", "Dave"),
				Arrays.asList("Eric", "Floyd")), 
				result);
		
	}
		
	@Test
	public void exerciseMovingAverage() {

		
		// 24 measuring points, one for each hour of the day: 
		List<Integer> temparaturArray = Arrays.asList(8, 7, 6, 6, 7, 7, 8, 10, 13, 16, 10, 20, 23, 26, 30, 29, 27, 45, 24, 23, 20, 18, 15, 11);
		Observable<Integer> temparaturSequence = Observable
				.interval(0, 1L, TimeUnit.HOURS, testScheduler)
				.take(temparaturArray.size())
				.map(i -> temparaturArray.get(i.intValue()));
		
		// TODO: calculate the moving average (SMA) of the given temperature sequence (each hour of a single day).
		//       use the previous three values for each data point.
        // HINT: use a suitable overload of the same method used in "Batching"
        // and then calculate the average of each batch using LINQ
        // HINT: don't forget to pass the scheduler

		//////////////////// UNRESOVED /////////////////////////////////////////
//		Observable<Double> movingAverage = Observable.empty();
		//////////////////// RESOLVED //////////////////////////////////////////
		Observable<Double> movingAverage = temparaturSequence.buffer(3,1)
				.map(seq -> new Double(seq.stream().mapToInt(Integer::intValue).average().getAsDouble()));
		////////////////////////////////////////////////////////////////////////

		// verify
		TestSubscriber<Double> testSubscriber = new TestSubscriber<>();
		movingAverage.subscribe(testSubscriber);

		// let the time elapse until completion
		testScheduler.advanceTimeBy(1, TimeUnit.DAYS);
		
		log(testSubscriber.getOnNextEvents());
		
		testSubscriber.assertNoErrors();
		testSubscriber.assertCompleted();
		
		// expected values:
		List<Double> expected = Arrays.asList(
				7.0, 6.333333333333333, 6.333333333333333, 6.666666666666667, 7.333333333333333, 
				8.333333333333334, 10.333333333333334, 13.0, 13.0, 15.333333333333334, 
				17.666666666666668, 23.0, 26.333333333333332, 28.333333333333332, 
				28.666666666666668, 33.666666666666664, 32.0, 30.666666666666668, 
				22.333333333333332, 20.333333333333332, 17.666666666666668, 14.666666666666666, 
				// the last two values have limited input, i.e.
				// (15+11)/2 and (11)/1
				13.0, 11.0);
		testSubscriber.assertReceivedOnNext(expected);
		
	}
	
	@Test
	public void exerciseInterruptible() {

		// For a news site offering articles about trending topics we want to keep reading about
        // a topic until a new trend appears, when we want to hear about the latest news there.
        // For the sake of simplicity, we assume fixed rates for topics and articles:
		
		List<String> articlesTopicA = Arrays.asList("Article A-1","Article A-2","Article A-3");
		List<String> articlesTopicB = Arrays.asList("Article B-1","Article B-2","Article B-3");
		List<String> articlesTopicC = Arrays.asList("Article C-1","Article C-2","Article C-3");
		
		List<Observable<String>> topicObservables = Arrays.asList(
				Observable.interval(0, 80L, TimeUnit.MILLISECONDS, testScheduler)
				.take(3)
				.map(i -> articlesTopicA.get(i.intValue())),
				Observable.interval(50, 60L, TimeUnit.MILLISECONDS, testScheduler)
				.take(3)
				.map(i -> articlesTopicB.get(i.intValue())),
				Observable.interval(0, 40L, TimeUnit.MILLISECONDS, testScheduler)
				.take(3)
				.map(i -> articlesTopicC.get(i.intValue())));
		
		Observable<Observable<String>> topics = Observable.interval(0, 100L, TimeUnit.MILLISECONDS, testScheduler)
				.take(3)
				.map(i -> topicObservables.get(i.intValue()));
		
//		 t[ms]:   0        100       200       300
//		topics:   A---------B---------C---------| 
//		     A:   1-------2-|-----3---|---------|
//		     B:   |---------|----1----|2-----3--|
//		     C:   |---------|---------1---2---3-|
//		Result:  A-1     A-2    B-1  C-1 C-2 C-3
				
		// TODO: always switch to the latest topic
		// HINT: http://reactivex.io/documentation/operators/switch.html
		
		//////////////////// UNRESOVED /////////////////////////////////////////
//		Observable<String> latestInteresting = Observable.empty();
		//////////////////// RESOLVED //////////////////////////////////////////
		Observable<String> latestInteresting = Observable.switchOnNext(topics);
		////////////////////////////////////////////////////////////////////////

		// verify
		TestSubscriber<String> testSubscriber = new TestSubscriber<>();
		latestInteresting
			.doOnNext(article -> log(String.format("    doOnNext(): %s  Time: %d", article, testScheduler.now())))
			.subscribe(testSubscriber);
		
		// let the time elapse until completion
		testScheduler.advanceTimeBy(300, TimeUnit.MILLISECONDS);
		
		testSubscriber.assertCompleted();
		testSubscriber.assertNoErrors();
		List<String> expectedArticles = Arrays.asList(
				"Article A-1","Article A-2", 
				"Article B-1", 
				"Article C-1", "Article C-2", "Article C-3");
		testSubscriber.assertReceivedOnNext(expectedArticles);
		
	}
		
}
