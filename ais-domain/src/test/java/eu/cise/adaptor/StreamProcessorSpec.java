package eu.cise.adaptor;

import com.greghaskins.spectrum.Spectrum;
import eu.cise.adaptor.normalize.AISNormalizer;
import eu.cise.adaptor.translate.AisMsgToCiseModel;
import eu.cise.adaptor.translate.CiseModelToCiseMessage;
import eu.cise.datamodel.v1.entity.vessel.Vessel;
import eu.cise.servicemodel.v1.message.Push;
import org.junit.runner.RunWith;
import reactor.core.publisher.Flux;

import java.time.Instant;

import static com.greghaskins.spectrum.Spectrum.*;
import static eu.cise.adaptor.normalize.NavigationStatus.UnderwayUsingEngine;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.*;

@RunWith(Spectrum.class)
public class StreamProcessorSpec {
    {
        describe("process the AISMsg message", () -> {

            AISAdaptorConfig config = mock(AISAdaptorConfig.class);
            AISNormalizer aisNormalizer = mock(AISNormalizer.class);
            AisMsgToCiseModel aisMsgToCiseModel = mock(AisMsgToCiseModel.class);
            CiseModelToCiseMessage ciseModelToCiseMessage = mock(CiseModelToCiseMessage.class);

            Vessel translatedVessel = new Vessel();
            Push translatedPush = new Push();

            StreamProcessor processor = new StreamProcessor(
                    aisNormalizer, aisMsgToCiseModel, ciseModelToCiseMessage);

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
                when(aisMsgToCiseModel.translate(aisMessage)).thenReturn(translatedVessel);
                when(ciseModelToCiseMessage.translate(singletonList(translatedVessel))).thenReturn(translatedPush);
                when(config.getGatewayAddress()).thenReturn("http://gateway.addr.gov/messages");

                processor.toCiseMessageFlux(Flux.just(aisMessage));
            });

            afterEach(() -> {
                reset(aisMsgToCiseModel);
                reset(ciseModelToCiseMessage);
                reset(config);
            });

            it("translates an AIS message into a CISE one", () -> {
                verify(aisMsgToCiseModel).translate(aisMessage);
            });

            it("translates an AIS message into a CISE one", () -> {
                verify(ciseModelToCiseMessage).translate(singletonList(translatedVessel));
            });

        });
    }
}

