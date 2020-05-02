package com.sebster.remarkable.cloudapi.impl.controller;

import static com.sebster.commons.collections.Lists.filter;
import static com.sebster.commons.collections.Lists.filterAndMap;
import static com.sebster.commons.collections.Lists.map;
import static com.sebster.remarkable.cloudapi.RemarkablePath.path;
import static com.sebster.remarkable.cloudapi.impl.controller.ItemInfoDto.FOLDER_TYPE;
import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.toList;

import java.time.Clock;
import java.time.Instant;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import com.sebster.commons.strings.Strings;
import com.sebster.remarkable.cloudapi.RemarkableClient;
import com.sebster.remarkable.cloudapi.RemarkableCollection;
import com.sebster.remarkable.cloudapi.RemarkableFolder;
import com.sebster.remarkable.cloudapi.RemarkableItem;
import com.sebster.remarkable.cloudapi.RemarkablePath;
import com.sebster.remarkable.cloudapi.RemarkableRootFolder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class RemarkableClientImpl implements RemarkableClient {

	private final @NonNull Clock clock;
	private final @NonNull RemarkableClientInfo info;
	private final @NonNull RemarkableApiClient apiClient;

	private RemarkableRootFolder cachedRoot;

	@Override
	@EqualsAndHashCode.Include
	public UUID getId() {
		return info.getClientId();
	}

	@Override
	public String getDescription() {
		return info.getDescription();
	}

	@Override
	public RemarkableRootFolder list() {
		if (cachedRoot != null) {
			return cachedRoot;
		}
		String sessionToken = apiClient.login(info.getLoginToken());
		cachedRoot = new ItemInfoDtoListUnmarshaller(apiClient.list(sessionToken, true)).unmarshal();
		return cachedRoot;
	}

	@Override
	public void createFolders(RemarkableFolder parent, @NonNull Collection<RemarkablePath> paths) {
		Map<RemarkablePath, ItemInfoDto> folderInfos = new LinkedHashMap<>();
		for (RemarkablePath path : paths) {
			// Navigate the path prefix until the component no longer exists.
			RemarkableCollection parentCollection = parent != null ? parent : list();
			RemarkableFolder newParent = null;
			while (path != null && parentCollection.hasFolder(path.getHead())) {
				newParent = parentCollection.getFolder(path.getHead());
				parentCollection = newParent;
				path = path.getTail().orElse(null);
			}

			// Create the folder creation request DTOs.
			Instant modificationTime = clock.instant();
			RemarkablePath parentPath = newParent != null ? newParent.getPath() : null;
			UUID parentId = newParent != null ? newParent.getId() : null;
			while (path != null) {
				UUID folderId = randomUUID();
				RemarkablePath folderPath = path(parentPath, path.getHead());
				folderInfos.putIfAbsent(folderPath,
						ItemInfoDto.builder()
								.id(folderId)
								.version(1)
								.modificationTime(modificationTime)
								.type(FOLDER_TYPE)
								.name(path.getHead())
								.parentId(parentId)
								.build()
				);
				parentPath = folderPath;
				parentId = folderInfos.get(parentPath).getId();
				path = path.getTail().orElse(null);
			}
		}

		// If any folders need to be created, invoke the API and check the results.
		if (!folderInfos.isEmpty()) {
			String sessionToken = apiClient.login(info.getLoginToken());
			apiClient.updateMetadata(sessionToken, folderInfos.values()).forEach(ErrorDto::throwOnError);
			clearCaches();
		}
	}

	@Override
	public void delete(@NonNull Collection<? extends RemarkableItem> items, boolean recursive) {
		var folders = filterAndMap(items, RemarkableItem::isFolder, RemarkableItem::asFolder);
		if (!recursive) {
			var nonEmptyFolders = filter(folders, RemarkableCollection::isNotEmpty);
			if (!nonEmptyFolders.isEmpty()) {
				throw new IllegalArgumentException("Folders not empty: " + Strings.join(", ", nonEmptyFolders));
			}
		}

		var foldersRecursive = folders.stream().flatMap(RemarkableCollection::recurse);
		var documents = items.stream().filter(RemarkableItem::isDocument);
		var itemsToDelete = Stream.concat(foldersRecursive, documents).distinct().collect(toList());

		// If any items need to be deleted, invoke the API and check the results.
		if (!itemsToDelete.isEmpty()) {
			String sessionToken = apiClient.login(info.getLoginToken());
			apiClient.delete(sessionToken, map(itemsToDelete, ItemInfoDto::fromItem)).forEach(ErrorDto::throwOnError);
			clearCaches();
		}
	}

	@Override
	public void clearCaches() {
		this.cachedRoot = null;
	}

	@Override
	public String toString() {
		return info.getDescription() + " (" + info.getClientId() + ")";
	}

}
