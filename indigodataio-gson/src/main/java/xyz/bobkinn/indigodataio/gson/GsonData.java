package xyz.bobkinn.indigodataio.gson;

import com.google.gson.*;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import xyz.bobkinn.indigodataio.DataHolder;
import xyz.bobkinn.indigodataio.MapBuilder;
import xyz.bobkinn.indigodataio.Pair;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

@RequiredArgsConstructor
public class GsonData implements DataHolder<GsonData, JsonElement> {
    @NonNull
    private final JsonObject data;

    public GsonData(){
        this(new JsonObject());
    }

    public static MapBuilder<GsonData, JsonElement> newBuilder(){
        return new GsonData().toBuilder();
    }

    @Override
    public GsonData getNew() {
        return new GsonData();
    }

    @Override
    public MapBuilder<GsonData, JsonElement> toBuilder() {
        return new MapBuilder<>(null, this, GsonData::new,
                GsonData::mapToPrimitive, GsonData::mapToPrimitive, GsonData::mapToPrimitive);
    }

    @Override
    public MapBuilder<GsonData, JsonElement> toBuilder(String key) {
        checkEmptyKey(key);
        GsonData root;
        if (containsSection(key)) {
            root = getSection(key);
        } else {
            root = new GsonData();
            putSection(key, root);
        }
        return new MapBuilder<>(null, root, GsonData::new,
                GsonData::mapToPrimitive, GsonData::mapToPrimitive, GsonData::mapToPrimitive);
    }

    @Override
    public String toString() {
        return data.toString();
    }

    public JsonObject getRaw(){
        return data;
    }

    /**
     * @param key full key
     * @return left - map key, right - key inside map
     */
    @Contract("_ -> new")
    private @NotNull Pair<String, String> extractMapKey(@NotNull String key) {
        int i = key.lastIndexOf(".");
        if (i == -1) return Pair.of("", key);
        var l = key.substring(0, i);
        var r = key.substring(i + 1);
        return Pair.of(l, r);
    }

    @Contract("_, true -> !null")
    private JsonObject resolveMap(@NotNull String key, boolean create) {
        var ret = data;
        for (String k : key.split("\\.")) {
            if (k.isEmpty()) return ret;
            if (ret == null) return null;
            JsonElement o;
            if (ret.has(k)) {
                o = ret.get(k);
            } else if (create) {
                o = new JsonObject();
                ret.add(k, o);
            } else {
                return null;
            }
            try {
                ret = o.getAsJsonObject();
            } catch (IllegalStateException e) {
                return null;
            }
        }
        return ret;
    }

    private static void checkEmptyKey(String key){
        if (key.isEmpty()) throw new IllegalArgumentException("Empty key '"+key+"'");
    }

    @Override
    public JsonElement remove(String key) {
        checkEmptyKey(key);
        var p = extractMapKey(key);
        var map = resolveMap(p.getLeft(), false);
        if (map == null) return null;
        return map.remove(p.getRight());
    }

    @Override
    public boolean contains(String key) {
        checkEmptyKey(key);
        var p = extractMapKey(key);
        var map = resolveMap(p.getLeft(), false);
        if (map == null) return false;
        return map.has(p.getRight());
    }

    @Override
    public boolean contains(String key, Class<? extends JsonElement> type) {
        var d = get(key);
        return type.isInstance(d) && !(d instanceof JsonNull);
    }

    @Override
    public boolean containsSection(String key) {
        return getMap(key) != null;
    }

    @Override
    public Set<String> keys() {
        return data.keySet();
    }

    @Override
    public Set<String> keys(String key) {
        var p = extractMapKey(key);
        var map = resolveMap(p.getLeft(), false);
        if (map == null) return Set.of();
        return map.keySet();
    }

    @Override
    public int size() {
        return data.size();
    }

    @Override
    public void clear() {
        data.keySet().clear();
    }

    @Override
    public JsonElement get(String key, JsonElement def) {
        var p = extractMapKey(key);
        var map = resolveMap(p.getLeft(), false);
        if (map == null) return null;
        var v = map.get(p.getRight());
        if (v instanceof JsonNull || v == null) return null;
        else return v;
    }

    public <A extends JsonElement> A getType(String key, Class<A> cls) {
        var p = extractMapKey(key);
        var map = resolveMap(p.getLeft(), false);
        if (map == null) return null;
        var v = map.get(p.getRight());
        if (v instanceof JsonNull || v == null) return null;
        if (cls.isInstance(v)) return cls.cast(v);
        else return null;
    }

    public static JsonObject mapAsObject(Map<String, JsonElement> map){
        if (map == null) return null;
        var o = new JsonObject();
        map.forEach(o::add);
        return o;
    }

    public static <A> JsonArray mapArray(List<A> source, Function<A, JsonElement> conv){
        if (source == null) return null;
        var arr = new JsonArray(source.size());
        for (A a : source) {
            if (a != null) {
                JsonElement jsonElement = conv.apply(a);
                arr.add(jsonElement);
            } else {
                arr.add(JsonNull.INSTANCE);
            }
        }
        return arr;
    }

