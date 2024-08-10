package xyz.bobkinn.indigodataio.gson.io;

import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;
import lombok.Getter;
import lombok.Setter;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;


@SuppressWarnings("LombokSetterMayBeUsed")
@Getter
public class WriterOptions {
    public static final WriterOptions MINIMIZED = new WriterOptions();
    /**
     * Equals to {@link GsonBuilder#setPrettyPrinting()}
     */
    public static final WriterOptions NORMAL = of(2);
    public static final WriterOptions TABS = new WriterOptions();

    public static WriterOptions of(int indent){
        var ret = new WriterOptions();
        ret.setIndent(indent);
        return ret;
    }

    static {
        MINIMIZED.setMinimized();
        TABS.setIndent("\t");
    }

    private String indent = "";
    @Setter
    private Charset charset = StandardCharsets.UTF_8;

    public void setMinimized(){
        setIndent(0);
    }

    public void setIndent(int indent){
        if (indent <= 0 ) setIndent("");
        else setIndent(" ".repeat(indent));
    }

    public void setIndent(String indent){
        this.indent = indent;
    }

    public void apply(JsonWriter w){
        w.setIndent(indent);
    }
}
