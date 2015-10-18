/**
 * 
 */
package ch.bbv.java.rx.example;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.schedulers.Schedulers;

/**
 * @author yvesgross
 *
 */
public class GrysRxTest {
	
    private static final Logger logger = Logger.getLogger(GrysRxTest.class.getName());
    private static final SimpleDateFormat SDF = new SimpleDateFormat("mm:ss.SSS");

	private SimpleRx toTest;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		toTest = new SimpleRx();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
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
		Subscription subscription = toTest.getElementsRxSlow().subscribeOn(Schedulers.io()).subscribe(
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
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
//		subscription.unsubscribe();
		
//		System.out.println(String.format("TERMINATED (with key value: %d)", keyVal));
		System.out.println(String.format("TERMINATED"));
	}
	
	
	@Test
	public void dummyTest() {
		
		Observable.just("Some Text").subscribe(
				s -> System.out.println("onNext() returned: " + s),
				e -> System.out.println("Exception: " + e),
				() -> System.out.println("Completed"));
				
		Observable<Integer> oIntArr1 = Observable.from(Arrays.asList(1,2,3,4,5,8));
		Observable<Integer> oIntArr2 = Observable.from(Arrays.asList(55, 66, 77));
				
		oIntArr1
			.map(i -> i*10)
			.mergeWith(oIntArr2)
			.forEach(System.out::println);
		
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

}
