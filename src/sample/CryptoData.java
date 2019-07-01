package sample;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

public class CryptoData {

    public List<AbstractMap.SimpleEntry> getPrices() {
        return prices;
    }

    private List<AbstractMap.SimpleEntry> prices = new ArrayList();

    private static final CryptoData instance = new CryptoData();

    public JSONArray getCryptoData() {
        return cryptoData;
    }

    public void setCryptoData(JSONArray cryptoData) {
        this.cryptoData = cryptoData;
        updatePrices();
    }

    private JSONArray cryptoData;

    private void CryptoData() {};

    public static CryptoData getInstance() {
        return instance;
    }

    private void updatePrices() {

        //for (int i = 0; i<prices.size(); i++) {
            prices.removeAll(prices);
        //}


        for (int i = 0; i<cryptoData.length(); i++) {
            try {
                double price = Double.parseDouble(cryptoData.getJSONObject(i).getJSONObject("quote").getJSONObject("USD").get("price").toString());
                String symbol = cryptoData.getJSONObject(i).getString("symbol");

                prices.add(new AbstractMap.SimpleEntry(symbol, price));
            } catch (JSONException exception) {

            }
        }
    }

}
