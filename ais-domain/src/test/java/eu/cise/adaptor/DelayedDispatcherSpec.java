package eu.cise.adaptor;


import com.greghaskins.spectrum.Spectrum;
import eu.cise.datamodel.v1.entity.vessel.Vessel;
import org.junit.runner.RunWith;

import static com.greghaskins.spectrum.Spectrum.describe;

@RunWith(Spectrum.class)
public class DelayedDispatcherSpec {
    {
        describe("aggregates a stream of messages", () -> {

            Vessel vessel = new Vessel();
            vessel.setMMSI(12345L);

//            MessageGrouper delayedDispatcher = new MessageGrouper();
//
//            it("does't dispatch a single message ", () -> {
//                assertThat(delayedDispatcher.process(vessel), is(Optional.empty()));
//            });
//
//            it("accumulate two messages and it dispatches the third", () -> {
//                delayedDispatcher.process(vessel);
//                delayedDispatcher.process(vessel);
//                assertThat(delayedDispatcher.process(vessel).get(), instanceOf(List.class));
//
//            });
//
//            it("the last message is actually a list with three entity", () -> {
//                delayedDispatcher.process(vessel);
//                delayedDispatcher.process(vessel);
//                assertThat(delayedDispatcher.process(vessel).get(), hasSize(3));
//            });
        });
    }
}