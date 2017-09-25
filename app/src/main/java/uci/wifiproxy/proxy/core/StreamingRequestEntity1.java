//package uci.ucintlm.ntlm.core;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//
//import cz.msebera.android.httpclient.Header;
//import cz.msebera.android.httpclient.HttpEntity;
//import cz.msebera.android.httpclient.message.BasicHeader;
//
///**
// * Created by daniel on 17/04/17.
// */
//
//public class StreamingRequestEntity1 implements HttpEntity {
//
//    HttpParser parser;
//    byte[] repeatable;
//
//    public StreamingRequestEntity1(HttpParser parser) {
//        this.parser = parser;
//    }
//
//    @Override
//    public boolean isRepeatable() {
//        return true;
//    }
//
//    @Override
//    public boolean isChunked() {
//        return false;
//    }
//
//    @Override
//    public long getContentLength() {
//        return this.parser.getContentLength();
//    }
//
//    @Override
//    public Header getContentType() {
//        return new BasicHeader("Content-Type", this.parser.getContentType());
//    }
//
//    @Override
//    public Header getContentEncoding() {
//        return new BasicHeader("Content-Encoding", this.parser.getContentEncoding());
//    }
//
//    @Override
//    public InputStream getContent() throws IOException, UnsupportedOperationException {
//        return this.parser;
//    }
//
//    @Override
//    public void writeTo(OutputStream outputStream) throws IOException {
//        if (this.repeatable == null) {
//            this.repeatable = new byte[(int) getContentLength()];
//            long length = getContentLength();
//            for (int i = 0; i < length; i++) {
//                this.repeatable[i] = (byte) this.parser.read();
//            }
//        }
//        outputStream.write(this.repeatable);
//    }
//
//    @Override
//    public boolean isStreaming() {
//        return false;
//    }
//
//    @Override
//    public void consumeContent() throws IOException {
//
//    }
//}
