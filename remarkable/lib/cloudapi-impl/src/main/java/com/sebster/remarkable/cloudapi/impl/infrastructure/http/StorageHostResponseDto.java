package com.sebster.remarkable.cloudapi.impl.infrastructure.http;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
class StorageHostResponseDto {

	@JsonProperty("Status")
	private String status;

	@JsonProperty("Host")
	private String host;

}
