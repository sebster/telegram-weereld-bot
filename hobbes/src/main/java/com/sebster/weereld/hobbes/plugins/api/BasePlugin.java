package com.sebster.weereld.hobbes.plugins.api;

import static com.sebster.weereld.hobbes.people.PersonSpecification.withNick;
import static com.sebster.weereld.hobbes.people.PersonSpecification.withTelegramUserId;
import static java.lang.String.format;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Optional;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.sebster.repository.api.Repository;
import com.sebster.telegram.botapi.TelegramEmoji;
import com.sebster.telegram.botapi.TelegramSendMessageOptions;
import com.sebster.telegram.botapi.TelegramService;
import com.sebster.telegram.botapi.data.TelegramChat;
import com.sebster.telegram.botapi.data.TelegramUser;
import com.sebster.telegram.botapi.messages.TelegramMessage;
import com.sebster.telegram.botapi.messages.TelegramMessageVisitorAdapter;
import com.sebster.weereld.hobbes.people.Person;
import lombok.NonNull;

public abstract class BasePlugin extends TelegramMessageVisitorAdapter implements Plugin {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	protected Clock clock;

	@Autowired
	protected TelegramService telegramService;

	@Autowired
	protected Repository<Person> personRepository;

	@Override
	@Transactional
	public void receiveMessage(TelegramMessage telegramMessage) {
		telegramMessage.accept(this);
	}

	@Override
	public void visitMessage(TelegramMessage message) {
		// Ignore message by default.
	}

	protected void sendMessage(TelegramChat chat, String text, Object... args) {
		sendMessage(chat.getId(), text, args);
	}

	protected void sendMessage(long chatId, String text, Object... args) {
		telegramService.sendMessage(chatId, TelegramEmoji.expand(format(text, args)));
	}

	protected void sendMessage(TelegramChat chat, TelegramSendMessageOptions options, String text, Object... args) {
		sendMessage(chat.getId(), options, text, args);
	}

	protected void sendMessage(long chatId, TelegramSendMessageOptions options, String text, Object... args) {
		telegramService.sendMessage(chatId, options, TelegramEmoji.expand(format(text, args)));
	}

	protected Optional<Integer> getFromUserId(TelegramMessage message) {
		return message.getFrom().map(TelegramUser::getId);
	}

	protected Optional<Person> getFrom(TelegramMessage message) {
		return getFromUserId(message).flatMap(userId -> personRepository.findOne(withTelegramUserId(userId)));
	}

	protected boolean isMe(@NonNull String nick) {
		return nick.equalsIgnoreCase("mij");
	}

	protected Optional<Person> meOrPersonByNick(@NonNull TelegramMessage message, @NonNull String nick) {
		return isMe(nick) ? getFrom(message) : personRepository.findOne(withNick(nick));
	}

	protected LocalDate date() {
		return LocalDate.now(clock);
	}

	protected LocalDate date(ZoneId zone) {
		return LocalDate.now(clock.withZone(zone));
	}

	protected LocalTime time() {
		return LocalTime.now(clock);
	}

	protected LocalTime time(ZoneId zone) {
		return LocalTime.now(clock.withZone(zone));
	}

	protected LocalDateTime dateTime() {
		return LocalDateTime.now(clock);
	}

	protected LocalDateTime dateTime(ZoneId zone) {
		return LocalDateTime.now(clock.withZone(zone));
	}

}