    public <A> A mapPrimitive(String key, Function<JsonPrimitive, A> conv, Predicate<JsonPrimitive> typeCheck, A def){
        var p = getType(key, JsonPrimitive.class);
        if (p == null) return def;
        else if (typeCheck.test(p)) return conv.apply(p);
        else return null;
    }

    public <A> List<A> mapPrimitiveList(String key, Function<JsonPrimitive, A> conv, List<A> def){
        var arr = getType(key, JsonArray.class);
        if (arr == null) return def;
        var ls = new ArrayList<A>(arr.size());
        var asList = arr.asList();
        for (var e : asList) {
            if (e instanceof JsonPrimitive p) {
                try {
                    ls.add(conv.apply(p));
                } catch (Exception ex) {
                    return def;
                }
            } else if (e.isJsonNull()) {
                ls.add(null);
            } else {
                return def;
            }
        }
        return ls;
    }

    public <A extends Number> List<A> mapNumberList(String key, Function<Number, A> conv, List<A> def){
        var ls = mapPrimitiveList(key, JsonPrimitive::getAsNumber, null);
        if (ls == null) return def;
        List<A> list = new ArrayList<>();
        for (Number l : ls) {
            if (l != null) {
                A a = conv.apply(l);
                list.add(a);
            } else list.add(null);
        }
        return list;
    }

    public static JsonPrimitive mapToPrimitive(Object value){
        if (value == null) return null;
        if (value instanceof Boolean b) {
            return new JsonPrimitive(b);
        } else if (value instanceof String s) {
            return new JsonPrimitive(s);
        } else if (value instanceof Number n) {
            return new JsonPrimitive(n);
        } else if (value instanceof Character c) {
            return new JsonPrimitive(c);
        } else {
            throw new IllegalArgumentException("Value is not bool, string, number or character but "+value.getClass().getName());
        }
    }

    @Override
    public JsonElement get(String key) {
        return get(key, null);
    }

    @Override
    public List<? extends JsonElement> getList(String key, List<? extends JsonElement> def) {
        var arr = getType(key, JsonArray.class);
        return arr.asList();
    }

    @Override
    public JsonElement put(String key, JsonElement value) {
        var p = extractMapKey(key);
        var map = resolveMap(p.getLeft(), true);
        var old = map.remove(p.getRight());
        map.add(p.getRight(), value != null ? value : JsonNull.INSTANCE);
        return old instanceof JsonNull || old == null ? null : old;
    }

    @Override
    public JsonElement putList(String key, List<? extends JsonElement> value) {
        if (value == null) return put(key, null);
        var arr = new JsonArray(value.size());
        value.forEach(arr::add);
        return put(key, arr);
    }

    public JsonObject getObject(String key, JsonObject def){
        var v = get(key);
        if (v == null) return def;
        return v.isJsonObject() ? v.getAsJsonObject() : def;
    }

    @Override
    public GsonData getSection(String key, GsonData def) {
        var d = getObject(key, null);
        if (d == null) return def;
        return new GsonData(d);
    }

    @Override
    public GsonData getSection(String key) {
        return getSection(key, null);
    }

    @Override
    public List<GsonData> getSectionList(String key, List<GsonData> def) {
        var arr = getType(key, JsonArray.class);
        if (arr == null) return def;
        List<GsonData> ls = new ArrayList<>(arr.size());
        arr.asList().forEach(e -> ls.add(new GsonData(e.getAsJsonObject())));
        return ls;
    }

    @Override
    public JsonElement putSection(String key, GsonData value) {
        return put(key, value.data);
    }

    @Override
    public JsonElement putSectionList(String key, List<GsonData> value) {
        return put(key, mapArray(value, GsonData::getRaw));
    }

    @Override
    public Map<String, JsonElement> getMap(String key, Map<String, JsonElement> def) {
        var o = getObject(key, null);
        if (o == null) return def;
        return o.asMap();
    }

    @Override
    public Map<String, JsonElement> getMap(String key) {
        return getMap(key, null);
    }

    @Override
    public List<Map<String, JsonElement>> getMapList(String key, List<Map<String, JsonElement>> def) {
        var arr = getType(key, JsonArray.class);
        if (arr == null) return def;
        List<Map<String, JsonElement>> ls = new ArrayList<>(arr.size());
        arr.asList().forEach(e -> ls.add(e.getAsJsonObject().asMap()));
        return ls;
    }

    @Override
    public JsonElement putMap(String key, Map<String, JsonElement> value) {
        return put(key, mapAsObject(value));
    }

    @Override
    public JsonElement putMapList(String key, List<Map<String, JsonElement>> value) {
        return put(key, mapArray(value, GsonData::mapAsObject));
    }

