package eu.cise.adaptor;

import com.greghaskins.spectrum.Spectrum;
import eu.cise.adaptor.translate.Message123Translator;
import eu.cise.datamodel.v1.entity.object.SensorType;
import eu.cise.datamodel.v1.entity.object.SourceType;
import eu.cise.datamodel.v1.entity.vessel.NavigationalStatusType;
import eu.cise.datamodel.v1.entity.vessel.Vessel;
import org.aeonbits.owner.ConfigFactory;
import org.junit.runner.RunWith;

import java.time.Instant;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static eu.cise.adaptor.helpers.Utils.*;
import static eu.cise.adaptor.translate.utils.NavigationStatus.UnderwayUsingEngine;
import static eu.cise.datamodel.v1.entity.location.LocationQualitativeAccuracyType.HIGH;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

@RunWith(Spectrum.class)
public class AIS_1_2_3_TranslatorSpec {
    {
        describe("an AIS to CISE message translator", () -> {

            AdaptorConfig config = ConfigFactory.create(AdaptorConfig.class);
            Message123Translator translator = new Message123Translator(config);

            final AisMsg m = new AisMsg.Builder(1)
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

            describe("when a message type is 1,2,3", () -> {
                final Vessel v = translator.translate(m);

                it("returns a Vessel with with geometry", () -> {
                    assertThat(v.getLocationRels(), is(not(empty())));

                    assertThat(extractLocationRel(v).getLocation(), is(notNullValue()));

                    assertThat(extractLocationRel(v).getLocation().getGeometries(), is(not(empty())));

                    assertThat(extractLocationRel(v).getLocation().getGeometries().get(0), is(notNullValue()));
                });

                it("returns a Vessel with latitude", () -> {
                    assertThat(extractGeometry(v).getLatitude(), is("47.443634"));
                });

                it("returns a Vessel with longitude", () -> {
                    assertThat(extractGeometry(v).getLongitude(), is("-6.9895167"));
                });

                it("returns a Vessel with location qualitative  accuracy", () -> {
                    assertThat(extractLocationRel(v).getLocation().getLocationQualitativeAccuracy(), is(HIGH));
                });

                it("returns a Vessel with cog (in degrees instead of 1/10 od degrees)", () -> {
                    assertThat(extractLocationRel(v).getCOG(), is(211.9D));
                });

                it("returns a Vessel with cog (null for cog=3600)", () -> {
                    AisMsg mc = new AisMsg.Builder(1)
                            .withCOG(3600f)
                            .build();

                    Vessel vc = translator.translate(mc);

                    assertThat(extractLocationRel(vc).getCOG(), is(nullValue()));
                });

                it("returns a Vessel with true heading", () -> {
                    assertThat(extractLocationRel(v).getHeading(), is(210D));
                });

                it("returns a Vessel with heading (null for trueHeading=511)", () -> {
                    AisMsg mh = new AisMsg.Builder(1)
                            .withTrueHeading(511)
                            .build();

                    Vessel vh = translator.translate(mh);

                    assertThat(extractLocationRel(vh).getHeading(), is(nullValue()));
                });

                it("returns a Vessel with source type", () -> {
                    assertThat(extractLocationRel(v).getSourceType(), is(SourceType.DECLARATION));
                });

                it("returns a Vessel with sensor type", () -> {
                    assertThat(extractLocationRel(v).getSensorType(), is(SensorType.AUTOMATIC_IDENTIFICATION_SYSTEM));
                });

                it("returns a Vessel with sog (in knots instead of 1/10th of knots)", () -> {
                    assertThat(extractLocationRel(v).getSOG(), is(13.8D));
                });

                it("returns a Vessel with sog (null for SOG=1023)", () -> {
                    AisMsg ms = new AisMsg.Builder(1)
                            .withSOG(1023f)
                            .build();

                    Vessel vs = translator.translate(ms);
                    assertThat(extractLocationRel(vs).getSOG(), is(nullValue()));
                });

                it("returns a Vessel with periodOfTime.startDate", () -> {
                    //"2018-02-19T14:43:16.550Z"
                    assertThat(extractLocationRel(v).getPeriodOfTime().getStartDate(),
                            is(xmlDate(2018, 02, 19)));
                });

                it("returns a Vessel with periodOfTime.startTime", () -> {
                    //"2018-02-19T14:43:16.550Z"
                    assertThat(extractLocationRel(v).getPeriodOfTime().getStartTime(),
                            is(xmlTime(14, 43, 16)));
                });

                it("returns a Vessel with MMSI", () -> {
                    assertThat(v.getMMSI(), is(538005989L));
                });

                it("returns a Vessel with navigationStatus", () -> {
                    assertThat(v.getNavigationalStatus(), is(NavigationalStatusType.UNDER_WAY_USING_ENGINE));
                });
            });

        });
    }
}

