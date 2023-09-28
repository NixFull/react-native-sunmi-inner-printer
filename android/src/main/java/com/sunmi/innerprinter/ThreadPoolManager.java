package com.sunmi.innerprinter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.ArrayList;

/**
 * 线程管理类
 *
 * @author lenovo
 *
 */
public class ThreadPoolManager {
	private ExecutorService service;
	private List<Future<?>> allTasks;

	private ScheduledExecutorService restartExecutor;


	private ThreadPoolManager() {
		int num = Runtime.getRuntime().availableProcessors() * 20;
		service = Executors.newFixedThreadPool(num);
		allTasks = new ArrayList<>();
		restartExecutor = Executors.newSingleThreadScheduledExecutor();
	}

	private static final ThreadPoolManager manager = new ThreadPoolManager();

	public static ThreadPoolManager getInstance() {
		return manager;
	}

	public void executeTask(Runnable runnable) {
		if (!service.isShutdown()) {
			Future<?> future = service.submit(runnable);
			allTasks.add(future);
		} else {
			System.out.println("ExecutorService is already shut down. Task not executed.");
		}
	}

	public void cancelAllTasks() {
		for (Future<?> task : allTasks) {
			task.cancel(true);
		}
		allTasks.clear();
		service.shutdownNow();

		// Schedule a service restart after 10 seconds
		restartExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				restartService();
			}
		}, 10, TimeUnit.SECONDS);
	}

	public void restartService() {
		int num = Runtime.getRuntime().availableProcessors() * 20;
		service = Executors.newFixedThreadPool(num);
	}
}