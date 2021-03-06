package com.sebster.telegram.botapi.impl;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.databind.PropertyNamingStrategy.SNAKE_CASE;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static java.util.stream.Collectors.toList;
import static org.springframework.http.converter.json.Jackson2ObjectMapperBuilder.json;

import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Validate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sebster.telegram.botapi.TelegramSendMessageOptions;
import com.sebster.telegram.botapi.TelegramService;
import com.sebster.telegram.botapi.TelegramServiceException;
import com.sebster.telegram.botapi.TelegramUpdate;
import com.sebster.telegram.botapi.data.TelegramFileLink;
import com.sebster.telegram.botapi.data.TelegramUser;
import com.sebster.telegram.botapi.impl.dto.data.TelegramFileDto;
import com.sebster.telegram.botapi.impl.dto.data.TelegramMessageDto;
import com.sebster.telegram.botapi.impl.dto.methods.TelegramGetFileResponseDto;
import com.sebster.telegram.botapi.impl.dto.methods.TelegramGetMeResponseDto;
import com.sebster.telegram.botapi.impl.dto.methods.TelegramGetUpdatesResponseDto;
import com.sebster.telegram.botapi.impl.dto.methods.TelegramResponseDto;
import com.sebster.telegram.botapi.impl.dto.methods.TelegramSendMessageRequestDto;
import com.sebster.telegram.botapi.impl.dto.methods.TelegramSendMessageResponseDto;
import com.sebster.telegram.botapi.impl.dto.methods.TelegramUpdateDto;
import com.sebster.telegram.botapi.messages.TelegramMessage;
import lombok.NonNull;

public class TelegramServiceImpl implements TelegramService {

	private static final String TELEGRAM_API_BASE_URL = "https://api.telegram.org/bot";
	private static final String TELEGRAM_API_FILE_BASE_URL = "https://api.telegram.org/file/bot";

	private static final String GET_ME = "getMe";
	private static final String GET_UPDATES = "getUpdates";
	private static final String GET_FILE = "getFile";
	private static final String SEND_MESSAGE = "sendMessage";

	private final @NonNull String token;
	private final @NonNull RestTemplate restTemplate;

	public TelegramServiceImpl(String token, RestTemplateBuilder restTemplateBuilder) {
		this.token = token;
		this.restTemplate = restTemplateBuilder.messageConverters(jsonMessageConverter()).build();
	}

	@Override
	public TelegramUser getMe() {
		return doGet(GET_ME, TelegramGetMeResponseDto.class).toTelegramUser();
	}

	@Override
	public List<TelegramUpdate> getUpdates(int offset, int limit, @NonNull Duration timeout) {
		Validate.inclusiveBetween(1, DEFAULT_GET_UPDATES_LIMIT, limit,
				"limit must be between 1 and %d: %s", DEFAULT_GET_UPDATES_LIMIT, limit
		);
		Map<String, Object> queryParams = new LinkedHashMap<>();
		queryParams.put("offset", offset);
		if (limit != DEFAULT_GET_UPDATES_LIMIT) {
			queryParams.put("limit", limit);
		}
		if (!timeout.isZero()) {
			queryParams.put("timeout", timeout.getSeconds());
		}
		List<TelegramUpdateDto> updates = doGet(GET_UPDATES, queryParams, TelegramGetUpdatesResponseDto.class);
		return updates.stream().map(TelegramUpdateDto::toTelegramUpdate).collect(toList());
	}

	@Override
	public TelegramFileLink getFileLink(@NonNull String fileId) {
		try {
			TelegramFileDto response = doGet(GET_FILE, singletonMap("file_id", fileId), TelegramGetFileResponseDto.class);
			return response.toTelegramFileLink(new URL(telegramApiFileBaseUrl()));
		} catch (MalformedURLException e) {
			// Should not happen.
			throw new UncheckedIOException(e);
		}
	}

	@Override
	public TelegramMessage sendMessage(long chatId, @NonNull TelegramSendMessageOptions options, @NonNull String text) {
		return sendMessageImpl(chatId, options, text);
	}

	@Override
	public TelegramMessage sendMessage(@NonNull String channel, @NonNull TelegramSendMessageOptions options, @NonNull String text) {
		return sendMessageImpl(channel, options, text);
	}

	private TelegramMessage sendMessageImpl(Object target, TelegramSendMessageOptions options, String text) {
		TelegramSendMessageRequestDto request = new TelegramSendMessageRequestDto(target, text, options);
		TelegramMessageDto response = doPost(SEND_MESSAGE, request, TelegramSendMessageResponseDto.class);
		return response.toTelegramMessage();
	}

	private <RESULT, RESPONSE extends TelegramResponseDto<RESULT>> RESULT doGet(String method, Class<RESPONSE> responseClass) {
		return doGet(method, emptyMap(), responseClass);
	}

	private <RESULT, RESPONSE extends TelegramResponseDto<RESULT>> RESULT doGet(
			String method, Map<String, Object> queryParams, Class<RESPONSE> responseClass
	) {
		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(telegramApiBaseUrl()).path(method);
		queryParams.forEach(uriBuilder::queryParam);
		RESPONSE response = restTemplate.getForObject(uriBuilder.toUriString(), responseClass);
		return getResult(response);
	}

	private <RESULT, RESPONSE extends TelegramResponseDto<RESULT>> RESULT doPost(
			String method, Object request, Class<RESPONSE> responseClass
	) {
		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(telegramApiBaseUrl()).path(method);
		RESPONSE response = restTemplate.postForObject(uriBuilder.toUriString(), request, responseClass);
		return getResult(response);
	}

	private <RESULT, RESPONSE extends TelegramResponseDto<RESULT>> RESULT getResult(RESPONSE response) {
		if (!response.isOk()) {
			throw new TelegramServiceException(response.getErrorCode(), response.getDescription());
		}
		return response.getResult();
	}

	private String telegramApiBaseUrl() {
		return TELEGRAM_API_BASE_URL + token + "/";
	}

	private String telegramApiFileBaseUrl() {
		return TELEGRAM_API_FILE_BASE_URL + token + "/";
	}

	private static HttpMessageConverter<Object> jsonMessageConverter() {
		return new MappingJackson2HttpMessageConverter(objectMapper());
	}

	private static ObjectMapper objectMapper() {
		return json()
				.serializationInclusion(NON_NULL)
				.propertyNamingStrategy(SNAKE_CASE)
				.build();
	}

}
