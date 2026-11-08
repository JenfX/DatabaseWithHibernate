package userservice.presentation;

import userservice.entity.Option;
import userservice.service.UserService;

import java.util.LinkedHashMap;
import java.util.Map;

public class MainMenu extends Menu{

    private final Map<String, Option> options = new LinkedHashMap<>();
    private final ConsoleIO console;

    public MainMenu(UserService service, ConsoleIO console) {
        super(service, console, null);
        this.console = console;
        options.put("1", new Option("Создать нового пользователя", new CreateMenu(service, console, this)));
        options.put("2", new Option("Найти пользователя", new SearchMenu(service, console, this)));
        options.put("3", new Option("Показать список пользователей", new ListMenu(service, console, this)));
    }

    @Override
    protected Menu execute() {
        System.out.println("=== Главное меню ===\n");
        options.forEach((key, option) -> System.out.println(key + " - " + option.title()));
        System.out.println("0 - Выход\n====================");

        String choice = console.readLine("> ");
        if (choice.equals("0")) return null;

        Option option = options.get(choice);
        if (option != null) option.menu().show();
        else System.out.println("> Неизвестная команда <");
        return this;
    }
}
