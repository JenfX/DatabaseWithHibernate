package userservice.presentation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import userservice.service.UserService;

@Component
@ConditionalOnProperty(name = "app.console.enabled", havingValue = "true", matchIfMissing = true)
public class ConsoleRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(ConsoleRunner.class);

    private final UserService userService;
    private final ConsoleIO console;
    private final ConfigurableApplicationContext context;

    public ConsoleRunner(UserService userService,
                         ConsoleIO console,
                         ConfigurableApplicationContext context) {
        this.userService = userService;
        this.console = console;
        this.context = context;
    }

    @Override
    public void run(String... args) {
        log.info("Запуск консольного меню");

        Menu current = new MainMenu(userService, console);
        while (current != null) {
            current = current.show();
        }

        log.info("Выход из консольного меню, остановка приложения");
        int exitCode = SpringApplication.exit(context, () -> 0);
        System.exit(exitCode);
    }
}