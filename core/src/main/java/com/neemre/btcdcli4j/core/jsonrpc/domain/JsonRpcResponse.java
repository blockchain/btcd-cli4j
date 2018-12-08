package com.neemre.btcdcli4j.core.jsonrpc.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.neemre.btcdcli4j.core.jsonrpc.deserialization.JsonRpcResponseDeserializer;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(using = JsonRpcResponseDeserializer.class)
public class JsonRpcResponse extends JsonRpcMessage {

    private String result;
    private JsonRpcError error;
}