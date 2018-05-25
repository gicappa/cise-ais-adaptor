package eu.cise.adaptor;


import com.greghaskins.spectrum.Spectrum;
import eu.cise.adaptor.dispatch.DelayedProcessor;
import eu.cise.adaptor.dispatch.Dispatcher;
import eu.cise.servicemodel.v1.message.Push;
import org.junit.runner.RunWith;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static eu.eucise.helpers.PushBuilder.newPush;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(Spectrum.class)
public class DelayedDispatcherSpec {
    {
        describe("aggregates a stream of messages", () -> {

            Dispatcher dispatcher = mock(Dispatcher.class);
            Push push = newPush().build();
            DelayedProcessor delayedDispatcher = new DelayedProcessor(dispatcher);

            it("accumulate two messages and it dispatches the third", () -> {
                delayedDispatcher.send(push, "address");
                delayedDispatcher.send(push, "address");
                delayedDispatcher.send(push, "address");

                verify(dispatcher, times(1)).send(any(), eq("address"));
            });
        });
    }
}