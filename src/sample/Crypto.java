package sample;

public class Crypto {
    public String symbol;
    public float amount;

    Crypto(String symbol, float amount) {
        this.symbol = symbol;
        this.amount = amount;
    }

    void addAmount(float amount) {
        this.amount += amount;
    }
}
