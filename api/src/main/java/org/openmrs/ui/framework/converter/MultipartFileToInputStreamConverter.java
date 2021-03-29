package org.openmrs.ui.framework.converter;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.core.convert.converter.Converter;
import org.springframework.web.multipart.MultipartFile;

public class MultipartFileToInputStreamConverter implements Converter<MultipartFile, InputStream> {
	
	@Override
	public InputStream convert(MultipartFile source) {
		try {
			return source.getInputStream();
		}
		catch (IOException e) {
			throw new IllegalArgumentException("Cannot convert MultipartFile to InputStream", e);
		}
	}
	
}
