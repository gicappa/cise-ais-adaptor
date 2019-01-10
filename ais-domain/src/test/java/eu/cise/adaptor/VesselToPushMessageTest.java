/*
 * Copyright CISE AIS Adaptor (c) 2018, European Union
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package eu.cise.adaptor;

import eu.cise.adaptor.exceptions.AdaptorException;
import eu.cise.adaptor.translate.ServiceProfileReader;
import eu.cise.adaptor.translate.VesselToPushMessage;
import eu.cise.datamodel.v1.entity.vessel.Vessel;
import eu.cise.servicemodel.v1.message.Push;
import eu.cise.servicemodel.v1.service.ServiceProfile;
import org.aeonbits.owner.ConfigFactory;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

import static eu.cise.servicemodel.v1.service.ServiceOperationType.SUBSCRIBE;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class VesselToPushMessageTest {

    private VesselToPushMessage vesselToPush;
    private ServiceProfileReader profiles;

    @Before
    public void before() {
        profiles = mock(ServiceProfileReader.class);
    }

    @Test
    public void it_translate_a_list_of_vessel_to_a_push() {
        vesselToPush = new VesselToPushMessage(configUsingPush(), profiles);

        when(profiles.list()).thenReturn(asList(new ServiceProfile(), new ServiceProfile()));

        Push actual = vesselToPush.translate(singletonList(new Vessel()));

        assertThat(actual.getDiscoveryProfiles(), hasSize(2));
    }

    @Test
    public void it_translate_a_list_of_vessel_to_a_push_subscribe() {
        vesselToPush = new VesselToPushMessage(configUsingSubscribe(), null);

        Push actual = vesselToPush.translate(singletonList(new Vessel()));

        assertThat(actual.getSender().getServiceOperation(), is(SUBSCRIBE));
    }

    @Test
    public void it_doesnt_use_discoveryprofiles_in_a_push_subscribe() {
        vesselToPush = new VesselToPushMessage(configUsingSubscribe(), null);

        Push actual = vesselToPush.translate(singletonList(new Vessel()));

        assertThat(actual.getDiscoveryProfiles(), hasSize(0));
    }

    private AdaptorConfig configUsingPush() {
        return adaptorConfigFromFile("/ais-adaptor-push.properties");
    }

    private AdaptorConfig configUsingSubscribe() {
        return adaptorConfigFromFile("/ais-adaptor-subscribe.properties");
    }

    private AdaptorConfig adaptorConfigFromFile(String filename) {
        try {
            Properties props = new Properties();
            InputStream inStream = resourceToInputStream(filename);

            props.load(inStream);
            return ConfigFactory.create(AdaptorConfig.class, props);
        } catch (IOException ioe) {
            throw new AdaptorException(ioe);
        }
    }

    private InputStream resourceToInputStream(String pathname) {
        return Optional.ofNullable(getClass().getResourceAsStream(pathname))
                .orElseThrow(() -> new AdaptorException("Can't find file " + pathname));
    }
}