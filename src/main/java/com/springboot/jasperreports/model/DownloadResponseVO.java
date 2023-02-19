package com.springboot.jasperreports.model;

import org.springframework.core.io.Resource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class DownloadResponseVO {

	private Resource arquivo;
	private String nome;
	private String mediaType;
}
