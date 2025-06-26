package xyz.bobkinn.indigodataio.gson;

import com.google.gson.JsonPrimitive;
import org.junit.Test;
import xyz.bobkinn.indigodataio.NestedKeyMap;
import xyz.bobkinn.indigodataio.ops.MapOps;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TestGson {
    @Test
    public void builderTest(){
        var b = GsonData.newBuilder()
                .put("k1", 11)
                .down("o")
                .put("k2", 22)
                .down("o2")
                .put("k3", 33)
                .up()
                .put("k4", 23)
                .up()
                .put("k5", 12)
                .build();
        System.out.println("b = " + b);

        b.toBuilder("ib")
                .put("k6", 66)
                .down("o3")
                .put("k7", 77)
                .up()
                .put("k8", 88);
        System.out.println("b = " + b);

        var b2 = GsonData.newBuilder()
                .put("k1", 11)
                .down("o")
                .put("k2", 22)
                .down("o2")
                .build();
        System.out.println("b2 = " + b2);
    }

    @Test
    public void testSectionList(){
        var map = new GsonData();
        map.putMapList("ls", List.of(
                Map.of("k", new JsonPrimitive(1)),
                Map.of("k2", new JsonPrimitive(2))));
        map.putIntArray("ir", new int[]{1, 3, -4});
        System.out.println(map);
        var maps = map.getMapList("ls");
        var mm = maps.get(0);
        System.out.println(mm);
        var ir = map.getIntArray("ir");
        System.out.println("Arrays.toString(ir) = " + Arrays.toString(ir));
        var sections = map.getSectionList("ls");
        System.out.println(sections);
        System.out.println(map);
        var maps2 = map.getMapArray("ls");
        System.out.println(Arrays.toString(maps2));
        map.putBoolean("b", true);

        System.out.println("Converting "+map);
        var converted = GsonOps.INSTANCE.convertMap(MapOps.INSTANCE, map.getRaw());
        System.out.println("converted = " + converted);
    }

    @Test
    public void testIntList(){
        var map = new GsonData();
        map.putIntArray("l", new int[]{3, 3, 3});
        System.out.println(map.getIntList("l", null));
    }

    @Test
    public void testNums(){
        var map = new GsonData();
        map.putShort("s", (short) 1);
        map.putInt("i", 2);
        map.putFloat("f", 3f);
        map.putDouble("d", 4d);
        map.putLong("l", 5L);
        var d = map.getDouble("d");
        assert d == 4.0d;
    }

    @Test
    public void testConvert() {
        var km = NestedKeyMap.newBuilder()
                .put("n",  (Integer) null)
                .put("s", "asdasd")
                .put("f", 3.3f)
                .put("d", 2.2d)
                .put("l", 5000L)
                .down("sasd")
                    .put("s2", "asds")
                    .up()
                .build();
        System.out.println(km);
        var gd = km.convertTo(new GsonData());
        System.out.println(gd);
        assert (km.getFloat("f") == 3.3f) && gd.getFloat("f") == 3.3f;
    }
}
