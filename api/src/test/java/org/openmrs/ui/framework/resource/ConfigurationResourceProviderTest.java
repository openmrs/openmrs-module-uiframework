package org.openmrs.ui.framework.resource;

import java.io.File;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openmrs.util.OpenmrsUtil;

public class ConfigurationResourceProviderTest {

	public static final String FILE_NAME = "test.txt";

	public static final String FILE_NAME_WITH_CONFIGURATION = "configuration" + File.separator + FILE_NAME;

	public static final String TEST_CONTENTS = "THIS IS A TEST";

	private static final String OPENMRS_APPLICATION_DATA_DIRECTORY = "OPENMRS_APPLICATION_DATA_DIRECTORY";

	private static final File TEMP_DIR = new File(System.getProperty("java.io.tmpdir"));

	private static File testDir;

	private final ConfigurationResourceProvider provider = new ConfigurationResourceProvider();

	@BeforeClass
	public static void setup() {
		testDir = new File(TEMP_DIR, UUID.randomUUID().toString());
		testDir.mkdirs();
	}

	@AfterClass
	public static void cleanup() {
		if (testDir.exists()) {
			testDir.delete();
		}
	}

	@Test
	public void shouldGetResourceByRelativePath() throws Exception {
		File testFile = new File(new File(testDir, "configuration"), FILE_NAME);
		if (testFile.exists()) {
			testFile.delete();
		}

		try {
			FileUtils.writeStringToFile(testFile, TEST_CONTENTS, "UTF-8");

			// If file does not exist relative to application data directory, should return null
			Assert.assertNotEquals(OpenmrsUtil.getApplicationDataDirectory(), testDir.getCanonicalPath());
			Assert.assertNull(provider.getResource(FILE_NAME));

			System.setProperty(OPENMRS_APPLICATION_DATA_DIRECTORY, testDir.getCanonicalPath());
			try {
				Assert.assertEquals(OpenmrsUtil.getApplicationDataDirectory(), testDir.getCanonicalPath());
				Assert.assertEquals(TEST_CONTENTS, FileUtils.readFileToString(provider.getResource(FILE_NAME), "UTF-8"));
			}
			finally {
				System.clearProperty(OPENMRS_APPLICATION_DATA_DIRECTORY);
			}
		}
		finally {
			if (testFile.exists()) {
				testFile.delete();
			}
		}
	}

	@Test
	public void shouldGetResourceByRelativePathStartingWithConfiguration() throws Exception {
		File testFile = new File(new File(testDir, "configuration"), FILE_NAME);
		if (testFile.exists()) {
			testFile.delete();
		}

		try {
			FileUtils.writeStringToFile(testFile, TEST_CONTENTS, "UTF-8");

			// If file does not exist relative to application data directory, should return null
			Assert.assertNotEquals(OpenmrsUtil.getApplicationDataDirectory(), testDir.getCanonicalPath());
			Assert.assertNull(provider.getResource(FILE_NAME_WITH_CONFIGURATION));

			System.setProperty(OPENMRS_APPLICATION_DATA_DIRECTORY, testDir.getCanonicalPath());
			try {
				Assert.assertEquals(OpenmrsUtil.getApplicationDataDirectory(), testDir.getCanonicalPath());
				Assert.assertEquals(TEST_CONTENTS, FileUtils
						.readFileToString(provider.getResource(FILE_NAME_WITH_CONFIGURATION), "UTF-8"));
			}
			finally {
				System.clearProperty(OPENMRS_APPLICATION_DATA_DIRECTORY);
			}
		}
		finally {
			if (testFile.exists()) {
				testFile.delete();
			}
		}
	}

	@Test
	public void shouldGetResourceByAbsolutePathInOpenmrsApplicationDirectory() throws Exception {
		File testFile = new File(new File(testDir, "configuration"), FILE_NAME);
		if (testFile.exists()) {
			testFile.delete();
		}

		try {
			FileUtils.writeStringToFile(testFile, TEST_CONTENTS, "UTF-8");

			System.setProperty(OPENMRS_APPLICATION_DATA_DIRECTORY, testDir.getCanonicalPath());
			try {
				Assert.assertEquals(TEST_CONTENTS, FileUtils.readFileToString(provider.getResource(
						new File(OpenmrsUtil.getDirectoryInApplicationDataDirectory("configuration"), FILE_NAME)
								.getAbsolutePath()), "UTF-8"));
			}
			finally {
				System.clearProperty(OPENMRS_APPLICATION_DATA_DIRECTORY);
			}
		}
		finally {
			if (testFile.exists()) {
				testFile.delete();
			}
		}
	}

	@Test
	public void shouldNotGetResourceByAbsolutePathNotInOpenmrsApplicationDirectory() throws Exception {
		File testFile = new File(new File(testDir, "configuration"), FILE_NAME);

		try {
			FileUtils.writeStringToFile(testFile, TEST_CONTENTS, "UTF-8");

			// If file does not exist relative to application data directory, should return null
			Assert.assertNotEquals(OpenmrsUtil.getApplicationDataDirectory(), testDir.getCanonicalPath());
			Assert.assertNull(provider.getResource(FILE_NAME));

			Assert.assertNull(provider.getResource(testFile.getAbsolutePath()));
		}
		finally {
			if (testFile.exists()) {
				testFile.delete();
			}
		}
	}
}
