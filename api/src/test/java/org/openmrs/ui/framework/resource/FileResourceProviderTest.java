package org.openmrs.ui.framework.resource;

import java.io.File;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.openmrs.util.OpenmrsUtil;

public class FileResourceProviderTest {

	public static final String FILE_NAME = "test.txt";
	public static final String TEST_CONTENTS = "THIS IS A TEST";

	FileResourceProvider provider = new FileResourceProvider();
	File tempDir = new File(System.getProperty("java.io.tmpdir"));

    @Test
	public void shouldGetResourceByAbsolutePath() throws Exception {

	    File testDir = new File(tempDir, UUID.randomUUID().toString());
	    File testFile = new File(testDir, FILE_NAME);

	    // If file does not exist, should return null
		Assert.assertNull(provider.getResource(testFile.getAbsolutePath()));

		FileUtils.writeStringToFile(testFile, TEST_CONTENTS, "UTF-8");
	    testDir.deleteOnExit();

	    // If file exists, should return it successfully by absolute path
	    String contents = FileUtils.readFileToString(provider.getResource(testFile.getAbsolutePath()), "UTF-8");
	    Assert.assertEquals(TEST_CONTENTS, contents);
	}

	@Test
	public void shouldGetResourceByRelativePath() throws Exception {

		File testDir = new File(tempDir, UUID.randomUUID().toString());
		File testFile = new File(testDir, FILE_NAME);

		FileUtils.writeStringToFile(testFile, TEST_CONTENTS, "UTF-8");
		testDir.deleteOnExit();

		// If file does not exist relative to application data directory, should return null
		Assert.assertNotEquals(OpenmrsUtil.getApplicationDataDirectory(), testDir.getAbsolutePath());
		Assert.assertNull(provider.getResource(FILE_NAME));

		System.setProperty("OPENMRS_APPLICATION_DATA_DIRECTORY", testDir.getAbsolutePath());
		Assert.assertEquals(OpenmrsUtil.getApplicationDataDirectory(), testDir.getAbsolutePath());
		Assert.assertEquals(TEST_CONTENTS, FileUtils.readFileToString(provider.getResource(FILE_NAME), "UTF-8"));
	}
}
