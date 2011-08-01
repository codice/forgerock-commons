    
package org.forgerock.json.crypto;

// Java Standard Edition
import java.util.HashMap;

// JSON Fluent library
import org.forgerock.json.fluent.JsonNodeException;
import org.forgerock.json.fluent.JsonNode;

/**
 * TODO: Description.
 *
 * @author Paul C. Bryan
 */
public class JsonCrypto {

    /** TODO: Description. */
    private String type;

    /** TODO: Description. */
    private JsonNode value;

    /**
     * TODO: Description.
     */
    public JsonCrypto() {
    }

    /**
     * TODO: Description.
     *
     * @param node TODO.
     */
    public JsonCrypto(JsonNode node) throws JsonNodeException {
        fromJsonNode(node);
    }

    public JsonCrypto(String type, JsonNode value) {
        setType(type);
        setValue(value);
    }

    /**
     * TODO: Description.
     */
    public String getType() {
        return type;
    }

    /**
     * TODO: Description.
     *
     * @param type TODO.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * TODO: Description.
     */
    public JsonNode getValue() {
        return value;
    }

    /**
     * TODO: Description.
     *
     * @param value TODO.
     */
    public void setValue(JsonNode value) {
        this.value = value;
    }

    /**
     * TODO: Description.
     *
     * @param node TODO.
     * @throws JsonNodeException TODO.
     */
    public void fromJsonNode(JsonNode node) throws JsonNodeException {
        JsonNode crypto = node.get("$crypto").required();
        this.type = crypto.get("type").required().asString();
        this.value = crypto.get("value").required();
    }

    /**
     * TODO: Description.
     *
     * @return TODO.
     */
    public JsonNode toJsonNode() {
        HashMap<String, Object> crypto = new HashMap<String, Object>();
        crypto.put("type", type);
        crypto.put("value", value == null ? null : value.getValue());
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("$crypto", crypto);
        return new JsonNode(result);
    }

    /**
     * Returns {@code true} if the specified JSON node contains a valid JSON crypto structure.
     */
    public static boolean isJsonCrypto(JsonNode node) {
        JsonNode crypto = (node == null ? new JsonNode(null) : node.get("$crypto"));
        return (crypto.get("type").isString() && crypto.isDefined("value"));
    }
}
