package com.sebster.telegram.api.data;

import java.util.Optional;

public interface WithMimeType {

	Optional<String> getMimeType();

	default boolean hasMimeType() {
		return getMimeType().isPresent();
	}

}