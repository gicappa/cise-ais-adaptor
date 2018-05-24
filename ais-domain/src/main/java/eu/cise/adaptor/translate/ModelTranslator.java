package eu.cise.adaptor.translate;

import eu.cise.adaptor.AISAdaptorConfig;
import eu.cise.adaptor.AISMsg;
import eu.cise.datamodel.v1.entity.Entity;

import java.util.Optional;

public class ModelTranslator implements Translator<AISMsg, Entity> {

    private final AISAdaptorConfig config;

    public ModelTranslator(AISAdaptorConfig config) {
        this.config = config;
    }

    @Override
    public Optional<Entity> translate(AISMsg type) {
        return Optional.empty();
    }
}
