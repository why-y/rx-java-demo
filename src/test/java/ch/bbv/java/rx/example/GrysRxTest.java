/**
 * 
 */
package ch.bbv.java.rx.example;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import rx.Observable;
import rx.Observer;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;

/**
 * @author yvesgross
 *
 */
@Ignore
public class GrysRxTest extends RxTest {
	
	private class Pair<X, Y> {
		
		private X first;
		private Y second;

		public Pair(X first, Y second) {
			this.first = first;
			this.second = second;
		}

		public X first() {
			return this.first;
		}

		public Y second() {
			return this.second;
		}

		@Override
		public String toString() {
			return "Pair [x=" + first + ", y=" + second + "]";
		}

	}
	
    private static final Logger logger = Logger.getLogger(GrysRxTest.class.getName());
    private static final SimpleDateFormat SDF = new SimpleDateFormat("mm:ss.SSS");

	private SimpleRx toTest;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		toTest = new SimpleRx();
	}

	@Test
	public void testRange() {
		Observable.range(1001, 50)
			.forEach(i -> System.out.println(String.format(
					"Thread: %s(%d): RangeNo: %d", 
					Thread.currentThread().getName(), 
					Thread.currentThread().getId(), i)));
	}
	
	@Test
	public void testInterval() {
		Observable.interval(500l, TimeUnit.MILLISECONDS)
			.forEach(l -> System.out.println(String.format("Interval No: %d", l)));
		
		try {
			Thread.sleep(3000L);
		} catch (InterruptedException e) {
		}

	}
	
	@Test
	public void testObserverToNormalData() {
		
		Iterable<String> it = toTest.getElementsRxSlow().subscribeOn(Schedulers.computation()).toBlocking().toIterable();
		
		Instant start = Instant.now();
		System.out.println("Start observing at " + start);
		
		Collection<String> result = new ArrayList<>();
		it.forEach(result::add);
		
		Instant stop = Instant.now();
		
		result.stream().forEach(System.out::println);
		System.out.println(String.format("Observing Done in %s. Show results:", Duration.between(start, stop)));
	}
	
	@Test
	public void testReadSlowDataSource() throws IOException {
		final long start = System.currentTimeMillis();		
		toTest.getElementsRxSlow().subscribeOn(Schedulers.io()).subscribe(
				s -> {
					System.out.println(String.format("String: \"%s\" emmitted at %s", s, SDF.format(new Date())));
				},
				e -> e.printStackTrace(), 
				() -> {
					final long stop = System.currentTimeMillis();
					System.out.println(String.format("Completed in %s", Duration.of(stop-start, ChronoUnit.MILLIS)));
				});
		System.out.println("Everything linked. Observation started.");

//		int keyVal = System.in.read();// waits until something is entered (keyboard)
//		BufferedReader stdInReader = new BufferedReader(new InputStreamReader(System.in));
//		for (String input = stdInReader.readLine();  !input.equalsIgnoreCase("exit"); input=stdInReader.readLine()) {
//			System.out.println("Input: ");
//		}
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
//		subscription.unsubscribe();
		
//		System.out.println(String.format("TERMINATED (with key value: %d)", keyVal));
		System.out.println(String.format("TERMINATED"));
	}
	
	/**
	 * Test method for {@link ch.bbv.java.rx.example.SimpleRx#getElements()}.
	 */
	@Test
	public void testGetElements() {
		toTest.getElements().forEach(System.out::println);
	}

	/**
	 * Test method for {@link ch.bbv.java.rx.example.SimpleRx#getElementsRx()}.
	 */
	@Test
	public void testGetElementsRx() {
//		toTest.getElementsRx().forEach(System.out::println);
		
		toTest.getElementsRx().subscribe(new Observer<String>() {

			@Override
			public void onNext(String s) {
				logger.info(String.format("onNext() delivered: %s", s));
			}
			
			@Override
			public void onCompleted() {
				logger.info("Completed");
			}

			@Override
			public void onError(Throwable e) {
				logger.log(Level.SEVERE, "Some Error occurred", e);;
			}

		});
		
		toTest.getElementsRx().subscribeOn(Schedulers.io());
		
		// or:
		toTest.getElementsRx().forEach(System.out::println);
		
	}
	
	@Test
	public void testZip() {
		Observable<Integer> numbers = Observable.just(100, 200, 300, 400, 500);
		Observable<String> words = Observable.from(Arrays.asList("Hello", "this", "is", "some", "more", "test"));
		
		numbers.zipWith(words, (n, w) -> String.format("Zipped: %d : %s", n, w))
			.subscribe(System.out::println);
		
	}
	
	@Test
	public void testSwitch() {
		Observable<String> words = Observable.just("Hello", "this", "is", "some", "text");
		Observable<String> moreWords = Observable.just("and", "here", "is", "even", "more", "words");
		
		Observable<Observable<String>> obsObs = Observable.just(words, moreWords);
		
		Observable.switchOnNext(obsObs)
			.subscribe(System.out::println);
		
	}
	
	@Test
	public void testGetABC() {
		long start = System.currentTimeMillis();
		System.out.println("--\\ testGetABC()");
		CountDownLatch cdl = new CountDownLatch(1);
		toTest.getABC()
			.onBackpressureBuffer()
			.subscribeOn(Schedulers.computation())
			.observeOn(Schedulers.immediate())
			.subscribe(
				s -> System.out.println(String.format("getABC() returned: %s", s)),
				e -> e.printStackTrace(),
				() -> {
					System.out.println("getABC() completed!"); 
					cdl.countDown();});
		try {
			cdl.await();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		long duration = System.currentTimeMillis()-start;
		System.out.println(String.format("--/ testGetABC()  duration: %s(%d)", SDF.format(new Date(duration)), duration));
	}

	@Test
	public void testFlatMap() {
		TestSubscriber<Integer> tester = new TestSubscriber<>();
		Observable.range(1, 4)
			.map(i -> i*100)
			.flatMap(i -> Observable.range(i, 3))
			.subscribe(tester);
		tester.assertCompleted();
		tester.assertNoErrors();
		tester.assertReceivedOnNext(Arrays.asList(100, 101, 102, 200, 201, 202, 300, 301, 302, 400, 401, 402));
	}
	
	@Test 
	public void testSideEffects() {
		TestSubscriber<String> tester = new TestSubscriber<>();
		Observable<String> wordsStream = Observable.just("It", "is", "important", "to", "avoid", "side", "effects");
		
		AtomicInteger counter = new AtomicInteger(0);
		
		Observable<String> letterCounts = wordsStream.map(word -> {
			counter.set(counter.get() + word.length());
			return String.format("%s(%d)", word, word.length());
		});
		
		letterCounts.subscribe(tester);
		checkLetterCounter(tester, counter.get());
		
		TestSubscriber<String> tester2 = new TestSubscriber<>();
		letterCounts.subscribe(tester2);
		checkLetterCounter(tester2, counter.get()/2);
	}
	
	@Test 
	public void testAvoidSideEffects() {
		TestSubscriber<Pair<String, Integer>> tester = new TestSubscriber<>();
		Observable<String> wordsStream = Observable.just("It", "is", "important", "to", "avoid", "side", "effects");
		Observable<Pair<String, Integer>> letterCounts = wordsStream
				.scan(new Pair<>("", 0), (prev, word) -> new Pair<>(word, word.length()+prev.second))
				.skip(1) // skip the initial value
				.doOnNext(System.out::println);
		
		letterCounts.subscribe(tester);
		checkLetterCounter(tester);
		
		TestSubscriber<Pair<String, Integer>> tester2 = new TestSubscriber<>();
		letterCounts.subscribe(tester2);
		checkLetterCounter(tester2);
	}
	
	@Test 
	public void testAvoidSideEffects1() {
		Observable<String> wordsStream = Observable.just("It", "is", "important", "to", "avoid", "side", "effects");
		
		wordsStream
			.reduce(0, (prev, word) -> prev+word.length()).last()
			.subscribe(System.out::println);
	}
	
	private void checkLetterCounter(final TestSubscriber<Pair<String, Integer>> subscriber) {
		subscriber.assertCompleted();
		subscriber.assertNoErrors();
		List<Pair<String, Integer>> result = subscriber.getOnNextEvents();
		System.out.println(result);
		assertEquals(31, result.get(result.size()-1).second().intValue());		
	}
	
	private void checkLetterCounter(final TestSubscriber<String> subscriber, int letters) {
		subscriber.assertCompleted();
		subscriber.assertNoErrors();
		System.out.println(subscriber.getOnNextEvents());
		assertEquals(31, letters);
	}
}

