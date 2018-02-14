package com.fasterxml.jackson.core.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter.NopIndenter;
import java.io.IOException;

public class DefaultIndenter extends NopIndenter {
    public static final DefaultIndenter SYSTEM_LINEFEED_INSTANCE = new DefaultIndenter("  ", SYS_LF);
    public static final String SYS_LF;
    private final int charsPerLevel;
    private final String eol;
    private final char[] indents;

    static {
        String lf;
        try {
            lf = System.getProperty("line.separator");
        } catch (Throwable th) {
            lf = "\n";
        }
        SYS_LF = lf;
    }

    public DefaultIndenter() {
        this("  ", SYS_LF);
    }

    public DefaultIndenter(String indent, String eol) {
        this.charsPerLevel = indent.length();
        this.indents = new char[(indent.length() << 4)];
        int offset = 0;
        for (int i = 0; i < 16; i++) {
            indent.getChars(0, indent.length(), this.indents, offset);
            offset += indent.length();
        }
        this.eol = eol;
    }

    public boolean isInline() {
        return false;
    }

    public void writeIndentation(JsonGenerator jg, int level) throws IOException {
        jg.writeRaw(this.eol);
        if (level > 0) {
            level *= this.charsPerLevel;
            while (level > this.indents.length) {
                jg.writeRaw(this.indents, 0, this.indents.length);
                level -= this.indents.length;
            }
            jg.writeRaw(this.indents, 0, level);
        }
    }
}
