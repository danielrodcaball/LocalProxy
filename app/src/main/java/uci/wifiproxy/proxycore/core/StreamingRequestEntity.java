package uci.wifiproxy.proxycore.core;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

public class StreamingRequestEntity
        implements HttpEntity {

    HttpParser parser;
    byte[] repeatable;

    public StreamingRequestEntity(HttpParser parser) {
        this.parser = parser;
    }

    public long getContentLength() {
        return this.parser.getContentLength();
    }

    public Header getContentType() {
        return new BasicHeader(HTTP.CONTENT_TYPE, this.parser.getContentType());
    }

    @Override
    public Header getContentEncoding() {
        return new BasicHeader(HTTP.CONTENT_ENCODING, parser.getContentEncoding());
    }

    @Override
    public InputStream getContent() throws IOException, UnsupportedOperationException {
        return this.parser;
    }

    @Override
    public void writeTo(OutputStream out) throws IOException {
        if (this.repeatable == null) {
            this.repeatable = new byte[(int) getContentLength()];
            long length = getContentLength();
            for (int i = 0; i < length; i++) {
                this.repeatable[i] = (byte) this.parser.read();
            }
        }
        out.write(this.repeatable);
    }

    @Override
    public boolean isStreaming() {
        return false;
    }

    @Override
    public void consumeContent() throws IOException {

    }

    public boolean isRepeatable() {
        return true;
    }

    @Override
    public boolean isChunked() {
        return false;
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
