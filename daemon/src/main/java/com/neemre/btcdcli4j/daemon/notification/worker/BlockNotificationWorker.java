package com.neemre.btcdcli4j.daemon.notification.worker;

import com.neemre.btcdcli4j.core.BitcoindException;
import com.neemre.btcdcli4j.core.CommunicationException;
import com.neemre.btcdcli4j.core.client.BtcdClient;
import com.neemre.btcdcli4j.core.domain.RawBlock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Socket;

public class BlockNotificationWorker extends NotificationWorker {

    private static final Logger LOG = LoggerFactory.getLogger(BlockNotificationWorker.class);


    public BlockNotificationWorker(Socket socket, BtcdClient client) {
        super(socket, client);
    }

    @Override
    protected Object getRelatedEntity(String headerHash) throws CommunicationException, BitcoindException {
        RawBlock rawBlock = new RawBlock();
        rawBlock.setHash(headerHash);
        if (getClient() != null) {
            try {
                LOG.debug("-- getRelatedEntity(..): fetching related block data from 'bitcoind' "
                        + "(via JSON-RPC API)");
                rawBlock = getClient().getBlock(headerHash, 2);
            } catch (CommunicationException e) {
                LOG.error("<< getRelatedEntity(..) / CommunicationException: failed to receive block data from 'bitcoind' "
                        + "(hash: '{}'), message was: '{}'", headerHash, e.getMessage());
                throw e;
            } catch (BitcoindException e) {
                LOG.error("<< getRelatedEntity(..) / BitcoindException: failed to receive block data from 'bitcoind' "
                        + "(hash: '{}'), message was: '{}'", headerHash, e.getMessage());
                throw e;
            }
        }
        return rawBlock;
    }
}