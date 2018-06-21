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

    AisStreamGenerator makeSource();

    AisStreamPipeline makeStreamProcessor();
}
