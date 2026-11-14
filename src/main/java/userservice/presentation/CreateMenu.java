package userservice.presentation;

import userservice.entity.User;
import userservice.service.UserService;

public class CreateMenu extends Menu{

    public CreateMenu(UserService service, ConsoleIO console, Menu parent) {
        super(service, console, parent);
    }

    @Override
    protected Menu execute() {
        System.out.println("=== Создание пользователя ===\n");
        String name;
        while (true) {
            name = console.readLine("Имя: ");
            if (!name.isEmpty()) break;
            System.out.println("Имя не может быть пустым.");
        }
        Integer age = console.readInt("Возраст (Enter - Пропустить): ");
        String email;
        while (true) {
            email = console.readEmail("Email: ");
            if (!service.emailExists(email)) break;
            else System.out.println("Email уже занят.");
        }

        System.out.println("\n- Создана запись -\n\n" + service.create(new User(name, email, age)) + "\n");

        pause();
        return back();
    }
}
