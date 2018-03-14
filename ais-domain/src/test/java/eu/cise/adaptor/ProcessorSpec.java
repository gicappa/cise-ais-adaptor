package eu.cise.adaptor;

import com.greghaskins.spectrum.Spectrum;
import eu.cise.servicemodel.v1.message.Push;
import jrc.cise.gw.sending.Dispatcher;
import org.junit.runner.RunWith;

import java.time.Instant;
import java.util.Optional;

import static com.greghaskins.spectrum.Spectrum.*;
import static eu.cise.adaptor.NavigationStatus.UnderwayUsingEngine;
import static org.mockito.Mockito.*;

@RunWith(Spectrum.class)
public class ProcessorSpec {
    {
        describe("process the AISMsg message", () -> {

            AISTranslator translator = mock(AISTranslator.class);
            Dispatcher dispatcher = mock(Dispatcher.class);
            AISAdaptorConfig config = mock(AISAdaptorConfig.class);
            Push ciseMessage = new Push();

            AISProcessor processor = new DefaultAISProcessor(translator, dispatcher, config);

            final AISMsg aisMessage = new AISMsg.Builder(1)
                    .withLatitude(47.443634F)
                    .withLongitude(-6.9895167F)
                    .withPositionAccuracy(1)
                    .withCOG(2119.0F)
                    .withTrueHeading(210)
                    .withTimestamp(Instant.parse("2018-02-19T14:43:16.550Z"))
                    .withSOG(138.0F)
                    .withMMSI(538005989)
                    .withNavigationStatus(UnderwayUsingEngine)
                    .build();

            beforeEach(() -> {
                when(translator.translate(aisMessage)).thenReturn(Optional.of(ciseMessage));
                when(config.getGatewayAddress()).thenReturn("http://gateway.addr.gov/messages");
            });

            afterEach(() -> {
                reset(translator);
                reset(dispatcher);
                reset(config);
            });

            it("translates an AIS message into a CISE one", () -> {
                processor.process(aisMessage);

                verify(translator).translate(aisMessage);
            });

            it("dispatches the translated CISE message", () -> {
                processor.process(aisMessage);

                verify(dispatcher).send(eq(ciseMessage), eq("http://gateway.addr.gov/messages"));
            });

        });
    }

}

