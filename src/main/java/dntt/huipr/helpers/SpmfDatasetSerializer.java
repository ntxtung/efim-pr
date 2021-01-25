package dntt.huipr.helpers;

import dntt.entities.Dataset;
import dntt.entities.Item;
import dntt.entities.Transaction;
import dntt.huipr.DatasetMeta;
import dntt.huipr.TransactionMeta;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class SpmfDatasetSerializer {
    public static DatasetMeta loadFrom(String filePath) {
        DatasetMeta datasetMeta = new DatasetMeta(new Dataset());
        try {
            BufferedReader inputStream = new BufferedReader(new InputStreamReader(SpmfDatasetSerializer.class.getResourceAsStream(filePath)));
            var datasetUtility = 0;
            while (inputStream.ready()) {
                String[] recordSplit = inputStream.readLine().split(":");
                String[] item = recordSplit[0].split(" ");
                Integer transactionUtility = Integer.parseInt(recordSplit[1]);
                String[] utility = recordSplit[2].split(" ");
                TransactionMeta transactionMeta = new TransactionMeta(new Transaction());
                for (int i=0; i<item.length; i++) {
                    Item newItem = new Item(item[i]);
                    datasetMeta.getAllItem().add(newItem);
                    transactionMeta.getTransaction().getItemUtilityMap().put(
                        newItem,
                        Integer.parseInt(utility[i])
                    );
                }
                transactionMeta.setUtilityOfTransaction(transactionUtility);
                datasetUtility += transactionUtility;
                datasetMeta.getTransactionMetas().add(transactionMeta);
                datasetMeta.getDataset().getTransactions().add(transactionMeta.getTransaction());
            }
            datasetMeta.setUtilityOfDataset(datasetUtility);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return datasetMeta;
    }
}
