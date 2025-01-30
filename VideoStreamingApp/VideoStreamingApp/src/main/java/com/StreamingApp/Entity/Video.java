package com.StreamingApp.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "video")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Video {
    @Id
    @GeneratedValue
    private int videoId;
    private String title;
    private String description;
    private String contentType;
    private String filePath;

}
