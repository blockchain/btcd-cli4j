package com.neemre.btcdcli4j.daemon;

import com.neemre.btcdcli4j.core.common.Constants;
import com.neemre.btcdcli4j.core.common.Errors;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * This exception is thrown to indicate an unrecoverable error in the client side of the
 * 'callback-via-shell-command' notification API provided by <i>bitcoind</i>. Unless specifically
 * caught, this exception will result in the termination of the offending monitor/worker thread.
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class NotificationHandlerException extends Exception {

    private static final long serialVersionUID = 1L;
    private final Errors error;
    private int code;

    public NotificationHandlerException(Errors error) {
        this(error, Constants.STRING_EMPTY);
    }

    public NotificationHandlerException(Errors error, String additionalMsg) {
        super(error.getDescription() + additionalMsg);
        this.error = error;
        code = error.getCode();
    }

    public NotificationHandlerException(Errors error, Throwable cause) {
        super(error.getDescription(), cause);
        this.error = error;
        code = error.getCode();
    }
}
