package uci.wifiproxy.proxycore.core;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.message.BasicHeader;

import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

/* *
 * Esta clase analiza las peticiones http
 * */

public class HttpParser extends InputStream {

    static List methods = Arrays.asList(new String[]{"GET", "HEAD", "POST"});
    int index;
    boolean first;
    byte[] buffer = new byte[4096];
    InputStream is;
    String method;
    String uri;
    String protocol;
    int bodyIndex;
    int contentLength;
    String contentEncoding;
    String contentType;
    Header[] headers;

    public HttpParser(InputStream is) {
        this.is = is;

    }

    public int read()
            throws IOException {
        if (this.bodyIndex < this.index) {
            return this.buffer[(this.bodyIndex++)];
        }
        return this.is.read();
    }

    public int read(byte[] b, int off, int len) throws IOException {
        if (this.bodyIndex < this.index) {
            int toCopy = Math.min(len, this.index - this.bodyIndex);
            System.arraycopy(this.buffer, this.bodyIndex, b, off, toCopy);
            this.bodyIndex += toCopy;
            return toCopy;
        }
        return this.is.read(b, off, len);
    }

    public boolean parse() throws IOException, ParseException {

        this.index += this.is.read(this.buffer, this.index, this.buffer.length - this.index);
        String line = new String(this.buffer);

        int splitAt = line.indexOf("\r\n\r\n");
        if (splitAt == -1) {
            return false;
        }
        this.bodyIndex = (splitAt + 4);

        line = line.substring(0, splitAt);
        if (line.length() == 0) {
            throw new IOException("Bad HTTP header");
        }
        String[] headerLines = line.split("\r\n");
        if (headerLines.length == 0) {
            throw new IOException("Bad HTTP header");
        }
        String[] httpStuff = headerLines[0].split(" ");
        if (httpStuff.length != 3) {
            throw new IOException("Bad HTTP header: " + httpStuff.length);
        }
        this.method = httpStuff[0];
        this.uri = httpStuff[1];
        this.protocol = httpStuff[2];


        this.headers = new Header[0];

        this.headers = new Header[headerLines.length - 1];
        for (int i = 1; i < headerLines.length; i++) {
            String[] header = headerLines[i].split(": ", 2);
            if (header.length != 2) {
                throw new IOException("Bad Header:" + headerLines[i]);
            }
            Header h = this.headers[(i - 1)] = new BasicHeader(header[0], header[1]);

            if (h.getName().equals("Content-Type")) {
                this.contentType = h.getValue();
            } else if (h.getName().equals("Content-Length")) {
                this.contentLength = NumberFormat.getIntegerInstance().parse(h.getValue()).intValue();
            }
            else if (h.getName().equals("Content-Encoding")){
                this.contentEncoding = h.getValue();
            }

        }
        return true;
    }

    public String getMethod() {
        return this.method;
    }

    public String getUri() {
        return this.uri;
    }

    public String getProtocol() {
        return this.protocol;
    }

    public int getLength() {
        return this.buffer.length;
    }

    public int getBodyIndex() {
        return this.bodyIndex;
    }

    public void setBodyIndex(int i) {
        this.bodyIndex = i;
    }

    public int getContentLength() {
        return this.contentLength;
    }

    public String getContentEncoding(){
        return this.contentEncoding;
    }

    public String getContentType() {
        return this.contentType;
    }

    public Header[] getHeaders() {
        return this.headers;
    }

    public void close() throws IOException {
        this.is.close();
    }

    public byte[] getBuffer() {
        return this.buffer;
    }

    public int getIndex() {
        return this.index;
    }
}
