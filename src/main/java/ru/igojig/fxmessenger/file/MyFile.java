package ru.igojig.fxmessenger.file;

import javafx.collections.ObservableList;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class MyFile {

    public static final int LAST_LINES_COUNT = 4;

    private final String filename;

    public MyFile(int id) {
//        this.filename = "h_" + id + ".txt";
        this.filename = String.format("h_%04d.txt", id);
    }

    public void write(ObservableList<CharSequence> list) {


        try (FileWriter fileWriter = new FileWriter(filename)) {

            // в начало файла пишем общее количество строк в нем
            fileWriter.write(String.valueOf(list.size()) + '\n');

            for (CharSequence sequence : list) {
                fileWriter.write(sequence.toString());
                fileWriter.write('\n');
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Ну удалось записать историю: " + filename);
        }

    }

    public List<String> read() {
        File file = new File(filename);
        if (!file.exists()) {
            System.out.println("Файл: " + filename + " не найден");
            return Collections.emptyList();
        }
        try (FileReader fileReader = new FileReader(file);
             LineNumberReader l = new LineNumberReader(fileReader)) {

            int numLines = Integer.parseInt(l.readLine());
//            System.out.println(numLines);

            int from = numLines <= LAST_LINES_COUNT ? 0 : numLines - LAST_LINES_COUNT;

            while (l.getLineNumber() < from) {
                l.readLine();
            }

            List<String> strings = new ArrayList<>();
            for (int i = from; i < numLines; i++) {
                String s = l.readLine();
                strings.add(s);
            }
            return strings;

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Файл с историей сообщений не удалось загрузить: " + filename);
            return Collections.emptyList();
        }

    }

}
