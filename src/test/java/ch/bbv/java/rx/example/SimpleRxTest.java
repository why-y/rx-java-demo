/**
 * 
 */
package ch.bbv.java.rx.example;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import rx.Observable;
import rx.Observer;
import rx.schedulers.Schedulers;
import rx.subjects.Subject;

/**
 * @author yvesgross
 *
 */
public class SimpleRxTest {
	
    private static final Logger logger = Logger.getLogger(SimpleRxTest.class.getName());

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
	public void dummyTest() {
		Observable<Integer> oIntArr1 = Observable.from(Arrays.asList(1,2,3,4,5,8));
		Observable<Integer> oIntArr2 = Observable.from(Arrays.asList(55, 66, 77));
		
		Observable<Integer> tenTimes = oIntArr1.map(i -> i*10).mergeWith(oIntArr2);
		
		
		tenTimes.forEach(System.out::println);
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

}
