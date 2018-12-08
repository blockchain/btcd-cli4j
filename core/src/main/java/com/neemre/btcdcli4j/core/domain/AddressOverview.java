package com.neemre.btcdcli4j.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.neemre.btcdcli4j.core.common.Defaults;
import com.neemre.btcdcli4j.core.jsonrpc.deserialization.AddressOverviewDeserializer;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(using = AddressOverviewDeserializer.class)
public class AddressOverview extends Entity {

    private String address;
    @Setter(AccessLevel.NONE)
    private BigDecimal balance;
    private String account;


    public AddressOverview(String address, BigDecimal balance, String account) {
        setAddress(address);
        setBalance(balance);
        setAccount(account);
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance.setScale(Defaults.DECIMAL_SCALE, Defaults.ROUNDING_MODE);
    }
}