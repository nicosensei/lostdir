package com.github.nicosensei.lostdir.elasticsearch;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created by nicos on 11/3/2016.
 */
public final class LocalNode {

    private static final String THREAD_POOl_NAME_PARAM = "name";
    private static final String THREAD_POOl_NAME_VALUE = "lostdir_es";

    private final File homeDir;

    private static final Logger LOG = LoggerFactory.getLogger(LocalNode.class);

    private Node node;

    private Client client;

    /**
     * Builds an Elasticsearch local environment
     * @param clusterName the name of the cluster
     * @param tcpPort the TCP port to use to connect the client
     * @param embedded if true, the node is embedded, if false it will fork the JVM
     */
    public LocalNode(
            final String homeDir,
            final String clusterName,
            final int tcpPort,
            final boolean embedded) throws ElasticsearchException {

        // Initialize paths
        this.homeDir = new File(homeDir);
        if (!this.homeDir.exists()
                || !this.homeDir.isDirectory()
                || !this.homeDir.canRead()
                || !this.homeDir.canWrite()) {
            throw new ElasticsearchException("Home dir is not readable or writable");
        }

        // Initialize ES client
        Settings.Builder settings = Settings.builder();
        settings.put("discovery.zen.ping.multicast.enabled", "false");
        settings.put("transport.tcp.port", Integer.toString(tcpPort));
        settings.put("http.port", Integer.toString(tcpPort - 100));
        settings.put("path.home", this.homeDir.getAbsolutePath());
        settings.put("path.data", new File(this.homeDir, "data").getAbsolutePath());
        settings.put("path.work", new File(this.homeDir, "work").getAbsolutePath());
        settings.put("path.logs", new File(this.homeDir, "logs").getAbsolutePath());
        settings.put(THREAD_POOl_NAME_PARAM, THREAD_POOl_NAME_VALUE);

        node = new Node(settings.build());
        try {
            node.start();
        } catch (final NodeValidationException e) {
            throw new ElasticsearchException(e.getMessage(), e);
        }

        client = node.client();
    }

    /**
     * @return a {@link Client} instance
     */
    public Client client() {
        return client;
    }

    /**
     * Finalizer method, closes client and purges temporary files.
     * @throws Exception
     */
    public void destroy() throws Exception {
        client.close();
        node.close();
    }

}
