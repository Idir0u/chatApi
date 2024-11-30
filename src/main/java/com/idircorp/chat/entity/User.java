package com.idircorp.chat.entity;

import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @Column(name = "username")
    @NonNull
    @Size(min = 3, max = 50)
    @Basic(optional = false)
    private String username;

    @Column(unique = true)
    @Column(name = "phone")
    @NonNull
    @Size(10)
    @Basic(optional = false)
    private String phone;

    @Column(name = "first_name")
    @NonNull
    @Size(min = 3, max = 50)
    private String firstName;

    @Column(name = "last_name")
    @NonNull
    @Size(min = 3, max = 50)
    private String lastName;

    @Column(name = "password")
    @NonNull
    @Size(min = 8, max = 100)
    @Basic(optional = false)
    private String password;
    
    @Lob
    @Column(name = "image")
    @Basic(fetch = FetchType.LAZY)
    private byte[] image;

    // Method to set image from a file
    public void setImageFile(File imageFile) throws IOException {
        this.image = Files.readAllBytes(imageFile.toPath());
    }

    // Method to get image as a file
    public File getImageFile(String filePath) throws IOException {
        File file = new File(filePath);
        Files.write(file.toPath(), this.image);
        return file;
    }
}