    @Override
    public String getString(String key, String def) {
        return mapPrimitive(key, JsonPrimitive::getAsString, JsonPrimitive::isString, def);
    }

    @Override
    public String getString(String key) {
        return getString(key, null);
    }

    @Override
    public List<String> getStringList(String key, List<String> def) {
        return mapPrimitiveList(key, JsonPrimitive::getAsString, def);
    }

    @Override
    public JsonElement putString(String key, String value) {
        return put(key, new JsonPrimitive(value));
    }

    @Override
    public JsonElement putStringList(String key, List<String> value) {
        return put(key, mapArray(value, GsonData::mapToPrimitive));
    }

    @Override
    public byte getByte(String key, byte def) {
        var n = mapPrimitive(key, JsonPrimitive::getAsNumber, JsonPrimitive::isNumber, null);
        if (n == null) return def;
        return n.byteValue();
    }

    @Override
    public byte getByte(String key) {
        return getByte(key, (byte) 0);
    }

    @Override
    public List<Byte> getByteList(String key, List<Byte> def) {
        return mapNumberList(key, Number::byteValue, def);
    }

    @Override
    public JsonElement putByte(String key, byte value) {
        return put(key, new JsonPrimitive(value));
    }

    @Override
    public JsonElement putByteList(String key, List<Byte> value) {
        return put(key, mapArray(value, GsonData::mapToPrimitive));
    }

    @Override
    public short getShort(String key, short def) {
        var n = mapPrimitive(key, JsonPrimitive::getAsNumber, JsonPrimitive::isNumber, null);
        if (n == null) return def;
        return n.shortValue();
    }

    @Override
    public short getShort(String key) {
        return getShort(key, (short) 0);
    }

    @Override
    public List<Short> getShortList(String key, List<Short> def) {
        return mapNumberList(key, Number::shortValue, def);
    }

    @Override
    public JsonElement putShort(String key, short value) {
        return put(key, new JsonPrimitive(value));
    }

    @Override
    public JsonElement putShortList(String key, List<Short> value) {
        return put(key, mapArray(value, GsonData::mapToPrimitive));
    }

    @Override
    public int getInt(String key, int def) {
        var n = mapPrimitive(key, JsonPrimitive::getAsNumber, JsonPrimitive::isNumber, null);
        if (n == null) return def;
        return n.intValue();
    }

    @Override
    public int getInt(String key) {
        return getInt(key, 0);
    }

    @Override
    public List<Integer> getIntList(String key, List<Integer> def) {
        return mapNumberList(key, Number::intValue, def);
    }

    @Override
    public JsonElement putInt(String key, int value) {
        return put(key, new JsonPrimitive(value));
    }

    @Override
    public JsonElement putIntList(String key, List<Integer> value) {
        return put(key, mapArray(value, GsonData::mapToPrimitive));
    }

    @Override
    public long getLong(String key, long def) {
        var n = mapPrimitive(key, JsonPrimitive::getAsNumber, JsonPrimitive::isNumber, null);
        if (n == null) return def;
        return n.longValue();
    }

    @Override
    public long getLong(String key) {
        return getLong(key, 0L);
    }

    @Override
    public List<Long> getLongList(String key, List<Long> def) {
        return mapNumberList(key, Number::longValue, def);
    }

    @Override
    public JsonElement putLong(String key, long value) {
        return put(key, new JsonPrimitive(value));
    }

    @Override
    public JsonElement putLongList(String key, List<Long> value) {
        return put(key, mapArray(value, GsonData::mapToPrimitive));
    }

    @Override
    public float getFloat(String key, float def) {
        var n = mapPrimitive(key, JsonPrimitive::getAsNumber, JsonPrimitive::isNumber, null);
        if (n == null) return def;
        return n.floatValue();
    }

    @Override
    public float getFloat(String key) {
        return getFloat(key, 0);
    }

    @Override
    public List<Float> getFloatList(String key, List<Float> def) {
        return mapNumberList(key, Number::floatValue, def);
    }

    @Override
    public JsonElement putFloat(String key, float value) {
        return put(key, new JsonPrimitive(value));
    }

    @Override
    public JsonElement putFloatList(String key, List<Float> value) {
        return put(key, mapArray(value, GsonData::mapToPrimitive));
    }

    @Override
    public double getDouble(String key, double def) {
        var n = mapPrimitive(key, JsonPrimitive::getAsNumber, JsonPrimitive::isNumber, null);
        if (n == null) return def;
        return n.doubleValue();
    }

    @Override
    public double getDouble(String key) {
        return getDouble(key, 0);
    }

    @Override
    public List<Double> getDoubleList(String key, List<Double> def) {
        return mapNumberList(key, Number::doubleValue, def);
    }

    @Override
    public JsonElement putDouble(String key, double value) {
        return put(key, new JsonPrimitive(value));
    }

    @Override
    public JsonElement putDoubleList(String key, List<Double> value) {
        return put(key, mapArray(value, GsonData::mapToPrimitive));
    }
}
