package ch.bbv.java.rx.example;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import ch.bbv.java.rx.example.model.Employee;
import ch.bbv.java.rx.example.model.Employee.Skill;
import rx.Observable;

public class Basics {
	
	private List<Employee> employeeDb = new ArrayList<>();
	
	/**
	 * @return
	 */
	public Observable<Employee> rxGetAll() {
		
		return Observable.empty();
		
//		return Observable.from(employeeDb);
		
	}
	
	/**
	 * @param id
	 * @return
	 */
	public Observable<Employee> rxGetById(long id) {
		return Observable.empty();
//		return rxGetAll()
//				.filter(e -> id == e.getId()).first();
	}
	
	/**
	 * @param skill
	 * @return
	 */
	public Observable<Employee> rxGetBySkill(Skill skill) {
		
		return Observable.empty();
		
//		return rxGetAll()
//				.filter(e -> e.getSkills().contains(skill))	;
		
//		// ... or, getting it the classic way, and emit the
//		// values through a "Subject"
//		List<Employee> employeesSkilledWith = getBySkill(skill);
//		ReplaySubject<Employee> subject = ReplaySubject.create();
//		employeesSkilledWith.stream().forEach(e -> subject.onNext(e));
//		subject.onCompleted();
//		return subject;
	}

	
	///////////////////////////////////////////////////////
	public List<Employee> getAll() {
		// return a new reference
		return employeeDb.stream().collect(Collectors.toList());
	}
	
	public Employee getById(long id) {
		List<Employee> matches = employeeDb.stream()
				.filter(e -> id == e.getId())
				.collect(Collectors.toList());
		return matches.isEmpty() ? null : matches.get(0);
	}
	
	public List<Employee> getBySkill(Skill skill) {
		List<Employee> matches = new ArrayList<>();
		for (Employee employee : employeeDb) {
			if (employee.getSkills().contains(Skill.JAVA)) {
				matches.add(employee);
			}
		}
		return matches;
	}
	
	
	
	
	public void create(final Employee newEmployee) {
		if(!employeeDb.contains(newEmployee)) {
			employeeDb.add(newEmployee);
		}
	}
	
	public void delete(final Employee newEmployee) {
		if(employeeDb.contains(newEmployee)) {
			employeeDb.remove(newEmployee);
		}
	}
	
	public void deleteAll() {
		employeeDb.clear();		
	}
}
