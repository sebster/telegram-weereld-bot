package com.sebster.telegram.impl.dto.data;

import com.sebster.telegram.api.data.TelegramAudio;
import lombok.Data;

@Data
public class TelegramAudioDto {

	String fileId;
	String fileUniqueId;
	int duration;
	String performer;
	String title;
	String mimeType;
	Integer fileSize;

	public TelegramAudio toTelegramAudio() {
		return new TelegramAudio(fileId, fileUniqueId, duration, performer, title, mimeType, fileSize);
	}

}
