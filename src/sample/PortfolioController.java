package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class PortfolioController {

    @FXML
    private Button homeButton;
    @FXML
    private ChoiceBox portfolioSelector;
    @FXML
    private SplitPane splitPane1;
    @FXML
    private SplitPane splitPane2;
    @FXML
    private Label priceLabel;
    @FXML
    private ListView listView;
    @FXML
    private ChoiceBox cryptoSelector;
    @FXML
    private LineChart priceChart;
    @FXML
    private TextField amountInput;

    private int numberOfPortfolios = 0;
    private JSONArray cryptoData;
    private List<Portfolio> portfolios = new ArrayList();
    private int selectedPortfolio;

    @FXML
    public void initialize() {
        portfolioSelector.getItems().add("Portfolio 1");
        portfolios.add(new Portfolio());
        numberOfPortfolios++;
        cryptoData = CryptoData.getInstance().getCryptoData();
        portfolioSelector.getSelectionModel().selectFirst();

        portfolioSelector.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                updateView();
            }
        });

        System.out.println(cryptoData.length());

        for (int i = 0; i<cryptoData.length(); i++) {
            try {
                System.out.println(cryptoData.getJSONObject(i).getString("symbol"));
                cryptoSelector.getItems().add(cryptoData.getJSONObject(i).getString("symbol"));
            } catch (JSONException exception) {
                System.out.println("EXCEPTION: " + exception);
            }
        }
        cryptoSelector.getSelectionModel().selectFirst();
        setupDividers();

    }

    private void setupDividers() {
        SplitPane.Divider divider1 = splitPane1.getDividers().get(0);
        divider1.positionProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                divider1.setPosition(0.25);
            }
        });
        SplitPane.Divider divider2 = splitPane2.getDividers().get(0);
        divider2.positionProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                divider2.setPosition(0.2);
            }
        });
        SplitPane.Divider divider3 = splitPane2.getDividers().get(1);
        divider3.positionProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                divider3.setPosition(0.9);
            }
        });
    }

    @FXML void handleChangeScene(Event event) {

        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        String named = "";

        if (event.getSource() == homeButton) {
            named = "home.fxml";
        }


        try {
            SceneLoader.getInstance().loadScene(named, stage);
        }
        catch (Exception e) {
            System.out.println("EXCEPTION " + e);
        }
    }

    @FXML void handleAddPortfolio(Event event) {
        portfolioSelector.getItems().add("Portfolio " + (numberOfPortfolios+1));
        numberOfPortfolios++;
        portfolios.add(new Portfolio());
        portfolioSelector.getSelectionModel().selectLast();
        updateView();
    }

    @FXML void handleAddCrypto(Event event) {
        if (amountInput.getText().isBlank()) return;

        String symbol = cryptoSelector.getSelectionModel().getSelectedItem().toString();
        float amount = Float.parseFloat(amountInput.getText());
        getSelectedPortfolio().addCrypto(new Crypto(symbol, amount));
        amountInput.clear();
        updateView();
    }

    Portfolio getSelectedPortfolio() {
        return portfolios.get(portfolioSelector.getSelectionModel().getSelectedIndex());
    }

    void updateView() {
        priceLabel.setText("USD " + getSelectedPortfolio().getPrice());
        listView.getItems().clear();
        Portfolio portfolio = getSelectedPortfolio();
        portfolio.getCryptos().forEach(crypto -> {
            listView.getItems().add(crypto.symbol + " : " + crypto.amount);
        });
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

}
