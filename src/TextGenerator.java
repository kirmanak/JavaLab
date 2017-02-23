import com.google.gson.Gson;

import java.util.Scanner;
import java.util.Vector;
import java.util.regex.Pattern;

/**
 * EntryPoint, читающий консоль, чтобы определить команду, подлежащую выполнению.
 * Работает с файлом, записанным в переменную окружения под названием jsonFile.
 */

public class TextGenerator {
    static final String jsonFile = System.getenv("jsonFile");
    static final Gson gson = new Gson();
    static final Scanner scanner = new Scanner(System.in);
    private static final Pattern removeLastRegexp = Pattern.compile(" *" + Commands.remove.name() + "_last *");
    private static final Pattern removeRegexp = Pattern.compile(removeLastRegexp.pattern() + "| *" + Commands.remove.name() + " *\\d+ *");
    static Vector<Adults> collection = new Vector<>();

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(Commands.save::doIt));
        Commands.load.doIt();
        while (true) {
            System.out.print("Введите команду: ");
            String command = scanner.nextLine();
            if (command.matches(removeRegexp.pattern())) { //если это remove
                if (command.matches(removeLastRegexp.pattern())) { //если это remove_last (или похожая)
                    Commands.remove.doIt(collection.size());
                } else { //если это remove element
                    int index = -1;
                    for (String str : Pattern.compile("[^0-9]").split(command)) {
                        try {
                            index = Integer.parseInt(str);
                            break;
                        } catch (NumberFormatException err) {
                        }
                    }
                    Commands.remove.doIt(index);
                }
            } else { //если это не remove
                try {
                    Commands.valueOf(command).doIt();
                } catch (IllegalArgumentException err) {
                    System.err.println("Моя твоя не понимай. Попробуй написать " + Commands.help.name());
                }
            }
        }
    }
}