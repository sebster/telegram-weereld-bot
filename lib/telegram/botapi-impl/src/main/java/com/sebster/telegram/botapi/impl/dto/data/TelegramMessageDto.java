package com.sebster.telegram.botapi.impl.dto.data;

import static com.sebster.telegram.botapi.data.TelegramChatType.CHANNEL;
import static com.sebster.telegram.botapi.data.TelegramChatType.GROUP;
import static com.sebster.telegram.botapi.data.TelegramChatType.SUPERGROUP;
import static com.sebster.telegram.botapi.impl.dto.data.TelegramPhotoSizeDto.toTelegramPhotoList;

import java.util.Date;
import java.util.List;

import com.sebster.telegram.botapi.messages.TelegramAudioMessage;
import com.sebster.telegram.botapi.messages.TelegramChatCreatedMessage;
import com.sebster.telegram.botapi.messages.TelegramChatMigratedFromGroupMessage;
import com.sebster.telegram.botapi.messages.TelegramChatMigratedToSupergroupMessage;
import com.sebster.telegram.botapi.messages.TelegramChatPhotoChangedMessage;
import com.sebster.telegram.botapi.messages.TelegramChatPhotoDeletedMessage;
import com.sebster.telegram.botapi.messages.TelegramChatTitleChangedMessage;
import com.sebster.telegram.botapi.messages.TelegramContactMessage;
import com.sebster.telegram.botapi.messages.TelegramDocumentMessage;
import com.sebster.telegram.botapi.messages.TelegramLocationMessage;
import com.sebster.telegram.botapi.messages.TelegramMessage;
import com.sebster.telegram.botapi.messages.TelegramMessage.TelegramMessageBuilder;
import com.sebster.telegram.botapi.messages.TelegramPhotoMessage;
import com.sebster.telegram.botapi.messages.TelegramStickerMessage;
import com.sebster.telegram.botapi.messages.TelegramTextMessage;
import com.sebster.telegram.botapi.messages.TelegramUnknownMessage;
import com.sebster.telegram.botapi.messages.TelegramUserJoinedChatMessage;
import com.sebster.telegram.botapi.messages.TelegramUserLeftChatMessage;
import com.sebster.telegram.botapi.messages.TelegramVideoMessage;
import com.sebster.telegram.botapi.messages.TelegramVoiceMessage;
import lombok.Data;

@Data
public class TelegramMessageDto {

	int messageId;
	TelegramUserDto from;
	int date;
	TelegramChatDto chat;
	TelegramUserDto forwardFrom;
	Integer forwardDate;
	TelegramMessageDto replyToMessage;
	String text;
	TelegramAudioDto audio;
	TelegramDocumentDto document;
	List<TelegramPhotoSizeDto> photo;
	TelegramStickerDto sticker;
	TelegramVideoDto video;
	TelegramVoiceDto voice;
	String caption;
	TelegramContactDto contact;
	TelegramLocationDto location;
	TelegramUserDto newChatParticipant;
	TelegramUserDto leftChatParticipant;
	String newChatTitle;
	List<TelegramPhotoSizeDto> newChatPhoto;
	Boolean deleteChatPhoto;
	Boolean groupChatCreated;
	Boolean supergroupChatCreated;
	Boolean channelChatCreated;
	Long migrateToChatId;
	Long migrateFromChatId;

	public TelegramMessage toTelegramMessage() {
		if (text != null) {
			return build(TelegramTextMessage.builder().text(text));
		}
		if (audio != null) {
			return build(TelegramAudioMessage.builder().audio(audio.toTelegramAudio()));
		}
		if (document != null) {
			return build(TelegramDocumentMessage.builder().document(document.toTelegramDocument()));
		}
		if (photo != null) {
			return build(TelegramPhotoMessage.builder().photoList(toTelegramPhotoList(photo)).caption(caption));
		}
		if (sticker != null) {
			return build(TelegramStickerMessage.builder().sticker(sticker.toTelegramSticker()));
		}
		if (video != null) {
			return build(TelegramVideoMessage.builder().video(video.toTelegramVideo()).caption(caption));
		}
		if (voice != null) {
			return build(TelegramVoiceMessage.builder().voice(voice.toTelegramVoice()));
		}
		if (contact != null) {
			return build(TelegramContactMessage.builder().contact(contact.toTelegramContact()));
		}
		if (location != null) {
			return build(TelegramLocationMessage.builder().location(location.toTelegramLocation()));
		}
		if (newChatParticipant != null) {
			return build(TelegramUserJoinedChatMessage.builder().user(newChatParticipant.toTelegramUser()));
		}
		if (leftChatParticipant != null) {
			return build(TelegramUserLeftChatMessage.builder().user(leftChatParticipant.toTelegramUser()));
		}
		if (newChatTitle != null) {
			return build(TelegramChatTitleChangedMessage.builder().newChatTitle(newChatTitle));
		}
		if (newChatPhoto != null) {
			return build(TelegramChatPhotoChangedMessage.builder().newChatPhoto(toTelegramPhotoList(newChatPhoto)));
		}
		if (deleteChatPhoto != null) {
			return build(TelegramChatPhotoDeletedMessage.builder());
		}
		if (groupChatCreated != null) {
			return build(TelegramChatCreatedMessage.builder().chatType(GROUP));
		}
		if (supergroupChatCreated != null) {
			return build(TelegramChatCreatedMessage.builder().chatType(SUPERGROUP));
		}
		if (channelChatCreated != null) {
			return build(TelegramChatCreatedMessage.builder().chatType(CHANNEL));
		}
		if (migrateToChatId != null) {
			return build(TelegramChatMigratedToSupergroupMessage.builder().supergroupChatId(migrateFromChatId));
		}
		if (migrateFromChatId != null) {
			return build(TelegramChatMigratedFromGroupMessage.builder().groupChatId(migrateFromChatId));
		}
		return build(TelegramUnknownMessage.builder());
	}

	private <T extends TelegramMessage> TelegramMessage build(TelegramMessageBuilder<T, ?> builder) {
		return builder
				.messageId(messageId)
				.from(from != null ? from.toTelegramUser() : null)
				.date(toDate(date))
				.chat(chat.toTelegramChat())
				.forwardFrom(forwardFrom != null ? forwardFrom.toTelegramUser() : null)
				.forwardDate(forwardDate != null ? toDate(forwardDate) : null)
				.replyToMessage(replyToMessage != null ? replyToMessage.toTelegramMessage() : null)
				.build();
	}

	private Date toDate(int unixTime) {
		return new Date(unixTime * 1000L);
	}

}
