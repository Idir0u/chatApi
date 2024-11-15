package entity;

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

    private String username;
    private String phone;
    private String firstName;
    private String lastName;
    
    @Lob
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
