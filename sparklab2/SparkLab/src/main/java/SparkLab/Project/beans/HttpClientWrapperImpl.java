package SparkLab.Project.beans;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;

public class HttpClientWrapperImpl implements HttpClientWrapper {
    private CloseableHttpClient httpClient;

    public HttpClientWrapperImpl() {
        this.httpClient = HttpClients.createDefault();
    }

    public CloseableHttpResponse execute(HttpUriRequest request) throws IOException {
        return httpClient.execute(request);
    }
}

