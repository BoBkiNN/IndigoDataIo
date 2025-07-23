package xyz.bobkinn.indigodataio.gson.io;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import xyz.bobkinn.indigodataio.gson.GsonData;

import java.io.*;
import java.lang.reflect.Type;

@SuppressWarnings("unused")
public class JsonIo {
    public static final GsonDataAdapter ADAPTER = new GsonDataAdapter();
    public static final WriterOptions DEFAULT_OPTIONS = WriterOptions.NORMAL;

    public static class GsonDataAdapter implements JsonSerializer<GsonData>, JsonDeserializer<GsonData> {

        @Override
        public JsonElement serialize(GsonData src, Type typeOfSrc, JsonSerializationContext context) {
            return src.getRaw();
        }

        @Override
        public GsonData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext ctx) throws JsonParseException {
            if (json instanceof JsonObject o){
                return new GsonData(o);
            }
            throw new JsonParseException("JsonElement must be JsonObject to construct GsonData");
        }
    }

    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(GsonData.class, ADAPTER)
            .create();

    private static void createParentFolder(File folder){
        if (!folder.isDirectory()) if (!folder.mkdirs()) throw new IllegalStateException("Failed to create folder "+folder);
    }

    public static void write(GsonData data, File to, WriterOptions options){
        createParentFolder(to.getParentFile());
        try (var wr = new JsonWriter(new BufferedWriter(new FileWriter(to, options.getCharset())))) {
            options.apply(wr);
            GSON.toJson(data, GsonData.class, wr);
        } catch (JsonIOException e){
            throw new RuntimeException("Failed to write json to "+to, e);
        } catch (IOException e){
            throw new RuntimeException("Failed to write file "+to, e);
        }
    }

    public static void write(GsonData data, File to){
        write(data, to, DEFAULT_OPTIONS);
    }

    public static GsonData read(File from, WriterOptions options){
        if (!from.isFile()) throw new IllegalArgumentException("File "+from+" does not exists or is directory");
        try (var r = new JsonReader(new BufferedReader(new FileReader(from, options.getCharset())))) {
            return GSON.fromJson(r, GsonData.class);
        } catch (JsonIOException e){
            throw new RuntimeException("Failed to read json to "+from, e);
        } catch (IOException e){
            throw new RuntimeException("Failed to read file "+from, e);
        }
    }

    public static GsonData read(File from){
        return read(from, DEFAULT_OPTIONS);
    }

}
