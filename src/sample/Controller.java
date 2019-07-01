package sample;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Controller {

    @FXML
    private SplitPane splitPane1;
    @FXML
    private SplitPane splitPane2;
    @FXML
    private SplitPane splitPane3;
    @FXML
    private Label priceText;
    @FXML
    private Button cryptoButton1;
    @FXML
    private Button cryptoButton2;
    @FXML
    private Button cryptoButton3;
    @FXML
    private Button cryptoButton4;
    @FXML
    private Button cryptoButton5;
    @FXML
    private LineChart priceChart;
    @FXML
    private Button portfolioButton;

    private List<Button> cryptoButtonArray = new ArrayList<Button>();
    private JSONArray dataArray;
    private int selectedCryptoIndex = 0;
    private double currentPrice;
    private XYChart.Series series;
    private int time = 0;

    @FXML
    public void initialize() {

        cryptoButtonArray.add(cryptoButton1);
        cryptoButtonArray.add(cryptoButton2);
        cryptoButtonArray.add(cryptoButton3);
        cryptoButtonArray.add(cryptoButton4);
        cryptoButtonArray.add(cryptoButton5);

        series = new XYChart.Series();
        series.setName("Price");

        priceChart.getData().add(series);

        SetupDividers();
        GetCryptoData();

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(60), ev -> {
            GetCryptoData();
        }));

        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    @FXML void HandleChangeScene(Event event) {

        Stage stage = (Stage) priceText.getScene().getWindow();
        String named = "";

        if (event.getSource() == portfolioButton) {
            named = "portfolio.fxml";
        }


        try {
            SceneLoader.getInstance().loadScene(named, stage);
        }
        catch (Exception e) {
            System.out.println("EXCEPTION " + e);
        }
    }

    @FXML void HandleSelectCrypto(Event event) {
        //System.out.println(event.getSource());

        //priceText.setSelectionFill();
        priceText.setTextFill(Paint.valueOf("white"));
        //series.getData().removeAll();
        series.getData().clear();
        //priceChart.getData().removeAll();
        priceChart.getData().clear();
        time = 0;
        priceChart.getData().add(series);

        if (event.getSource() == cryptoButton1) {
            SelectCrypto(0);
        }
        else if (event.getSource() == cryptoButton2) {
            SelectCrypto(1);
        }
        else if (event.getSource() == cryptoButton3) {
            SelectCrypto(2);
        }
        else if (event.getSource() == cryptoButton4) {
            SelectCrypto(3);
        }
        else if (event.getSource() == cryptoButton5) {
            SelectCrypto(4);
        }
    }

    private void SelectCrypto(int index) {
        try {
            double price = Double.parseDouble(dataArray.getJSONObject(index).getJSONObject("quote").getJSONObject("USD").get("price").toString());

            series.getData().add(new XYChart.Data("" + time, price));
            NumberAxis yAxis = (NumberAxis) priceChart.getYAxis();

            if (series.getData().size() > 15){
                series.getData().remove(0);
            }

            yAxis.setAutoRanging(false);

            if (price < 1) {
                yAxis.setUpperBound(round(price, 4) + 0.005);
                yAxis.setLowerBound(round(price, 4) - 0.005);
                yAxis.setTickUnit(0.005);
            } else if (price < 100) {
                yAxis.setUpperBound(100);
                yAxis.setLowerBound(0);
                yAxis.setTickUnit(5);
            } else {
                yAxis.setUpperBound(round(price, 2) + 150);
                yAxis.setLowerBound(round(price,2) - 150);
                yAxis.setTickUnit(100);
            }

            if (price > currentPrice) {
                priceText.setTextFill(Paint.valueOf("green"));
            }
            else if (price < currentPrice) {
                priceText.setTextFill(Paint.valueOf("#ca3e47"));
            } else {
                priceText.setTextFill(Paint.valueOf("white"));
            }

            if (currentPrice == 0.0) {
                priceText.setTextFill(Paint.valueOf("white"));
            }

            time++;

            if (price < 1) {
                priceText.setText("USD " + round(price, 4));
            }
            else {
                priceText.setText("USD " + round(price, 2));
            }

            selectedCryptoIndex = index;
            currentPrice = price;
        }
        catch (JSONException e) {

        }

    }

    private void GetCryptoData() {

        System.out.println("GETTING DATA");

        String API_KEY = "2755ef08-46f9-4e61-8b71-678f7a005748";
        String uri = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest";
        List<NameValuePair> paratmers = new ArrayList<NameValuePair>();
        paratmers.add(new BasicNameValuePair("start","1"));
        paratmers.add(new BasicNameValuePair("limit","5"));
        paratmers.add(new BasicNameValuePair("convert","USD"));

        try {
            String result = makeAPICall(uri, paratmers, API_KEY);

            try {
                JSONObject jsonData = new JSONObject(result);
                dataArray = jsonData.getJSONArray("data");
                CryptoData.getInstance().setCryptoData(dataArray);

                for (int i = 0; i < dataArray.length(); i++) {
                    JSONObject cryptoData = dataArray.getJSONObject(i);
                    String symbol = cryptoData.get("symbol").toString();
                    cryptoButtonArray.get(i).setText(symbol);
                }
                SelectCrypto(selectedCryptoIndex);
            } catch (JSONException e) {

            }
        }
        catch (IOException e) {
            System.out.println("Error: cannont access content - " + e.toString());
        }
        catch (URISyntaxException e) {
            System.out.println("Error: Invalid URL " + e.toString());
        }
    }

    private String makeAPICall(String uri, List<NameValuePair> parameters, String apiKey)
        throws URISyntaxException, IOException {
            String response_content = "";
            URIBuilder query = new URIBuilder(uri);
            query.addParameters(parameters);

            CloseableHttpClient client = HttpClients.createDefault();
            HttpGet request = new HttpGet(query.build());

            request.setHeader(HttpHeaders.ACCEPT, "application/json");
            request.addHeader("X-CMC_PRO_API_KEY", apiKey);

            CloseableHttpResponse response = client.execute(request);

            try {
                //System.out.println(response.getStatusLine());
                HttpEntity entity = response.getEntity();
                response_content = EntityUtils.toString(entity);
                EntityUtils.consume(entity);
            }
            finally {
                response.close();
            }

            return response_content;
    }

    private void SetupDividers() {
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
        SplitPane.Divider divider4 = splitPane3.getDividers().get(0);
        divider4.positionProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                divider4.setPosition(0.1);
            }
        });
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
