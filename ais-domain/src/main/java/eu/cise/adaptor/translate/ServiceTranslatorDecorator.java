package eu.cise.adaptor.translate;

import eu.cise.datamodel.v1.entity.Entity;
import eu.cise.servicemodel.v1.message.Push;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ServiceTranslatorDecorator implements Translator<Entity, Optional<Push>> {

    private final List<Entity> invocations;
    private final ServiceTranslator proxy;

    public ServiceTranslatorDecorator(ServiceTranslator translator) {
        this.proxy = translator;
        this.invocations = new ArrayList<>();
    }

    @Override
    public Optional<Push> translate(Entity entity) {
        if (invocations.size() > 2)
            return Optional.of(proxy.translate(invocations));

        invocations.add(entity);

        return Optional.empty();
    }
}
