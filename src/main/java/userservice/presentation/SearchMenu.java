package userservice.presentation;

import userservice.entity.Option;
import userservice.entity.User;
import userservice.service.UserService;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class SearchMenu extends Menu{

    public SearchMenu(UserService service, ConsoleIO console, Menu parent) {
        super(service, console, parent);
    }

    @Override
    protected Menu execute() {
        System.out.println("=== Поиск пользователя ===\n");
            long id = console.readLong("Введите id (0 - для выхода): ");
            if (id == 0) return back();

        Optional<User> result = service.read(id);
        if (!result.isPresent()) {
            System.out.println("Пользователь не найден");
            pause();
            return this;
        }

        User user = result.get();
        System.out.println("- Найден пользователь -");
        System.out.println(user);

        Map<String, Option> options = new LinkedHashMap<>();
        options.put("1", new Option("Обновить данные", new UpdateMenu(service, console, this, user)));
        options.put("2", new Option("Удалить пользователя", new DeleteMenu(service, console, this, user)));

        while (true) {
            System.out.println();
            options.forEach((key, option) -> System.out.println(key + " - " + option.title()));
            System.out.println("0 - Выход\n====================");
            String choice = console.readLine("> ");

            if (choice.equals("0")) return back();
            Option option = options.get(choice);
            if (option != null) return option.menu().show();
            else System.out.println("> Неизвестная команда <");
        }
    }
}
