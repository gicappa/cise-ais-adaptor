package eu.cise.adaptor;

import com.greghaskins.spectrum.Spectrum;
import eu.cise.adaptor.dispatch.Dispatcher;
import eu.cise.adaptor.process.AISProcessor;
import eu.cise.adaptor.process.DefaultAISProcessor;
import eu.cise.adaptor.translate.ModelTranslator;
import eu.cise.adaptor.translate.ServiceTranslator;
import eu.cise.datamodel.v1.entity.vessel.Vessel;
import eu.cise.servicemodel.v1.message.Push;
import org.junit.runner.RunWith;

import java.time.Instant;
import java.util.Optional;

import static com.greghaskins.spectrum.Spectrum.*;
import static eu.cise.adaptor.normalize.NavigationStatus.UnderwayUsingEngine;
import static org.mockito.Mockito.*;

@RunWith(Spectrum.class)
public class ProcessorSpec {
    {
        describe("process the AISMsg message", () -> {

            AISAdaptorConfig config = mock(AISAdaptorConfig.class);
            ModelTranslator modelTranslator = mock(ModelTranslator.class);
            ServiceTranslator serviceTranslator = mock(ServiceTranslator.class);
            Dispatcher dispatcher = mock(Dispatcher.class);

            Vessel translatedVessel = new Vessel();
            Push translatedPush = new Push();

            AISProcessor processor = new DefaultAISProcessor(modelTranslator, serviceTranslator, dispatcher, config);

            final AISMsg aisMessage = new AISMsg.Builder(1)
                    .withLatitude(47.443634F)
                    .withLongitude(-6.9895167F)
                    .withPositionAccuracy(1)
                    .withCOG(2119.0F)
                    .withTrueHeading(210)
                    .withTimestamp(Instant.parse("2018-02-19T14:43:16.550Z"))
                    .withSOG(138.0F)
                    .withUserId(538005989)
                    .withNavigationStatus(UnderwayUsingEngine)
                    .build();

            beforeEach(() -> {
                when(config.isOverridingTimestamps()).thenReturn(true);
                when(config.isDemoEnvironment()).thenReturn(true);
                when(modelTranslator.translate(aisMessage)).thenReturn(Optional.of(translatedVessel));
                when(serviceTranslator.translate(translatedVessel)).thenReturn(translatedPush);
                when(config.getGatewayAddress()).thenReturn("http://gateway.addr.gov/messages");

                processor.process(aisMessage);

            });

            afterEach(() -> {
                reset(modelTranslator);
                reset(serviceTranslator);
                reset(dispatcher);
                reset(config);
            });

            it("translates an AIS message into a CISE one", () -> {
                verify(modelTranslator).translate(aisMessage);
            });

            it("translates an AIS message into a CISE one", () -> {
                verify(serviceTranslator).translate(translatedVessel);
            });

            it("dispatches the translated CISE message", () -> {
                verify(dispatcher).send(eq(translatedPush), eq("http://gateway.addr.gov/messages"));
            });
        });
    }
}

