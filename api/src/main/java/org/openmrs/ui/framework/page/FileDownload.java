/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.ui.framework.page;

/**
 * Page controllers may return this to indicate that a file download should be sent back to the
 * client, rather than an HTML page
 */
public class FileDownload extends PageAction {
	
	private static final long serialVersionUID = 1L;
	
	private String filename;
	
	private String contentType;
	
	private byte[] fileContent;
	
	public FileDownload(String filename, String contentType, byte[] fileContent) {
		this.filename = filename;
		this.contentType = contentType;
		this.fileContent = fileContent;
	}
	
	/**
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}
	
	/**
	 * @param filename the filename to set
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	/**
	 * @return the fileContent
	 */
	public byte[] getFileContent() {
		return fileContent;
	}
	
	/**
	 * @param fileContent the fileContent to set
	 */
	public void setFileContent(byte[] fileContent) {
		this.fileContent = fileContent;
	}
	
	/**
	 * @return the contentType
	 */
	public String getContentType() {
		return contentType;
	}
	
	/**
	 * @param contentType the contentType to set
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
}
