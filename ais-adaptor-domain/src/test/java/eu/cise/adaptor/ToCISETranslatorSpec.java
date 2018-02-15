package eu.cise.adaptor;

import com.greghaskins.spectrum.Spectrum;
import org.junit.runner.RunWith;

import java.util.Optional;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

@RunWith(Spectrum.class)
public class ToCISETranslatorSpec {
    {
        describe("an AIS to CISE message translator", () -> {

            ToCISETranslator translator = new ToCISETranslator();

            describe("when a message type is not supported", () -> {

                it("returns an empty optional", () -> {
                    assertThat(translator.translate(new InternalAISMessage.Builder(8)), is(Optional.empty()));
                });

            });


            describe("when a message type is 1,2,3 or 5", () -> {
                asList(1, 2, 3, 5).forEach((n) ->
                        it("returns an optional with a push message / " + n, () -> {
                            assertThat(translator.translate(new InternalAISMessage.Builder(n)), is(not(Optional.empty())));
                        })
                );
            });

        });


    }
}

