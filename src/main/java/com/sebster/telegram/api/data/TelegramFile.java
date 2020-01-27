package com.sebster.telegram.api.data;

import java.util.Optional;

/**
 * This interface represents any object that is a file. To download the file, the getFile API method should be used to get a {@link
 * TelegramFileLink}.
 */
public interface TelegramFile {

	/**
	 * Identifier for this file, which can be used to download or reuse the file.
	 */
	String getFileId();

	/**
	 * Unique identifier for this file, which is supposed to be the same over time and for different bots. Can't be used to download
	 * or reuse the file.
	 */
	String getFileUniqueId();

	/**
	 * Optional. File size, if known.
	 */
	Optional<Integer> getFileSize();

}
