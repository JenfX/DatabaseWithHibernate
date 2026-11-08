package userservice.presentation;

import userservice.entity.User;
import userservice.exception.DaoException;
import userservice.service.UserService;

public class DeleteMenu extends Menu{
    private User user;

    public DeleteMenu(UserService service, ConsoleIO console, Menu parent, User user) {
        super(service, console, parent);
        this.user = user;
    }

    @Override
    protected Menu execute() {
        System.out.println("=== Обновление данных ===\n");
        System.out.println("Хотите удалить пользователя? (y/n)");

        if (console.readLine("> ").equals("y")) {
            try {
                service.delete(user.getId());
                System.out.println("- Пользователь удалён -");
                pause();
                return back();
            } catch (DaoException e) {
                System.out.println("Произошла ошибка: " + e.getMessage());
                return back();
            }
        }
        else {
            System.out.println("- Отмена операции -");
            pause();
            return back();
        }
    }
}
