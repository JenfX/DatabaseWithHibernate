package userservice.presentation;

import userservice.exception.DaoException;
import userservice.exception.NotFoundException;
import userservice.exception.ValidationException;
import userservice.service.UserService;

public abstract class Menu {
    protected final UserService service;
    protected final ConsoleIO console;
    private final Menu parent;

    protected Menu(UserService service, ConsoleIO console, Menu parent) {
        this.service = service;
        this.console = console;
        this.parent = parent;
    }

    public final Menu show() {
        try {
            return execute();
        }
        catch (ValidationException | NotFoundException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
        catch (DaoException e) {
            System.out.println("Ошибка БД: " + e.getMessage());
        }
        return parent;
    }

    protected abstract Menu execute();

    protected Menu back() { return parent; }
    protected void pause() { console.readLine("\nНажмите Enter, чтобы продолжить.\n\n\n"); }
}
