package org.openmrs.ui.framework.converter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.Program;
import org.openmrs.api.ProgramWorkflowService;

import static org.mockito.Mockito.verify;

public class StringToProgramConverterTest {

    public static final String UUID = "abc-123";

    @Mock
    private ProgramWorkflowService service;

    @InjectMocks
    private StringToProgramConverter converter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testById() throws Exception {
        Program converted = converter.convert("15");
        verify(service).getProgram(15);
    }

    @Test
    public void testByUuid() throws Exception {
        Program converted = converter.convert(UUID);
        verify(service).getProgramByUuid(UUID);
    }
}