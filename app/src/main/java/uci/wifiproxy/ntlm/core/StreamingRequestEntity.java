package uci.wifiproxy.ntlm.core;

import org.apache.commons.httpclient.methods.RequestEntity;

import java.io.IOException;
import java.io.OutputStream;

public class StreamingRequestEntity
        implements RequestEntity {

    HttpParser parser;
    byte[] repeatable;

    public StreamingRequestEntity(HttpParser parser) {
        this.parser = parser;
    }

    public long getContentLength() {
        return this.parser.getContentLength();
    }

    public String getContentType() {
        return this.parser.getContentType();
    }

    public boolean isRepeatable() {
        return true;
    }

    public void writeRequest(OutputStream out) throws IOException {
        if (this.repeatable == null) {
            this.repeatable = new byte[(int) getContentLength()];
            long length = getContentLength();
            for (int i = 0; i < length; i++) {
                this.repeatable[i] = (byte) this.parser.read();
            }
        }
        out.write(this.repeatable);
    }
}
