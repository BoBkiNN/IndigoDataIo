package xyz.bobkinn.indigodataio.gson;

import com.google.gson.*;
import org.jetbrains.annotations.Nullable;
import xyz.bobkinn.indigodataio.Pair;
import xyz.bobkinn.indigodataio.ops.BaseMap;
import xyz.bobkinn.indigodataio.ops.TypeOps;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class GsonOps implements TypeOps<JsonElement> {
    public static final GsonOps INSTANCE = new GsonOps();

    @Override
    public JsonElement empty() {
        return JsonNull.INSTANCE;
    }

    @Override
    public <U> U convertTo(TypeOps<U> outOps, JsonElement input) {
        if (input instanceof JsonObject) return convertMap(outOps, input);
        if (input instanceof JsonArray) return convertList(outOps, input);
        if (input instanceof JsonNull) return outOps.empty();
        final JsonPrimitive primitive = input.getAsJsonPrimitive();
        if (primitive.isString()) return outOps.createString(primitive.getAsString());
        if (primitive.isBoolean()) return outOps.createBoolean(primitive.getAsBoolean());
        final BigDecimal value = primitive.getAsBigDecimal();
        try {
            final long l = value.longValueExact();
            if ((byte) l == l) {
                return outOps.createByte((byte) l);
            }
            if ((short) l == l) {
                return outOps.createShort((short) l);
            }
            if ((int) l == l) {
                return outOps.createInt((int) l);
            }
            return outOps.createLong(l);
        } catch (final ArithmeticException e) {
            final double d = value.doubleValue();
            if ((float) d == d) {
                return outOps.createFloat((float) d);
            }
            return outOps.createDouble(d);
        }
    }

    @Override
    public Optional<Number> getNumberValue(JsonElement input) {
        if (input instanceof JsonPrimitive p && p.isNumber()) {
            return Optional.of(p.getAsNumber());
        }
        return Optional.empty();
    }

    @Override
    public JsonElement createNumeric(Number i) {
        return new JsonPrimitive(i);
    }

    @Override
    public JsonElement createBoolean(boolean value) {
        return new JsonPrimitive(value);
    }

    @Override
    public Optional<String> getString(JsonElement input) {
        if (input instanceof JsonPrimitive p && p.isString()) {
            return Optional.of(p.getAsString());
        }
        return Optional.empty();
    }

    @Override
    public JsonElement createString(String value) {
        return new JsonPrimitive(value);
    }

    @Override
    public Optional<JsonElement> mergeToList(JsonElement list, JsonElement value) {
        if (!(list instanceof JsonArray) && list != empty()) {
            return Optional.empty();
        }

        final JsonArray result = new JsonArray();
        if (list != empty()) {
            result.addAll(list.getAsJsonArray());
        }
        result.add(value);
        return Optional.of(result);
    }

    @Override
    public Optional<JsonElement> mergeToMap(JsonElement map, JsonElement key, JsonElement value) {
        if (!(map instanceof JsonObject) && map != empty()) {
            return Optional.empty();
        }
        if (!(key instanceof JsonPrimitive p) || !p.isString()) {
            return Optional.empty();
        }

        final JsonObject output = new JsonObject();
        if (map != empty()) {
            map.getAsJsonObject().entrySet().forEach(entry -> output.add(entry.getKey(), entry.getValue()));
        }
        output.add(key.getAsString(), value);

        return Optional.of(output);
    }

    @Override
    public Optional<Stream<Pair<JsonElement, JsonElement>>> getMapValues(JsonElement input) {
        if (!(input instanceof JsonObject o)) {
            return Optional.empty();
        }
        return Optional.of(o.entrySet().stream().map(entry ->
                Pair.of(new JsonPrimitive(entry.getKey()), entry.getValue() instanceof JsonNull ? null : entry.getValue())));
    }

    @Override
    public Optional<BaseMap<JsonElement>> getMap(JsonElement input) {
        if (!(input instanceof JsonObject object)) {
            return Optional.empty();
        }
        return Optional.of(new BaseMap<>() {
            @Nullable
            @Override
            public JsonElement get(final JsonElement key) {
                final JsonElement element = object.get(key.getAsString());
                if (element instanceof JsonNull) {
                    return null;
                }
                return element;
            }

            @Nullable
            @Override
            public JsonElement get(final String key) {
                final JsonElement element = object.get(key);
                if (element instanceof JsonNull) {
                    return null;
                }
                return element;
            }

            @Override
            public Stream<Pair<JsonElement, JsonElement>> entries() {
                return object.entrySet().stream().map(e -> Pair.of(new JsonPrimitive(e.getKey()), e.getValue()));
            }

            @Override
            public String toString() {
                return "BaseMap[" + object + "]";
            }
        });
    }

    @Override
    public JsonElement createMap(Stream<Pair<JsonElement, JsonElement>> map) {
        final JsonObject result = new JsonObject();
        map.forEach(p -> result.add(p.getFirst().getAsString(), p.getSecond()));
        return result;
    }

    @Override
    public Optional<Stream<JsonElement>> getStream(JsonElement input) {
        if (input instanceof JsonArray arr) {
            return Optional.of(StreamSupport.stream(arr.spliterator(), false).map(e -> e instanceof JsonNull ? null : e));
        }
        return Optional.empty();
    }

    @Override
    public JsonElement createList(Stream<? extends JsonElement> input) {
        var list = input.map(v -> v == null ? empty() : v).toList();
        final JsonArray ret = new JsonArray(list.size());
        list.forEach(ret::add);
        return ret;
    }

    @Override
    public JsonElement createArray(JsonElement[] input) {
        return createList(Arrays.stream(input));
    }

    @Override
    public JsonElement remove(JsonElement input, String key) {
        if (input instanceof JsonObject) {
            final JsonObject result = new JsonObject();
            input.getAsJsonObject().entrySet().stream()
                    .filter(entry -> !Objects.equals(entry.getKey(), key))
                    .forEach(entry -> result.add(entry.getKey(), entry.getValue()));
            return result;
        }
        return input;
    }
}
