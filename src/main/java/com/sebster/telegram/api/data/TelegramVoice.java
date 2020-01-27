package com.sebster.telegram.api.data;

import java.util.Optional;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;

/**
 * This object represents a voice note.
 */
@Value
@EqualsAndHashCode(of = "fileUniqueId")
public class TelegramVoice implements TelegramFile, WithDuration, WithMimeType {

	@NonNull String fileId;
	@NonNull String fileUniqueId;
	int duration;
	String mimeType;
	Integer fileSize;

	/**
	 * Duration of the audio in seconds as defined by sender.
	 */
	@Override
	public int getDuration() {
		return duration;
	}

	/**
	 * Optional. MIME type of the file as defined by sender.
	 */
	@Override
	public Optional<String> getMimeType() {
		return Optional.ofNullable(mimeType);
	}

	@Override
	public Optional<Integer> getFileSize() {
		return Optional.ofNullable(fileSize);
	}

}
