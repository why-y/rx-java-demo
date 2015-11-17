package ch.bbv.java.rx.example;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Scheduler;
import rx.Single;
import rx.Subscriber;
import rx.functions.Action0;
import rx.schedulers.Schedulers;
import rx.subjects.ReplaySubject;
import rx.subjects.Subject;

public class SimpleRx {
	
//	private static final Long DURATION = 1000L;
	private static final Duration TIMEOUT = Duration.ofMillis(100);

	private static final Collection<String> DATA = Arrays.asList((new StringBuilder())
			.append("In computing, reactive programming is a programming paradigm oriented ")
			.append("around data flows and the propagation of change. This means that it ")
			.append("should be possible to express static or dynamic data flows with ease ")
			.append("in the programming languages used, and that the underlying execution ")
			.append("model will automatically propagate changes through the data flow.")
			.toString().split(" "));

	public Collection<String> getElements() {
		return DATA;
	}
	
	public Observable<String> getElementsRx() {
		return Observable.from(DATA);		
	}
	
	public Observable<String> getElementsRxSlow () {
		return Observable.from(DATA).map(s -> someExpensiveStringOperation(s));
	}
	
	
	public Observable<String> getABC() {
		System.out.println("----\\ getABC()");
		Observable<String> ret = Observable.merge(getA(), getB(), getC());
		System.out.println("----/ getABC()");
		return ret;
	}
	
	public Observable<String> getA() {
		System.out.println("------\\ getA()");
		Observable<String> ret = Observable.create(subscriber -> {
			String theString = "AAAAAA";
			waistSomeTime(TIMEOUT);
			// .. this took quite some time, so ..
			if(!subscriber.isUnsubscribed()) {
				subscriber.onNext(theString);
				subscriber.onCompleted();
			}
		});
		System.out.println("------/ getA()");
		return ret;
	}
	
	public Observable<String> getB() {
		System.out.println("------\\ getB()");
		Observable<String> ret = Observable.create(subscriber -> {
			String theString = "BBBBBB";
			waistSomeTime(TIMEOUT);
			// .. this took quite some time, so ..
			if(!subscriber.isUnsubscribed()) {
				subscriber.onNext(theString);
				subscriber.onCompleted();
			}
		});
		System.out.println("------/ getB()");
		return ret;
	}
	
	public Observable<String> getC() {
		System.out.println("------\\ getC()");
		Observable<String> ret = Observable.create(subscriber -> {
			String theString = "CCCCC";
			waistSomeTime(TIMEOUT);
			// .. this took quite some time, so ..
			if(!subscriber.isUnsubscribed()) {
				subscriber.onNext(theString);
				subscriber.onCompleted();
			}
		});
		System.out.println("------/ getC()");
		return ret;
	}
	
	
	private String toUpperCase(final String in) {
		return in.toUpperCase();
	}
	
	private Observable<String> toUpperCase(final Observable<String> in) {
		return in.map(s -> s.toUpperCase());
	}
	
	private String someNoneBlockingExpensiveStringOperation(final String in, final Scheduler scheduler) {
		String result = String.format("converted in thread:%s(%d)   Value:(%s)", Thread.currentThread().getName(), Thread.currentThread().getId(), in);
		CountDownLatch cdl = new CountDownLatch(1);
		scheduler.createWorker().schedule(() -> {
			waistSomeTime(TIMEOUT);
			cdl.countDown();
		});
		try {
			cdl.await();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		return result;
	}
	
	private String someExpensiveStringOperation(final String in) {
		String result = String.format("converted in thread:%s(%d)   Value:(%s)", Thread.currentThread().getName(), Thread.currentThread().getId(), in);
		waistSomeTime(TIMEOUT);
		return result;
	}

	private void waistSomeTime(Duration duration) {
		long start = System.currentTimeMillis();
		
		// some senseless calculation
		for (double i = 0; i < duration.toNanos()/2; i++) {
			double y = i * i;
		}
		
//		try {
//			Thread.sleep(duration.toMillis());
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		
		long waistedTime = System.currentTimeMillis()-start;
		System.out.println(String.format("-------- waistSomeTime() for %s millis on thread:%d", waistedTime, Thread.currentThread().getId()));
	}
	
}
