import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cz.msebera.android.httpclient.HttpHost;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.CloseableHttpClient;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import uci.wifiproxy.proxy.core.HttpForwarder;

import static org.junit.Assert.assertEquals;

/**
 * Created by daniel on 3/04/18.
 */

@RunWith(AndroidJUnit4.class)
@SmallTest
public class HttpForwarderTest {

    Context mMockContext;

    private ExecutorService executor;

    private CloseableHttpClient delegateClient;

    HttpForwarder httpForwarder;

    @Before
    public void initVars() {
        executor = Executors.newSingleThreadExecutor();
        mMockContext = InstrumentationRegistry.getTargetContext();
        delegateClient = HttpClientBuilder.create()
                .setProxy(new HttpHost("127.0.0.1", 8080))
                .build();
        httpForwarder = null;
        try {
            httpForwarder = new HttpForwarder("10.0.0.1", 8080,
                    "darodriguez", "Estoesparaquemesirvaelpi098", 8080,
                    true, "", "", mMockContext);
        } catch (IOException e) {
            e.printStackTrace();
        }
        executor.execute(httpForwarder);
    }

    @Test
    public void httpRequestsValidator() {
        try {
            HttpGet request0 = new HttpGet("http://cubadebate.cu");
            HttpGet request1 = new HttpGet("http://google.com.cu");
            HttpResponse response = null;
            response = delegateClient.execute(request0);
            assertEquals(response.getStatusLine().toString(), "HTTP/1.0 200 OK");
            response = delegateClient.execute(request1);
            assertEquals(response.getStatusLine().toString(), "HTTP/1.0 200 OK");
        } catch (IOException e) {
            e.printStackTrace();
        }
        httpForwarder.halt();
        executor.shutdown();
    }

    @Test
    public void httpsRequestsValidator() {
        try {
            HttpGet request0 = new HttpGet("https://stackoverflow.com");
            HttpGet request1 = new HttpGet("http://github.com");
            HttpResponse response = null;
            response = delegateClient.execute(request0);
            assertEquals(response.getStatusLine().toString(), "HTTP/1.1 200 OK");
            response = delegateClient.execute(request1);
            assertEquals(response.getStatusLine().toString(), "HTTP/1.1 200 OK");
        } catch (IOException e) {
            e.printStackTrace();
        }
        httpForwarder.halt();
        executor.shutdown();
    }

}
