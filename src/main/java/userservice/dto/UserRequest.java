package userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequest(

        @NotBlank(message = "Имя не может быть пустым")
        @Size(max = 50, message = "Имя не длиннее 50 символов")
        String name,

        @NotBlank(message = "Email не может быть пустым")
        @Email(message = "Некорректный email")
        String email,

        @Min(value = 0, message = "Возраст не меньше 0")
        @Max(value = 150, message = "Возраст не больше 150")
        Integer age
) {}