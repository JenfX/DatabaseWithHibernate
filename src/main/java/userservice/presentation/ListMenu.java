package userservice.presentation;

import userservice.service.UserService;

public class ListMenu extends Menu{

    public ListMenu(UserService service, ConsoleIO console, Menu parent) {
        super(service, console, parent);
    }

    @Override
    protected Menu execute() {
        System.out.println("=== Список пользователей ===\n");
        var users = service.readAll();
        if (users.isEmpty()) System.out.println("Список пуст.");
        else users.forEach(System.out::println);

        pause();
        return back();
    }
}
