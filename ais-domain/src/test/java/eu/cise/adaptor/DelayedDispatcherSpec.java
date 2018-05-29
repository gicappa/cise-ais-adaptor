package eu.cise.adaptor;


import com.greghaskins.spectrum.Spectrum;
import eu.cise.datamodel.v1.entity.vessel.Vessel;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Optional;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(Spectrum.class)
public class DelayedDispatcherSpec {
    {
        describe("aggregates a stream of messages", () -> {

            Vessel vessel = new Vessel();
            vessel.setMMSI(12345L);

            MessageGrouper delayedDispatcher = new MessageGrouper();

            it("does't dispatch a single message ", () -> {
                assertThat(delayedDispatcher.translate(vessel), is(Optional.empty()));
            });

            it("accumulate two messages and it dispatches the third", () -> {
                delayedDispatcher.translate(vessel);
                delayedDispatcher.translate(vessel);
                assertThat(delayedDispatcher.translate(vessel).get(), instanceOf(List.class));

            });

            it("the last message is actually a list with three entity", () -> {
                delayedDispatcher.translate(vessel);
                delayedDispatcher.translate(vessel);
                assertThat(delayedDispatcher.translate(vessel).get(), hasSize(3));
            });
        });
    }
}