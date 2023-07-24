package tg_application.pojo_classes;

import lombok.Builder;
import lombok.Data;

@Data
@Builder

public class PatchUser {

    private String email;
    private  String dob;
}
