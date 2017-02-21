import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.util.Scanner;
import java.util.Vector;

/**
 * remove element - удалить элемент под номером element. <br>
 * remove_last - удалить последний элемент. <br>
 * save - сохранить коллекцию в JSON-файл. <br>
 * load - считать коллекцию из JSON-файла. <br>
 * print - напечатать содержимое коллекции и количество элементов. <br>
 * exit - сохранить и выйти. <br>
 * help - вызвать эту справку. <br>
 * add - добавить нового человека
 */

public class TextGenerator {
    private static final String jsonFile = System.getenv("jsonFile");
    private static final Gson gson = new Gson();
    private static final Scanner scanner = new Scanner(System.in);
    private static Vector<Adults> collection = new Vector<>();

    public static void main(String[] args) {
        load();
        while (true) {
            System.out.print("Введите команду:");
            String command = scanner.nextLine();
            switch (command) {
                case "exit":
                    save();
                    System.exit(0);
                case "print":
                    print();
                    break;
                case "help":
                    help();
                    break;
                case "save":
                    save();
                    break;
                case "load":
                    load();
                    break;
                case "add":
                    add();
                    break;
                default:
                    if (command.contains("remove")) {
                        remove(command);
                    } else {
                        System.err.println("Моя твоя не понимай. Можешь написать \"help\".");
                    }
            }
        }
    }

    /**
     * Печатает содержимое коллекции и количество элементов в ней
     */
    private static void print() {
        System.out.println("В коллекции " + collection.size() + " элемент(ов)(а):");
        collection.forEach(adult -> System.out.println(adult.toString()));
    }

    /**
     * Добавляет нового человека
     */
    private static void add() {
        System.out.print("Введите имя: ");
        String name = scanner.nextLine();
        System.out.print("Введите характер: ");
        String character = scanner.nextLine();
        System.out.print("Введите местонахождение: ");
        String location = scanner.nextLine();
        System.out.print("Введите время: ");
        String time = scanner.nextLine();
        System.out.println("Степени родства: ");
        for (Relative relative : Relative.values()) {
            System.out.print(relative + " ");
        }
        System.out.println();
        Adults adult = null;
        while (adult == null) {
            System.out.print("Введите степень родства: ");
            String relative = scanner.nextLine();
            try {
                adult = new Adults(name, character, new Location(location), time, Relative.valueOf(relative));
            } catch (IllegalArgumentException err) {
                System.err.println("Не могу понять. Если что, надо на английском.");
            }
        }
        collection.add(adult);
    }

    /**
     * Удаляет элемент из коллекции
     * @param command строка, содержащая слово "remove" и номер элемента либо _last
     */
    private static void remove(String command) {
        try {
            int index = Integer.parseInt(command.substring(7)) - 1;
            if (index < collection.size() && index >= 0) {
                collection.remove(index);
                System.out.println(index + 1 + "-й элемент удалён.");
            } else {
                System.out.println("Нет такого элемента.");
            }
        } catch (NumberFormatException | StringIndexOutOfBoundsException err) {
            if (command.equals("remove_last")) {
                remove("remove " + collection.size());
            } else System.err.println("Моя твоя не понимай.");
        }
    }

    /**
     * Считывает коллекцию из JSON-файла
     */
    private static void load() {
        try (BufferedReader br = new BufferedReader(new FileReader(jsonFile))) {
            String read = br.readLine();
            if (!(read == null || read.equals("null") || read.equals(""))) {
                collection.clear();
                collection = gson.fromJson(read, new TypeToken<Vector<Adults>>() {
                }.getType());
            } else System.out.println("Файл пуст");
        } catch (IOException | NullPointerException err) {
            System.err.println("Файл ввода не найден. Не буду ничего читать.");
        }
    }

    /**
     * Записывает коллекцию в JSON-файл.
     */
    private static void save() {
        try (PrintWriter pw = new PrintWriter(new File(jsonFile))) {
            pw.flush();
            pw.print(gson.toJson(collection));
        } catch (IOException | NullPointerException err) {
            System.err.println("Файл вывода не найден. Не буду ничего писать.");
        }
    }

    /**
     * Печатает справку по командам приложения
     */
    private static void help() {
        System.out.println("Я могу:");
        System.out.println("1. remove element - удалить элемент под номером element.");
        System.out.println("2. remove_last - удалить последний элемент.");
        System.out.println("3. save - сохранить коллекцию в JSON-файл.");
        System.out.println("4. load - считать коллекцию из JSON-файла.");
        System.out.println("5. print - напечатать коллекцию.");
        System.out.println("6. exit - сохранить и выйти.");
        System.out.println("7. help - напечатать эту справку.");
        System.out.println("8. add - добавить нового человека.");
    }
}
