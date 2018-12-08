package com.neemre.btcdcli4j.core.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonValue;
import com.neemre.btcdcli4j.core.common.Errors;
import lombok.AllArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public enum ScriptTypes {

    // https://github.com/bitcoin/bitcoin/blob/8152d3fe57a991e9088d0b9d261d2b10936f45a9/src/script/standard.cpp
    PUB_KEY("pubkey"),
    PUB_KEY_HASH("pubkeyhash"),
    SCRIPT_HASH("scripthash"),
    MULTISIG("multisig"),
    NULL_DATA("nulldata"),
    WITNESS_V0_KEYHASH("witness_v0_keyhash"),
    WITNESS_V0_SCRIPTHASH("witness_v0_scripthash"),
    WITNESS_UNKNOWN("witness_unknown"),
    NONSTANDARD("nonstandard");

    private final String name;


    @JsonValue
    public String getName() {
        return name;
    }

    @JsonCreator
    public static ScriptTypes forName(String name) {
        if (name != null) {
            for (ScriptTypes scriptType : ScriptTypes.values()) {
                if (name.equals(scriptType.getName())) {
                    return scriptType;
                }
            }
        }
        throw new IllegalArgumentException(Errors.ARGS_BTCD_SCRIPTTYPE_UNSUPPORTED.getDescription());
    }
}