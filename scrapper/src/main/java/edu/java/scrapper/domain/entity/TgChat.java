package edu.java.scrapper.domain.entity;

import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TgChat {
    private Long id;
    private final OffsetDateTime createdAt;
}
