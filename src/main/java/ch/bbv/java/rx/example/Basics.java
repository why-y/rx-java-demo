package ch.bbv.java.rx.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import ch.bbv.java.rx.example.model.Employee;
import ch.bbv.java.rx.example.model.Employee.Skill;
import rx.Observable;

public class Basics {
	
	private static final Collection<Employee> DATA = Arrays.asList(
			new Employee(100L, "Eric Smith", Arrays.asList(Skill.DOTNET, Skill.SCRUM)),
			new Employee(101L, "Mary Miller", Arrays.asList(Skill.JAVA, Skill.SCRUM, Skill.RX)),
			new Employee(102L, "Tom Kenneth", Arrays.asList(Skill.EMBEDDED)),
			new Employee(103L, "Jane Smith", Arrays.asList(Skill.DOTNET, Skill.EMBEDDED)),
			new Employee(104L, "Ben Richards", Arrays.asList(Skill.DOTNET, Skill.JAVA, Skill.SCRUM, Skill.RX))
			);
	
	public Collection<Employee> getAll() {
		return DATA;
	}
	
	public Employee getById(long id) {
		List<Employee> matches = DATA.stream()
				.filter(e -> id == e.getId())
				.collect(Collectors.toList());
		return matches.isEmpty() ? null : matches.get(0);
	}
	
	public Collection<Employee> getBySkill(Skill skill) {
		Collection<Employee> matches = new ArrayList<>();
		for (Employee employee : DATA) {
			if (employee.getSkills().contains(Skill.JAVA)) {
				matches.add(employee);
			}
		}
		return matches;
	}
	
	
	
	public Observable<Employee> rxGetAll() {
		return Observable.from(DATA);
	}
	
	public Observable<Employee> rxGetById(long id) {
		return rxGetAll()
			.filter(e -> id == e.getId()).first();
	}
	
	public Observable<Employee> rxGetBySkill(Skill skill) {
		return rxGetAll()
			.filter(e -> e.getSkills().contains(skill))	;
	}

}
