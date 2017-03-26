import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import javafx.scene.layout.GridPane;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import java.util.Vector;

/**
 * Enumeration команд, использующихся в программе
 */
enum Commands implements Runnable {
    /** Удаляет элемент из коллекции */
    remove {
        @Override
        public void run() {
            System.err.println("Something went wrong.");
        }

        public String toString() {
            return "remove element - удалить элемент под номером element.\n";
        }

        public String doIt(int index) {
            index--;
            if (index < TextGenerator.collection.size() && index >= 0) {
                TextGenerator.collection.remove(index);
                return String.format("%d-й элемент удалён", index+1);
            } else {
                return "Нет такого элемента.";
            }
        }
    },
    /** Записывает коллекцию в JSON-файл. */
    save {
        @Override
        public void run() {
            System.err.println(doIt());
        }

        public String toString() {
            return "save - сохранить коллекцию в JSON-файл.\n";
        }

        public String doIt() {
            try (PrintWriter pw = new PrintWriter(new File(jsonFile))) {
                pw.flush();
                pw.print(gson.toJson(TextGenerator.collection));
            } catch (IOException | NullPointerException err) {
                return "Файл вывода не найден. Не буду ничего писать. $jsonFile = " + jsonFile;
            }
            return null;
        }
    },
    /** Считывает коллекцию из JSON-файла */
    load{
        @Override
        public void run() {
            System.err.println(doIt());
            Thread.currentThread().notifyAll();
        }

        public String toString() {
            return "load - считать коллекцию из JSON-файла.\n";
        }

        public String doIt() {
            try (BufferedReader br = new BufferedReader(new FileReader(jsonFile))) {
                String read = br.readLine();
                if (!(read == null || read.isEmpty())) {
                    try {
                        TextGenerator.collection = gson.fromJson(read, new TypeToken<Vector<Humans>>() {
                        }.getType());
                    } catch (JsonSyntaxException err) {
                        return "Ошибка чтения коллекции из файла.";
                    }
                } else return "Файл пуст";
            } catch (IOException | NullPointerException err) {
                return "Файл ввода не найден. Не буду ничего читать. $jsonFile = " + jsonFile;
            }
            return null;
        }
    },
    /** Печатает содержимое коллекции и количество элементов в ней */
    print {
        @Override
        public void run() {
            System.err.println("Something went wrong.");
        }

        public String toString() {
            return "print - напечатать коллекцию.\n";
        }

        public String doIt() {
            StringBuilder string = new StringBuilder();
            for (Humans humans : TextGenerator.collection) string.append(humans.toString()).append("\n");
            return string.toString();
        }
    },
    /** Добавляет нового человека */
    add {
        @Override
        public void run() {
            System.err.println("Something went wrong.");
        }

        public String toString() {
            return "add - добавить нового человека.\n";
        }

        public String doIt() {
            System.out.print("Введите имя: ");
            String name = TextGenerator.scanner.nextLine();
            System.out.print("Введите характер: ");
            String character = TextGenerator.scanner.nextLine();
            System.out.print("Введите местонахождение: ");
            String location = TextGenerator.scanner.nextLine();
            System.out.print("Введите время: ");
            String time = TextGenerator.scanner.nextLine();
            System.out.println("Степени родства: ");
            for (Relative relative : Relative.values()) {
                System.out.print(relative.name() + " ");
            }
            System.out.println();
            Humans human = null;
            while (human == null) {
                System.out.print("Введите степень родства: ");
                String relative = TextGenerator.scanner.nextLine();
                try {
                    human = new Humans(name, character, new Location(location), time, Relative.valueOf(relative));
                } catch (IllegalArgumentException err) {
                    System.err.println("Не могу понять. Если что, надо на английском.");
                }
            }
            TextGenerator.collection.add(human);
            return null;
        }
    },
    /** Генерирует новых людей */
    generate {
        @Override
        public void run() {
            System.err.println("Something went wrong.");
        }

        public String toString() {
            return "generate - сгенерировать нового человека на основе данных, заложенных разработчиком" +
                    "(может быть довольно весело)";
        }

        private Humans generate() {
            String name = names[randomize.nextInt(names.length)];
            String time = times[randomize.nextInt(times.length)];
            String character = characters[randomize.nextInt(characters.length)];
            Location location = new Location(locations[randomize.nextInt(locations.length)]);
            Relative relative = Relative.values()[randomize.nextInt(Relative.values().length)];
            return new Humans(name, character, location, time, relative);
        }

        public String doIt() {
            return "Нужно указать количество элементов, которое нужно сгенерировать.";
        }

        public String doIt(int amountOfElements) {
            if (amountOfElements >= 0 && amountOfElements <= 100) {
                for (int i = 0; i < amountOfElements; i++) {
                    TextGenerator.collection.add(generate());
                }
                return "Сгенерировал " + amountOfElements + " элементов.";
            } else {
                return "Кажется, ты ошибся в количестве элементов. Я работаю с числами в диапозоне [0;100].";
            }
        }
    };

    /** Переменная с именем файла, в котором хранится коллекция */
    private static final String jsonFile = System.getenv("jsonFile");

    /** Служебная переменная для работы с файлом */
    private static final Gson gson = new Gson();

    /** "Коллекция" имён для генератора */
    private static final String[] names = {"Папа", "Мама", "Юлиус", "Боссе", "Бетан", "Хильдур Бок"};

    /** "Коллекция" мест для генератора */
    private static final String[] locations = {"дома", "на крыше", "на улице", "у бабушки"};

    /** "Коллекция" времён для генератора (как долго персонаж будет в этом месте) */
    private static final String[] times = {"вечно", "на каникулы", "весь отпуск", "день", "неделю"};
    /** "Коллекция" характеров */
    private static final String[] characters = {"твёрдым", "мягким", "игривым", "тяжёлым", "весёлым"};

    /** Служебная переменная рандомайзер для генератора */
    private static final Random randomize = new Random();

    /** Метод, который вызывается у управляющих команд без параметра */
    public String doIt() {
        return "Что-то пошло не так.";
    }

    /**
     * Метод, вызывающийся у управляющих команд с параметром
     * @param i параметр, передающийся команде
     */
    public String doIt(int i) {
        return "Что-то пошло не так.";
    }

    public void action(GridPane pane) {
        System.err.println("Ты забыл добавить действие новой команде.");
    }

    @Override
    public void run() {
        System.err.println("Something went wrong.");
    }
}
