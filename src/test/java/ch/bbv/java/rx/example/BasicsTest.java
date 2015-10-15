package ch.bbv.java.rx.example;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.bbv.java.rx.example.model.Employee;
import ch.bbv.java.rx.example.model.Employee.Skill;
import rx.Observable;
import rx.Subscriber;
import rx.observers.Subscribers;
import rx.observers.TestObserver;

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
		api = new Basics();
		TEST_DATA.stream().forEach(e -> api.create(e));
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
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
