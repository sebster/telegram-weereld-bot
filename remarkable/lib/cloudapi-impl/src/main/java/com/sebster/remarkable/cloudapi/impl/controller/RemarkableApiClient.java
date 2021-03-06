package com.sebster.remarkable.cloudapi.impl.controller;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import com.sebster.commons.io.InputStreamProcessor;
import com.sebster.remarkable.cloudapi.RemarkableDownloadLink;
import lombok.NonNull;

public interface RemarkableApiClient {

	/**
	 * Register a new client with the specified client id and client type. Returns a login token that can be used to log in.
	 */
	String register(@NonNull UUID clientId, @NonNull String clientType, @NonNull String code);

	/**
	 * Unregister the client with the specified login token.
	 */
	void unregister(@NonNull String loginToken);

	/**
	 * Log the client in. Returns a session token that can be used for subsequent API calls.
	 */
	String login(@NonNull String loginToken);

	/**
	 * List all files on the reMarkable. You can choose whether to include a download link. Note that the link expires.
	 */
	List<ItemInfoDto> list(@NonNull String sessionToken, boolean includeBlobUrl);

	/**
	 * Download the specified link to the specified input stream processor.
	 */
	void download(@NonNull RemarkableDownloadLink link, @NonNull InputStreamProcessor processor);

	/**
	 * Update the metadata of one or more items on the reMarkable.
	 */
	List<ItemInfoDto> updateMetadata(@NonNull String sessionToken, @NonNull Collection<ItemInfoDto> itemInfos);

	/**
	 * Delete files or folders.
	 */
	List<ItemInfoDto> delete(@NonNull String sessionToken, @NonNull Collection<ItemInfoDto> itemInfos);

}
