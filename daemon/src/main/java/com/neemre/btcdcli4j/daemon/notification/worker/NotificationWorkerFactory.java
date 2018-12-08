package com.neemre.btcdcli4j.daemon.notification.worker;

import com.neemre.btcdcli4j.core.client.BtcdClient;
import com.neemre.btcdcli4j.core.common.Errors;
import com.neemre.btcdcli4j.daemon.Notifications;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;
import java.net.Socket;

import static com.google.common.base.Preconditions.checkNotNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NotificationWorkerFactory {

    public static NotificationWorker createWorker(Notifications type, Socket socket,
                                                  @Nullable BtcdClient client) {
        if (type.equals(Notifications.ALERT)) {
            return new AlertNotificationWorker(socket);
        } else if (type.equals(Notifications.BLOCK)) {
            checkNotNull(client, "client must not be null in case of Notifications.BLOCK");
            return new BlockNotificationWorker(socket, client);
        } else if (type.equals(Notifications.WALLET)) {
            checkNotNull(client, "client must not be null in case of Notifications.WALLET");
            return new WalletNotificationWorker(socket, client);
        } else {
            throw new IllegalArgumentException(Errors.ARGS_BTCD_NOTIFICATION_UNSUPPORTED.getDescription());
        }
    }
}