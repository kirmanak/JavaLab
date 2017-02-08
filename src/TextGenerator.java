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
 */

public class TextGenerator {
    private static Vector<Adults> collection = new Vector<>();
    private static final File jsonFile = new File(System.getenv("jsonFile"));

    public static void main(String[] args) {
        load();
        while (true) {
            System.out.print("Введите команду:");
            String command = new Scanner(System.in).nextLine();
            if (command.contains("remove")) {
                try {
                    remove(Integer.parseInt(command.substring(7))-1);
                    System.out.println(Integer.parseInt(command.substring(7)) + " -й элемент удалён.");
                } catch (NumberFormatException err) {
                    if (command.equals("remove_last")) remove();
                    System.out.println("Последний элемент удалён.");
                }
            }
            if (command.equals("exit")) {
                save();
                break;
            }
            if (command.equals("load")) load();
            if (command.equals("save")) save();
            if (command.equals("help")) help();
            if (command.equals("print")) print();
        }
    }

    /**
     * Печатает содержимое коллекции и количество элементов в ней
     */
    private static void print () {
        System.out.println("В коллекции " + collection.size() + " элемент(ов)(а):");
        collection.forEach(adult -> System.out.println(adult.toString()));
    }

    /**
     * Удаляет элемент на позиции index
     * @param index позиция удаляемого элемента
     */
    private static void remove (int index) {
        if (index < collection.size() && index >= 0) {
            collection.remove(index);
        } else {
            System.out.println("Нет такого элемента.");
        }
    }

    /**
     * Удаляет последний элемент
     */
    private static void remove() {
        remove(collection.size()-1);
    }

    /**
     * Считывает коллекцию из JSON-файла
     */
    private static void load () {
        try {
            BufferedReader br = new BufferedReader(new FileReader(jsonFile));
            collection.clear();
            collection = new Gson().fromJson(br.readLine(), new TypeToken<Vector<Adults>>(){}.getType());
            br.close();
            collection.sort((o1, o2) -> o1.toString().compareTo(o2.toString()));
        } catch (IOException | NullPointerException err) {
            System.out.println("Файл ввода не найден. Не буду ничего читать.");
        }
    }

    /**
     * Записывает коллекцию в JSON-файл.
     */
    private static void save() {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(jsonFile));
            pw.flush();
            pw.print(new Gson().toJson(collection));
            pw.close();
        } catch (IOException | NullPointerException err) {
            System.out.println("Файл ввода не найден. Не буду ничего писать.");
        }
    }

    /**
     * Печатает справку по командам приложения
     */
    private static void help() {
        System.out.println("Я могу:");
        System.out.println("1. remove element - удалить элемент под номером element.");
        System.out.println("2. remove - удалить последний элемент.");
        System.out.println("3. save - сохранить коллекцию в JSON-файл.");
        System.out.println("4. load - считать коллекцию из JSON-файла.");
        System.out.println("5. print - напечатать коллекцию.");
        System.out.println("6. exit - сохранить и выйти.");
        System.out.println("7. help - вызвать эту справку.");
    }
}
