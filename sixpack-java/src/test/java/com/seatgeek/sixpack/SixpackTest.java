package com.seatgeek.sixpack;

import com.google.common.collect.Sets;
import com.seatgeek.sixpack.log.LogLevel;
import com.seatgeek.sixpack.response.AlternativeName;
import com.seatgeek.sixpack.response.ConvertResponse;
import com.seatgeek.sixpack.response.ParticipateResponse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import retrofit.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

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
    public void testParticipateInSuccess() {
        ParticipateResponse response = new ParticipateResponse();
        AlternativeName name = new AlternativeName();
        name.name = "red";
        response.alternative = name;

        when(mockApi.participate(Matchers.<Experiment>anyObject(), Matchers.<List<Alternative>>anyObject(), Matchers.<Alternative>anyObject(), anyDouble(), Matchers.<Boolean>eq(null)))
                .thenReturn(response);

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
    public void testParticipateNetworkFailureReturnsControl() {
        when(mockApi.participate(Matchers.<Experiment>anyObject(), Matchers.<List<Alternative>>anyObject(), Matchers.<Alternative>anyObject(), anyDouble(), Matchers.<Boolean>eq(null)))
                .thenThrow(RetrofitError.networkError("http://sixpack.seatgeek.com", new IOException()));

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
    public void testPrefetchSuccess() {
        ParticipateResponse response = new ParticipateResponse();
        AlternativeName name = new AlternativeName();
        name.name = "red";
        response.alternative = name;

        when(mockApi.participate(Matchers.<Experiment>anyObject(), Matchers.<List<Alternative>>anyObject(), Matchers.<Alternative>anyObject(), anyDouble(), eq(true)))
                .thenReturn(response);

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
    public void testPrefetchNetworkFailureReturnsControl() {
        when(mockApi.participate(Matchers.<Experiment>anyObject(), Matchers.<List<Alternative>>anyObject(), Matchers.<Alternative>anyObject(), anyDouble(), eq(true)))
                .thenThrow(RetrofitError.networkError("http://sixpack.seatgeek.com", new IOException()));

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
    public void testConvertSuccess() {
        Alternative selected = new Alternative("green");

        when(mockApi.convert(Matchers.<Experiment>anyObject())).thenReturn(new ConvertResponse());

        Sixpack sixpack = new Sixpack(mockApi);
        Experiment experiment = new Experiment(sixpack, "test-experience", new HashSet<Alternative>(), null, 1.0d);

        ParticipatingExperiment participating = new ParticipatingExperiment(sixpack, experiment, selected);

        ConvertedExperiment convert = sixpack.convert(participating);

        assertEquals(new ConvertedExperiment(sixpack, experiment), convert);
    }

    @Test(expected = ConversionError.class)
    public void testConvertFailure() {
        Alternative selected = new Alternative("green");

        when(mockApi.convert(Matchers.<Experiment>anyObject())).thenThrow(RetrofitError.networkError("http://sixpack.seatgeek.com", new IOException()));

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
        assertEquals(RestAdapter.LogLevel.FULL, Sixpack.getRetrofitLogLevel(LogLevel.VERBOSE));
        assertEquals(RestAdapter.LogLevel.HEADERS_AND_ARGS, Sixpack.getRetrofitLogLevel(LogLevel.DEBUG));
        assertEquals(RestAdapter.LogLevel.NONE, Sixpack.getRetrofitLogLevel(LogLevel.NONE));
        assertEquals(RestAdapter.LogLevel.NONE, Sixpack.getRetrofitLogLevel(null));
    }

    @Test
    public void testGetClientIdInterceptor() {
        String clientId = "test-client-id";
        RequestInterceptor clientIdInterceptor = Sixpack.getClientIdInterceptor(clientId);

        assertNotNull(clientIdInterceptor);

        RequestInterceptor.RequestFacade mockRequest = mock(RequestInterceptor.RequestFacade.class);
        clientIdInterceptor.intercept(mockRequest);

        verify(mockRequest).addQueryParam("client_id", clientId);
    }

    @Test
    public void testGetSixpackEndpoint() {
        String url = "http://example.com/sixpack";
        Endpoint sixpackEndpoint = Sixpack.getSixpackEndpoint(url);

        assertNotNull(sixpackEndpoint);
        assertEquals(sixpackEndpoint.getName(), "SixPack");
        assertEquals(sixpackEndpoint.getUrl(), url);
    }

    @Test
    public void testGetSixpackEndpointNullUrl() {
        Endpoint sixpackEndpoint = Sixpack.getSixpackEndpoint(null);

        assertNotNull(sixpackEndpoint);
        assertEquals(sixpackEndpoint.getName(), "SixPack");
        assertEquals(sixpackEndpoint.getUrl(), Sixpack.DEFAULT_URL);
    }
}
