import java.util.Scanner;
import java.util.Vector;
import java.util.regex.Pattern;

/**
 * EntryPoint, читающий консоль, чтобы определить команду, подлежащую выполнению.
 * Работает с файлом, записанным в переменную окружения под названием jsonFile.
 */

public class TextGenerator {
    /**
     * Служебная переменная, служащая для чтения командной строки
     */
    static final Scanner scanner = new Scanner(System.in);
    /**
     * Регулярное выражение для команды remove_last
     */
    private static final Pattern removeLastRegexp = Pattern.compile(" *" + Commands.remove.name() + "_last *");
    /**
     * Регулярное выражение для команд с параметром
     */
    private static final Pattern elements = Pattern.compile(" *" + Commands.generate.name() +
            " *\\d+ *| *" + Commands.remove.name() + " *\\d+ *");
    /** Сама коллекция  */
    static Vector<Humans> collection = new Vector<>();

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(Commands.save::doIt));
        Commands.load.doIt();
        while (true) {
            System.out.print("Введите команду: ");
            String command = scanner.nextLine();
            decodeCommand(command);
        }
    }

    /**
     * Метод, декодирующий команду (и отправляющий её на исполнение)
     */
    private static void decodeCommand(String command) {
        if (command.matches(removeLastRegexp.pattern())) Commands.remove.doIt(collection.size());
        else {
            if (command.matches(elements.pattern())) { //если это команда, работающая с номером(количеством)
                int i = -1;
                for (String str : Pattern.compile("[^0-9]").split(command)) {
                    try {
                        i = Integer.parseInt(str);
                        break;
                    } catch (NumberFormatException ignored) {
                    }
                }
                if (command.matches(" *" + Commands.generate.name() + " *\\d+ *")) Commands.generate.doIt(i);
                else Commands.remove.doIt(i);
            } else { //если это команда без параметров
                try {
                    Commands.valueOf(command.replaceAll(" ", "")).doIt();
                } catch (IllegalArgumentException err) {
                    System.err.println("Моя твоя не понимай. Попробуй написать " + Commands.help.name());
                }
            }
        }
    }
}