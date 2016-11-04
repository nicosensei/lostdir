package com.github.nicosensei.lostdir.elasticsearch;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by nicos on 11/4/2016.
 */
public final class LocalNodeRunner implements Callable<Void> {

    private LocalNode localNode;
    private final String homeDir;

    public LocalNodeRunner(final String homeDir) {
        this.homeDir = homeDir;
    }

    public void start() {
        localNode = new LocalNode(
                homeDir,
                LocalNodeDefaults.CLUSTER_NAME,
                LocalNodeDefaults.TCP_PORT,
                false);
    }

    public void stop() {
        if (localNode != null) {
            localNode.destroy();
        }
    }

    @Override
    public Void call() throws Exception {
        start();
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(100);
            } catch (final InterruptedException e) {}
        }
        return null;
    }

    public static final void main(final String[] args) throws Exception {
        final LocalNodeRunner runner = new LocalNodeRunner(args[0]);

        final ExecutorService exec = Executors.newSingleThreadExecutor();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                runner.stop();
                exec.shutdownNow();
            }
        });

        exec.submit(runner);
    }
}
