package org.openmrs.ui.framework.converter;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.EncounterService;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class StringToEncounterConverterTest {
	
	private StringToEncounterConverter converter;
	
	private EncounterService encounterService;
	
	@Before
	public void setUp() throws Exception {
		encounterService = mock(EncounterService.class);
		converter = new StringToEncounterConverter();
		converter.setEncounterService(encounterService);
	}
	
	@Test
	public void testConvertByPatientId() throws Exception {
		converter.convert("1234");
		verify(encounterService).getEncounter(1234);
	}
	
	@Test
	public void testConvertByPatientUuid() throws Exception {
		String uuid = "8d793bee-c2cc-11de-8d13-0010c6dffd0f";
		converter.convert(uuid);
		verify(encounterService).getEncounterByUuid(uuid);
	}
	
}
