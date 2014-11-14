package com.ofg.infrastructure.property;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.context.scope.refresh.RefreshScope;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.TimeUnit;

import static java.nio.file.StandardWatchEventKinds.*;

public class FileSystemPoller {

	private static final Logger log = LoggerFactory.getLogger(FileSystemPoller.class);

	private final Path configPath;
	private final FileSystemLocator fileSystemLocator;
	private final ConfigurableEnvironment environment;
	private final RefreshScope refreshScope;
	private WatchService watcher;
	private Thread pollingThread;

	public FileSystemPoller(FileSystemLocator fileSystemLocator, ConfigurableEnvironment environment, RefreshScope refreshScope) {
		this.fileSystemLocator = fileSystemLocator;
		this.environment = environment;
		this.refreshScope = refreshScope;
		this.configPath = fileSystemLocator.getConfigPath().toPath();
	}

	@PostConstruct
	public void initConfigDirectoryPoller() {
		startWatcher();
		startFileSystemPollingThread();
	}

	@PreDestroy
	public void stopFileSystemWatcher() throws IOException {
		pollingThread.interrupt();
		watcher.close();
	}

	private void startWatcher() {
		try {
			watcher = FileSystems.getDefault().newWatchService();
			configPath.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
		} catch (IOException e) {
			throw new IllegalStateException("Can't poll for configuration changes", e);
		}
	}

	private void startFileSystemPollingThread() {
		pollingThread = new Thread(new PollerRunnable(), getClass().getSimpleName());
		pollingThread.start();
	}

	private void refreshBeans() {
		final PropertySource<?> propertySource = fileSystemLocator.locate(environment);
		final MutablePropertySources sources = environment.getPropertySources();

		if (sources.contains(propertySource.getName())) {
			sources.remove(propertySource.getName());
		}
		sources.addFirst(propertySource);

		refreshScope.refreshAll();
	}

	private class PollerRunnable implements Runnable {
		@Override
		public void run() {
			log.info("Started monitoring configuration directory for changes: {}", configPath);
			try {
				while (!Thread.currentThread().isInterrupted()) {
					waitForChanges();
				}
			} catch (InterruptedException e) {
				log.debug("Polling thread interrupted, shutting down", e);
			} catch (Exception e) {
				log.error("Unexpected error, terminating", e);
			}
		}

		private void waitForChanges() throws InterruptedException {
			final WatchKey key = watcher.poll(10, TimeUnit.SECONDS);
			if (key != null) {
				handleChange(key);
			}
		}

		private void handleChange(WatchKey key) {
			try {
				for (WatchEvent<?> watchEvent : key.pollEvents()) {
					log.info("Found file system change '{}', refreshing beans", watchEvent.context());
				}
				refreshBeans();
			} finally {
				key.reset();
			}
		}
	}
}
