package com.ofg.infrastructure.property;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.context.scope.refresh.RefreshScope;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

@ManagedResource
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
        this.configPath = fileSystemLocator.getConfigDir().toPath();
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

    @ManagedAttribute
    public File getConfigPath() {
        return configPath.toFile();
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
                    handleChangeIfRelatedFile(watchEvent);
                }
            } finally {
                key.reset();
            }
        }

        private void handleChangeIfRelatedFile(WatchEvent<?> watchEvent) {
            final Object context = watchEvent.context();
            if (context instanceof Path) {
                handlePathChange(context);
            } else {
                log.warn("Unsupported change event: {} of type {}", context, (context != null) ? context.getClass() : null);
            }
        }

        private void handlePathChange(Object context) {
            final String file = ((Path) context).toFile().getName();
            final Set<String> relatedFiles = getRelatedFiles();
            final boolean relatedFile = relatedFiles.contains(file);
            log.info("Found file system change '{}', related file: {}", context, relatedFile);
            if (relatedFile) {
                refreshConfiguration();
            }
        }

        private Set<String> getRelatedFiles() {
            final HashSet<String> files = new HashSet<String>();
            for (File file : fileSystemLocator.getConfigFiles()) {
                files.add(file.getName());
            }
            return files;
        }

        @ManagedOperation
        public void refreshConfiguration() {
            final PropertySource<?> propertySource = fileSystemLocator.locate(environment);
            final MutablePropertySources sources = environment.getPropertySources();

            if (sources.contains(propertySource.getName())) {
                sources.remove(propertySource.getName());
            }
            sources.addFirst(propertySource);

            refreshScope.refreshAll();
        }
    }
}
