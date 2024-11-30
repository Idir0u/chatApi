package com.idircorp.chat.entity;

import javax.persistence.Entity;
import javax.persistence.Column;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class TextContent extends Content {

    @Column(name = "text", nullable = false)
    private String text;

}