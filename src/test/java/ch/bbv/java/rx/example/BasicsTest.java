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
import org.junit.Ignore;
import org.junit.Test;

import ch.bbv.java.rx.example.model.Employee;
import ch.bbv.java.rx.example.model.Employee.Skill;
import rx.Observable;
import rx.observers.TestObserver;
import rx.subjects.PublishSubject;

/**
 * @author yvesgross
 *
 */
public class BasicsTest {
	
	private static final List<Employee> TEST_DATA = Arrays.asList(
			new Employee(100L, "Eric Smith", Arrays.asList(Skill.DOTNET, Skill.SCRUM)),
			new Employee(101L, "Mary Miller", Arrays.asList(Skill.JAVA, Skill.SCRUM, Skill.RX)),
			new Employee(102L, "Tom Kenneth", Arrays.asList(Skill.EMBEDDED)),
			new Employee(103L, "Jane Smith", Arrays.asList(Skill.DOTNET, Skill.EMBEDDED)),
			new Employee(104L, "Ben Richards", Arrays.asList(Skill.DOTNET, Skill.JAVA, Skill.SCRUM, Skill.RX))
			);
	
	private static boolean errorHappened = false;
	
	Basics api;
	
	TestObserver<Employee> testObserver = new TestObserver<>();

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		errorHappened=false;
		api = new Basics();
		TEST_DATA.stream().forEach(e -> api.create(e));
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
	
	@Test
	@Ignore
	public void testGetAll() {
		
		// Classic
		api.getAll().stream().forEach(BasicsTest::printIt);;

		// Rx
		api.rxGetAll().subscribe(testObserver);

		// verify
		testObserver.assertReceivedOnNext(TEST_DATA);
		testObserver.assertTerminalEvent();
		printIt(testObserver.getOnNextEvents());

	}

	@Test
	@Ignore
	public void testGet() {

		int index = 2;
		long existingId = TEST_DATA.get(index).getId();
		
		// Classic
		printIt(api.getById(existingId));
		
		// Rx
		api.rxGetById(existingId).subscribe(testObserver);

		// verify
		testObserver.assertReceivedOnNext(Arrays.asList(TEST_DATA.get(index)));
		testObserver.assertTerminalEvent();
		assertTrue(testObserver.getOnErrorEvents().isEmpty());

	}

	@Test
	@Ignore
	public void testGetJavaEmloyees() {
		
		// Classic
		api.getBySkill(Skill.JAVA).stream().forEach(BasicsTest::printIt);
		
		// Rx
		api.rxGetBySkill(Skill.JAVA).subscribe(testObserver);
		
		// verify
		testObserver.assertReceivedOnNext(Arrays.asList(TEST_DATA.get(1), TEST_DATA.get(4)));
		testObserver.assertTerminalEvent();	
		assertTrue(testObserver.getOnErrorEvents().isEmpty());
		
	}

	
	/**
	 * Prints the given object to standard out by also
	 * showing the Thread ID.
	 * @param text
	 */
	private static void printIt(Object text) {
		System.out.println(String.format("Thread(%d): - %s", Thread.currentThread().getId(), text));
	}
}
