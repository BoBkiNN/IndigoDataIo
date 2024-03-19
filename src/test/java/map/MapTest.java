package map;

import org.junit.Test;
import xyz.bobkinn.indigodataio.NestedKeyMap;

import java.util.List;
import java.util.Map;

public class MapTest{
    @Test
    public void testSectionList(){
        var map = new NestedKeyMap();
        map.put("ls", List.of(Map.of("k", 1), Map.of("k2", 2)));
        System.out.println(map);
        var sections = map.getSections("ls");
        System.out.println(sections);
    }

    @Test
    public void testIntList(){
        var map = new NestedKeyMap();
        map.put("l", new int[]{3, 3, 3});
        System.out.println(map.getIntList("l", null));
    }

    @Test
    public void testNums(){
        var map = new NestedKeyMap();
        map.put("s", (short) 1);
        map.put("i", 2);
        map.put("f", 3f);
        map.put("d", 4d);
        map.put("l", 5L);
        var d = map.getDouble("d");
        assert d != null;
        assert d == 4.0d;
    }
}
