package ru.ifmo.se.kirmanak;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Random;
import java.util.Vector;

/**
 * Enumeration команд, использующихся в программе
 */
enum Commands {
    /** Удаляет элемент из коллекции */
    remove {
        public String toString() {
            return "Удалить элемент, на который указывает слайдер.\n";
        }

        public String doIt(int index) {
            index--;
            if (index < Main.collection.size() && index >= 0) {
                Main.collection.remove(index);
                return String.format("%d-й элемент удалён", index+1);
            } else {
                return "Нет такого элемента.";
            }
        }
    },
    /** Записывает коллекцию в JSON-файл. */
    save {
        public String toString() {
            return "Сохранить коллекцию в JSON-файл.\n";
        }

        public String doIt() {
            try (PrintWriter pw = new PrintWriter(new File(jsonFile))) {
                pw.flush();
                pw.print(gson.toJson(Main.collection));
            } catch (IOException | NullPointerException err) {
                return "Файл вывода не найден. Не буду ничего писать. $jsonFile = " + jsonFile;
            }
            return "Запись в файл прошла успешно.";
        }
    },
    /** Считывает коллекцию из JSON-файла */
    load {
        public String toString() {
            return "Cчитать коллекцию из JSON-файла.\n";
        }

        public String doIt() {
            try (BufferedReader br = new BufferedReader(new FileReader(jsonFile))) {
                String read = br.readLine();
                if (!(read == null || read.isEmpty())) {
                    try {
                        Main.collection = gson.fromJson(read, new TypeToken<Vector<Humans>>() {
                        }.getType());
                    } catch (JsonSyntaxException err) {
                        return "Ошибка чтения коллекции из файла.";
                    }
                } else return "Файл пуст";
            } catch (IOException | NullPointerException err) {
                return "Файл ввода не найден. Не буду ничего читать. $jsonFile = " + jsonFile;
            }
            return "Чтение из файла прошло успешно.";
        }
    },
    /** Добавляет нового человека */
    add {
        public String toString() {
            return "Добавить нового человека.\n";
        }

        public String doIt() {
            String name = Main.name.getText(),
                    character = Main.character.getText();
            LocalDate time = Main.picker.getValue();
            Relative relative = Main.relations.getValue();
            Location location = new Location(Main.location.getText());
            Main.collection.add(new Humans(name, character, location, time, relative));
            return "Новый человек добавлен в коллекцию.";
        }
    },
    /** Генерирует новых людей */
    generate {
        public String toString() {
            return "Сгенерировать элементов столько, сколько показывает слайдер, " +
                    "\n со случайными наборами полей (может быть довольно весело)";
        }

        private Humans generate() {
            String name = names[randomize.nextInt(names.length)];
            LocalDate time = LocalDate.now();
            try {
                time = LocalDate.ofEpochDay(LocalDate.now().toEpochDay() + Math.abs(randomize.nextInt(365)));
            } catch (DateTimeException err) {
                System.err.println(err.getLocalizedMessage());
            }
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
                    Main.collection.add(generate());
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
    /** "Коллекция" характеров */
    private static final String[] characters = {"твёрдым", "мягким", "тяжёлым", "весёлым"};

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
}
