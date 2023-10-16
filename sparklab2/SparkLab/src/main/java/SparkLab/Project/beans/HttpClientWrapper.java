package SparkLab.Project.beans;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;

import java.io.IOException;

public interface HttpClientWrapper {
    CloseableHttpResponse execute(HttpUriRequest request) throws IOException;
}

