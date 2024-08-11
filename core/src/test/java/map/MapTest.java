package map;

import org.junit.Test;
import xyz.bobkinn.indigodataio.NestedKeyMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MapTest {

    @Test
    public void builderTest(){
        var b = NestedKeyMap.newBuilder()
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

        var b2 = NestedKeyMap.newBuilder()
                .put("k1", 11)
                .down("o")
                .put("k2", 22)
                .down("o2")
                .build();
        System.out.println("b2 = " + b2);
    }

    @Test
    public void testSectionList(){
        var map = new NestedKeyMap();
        map.put("ls", List.of(Map.of("k", 1), Map.of("k2", 2)));
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
    }

    @Test
    public void testLists(){
        var map = new NestedKeyMap();
        map.putIntList("i", List.of(1231, 123123, 123123));
        map.putStringList("s", List.of("asdasd", "asdasdasd", "ieiei"));
        map.putMapList("m", List.of(Map.of("k1", 234, "k2", "234")));
        assert map.getIntList("i").getClass() == ArrayList.class;
        assert map.getStringList("s").getClass() == ArrayList.class;
        assert map.getSectionList("m").getClass() == ArrayList.class;
    }

    @Test
    public void testIntList(){
        var map = new NestedKeyMap();
        map.put("l", new int[]{3, 3, 3});
        System.out.println(Arrays.toString(map.getIntArray("l", null)));
    }

    @Test
    public void testNums(){
        var map = new NestedKeyMap();
        map.putValue("s", (short) 1);
        map.putValue("i", 2);
        map.putValue("f", 3f);
        map.putValue("d", 4d);
        map.putValue("l", 5L);
        var d = map.getDouble("d");
        assert d == 4.0d;
        System.out.println("map = " + map);
    }
}
