package com.sebster.telegram.botapi.messages;

import static lombok.AccessLevel.PRIVATE;

import java.util.Date;

import com.sebster.telegram.botapi.data.TelegramChat;
import com.sebster.telegram.botapi.data.TelegramUser;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@FieldDefaults(level = PRIVATE, makeFinal = true)
@Getter
@ToString(doNotUseGetters = true, callSuper = true)
@SuperBuilder(toBuilder = true)
public final class TelegramChatTitleChangedMessage extends TelegramMessage {

	@NonNull String newChatTitle;

	public TelegramChatTitleChangedMessage(
			int messageId, TelegramUser from, @NonNull Date date, @NonNull TelegramChat chat,
			TelegramUser forwardFrom, Date forwardDate, TelegramMessage replyToMessage,
			@NonNull String newChatTitle
	) {
		super(messageId, from, date, chat, forwardFrom, forwardDate, replyToMessage);
		this.newChatTitle = newChatTitle;
	}

	@Override
	public <T> T accept(TelegramMessageTransformer<T> transformer) {
		return transformer.transformChatTitleChangedMessage(this);
	}

	@Override
	public void accept(TelegramMessageVisitor visitor) {
		visitor.visitChatTitleChangedMessage(this);
	}

}
