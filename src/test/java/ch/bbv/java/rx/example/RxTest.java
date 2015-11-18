package ch.bbv.java.rx.example;

import rx.schedulers.TestScheduler;

public abstract class RxTest {

	protected TestScheduler testScheduler = new TestScheduler();
	
	public void log(Object obj) {
		System.out.println(String.format("RxTest::log Thread: %d Log: %s", Thread.currentThread().getId(), String.valueOf(obj)));
	}
}
