package edu.java.scrapper.domain.entity;

import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Link {
    private Long id;
    private String url;
    private OffsetDateTime lastCheckTime;
    private OffsetDateTime createdAt;
    private String createdBy;
}
