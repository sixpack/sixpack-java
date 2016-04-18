package com.seatgeek.sixpack;

import com.seatgeek.sixpack.log.LogLevel;
import com.seatgeek.sixpack.response.AlternativeName;
import com.seatgeek.sixpack.response.ConvertResponse;
import com.seatgeek.sixpack.response.ParticipateResponse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Call;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class SixpackTest {

    @Mock SixpackApi mockApi;

    String clientId;

    @Before
    public void setUp() {
        initMocks(this);

        clientId = Sixpack.generateRandomClientId();
    }

    @Test
    public void testUrlConstructor() {
        Sixpack sixpack = new Sixpack(Sixpack.DEFAULT_URL, clientId, null);

        assertNotNull(sixpack);
        assertEquals(clientId, sixpack.getClientId());
    }


    @Test
    public void testApiConstructor() {
        Sixpack sixpack = new Sixpack(mockApi, clientId);

        assertNotNull(sixpack);
        assertEquals(clientId, sixpack.getClientId());
    }

    @Test
    public void testApiConstructorDefaultClientId() {
        Sixpack sixpack = new Sixpack(mockApi);

        assertNotNull(sixpack);
        assertNotNull(sixpack.getClientId());
    }

    @Test
    public void testGetLogLevel() {
        Sixpack sixpack = new Sixpack(mockApi);

        sixpack.setLogLevel(LogLevel.VERBOSE);

        assertEquals(sixpack.getLogLevel(), LogLevel.VERBOSE);
    }

    @Test
    public void testGenerateRandomClientId() {
        String clientIdA = Sixpack.generateRandomClientId();
        String clientIdB = Sixpack.generateRandomClientId();

        assertNotNull(clientIdA);
        assertNotNull(clientIdB);
        assertTrue(clientIdA.length() > 0);
        assertTrue(clientIdB.length() > 0);
        assertNotEquals(clientIdA, clientIdB);
    }

    @Test
    public void testExperimentNotNull() {
        Sixpack sixpack = new Sixpack(mockApi);

        assertNotNull(sixpack.experiment());
    }

    @Test
    public void testParticipateInSuccess() throws IOException {
        ParticipateResponse response = new ParticipateResponse();
        AlternativeName name = new AlternativeName();
        name.name = "red";
        response.alternative = name;

        Call<ParticipateResponse> call = mock(Call.class);
        when(call.execute()).thenReturn(retrofit2.Response.success(response));

        when(mockApi.participate(Matchers.<Experiment>anyObject(), Matchers.<List<Alternative>>anyObject(), Matchers.<Alternative>anyObject(), anyDouble(), Matchers.<Boolean>eq(null)))
                .thenReturn(call);

        Sixpack sixpack = new Sixpack(mockApi);

        Alternative greenAlternative = new Alternative("green");
        Alternative redAlternative = new Alternative("red");

        Experiment experiment = new ExperimentBuilder(sixpack)
                .withName("test-experience")
                .withAlternatives(greenAlternative, redAlternative)
                .build();
        ParticipatingExperiment participatingExperiment = sixpack.participate(experiment);

        assertEquals(new ParticipatingExperiment(sixpack, experiment, redAlternative), participatingExperiment);
    }

    @Test
    public void testParticipateNetworkFailureReturnsControl() throws IOException {
        Call<ParticipateResponse> mockCall = mock(Call.class);
        when(mockCall.execute()).thenThrow(new IOException());
        when(mockApi.participate(Matchers.<Experiment>anyObject(), Matchers.<List<Alternative>>anyObject(), Matchers.<Alternative>anyObject(), anyDouble(), Matchers.<Boolean>eq(null)))
                .thenReturn(mockCall);

        Sixpack sixpack = new Sixpack(mockApi);

        Alternative greenAlternative = new Alternative("green");
        Alternative redAlternative = new Alternative("red");

        Experiment experiment = new ExperimentBuilder(sixpack)
                .withName("test-experience")
                .withAlternatives(greenAlternative, redAlternative)
                .build();

        ParticipatingExperiment participatingExperiment = sixpack.participate(experiment);

        assertEquals(new ParticipatingExperiment(sixpack, experiment, greenAlternative), participatingExperiment);
    }

    @Test
    public void testParticipateFailedResponseReturnsControl() throws IOException {
        Call<ParticipateResponse> mockCall = mock(Call.class);
        when(mockCall.execute()).thenReturn(retrofit2.Response.<ParticipateResponse>error(404, ResponseBody.create(MediaType.parse("application/json"), "{}")));
        when(mockApi.participate(Matchers.<Experiment>anyObject(), Matchers.<List<Alternative>>anyObject(), Matchers.<Alternative>anyObject(), anyDouble(), Matchers.<Boolean>eq(null)))
                .thenReturn(mockCall);

        Sixpack sixpack = new Sixpack(mockApi);

        Alternative greenAlternative = new Alternative("green");
        Alternative redAlternative = new Alternative("red");

        Experiment experiment = new ExperimentBuilder(sixpack)
                .withName("test-experience")
                .withAlternatives(greenAlternative, redAlternative)
                .build();

        ParticipatingExperiment participatingExperiment = sixpack.participate(experiment);

        assertEquals(new ParticipatingExperiment(sixpack, experiment, greenAlternative), participatingExperiment);
    }

    @Test
    public void testPrefetchSuccess() throws IOException {
        ParticipateResponse response = new ParticipateResponse();
        AlternativeName name = new AlternativeName();
        name.name = "red";
        response.alternative = name;

        Call<ParticipateResponse> call = mock(Call.class);
        when(call.execute()).thenReturn(retrofit2.Response.success(response));

        when(mockApi.participate(Matchers.<Experiment>anyObject(), Matchers.<List<Alternative>>anyObject(), Matchers.<Alternative>anyObject(), anyDouble(), eq(true)))
                .thenReturn(call);

        Sixpack sixpack = new Sixpack(mockApi);

        Alternative greenAlternative = new Alternative("green");
        Alternative redAlternative = new Alternative("red");

        Experiment experiment = new ExperimentBuilder(sixpack)
                .withName("test-experience")
                .withAlternatives(greenAlternative, redAlternative)
                .build();

        PrefetchedExperiment prefetched = sixpack.prefetch(experiment);

        assertEquals(new PrefetchedExperiment(sixpack, experiment, redAlternative), prefetched);
    }

    @Test
    public void testPrefetchNetworkFailureReturnsControl() throws IOException {
        Call<ParticipateResponse> mockCall = mock(Call.class);
        when(mockCall.execute()).thenThrow(new IOException());
        when(mockApi.participate(Matchers.<Experiment>anyObject(), Matchers.<List<Alternative>>anyObject(), Matchers.<Alternative>anyObject(), anyDouble(), eq(true)))
                .thenReturn(mockCall);

        Sixpack sixpack = new Sixpack(mockApi);

        Alternative greenAlternative = new Alternative("green");
        Alternative redAlternative = new Alternative("red");

        Experiment experiment = new ExperimentBuilder(sixpack)
                .withName("test-experience")
                .withAlternatives(greenAlternative, redAlternative)
                .build();

        PrefetchedExperiment prefetched = sixpack.prefetch(experiment);

        assertEquals(new PrefetchedExperiment(sixpack, experiment, greenAlternative), prefetched);
    }

    @Test
    public void testPrefetchFailedResponseReturnsControl() throws IOException {
        Call<ParticipateResponse> mockCall = mock(Call.class);
        when(mockCall.execute()).thenReturn(retrofit2.Response.<ParticipateResponse>error(404, ResponseBody.create(MediaType.parse("application/json"), "{}")));
        when(mockApi.participate(Matchers.<Experiment>anyObject(), Matchers.<List<Alternative>>anyObject(), Matchers.<Alternative>anyObject(), anyDouble(), eq(true)))
                .thenReturn(mockCall);

        Sixpack sixpack = new Sixpack(mockApi);

        Alternative greenAlternative = new Alternative("green");
        Alternative redAlternative = new Alternative("red");

        Experiment experiment = new ExperimentBuilder(sixpack)
                .withName("test-experience")
                .withAlternatives(greenAlternative, redAlternative)
                .build();

        PrefetchedExperiment prefetched = sixpack.prefetch(experiment);

        assertEquals(new PrefetchedExperiment(sixpack, experiment, greenAlternative), prefetched);
    }


    @Test
    public void testConvertSuccess() throws IOException {
        Alternative selected = new Alternative("green");

        Call<ConvertResponse> call = mock(Call.class);
        when(call.execute()).thenReturn(retrofit2.Response.success(new ConvertResponse()));

        when(mockApi.convert(Matchers.<Experiment>anyObject())).thenReturn(call);

        Sixpack sixpack = new Sixpack(mockApi);
        Experiment experiment = new Experiment(sixpack, "test-experience", new HashSet<Alternative>(), null, 1.0d);

        ParticipatingExperiment participating = new ParticipatingExperiment(sixpack, experiment, selected);

        ConvertedExperiment convert = sixpack.convert(participating);

        assertEquals(new ConvertedExperiment(sixpack, experiment), convert);
    }

    @Test(expected = ConversionError.class)
    public void testConvertNetworkFailure() throws IOException {
        Alternative selected = new Alternative("green");

        Call<ConvertResponse> mockCall = mock(Call.class);
        when(mockCall.execute()).thenReturn(retrofit2.Response.<ConvertResponse>error(404, ResponseBody.create(MediaType.parse("application/json"), "{}")));
        when(mockApi.convert(Matchers.<Experiment>anyObject())).thenReturn(mockCall);

        Sixpack sixpack = new Sixpack(mockApi);
        Experiment experiment = new Experiment(sixpack, "test-experience", new HashSet<Alternative>(), null, 1.0d);

        ParticipatingExperiment participating = new ParticipatingExperiment(sixpack, experiment, selected);

        sixpack.convert(participating);
    }

    @Test(expected = ConversionError.class)
    public void testConvertFailedResponse() throws IOException {
        Alternative selected = new Alternative("green");

        Call<ConvertResponse> mockCall = mock(Call.class);
        when(mockCall.execute()).thenThrow(new IOException());
        when(mockApi.convert(Matchers.<Experiment>anyObject())).thenReturn(mockCall);

        Sixpack sixpack = new Sixpack(mockApi);
        Experiment experiment = new Experiment(sixpack, "test-experience", new HashSet<Alternative>(), null, 1.0d);

        ParticipatingExperiment participating = new ParticipatingExperiment(sixpack, experiment, selected);

        sixpack.convert(participating);
    }

    @Test
    public void testGetDefaultApi() {
        assertNotNull(Sixpack.getDefaultApi(Sixpack.DEFAULT_URL, "client_id", LogLevel.NONE, null));
    }

    @Test
    public void testGetRetrofitLogLevel() {
        assertEquals(HttpLoggingInterceptor.Level.BODY, Sixpack.getRetrofitLogLevel(LogLevel.VERBOSE));
        assertEquals(HttpLoggingInterceptor.Level.HEADERS, Sixpack.getRetrofitLogLevel(LogLevel.DEBUG));
        assertEquals(HttpLoggingInterceptor.Level.NONE, Sixpack.getRetrofitLogLevel(LogLevel.NONE));
        assertEquals(HttpLoggingInterceptor.Level.NONE, Sixpack.getRetrofitLogLevel(null));
    }

    @Test
    public void testGetClientIdInterceptor() throws IOException {
        String clientId = "test-client-id";

        MockWebServer webServer = new MockWebServer();
        webServer.enqueue(new MockResponse());
        webServer.start();
        HttpUrl url = webServer.url("/");

        Interceptor clientIdInterceptor = Sixpack.getClientIdInterceptor(clientId);
        assertNotNull(clientIdInterceptor);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(clientIdInterceptor)
                .build();

        Response response = client.newCall(new Request.Builder().url(url).build()).execute();

        assertEquals(clientId, response.request().url().queryParameter("client_id"));
    }


    @Test
    public void testGetSixpackEndpoint() {
        HttpUrl url = HttpUrl.parse("http://example.com/sixpack");
        HttpUrl sixpackEndpoint = Sixpack.getSixpackEndpoint(url);

        assertNotNull(sixpackEndpoint);
        assertEquals(sixpackEndpoint.toString(), url.toString());
    }

    @Test
    public void testGetSixpackEndpointNullUrl() {
        HttpUrl sixpackEndpoint = Sixpack.getSixpackEndpoint(null);

        assertNotNull(sixpackEndpoint);
        assertEquals(sixpackEndpoint.toString(), Sixpack.DEFAULT_URL.toString());
    }
}
