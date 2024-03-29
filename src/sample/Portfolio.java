package sample;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

public class Portfolio {

    private List<Crypto> cryptos = new ArrayList();

    Portfolio() {

    }

    public List<Crypto> getCryptos() {
        return cryptos;
    }

    public void addCrypto(Crypto crypto) {
        for (int i = 0; i<cryptos.size(); i++) {
            if (cryptos.get(i).symbol == crypto.symbol) {
                cryptos.get(i).addAmount(crypto.amount);
                return;
            }
        }
        this.cryptos.add(crypto);
    }

    public float getPrice() {
        float price = 0;

        for (Crypto crypto : cryptos) {
            for (AbstractMap.SimpleEntry cryptoPrice : CryptoData.getInstance().getPrices()) {
                if (cryptoPrice.getKey() == crypto.symbol) {
                    price += crypto.amount * Double.parseDouble(cryptoPrice.getValue().toString());
                }
            }
        }

        return price;
    }
}
