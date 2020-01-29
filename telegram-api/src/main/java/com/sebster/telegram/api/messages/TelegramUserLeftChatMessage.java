package com.sebster.telegram.api.messages;

import static lombok.AccessLevel.PRIVATE;

import java.util.Date;

import com.sebster.telegram.api.data.TelegramChat;
import com.sebster.telegram.api.data.TelegramUser;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = PRIVATE, makeFinal = true)
@Getter
@ToString(doNotUseGetters = true, callSuper = true)
public final class TelegramUserLeftChatMessage extends TelegramMessage {

	@NonNull TelegramUser user;

	@Builder(toBuilder = true)
	public TelegramUserLeftChatMessage(
			int messageId, TelegramUser from, @NonNull Date date, @NonNull TelegramChat chat,
			TelegramUser forwardFrom, Date forwardDate, TelegramMessage replyToMessage,
			@NonNull TelegramUser user
	) {
		super(messageId, from, date, chat, forwardFrom, forwardDate, replyToMessage);
		this.user = user;
	}

	@Override
	public <T> T accept(TelegramMessageTransformer<T> transformer) {
		return transformer.transformUserLeftChatMessage(this);
	}

	@Override
	public void accept(TelegramMessageVisitor visitor) {
		visitor.visitUserLeftChatMessage(this);
	}

	public static class TelegramUserLeftChatMessageBuilder implements TelegramMessageBuilder {
	}

}
