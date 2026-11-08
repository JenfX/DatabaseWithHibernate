package userservice.presentation;

import java.util.Scanner;
import java.util.regex.Pattern;

public class ConsoleIO {
    private final Scanner scanner = new Scanner(System.in);
    private static final Pattern EMAIL = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");

    public String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public long readLong(String prompt) {
        while (true) {
            String line = readLine(prompt);
            try { return Long.parseLong(line); }
            catch (NumberFormatException e) { System.out.println("Нужно ввести целое число."); }
        }
    }

    public Integer readInt(String prompt) {
        while (true) {
            String line = readLine(prompt);
            if (line.isEmpty()) return null;
            try { return Integer.parseInt(line); }
            catch (NumberFormatException e) { System.out.println("Нужно ввести целое число или оставить пустым."); }
        }
    }

    public String readEmail(String prompt) {
        while (true) {
            String line = readLine(prompt);
            if (line == null || !EMAIL.matcher(line).matches()) System.out.println("Неверный формат email!");
            else return line.trim().toLowerCase();
        }
    }
}
