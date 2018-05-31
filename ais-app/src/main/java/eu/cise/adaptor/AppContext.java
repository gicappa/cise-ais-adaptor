/*
 * Copyright 2018 JRC
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the
 * Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://www.osor.eu/eupl/european-union-public-licence-eupl-v.1.1
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 *
 */

package eu.cise.adaptor;

/**
 * The application context is an interface where the major abstractions may be
 * built and created using the specific adaptors.
 *
 * The idea of the hexagonal architecture is that the inner domain module
 * where the business logic is not depending on any detail of the implementation
 * and of the connection of the outside world.
 *
 *
 */
public interface AppContext {

    Dispatcher makeDispatcher();

    AisSource makeSource();

    StreamProcessor makeStreamProcessor();
}
