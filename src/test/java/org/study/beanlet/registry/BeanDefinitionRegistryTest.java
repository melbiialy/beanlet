package org.study.beanlet.registry;

import org.junit.jupiter.api.Test;
import org.study.beanlet.bean.BeanDefinition;
import org.study.beanlet.bean.BeanDefinitionBuilder;
import org.study.beanlet.bean.BeanScope;
import org.study.beanlet.exception.NoUniqueBeanDefinitionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BeanDefinitionRegistryTest {

    @Test
    void choosesPrimaryCandidateWhenMultipleBeansMatchAType() {
        BeanDefinitionRegistry registry = registryWith(
                definition(EmailNotifier.class, "email", false),
                definition(SmsNotifier.class, "sms", true));

        assertEquals("SmsNotifier", registry.getTypeMatchBeanDefinition(Notifier.class, null));
    }

    @Test
    void qualifierTakesPrecedenceOverPrimaryCandidate() {
        BeanDefinitionRegistry registry = registryWith(
                definition(EmailNotifier.class, "email", false),
                definition(SmsNotifier.class, "sms", true));

        assertEquals("EmailNotifier", registry.getTypeMatchBeanDefinition(Notifier.class, "email"));
    }

    @Test
    void rejectsAmbiguousCandidatesWithoutPrimaryOrQualifier() {
        BeanDefinitionRegistry registry = registryWith(
                definition(EmailNotifier.class, "email", false),
                definition(SmsNotifier.class, "sms", false));

        assertThrows(NoUniqueBeanDefinitionException.class,
                () -> registry.getTypeMatchBeanDefinition(Notifier.class, null));
    }

    private static BeanDefinitionRegistry registryWith(BeanDefinition... definitions) {
        BeanDefinitionRegistry registry = new BeanDefinitionRegistry();
        for (BeanDefinition definition : definitions) {
            registry.registerBeanDefinition(definition.getBeanClass().getSimpleName(), definition);
        }
        return registry;
    }

    private static BeanDefinition definition(Class<?> type, String qualifier, boolean primary) {
        return new BeanDefinitionBuilder().beanClass(type).scope(BeanScope.SINGLETON)
                .beanQualifiedName(qualifier).primary(primary).build();
    }

    interface Notifier { }
    static class EmailNotifier implements Notifier { }
    static class SmsNotifier implements Notifier { }
}
