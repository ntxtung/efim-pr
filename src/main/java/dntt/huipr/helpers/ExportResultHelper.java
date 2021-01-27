package dntt.huipr.helpers;
import dntt.entities.Item;
import dntt.entities.ItemSet;

import java.io.*;
import java.util.HashSet;

public class ExportResultHelper {
    public static void exportFrom(HashSet<ItemSet> result, String outputPath) {
        try {
            FileWriter fileWriter = new FileWriter(outputPath);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            for (ItemSet itemSet : result) {
                for (Item item : itemSet.getSet()) {
                    printWriter.print(item + " ");
                }
                printWriter.println();
            }
            printWriter.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
