package ch.bbv.java.rx.example;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.bbv.java.rx.example.model.Employee;
import rx.observers.TestObserver;
import rx.subjects.BehaviorSubject;

/**
 * @author yvesgross
 *
 */
public class CreatingObservablesExcercises {
	
	
	TestObserver<Employee> testObserver = new TestObserver<>();

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void exercise_UsingFactoryMethod() {
		
		//////////////////// UNRESOVED /////////////////////////////////////////
		//////////////////// RESOLVED //////////////////////////////////////////
		////////////////////////////////////////////////////////////////////////

		
        // The preferred way of creating production observables is through combinators
        // (i.e. Return, Never, Interval, ...). In tests predefined sequences are predominant.
        // For more complex scenarios there is Observable.create(...)
//        TestScheduler scheduler = new TestScheduler();
//        var random = new Random(7);

        // TODO: Create an infinite observable generating integers at random times but at least every 100 ticks
//        Observable<Long> observable = Observable.a
//        Observable<Long> observable = Observable.never();

        // Extremely poor mans randomness test, for better variants see Knuth's TAOCP
//        var result = scheduler.Start(() => observable, 1000);
//        var timings = result.Messages.Select(m => m.Time).Distinct();
//        timings.Count().Should().BeGreaterThan(1);
		
        // checkit
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

		// checkit
        assertTrue("Error Event received!", testObserver.getOnErrorEvents().isEmpty());
        assertTrue("No Completed Event received!", testObserver.getOnCompletedEvents().size()==1);
        testObserver.assertReceivedOnNext(Arrays.asList(34, 29, 93, 45, 13));
        
	}
	
	@Test
	public void exercise_3() {
		
		//////////////////// UNRESOVED /////////////////////////////////////////
		//////////////////// RESOLVED //////////////////////////////////////////
		////////////////////////////////////////////////////////////////////////

		// checkit
		
	}
	
	@Test
	public void exercise_4() {
		
		//////////////////// UNRESOVED /////////////////////////////////////////
		//////////////////// RESOLVED //////////////////////////////////////////
		////////////////////////////////////////////////////////////////////////

		// checkit
		
	}
	
	
}
