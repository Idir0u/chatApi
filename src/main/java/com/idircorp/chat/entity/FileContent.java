package com.idircorp.chat.entity;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Column;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class FileContent extends Content {

    @Lob
    @Column(name = "file", nullable = false)
    private byte[] file;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_type", nullable = false)
    private String fileType;

}