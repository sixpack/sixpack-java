package com.seatgeek.sixpack;

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

        sixpack.setLogLevel(LogLevel.BASIC);

        assertEquals(sixpack.getLogLevel(), LogLevel.BASIC);
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
        OnParticipationSuccess mockSuccess = mock(OnParticipationSuccess.class);

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Callback cb = (Callback) invocation.getArguments()[4];

                ParticipateResponse response = new ParticipateResponse();
                AlternativeName name = new AlternativeName();
                name.name = "green";
                response.alternative = name;
                cb.success(response, null);
                return null;
            }
        }).when(mockApi).participate(Matchers.<Experiment>anyObject(), Matchers.<List<Alternative>>anyObject(), Matchers.<Alternative>anyObject(), anyDouble(),  Matchers.<Callback>anyObject());

        Sixpack sixpack = new Sixpack(mockApi);
        Experiment experiment = new Experiment(sixpack, "test-experience", new HashSet<Alternative>(), null, 1.0d);

        sixpack.participateIn(experiment, mockSuccess, null);

        verify(mockSuccess).onParticipation(new ParticipatingExperiment(sixpack, experiment, new Alternative("green")));
    }

    @Test
    public void testParticipateInSuccessNullCallbackDoesntThrow() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Callback cb = (Callback) invocation.getArguments()[4];

                ParticipateResponse response = new ParticipateResponse();
                AlternativeName name = new AlternativeName();
                name.name = "green";
                response.alternative = name;
                cb.success(response, null);
                return null;
            }
        }).when(mockApi).participate(Matchers.<Experiment>anyObject(), Matchers.<List<Alternative>>anyObject(), Matchers.<Alternative>anyObject(), anyDouble(),  Matchers.<Callback>anyObject());

        Sixpack sixpack = new Sixpack(mockApi);
        Experiment experiment = new Experiment(sixpack, "test-experience", new HashSet<Alternative>(), null, 1.0d);

        sixpack.participateIn(experiment, null, null);
    }

    @Test
    public void testParticipateInFailure() {
        OnParticipationFailure mockFailure = mock(OnParticipationFailure.class);
        final RetrofitError error = RetrofitError.unexpectedError(Sixpack.DEFAULT_URL, new RuntimeException());

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Callback cb = (Callback) invocation.getArguments()[4];

                cb.failure(error);
                return null;
            }
        }).when(mockApi).participate(Matchers.<Experiment>anyObject(), Matchers.<List<Alternative>>anyObject(), Matchers.<Alternative>anyObject(), anyDouble(),  Matchers.<Callback>anyObject());

        Sixpack sixpack = new Sixpack(mockApi);
        Experiment experiment = new Experiment(sixpack, "test-experience", new HashSet<Alternative>(), null, 1.0d);

        sixpack.participateIn(experiment, null, mockFailure);

        verify(mockFailure).onParticipationFailed(experiment, error);
    }

    @Test
    public void testParticipateInFailureNullCallbackDoesntThrow() {
        final RetrofitError error = RetrofitError.unexpectedError(Sixpack.DEFAULT_URL, new RuntimeException());

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Callback cb = (Callback) invocation.getArguments()[4];

                cb.failure(error);
                return null;
            }
        }).when(mockApi).participate(Matchers.<Experiment>anyObject(), Matchers.<List<Alternative>>anyObject(), Matchers.<Alternative>anyObject(), anyDouble(),  Matchers.<Callback>anyObject());

        Sixpack sixpack = new Sixpack(mockApi);
        Experiment experiment = new Experiment(sixpack, "test-experience", new HashSet<Alternative>(), null, 1.0d);

        sixpack.participateIn(experiment, null, null);
    }

    @Test
    public void testConvertSuccess() {
        OnConvertSuccess mockSuccess = mock(OnConvertSuccess.class);
        Alternative selected = new Alternative("green");

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Callback cb = (Callback) invocation.getArguments()[1];

                ConvertResponse response = new ConvertResponse();
                cb.success(response, null);
                return null;
            }
        }).when(mockApi).convert(Matchers.<Experiment>anyObject(), Matchers.<Callback>anyObject());

        Sixpack sixpack = new Sixpack(mockApi);
        Experiment experiment = new Experiment(sixpack, "test-experience", new HashSet<Alternative>(), null, 1.0d);

        ParticipatingExperiment participating = new ParticipatingExperiment(sixpack, experiment, selected);

        sixpack.convert(participating, mockSuccess, null);

        verify(mockSuccess).onConverted(new ConvertedExperiment(sixpack, experiment));
    }

    @Test
    public void testConvertSuccessNullCallbackDoesntThrow() {
        Alternative selected = new Alternative("green");

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Callback cb = (Callback) invocation.getArguments()[1];

                ConvertResponse response = new ConvertResponse();
                cb.success(response, null);
                return null;
            }
        }).when(mockApi).convert(Matchers.<Experiment>anyObject(), Matchers.<Callback>anyObject());

        Sixpack sixpack = new Sixpack(mockApi);
        Experiment experiment = new Experiment(sixpack, "test-experience", new HashSet<Alternative>(), null, 1.0d);

        ParticipatingExperiment participating = new ParticipatingExperiment(sixpack, experiment, selected);

        sixpack.convert(participating, null, null);
    }

    @Test
    public void testConvertFailure() {
        OnConvertFailure mockFailure = mock(OnConvertFailure.class);
        final RetrofitError error = RetrofitError.unexpectedError(Sixpack.DEFAULT_URL, new RuntimeException());

        Alternative selected = new Alternative("green");

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Callback cb = (Callback) invocation.getArguments()[1];

                cb.failure(error);
                return null;
            }
        }).when(mockApi).convert(Matchers.<Experiment>anyObject(), Matchers.<Callback>anyObject());

        Sixpack sixpack = new Sixpack(mockApi);
        Experiment experiment = new Experiment(sixpack, "test-experience", new HashSet<Alternative>(), null, 1.0d);

        ParticipatingExperiment participating = new ParticipatingExperiment(sixpack, experiment, selected);

        sixpack.convert(participating, null, mockFailure);

        verify(mockFailure).onConvertFailure(participating, error);
    }

    @Test
    public void testConvertFailureNullCallbackDoesntThrow() {
        final RetrofitError error = RetrofitError.unexpectedError(Sixpack.DEFAULT_URL, new RuntimeException());

        Alternative selected = new Alternative("green");

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Callback cb = (Callback) invocation.getArguments()[1];

                cb.failure(error);
                return null;
            }
        }).when(mockApi).convert(Matchers.<Experiment>anyObject(), Matchers.<Callback>anyObject());

        Sixpack sixpack = new Sixpack(mockApi);
        Experiment experiment = new Experiment(sixpack, "test-experience", new HashSet<Alternative>(), null, 1.0d);

        ParticipatingExperiment participating = new ParticipatingExperiment(sixpack, experiment, selected);

        sixpack.convert(participating, null, null);
    }

    @Test
    public void testGetParticipateCallback() {
        Sixpack sixpack = new Sixpack(mockApi);
        Experiment experiment = new Experiment(sixpack, "test-experience", new HashSet<Alternative>(), null, 1.0d);

        assertNotNull(sixpack.getParticipateCallback(experiment, null, null));
    }

    @Test
    public void testGetConvertCallback() {
        Sixpack sixpack = new Sixpack(mockApi);
        Experiment experiment = new Experiment(sixpack, "test-experience", new HashSet<Alternative>(), null, 1.0d);

        assertNotNull(sixpack.getConvertCallback(new ParticipatingExperiment(sixpack, experiment, new Alternative("test")), null, null));
    }

    @Test
    public void testGetDefaultApi() {
        assertNotNull(Sixpack.getDefaultApi(Sixpack.DEFAULT_URL, "client_id", LogLevel.NONE, null));
    }

    @Test
    public void testGetRetrofitLogLevel() {
        assertEquals(RestAdapter.LogLevel.FULL, Sixpack.getRetrofitLogLevel(LogLevel.VERBOSE));
        assertEquals(RestAdapter.LogLevel.BASIC, Sixpack.getRetrofitLogLevel(LogLevel.BASIC));
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
