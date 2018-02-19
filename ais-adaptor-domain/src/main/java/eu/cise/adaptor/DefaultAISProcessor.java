package eu.cise.adaptor;

import static eu.cise.adaptor.Result.NOT_SENT;

public class DefaultAISProcessor implements AISProcessor {

    private final Translator translator;
    private final Dispatcher dispatcher;

    public DefaultAISProcessor(Translator translator, Dispatcher dispatcher) {
        this.translator = translator;
        this.dispatcher = dispatcher;
    }

    @Override
    public void process(AISMsg message) {
        translator.translate(message).map(dispatcher::dispatch).orElse(NOT_SENT);
    }
}
