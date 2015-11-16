package org.openmrs.ui.framework.formatter;

import org.junit.Test;
import org.junit.Ignore;
import org.junit.After;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.ui.framework.Formatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.Ordered;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import java.util.Locale;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@Ignore("2.0 platform - another unnamed CacheManager exists")
public class FormatterServiceTest extends BaseModuleContextSensitiveTest {

    @Autowired
    private FormatterService formatterService;

    @After
    public void tearDown()
    {
    	formatterService.clearClassFormatters();
    }
    
    @Test
    public void testFormatting() throws Exception {

    	HandlebarsFormatterFactory classFormatter = new HandlebarsFormatterFactory();
        classFormatter.setForClass("org.openmrs.Obs");
        classFormatter.setTemplate("{{ valueNumeric }} at {{ format location }}");
        formatterService.addClassFormatter(classFormatter);

        Location location = new Location() {
            @Override
            public String toString() {
                return "wrong";
            }
        };
        location.setName("Somewhere");

        Obs obs = new Obs();
        obs.setValueNumeric(75d);
        obs.setLocation(location);

        String result = formatterService.getFormatter().format(obs, Locale.ENGLISH);
        assertThat(result, is("75.0 at Somewhere"));
    }

    @Test
    public void testMessage() throws Exception {

    	MessageSource messageSource = mock(MessageSource.class);

        HandlebarsFormatterFactory classFormatter = new HandlebarsFormatterFactory();
        classFormatter.setForClass("org.openmrs.Obs");
        classFormatter.setTemplate("{{ message 'testing.123.testing' }} something");
        formatterService.addClassFormatter(classFormatter);

        Context.setLocale(Locale.ENGLISH);
        Formatter formatter = formatterService.getFormatter();
        formatterService.setMessageSource(messageSource);

        String result = formatter.format(new Obs(), Locale.ENGLISH);
        verify(messageSource).getMessage("testing.123.testing", null, Locale.ENGLISH);
    }

    @Test
    public void testOrder() throws Exception {

    	HandlebarsFormatterFactory wrongFormatter1 = new HandlebarsFormatterFactory();
        wrongFormatter1.setForClass("org.openmrs.Obs");
        wrongFormatter1.setTemplate("wrong");
        wrongFormatter1.setOrder(Ordered.LOWEST_PRECEDENCE);

        HandlebarsFormatterFactory classFormatter = new HandlebarsFormatterFactory();
        classFormatter.setForClass("org.openmrs.Obs");
        classFormatter.setTemplate("{{ valueNumeric }} at {{ format location }}");
        classFormatter.setOrder(Ordered.HIGHEST_PRECEDENCE);

        HandlebarsFormatterFactory wrongFormatter2 = new HandlebarsFormatterFactory();
        wrongFormatter2.setForClass("org.openmrs.Obs");
        wrongFormatter2.setTemplate("wrong");

        formatterService.addClassFormatter(wrongFormatter1);
        formatterService.addClassFormatter(classFormatter);
        formatterService.addClassFormatter(wrongFormatter2);

        Obs obs = new Obs();
        obs.setValueNumeric(75d);
        obs.setLocation(Context.getLocationService().getLocation(1));

        String result = formatterService.getFormatter().format(obs, Locale.ENGLISH);
        assertThat(result, is("75.0 at Unknown Location"));
    }
}