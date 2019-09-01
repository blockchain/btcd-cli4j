package com.neemre.btcdcli4j.daemon;

import com.google.common.util.concurrent.*;
import com.neemre.btcdcli4j.core.BitcoindException;
import com.neemre.btcdcli4j.core.CommunicationException;
import com.neemre.btcdcli4j.core.NodeProperties;
import com.neemre.btcdcli4j.core.client.BtcdClient;
import com.neemre.btcdcli4j.daemon.event.AlertListener;
import com.neemre.btcdcli4j.daemon.event.BlockListener;
import com.neemre.btcdcli4j.daemon.event.WalletListener;
import com.neemre.btcdcli4j.daemon.notification.NotificationMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import static com.google.common.util.concurrent.MoreExecutors.directExecutor;

public class BtcdDaemonImpl implements BtcdDaemon {

    private static final Logger LOG = LoggerFactory.getLogger(BtcdDaemonImpl.class);

    private DaemonConfigurator configurator;
    private Map<Notifications, NotificationMonitor> monitors;
    private Map<Notifications, Future<Void>> futures;
    private ListeningExecutorService monitorPool;

    private BtcdClient client;

    @Nullable
    private Consumer<Throwable> errorHandler;


    public BtcdDaemonImpl() {
        this(new Properties());
    }

    public BtcdDaemonImpl(BtcdClient btcdProvider) throws BitcoindException, CommunicationException {
        this(btcdProvider, null);
    }

    public BtcdDaemonImpl(BtcdClient btcdProvider, @Nullable Consumer<Throwable> errorHandler)
            throws BitcoindException, CommunicationException {
        this.errorHandler = errorHandler;
        initialize();
        this.client = configurator.checkBtcdProvider(btcdProvider);
        buildMonitors(configurator.checkNodeConfig(client.getNodeConfig()));
        configurator.checkNodeLiveness(client.getNetworkInfo());
        startMonitors();
        configurator.checkMonitorStates(futures);
    }

    public BtcdDaemonImpl(Properties nodeConfig) {
        initialize();
        buildMonitors(configurator.checkNodeConfig(nodeConfig));
        startMonitors();
        configurator.checkMonitorStates(futures);
    }

    public BtcdDaemonImpl(Integer alertPort, Integer blockPort, Integer walletPort) {
        this(new DaemonConfigurator().toProperties(alertPort, blockPort, walletPort));
    }

    @Override
    public void addAlertListener(AlertListener listener) {
        monitors.get(Notifications.ALERT).addObserver(listener.getObserver());
    }

    @Override
    public int countAlertListeners() {
        return monitors.get(Notifications.ALERT).countObservers();
    }

    @Override
    public void removeAlertListener(AlertListener listener) {
        monitors.get(Notifications.ALERT).deleteObserver(listener.getObserver());
    }

    @Override
    public void removeAlertListeners() {
        monitors.get(Notifications.ALERT).deleteObservers();
    }

    @Override
    public void addBlockListener(BlockListener listener) {
        monitors.get(Notifications.BLOCK).addObserver(listener.getObserver());
    }

    @Override
    public int countBlockListeners() {
        return monitors.get(Notifications.BLOCK).countObservers();
    }

    @Override
    public void removeBlockListener(BlockListener listener) {
        monitors.get(Notifications.BLOCK).deleteObserver(listener.getObserver());
    }

    @Override
    public void removeBlockListeners() {
        monitors.get(Notifications.BLOCK).deleteObservers();
    }

    @Override
    public void addWalletListener(WalletListener listener) {
        monitors.get(Notifications.WALLET).addObserver(listener.getObserver());
    }

    @Override
    public int countWalletListeners() {
        return monitors.get(Notifications.WALLET).countObservers();
    }

    @Override
    public void removeWalletListener(WalletListener listener) {
        monitors.get(Notifications.WALLET).deleteObserver(listener.getObserver());
    }

    @Override
    public void removeWalletListeners() {
        monitors.get(Notifications.WALLET).deleteObservers();
    }

    @Override
    public boolean isMonitoring(Notifications notificationType) {
        Future<Void> monitorHandle = futures.get(notificationType);
        if (monitorHandle != null) {
            return !monitorHandle.isDone();
        } else {
            return false;
        }
    }

    @Override
    public boolean isMonitoringAny() {
        for (Notifications notificationType : Notifications.values()) {
            Future<Void> monitorHandle = futures.get(notificationType);
            if ((monitorHandle != null) && (!monitorHandle.isDone())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isMonitoringAll() {
        for (Notifications notificationType : Notifications.values()) {
            Future<Void> monitorHandle = futures.get(notificationType);
            if ((monitorHandle == null) || (monitorHandle.isDone())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Properties getNodeConfig() {
        return configurator.getNodeConfig();
    }

    @Override
    public synchronized void shutdown() {
        LOG.info(">> shutdown(..): shutting down the 'bitcoind' notification daemon");
        monitorPool.shutdownNow();
    }

    private void initialize() {
        LOG.info(">> initialize(..): initiating the 'bitcoind' notification daemon");
        configurator = new DaemonConfigurator();
        monitors = new HashMap<>();
        futures = new HashMap<>();
    }

    private void buildMonitors(Properties nodeConfig) {
        String allowedHost = nodeConfig.getProperty(NodeProperties.ALLOWED_HOST.getKey());
        int alertPort = Integer.parseInt(nodeConfig.getProperty(NodeProperties.ALERT_PORT.getKey()));
        NotificationMonitor alertNotificationMonitor = new NotificationMonitor(Notifications.ALERT, alertPort,
                allowedHost, null,
                throwable -> {
                    if (errorHandler != null)
                        errorHandler.accept(throwable);
                });
        monitors.put(Notifications.ALERT, alertNotificationMonitor);

        int blockPort = Integer.parseInt(nodeConfig.getProperty(NodeProperties.BLOCK_PORT.getKey()));
        NotificationMonitor blockNotificationMonitor = new NotificationMonitor(Notifications.BLOCK, blockPort,
                allowedHost, client,
                throwable -> {
                    if (errorHandler != null)
                        errorHandler.accept(throwable);
                });
        monitors.put(Notifications.BLOCK, blockNotificationMonitor);

        int walletPort = Integer.parseInt(nodeConfig.getProperty(NodeProperties.WALLET_PORT.getKey()));
        NotificationMonitor walletNotificationMonitor = new NotificationMonitor(Notifications.WALLET, walletPort,
                allowedHost, client,
                throwable -> {
                    if (errorHandler != null)
                        errorHandler.accept(throwable);
                });
        monitors.put(Notifications.WALLET, walletNotificationMonitor);

        monitorPool = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(monitors.size()));
    }

    private void startMonitors() {
        for (Notifications notificationType : monitors.keySet()) {
            NotificationMonitor monitor = monitors.get(notificationType);

            ListenableFuture<Void> future = monitorPool.submit(monitor);
            futures.put(notificationType, future);

            Futures.addCallback(future, new FutureCallback<Void>() {
                public void onSuccess(Void ignore) {
                }

                public void onFailure(Throwable throwable) {
                    if (errorHandler != null)
                        errorHandler.accept(throwable);
                }
            }, directExecutor());
        }
    }
}
