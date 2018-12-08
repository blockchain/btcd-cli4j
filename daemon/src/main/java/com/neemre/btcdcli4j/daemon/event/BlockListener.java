package com.neemre.btcdcli4j.daemon.event;

import com.neemre.btcdcli4j.core.domain.RawBlock;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Observable;
import java.util.Observer;

/**
 * An abstract adapter class for receiving {@code BLOCK} notifications. Extend this class to
 * override any methods of interest.
 */
public abstract class BlockListener {

    private static final Logger LOG = LoggerFactory.getLogger(BlockListener.class);

    @Getter
    private Observer observer;


    public BlockListener() {
        observer = new Observer() {
            @Override
            public void update(Observable monitor, Object cause) {
                RawBlock rawBlock = (RawBlock) cause;
                LOG.trace("-- update(..): forwarding incoming 'BLOCK' notification to "
                        + "'blockDetected(..)'");
                blockDetected(rawBlock);
            }
        };
    }

    public void blockDetected(RawBlock rawBlock) {
    }
}