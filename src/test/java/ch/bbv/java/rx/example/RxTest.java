package ch.bbv.java.rx.example;

import rx.schedulers.TestScheduler;

public abstract class RxTest {

	protected TestScheduler testScheduler = new TestScheduler();
	
	protected static class Value<T> {
		private T theValue;
		public Value(T initVal) {
			theValue = initVal;
		}
		public void setValue(T newVal) {
			theValue = newVal;
		}
		public T getValue() {
			return theValue;
		}
	}
	
	public void log(Object obj) {
		System.out.println(String.format("RxTest::log Thread: %d Log: %s", Thread.currentThread().getId(), String.valueOf(obj)));
	}
}
