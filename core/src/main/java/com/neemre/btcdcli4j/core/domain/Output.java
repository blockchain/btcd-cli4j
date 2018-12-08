package com.neemre.btcdcli4j.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.neemre.btcdcli4j.core.common.Defaults;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Output extends OutputOverview {

    private String address;
    private String account;
    private String scriptPubKey;
    private String redeemScript;
    @Setter(AccessLevel.NONE)
    private BigDecimal amount;
    private Integer confirmations;
    private Boolean spendable;


    public Output(String txId, Integer vOut, String scriptPubKey, String redeemScript) {
        super(txId, vOut);
        this.scriptPubKey = scriptPubKey;
        this.redeemScript = redeemScript;
    }

    public Output(String address, String account, String scriptPubKey, String redeemScript,
                  BigDecimal amount, Integer confirmations, Boolean spendable) {
        setAddress(address);
        setAccount(account);
        setScriptPubKey(scriptPubKey);
        setRedeemScript(redeemScript);
        setAmount(amount);
        setConfirmations(confirmations);
        setSpendable(spendable);
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount.setScale(Defaults.DECIMAL_SCALE, Defaults.ROUNDING_MODE);
    }
}