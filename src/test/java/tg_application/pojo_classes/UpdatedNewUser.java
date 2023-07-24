package tg_application.pojo_classes;

import lombok.Builder;
import lombok.Data;

@Data
@Builder

public class UpdatedNewUser {
    private String firstName;
    private String lastName;
    private String email;
    private String dob;



}
