package userservice.presentation;

import userservice.entity.User;
import userservice.exception.ValidationException;
import userservice.service.UserService;

public class UpdateMenu extends Menu{
    private User user;

    public UpdateMenu(UserService service, ConsoleIO console, Menu parent, User user) {
        super(service, console, parent);
        this.user = user;
    }

    @Override
    protected Menu execute() {
        System.out.println("=== Обновление данных ===\n");
        System.out.println("""
                1 - Имя
                2 - Возраст
                3 - Email
                9 - Обновить данные
                0 - Назад
                """);

        while (true) {
            switch (console.readLine("> ")) {
                case "1" -> user.setName(console.readLine("Введите имя: "));
                case "2" -> user.setAge(console.readInt("Введите возраст: "));
                case "3" -> user.setEmail(console.readEmail("Введите адрес почты: "));
                case "9" -> {
                    try {
                        service.update(user);
                        System.out.println("- Изменения сохранены -");
                        pause();
                        return back();
                    } catch (ValidationException e) {
                        System.out.println("Ошибка: " + e.getMessage());
                        System.out.println("Проверьте данные и попробуйте снова");
                    }
                }
                case "0" -> {
                    System.out.println("- Изменения отменены -");
                    return back();
                }
                default -> System.out.println("> Неизвестная команда <");
            }
        }
    }
}
