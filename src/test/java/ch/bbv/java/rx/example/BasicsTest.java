package ch.bbv.java.rx.example;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.bbv.java.rx.example.model.Employee;
import ch.bbv.java.rx.example.model.Employee.Skill;

/**
 * @author yvesgross
 *
 */
public class BasicsTest {
	
	Basics api;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		api = new Basics();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		// TODO: create an 'Observable' that 'return's an integer
//		Observable<Integer> observable = Observable.<Integer>empty();
		
		
//        var observer = new TestSink<int>();
		
		//// TODO: subscribe to the observable
		
//        observer.Values.Single().Should().Be(42);		
	}

	@Test
	public void testGetAll() {
		
		// Classic
		Collection<Employee> employees = api.getAll();
		printIt(employees);

		// Rx
		api.rxGetAll().forEach(BasicsTest::printIt);
	}

	@Test
	public void testGetOne() {

		long theId = 102L;
		
		// Classic
		Employee employee = api.getById(theId);
		printIt(employee);
		
		// Rx
		api.rxGetById(theId).subscribe(
				result -> printIt(result),
				exc -> exc.printStackTrace(),
				() -> printIt("Compeleted:"));

	}

	@Test
	public void testGetJavaEmloyees() {
		
		// Classic
		Collection<Employee> all = api.getAll();
		Collection<Employee> result = new ArrayList<>();
		for (Employee employee : all) {
			if(employee.getSkills().contains(Skill.JAVA)) {
				result.add(employee);
			}
		}
		printIt(result);
		
		// Rx
		api.rxGetBySkill(Skill.JAVA).forEach(BasicsTest::printIt);
		
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
