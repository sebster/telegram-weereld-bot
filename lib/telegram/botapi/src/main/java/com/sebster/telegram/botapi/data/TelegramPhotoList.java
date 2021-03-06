package com.sebster.telegram.botapi.data;

import static java.util.List.copyOf;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.Validate;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

/**
 * This object represents a photo or a file / sticker thumbnail. The photo consists one or more sizes.
 */
@Value
@Builder(toBuilder = true)
public class TelegramPhotoList implements Iterable<TelegramPhoto> {

	@NonNull List<TelegramPhoto> photos;

	public TelegramPhotoList(@NonNull List<TelegramPhoto> photos) {
		Validate.noNullElements(photos, "photos[%d] must not be null");
		this.photos = copyOf(photos);
	}

	public int getNumberOfSizes() {
		return photos.size();
	}

	public TelegramPhoto getPhoto(int index) {
		return photos.get(index);
	}

	@Override
	public Iterator<TelegramPhoto> iterator() {
		return photos.iterator();
	}

}
