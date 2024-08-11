package xyz.bobkinn.indigodataio;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import xyz.bobkinn.indigodataio.ops.MapOps;
import xyz.bobkinn.indigodataio.ops.TypeOps;

import java.util.*;

@RequiredArgsConstructor
public class NestedKeyMap extends AbstractDataHolder<NestedKeyMap, Object, Map<String, Object>>{
    @NonNull // to generate check
    private final @NotNull Map<String, Object> data;

    public NestedKeyMap(){
        this(new HashMap<>());
    }

    public static MapBuilder<NestedKeyMap, Object> newBuilder(){
        return new NestedKeyMap().toBuilder();
    }

    public Map<String, Object> getRaw(){
        return data;
    }

    @Override
    public NestedKeyMap getNew() {
        return new NestedKeyMap();
    }

    @Override
    public Map<String, Object> getNewRaw() {
        return new HashMap<>();
    }

    @Override
    public NestedKeyMap getNewRaw(Map<String, Object> data) {
        return new NestedKeyMap(data);
    }

    @Override
    public TypeOps<Object> getOps() {
        return MapOps.INSTANCE;
    }

    @Override
    public MapBuilder<NestedKeyMap, Object> toBuilder() {
        return new MapBuilder<>(null, this, NestedKeyMap::new,
                n -> n, b -> b, s -> s);
    }

    @Override
    public MapBuilder<NestedKeyMap, Object> toBuilder(String key) {
        checkEmptyKey(key);
        NestedKeyMap root;
        if (containsSection(key)) {
            root = getSection(key);
        } else {
            root = getNew();
            putSection(key, root);
        }
        return new MapBuilder<>(null, root, NestedKeyMap::new,
                n -> n, b -> b, s -> s);
    }

    @Override
    public String toString() {
        return data.toString();
    }

    @Contract("_, true -> !null")
    @SuppressWarnings("unchecked")
    protected Map<String, Object> resolveMap(@NotNull String key, boolean create) {
        var ret = data;
        for (String k : key.split("\\.")) {
            if (k.isEmpty()) return ret;
            if (ret == null) return null;
            Object o;
            if (ret.containsKey(k)) {
                o = ret.get(k);
            } else if (create) {
                o = getNewRaw();
                ret.put(k, o);
            } else {
                return null;
            }
            try {
                ret = (Map<String, Object>) o;
            } catch (ClassCastException e) {
                return null;
            }
        }
        return ret;
    }

    private static void checkEmptyKey(String key){
        if (key.isEmpty()) throw new IllegalArgumentException("Empty key '"+key+"'");
    }

    @Override
    public Object remove(String key) {
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
        return map.containsKey(p.getRight());
    }

    @Override
    public boolean contains(String key, Class<?> type) {
        var d = get(key);
        return type.isInstance(d);
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
        data.clear();
    }

    @NotNull
    @Override
    public Set<Entry<String, Object>> entrySet() {
        return data.entrySet();
    }

    @Override
    public Object get(String key, Object def) {
        var p = extractMapKey(key);
        var map = resolveMap(p.getLeft(), false);
        if (map == null) return def;
        return map.getOrDefault(p.getRight(), def);
    }

    @Override
    public Object get(String key) {
        return get(key, null);
    }

    @Override
    public List<?> getList(String key, List<?> def) {
        var o = get(key, def);
        if (o instanceof List<?> ls){
            return ls;
        } else return def;
    }

    @Override
    public Object putValue(String key, Object value) {
        if (value instanceof NestedKeyMap){
            throw new IllegalArgumentException("Use putSection to put NestedKeyMap");
        }
        var p = extractMapKey(key);
        var map = resolveMap(p.getLeft(), true);
        return map.put(p.getRight(), value);
    }

    @Override
    public Object putList(String key, List<?> value) {
        return putValue(key, value);
    }

    @Override
    public NestedKeyMap getSection(String key, NestedKeyMap def) {
        var m = getMap(key, null);
        if (m == null) return def;
        return new NestedKeyMap(m);
    }

    @Override
    public NestedKeyMap getSection(String key) {
        return getSection(key, null);
    }

    @Override
    public List<NestedKeyMap> getSectionList(String key, List<NestedKeyMap> def) {
        var ls = getMapList(key);
        if (ls == null) return def;
        return ls.stream().map(this::getNewRaw).toList();
    }

    @Override
    public Object putSection(String key, NestedKeyMap value) {
        return putValue(key, value != null ? value.data : null);
    }

    @Override
    public Object putSectionList(String key, List<NestedKeyMap> value) {
        if (value == null) return putValue(key, null);
        return putValue(key, value.stream().map(NestedKeyMap::getRaw).toList());
    }

    @Override
    public Map<String, Object> getMap(String key, Map<String, Object> def) {
        var o = get(key, def);
        try {
            //noinspection unchecked
            return (Map<String, Object>) o;
        } catch (Exception e){
            return def;
        }
    }

    @Override
    public Map<String, Object> getMap(String key) {
        return getMap(key, null);
    }

    @Override
    public List<Map<String, Object>> getMapList(String key, List<Map<String, Object>> def) {
        var v = get(key, def);
        try {
            //noinspection unchecked
            return (List<Map<String, Object>>) v;
        } catch (ClassCastException ignored){
            return def;
        }
    }

    @Override
    public Object putMap(String key, Map<String, Object> value) {
        return putValue(key, value);
    }

    @Override
    public Object putMapList(String key, List<Map<String, Object>> value) {
        return putValue(key, value);
    }

}
