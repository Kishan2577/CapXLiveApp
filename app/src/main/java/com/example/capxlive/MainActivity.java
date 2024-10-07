package com.example.capxlive;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

public class MainActivity extends AppCompatActivity {
    private EditText symbol;
    private Button search;
    private TextView result;
    private StockViewModel stockViewModel;
    private ProgressBar progressBar;
    private Handler handler = new Handler();
    private Runnable fetchStockDataRunnable;
    private static final int FETCH_INTERVAL = 30000; // INTERVAL CAN BE CHANGE.
    private final String error_msg = "Invalid symbol or no data found";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        symbol = findViewById(R.id.symbol);
        search = findViewById(R.id.search);
        result = findViewById(R.id.result);
        progressBar = findViewById(R.id.progressBar);

        stockViewModel = new ViewModelProvider(this).get(StockViewModel.class);

        stockViewModel.getStockData().observe(this, stockData -> {
            progressBar.setVisibility(View.GONE);
            if (stockData != null) {
                double per_change = ((stockData.getClose() - stockData.getOpen())/stockData.getOpen())*100;
                String data = "Open: " + stockData.getOpen() + "\nClose: " + stockData.getClose()+ "\nPercentage Change : " + String.format("%.4f", per_change);;

                result.setText(data);
            } else {
                result.setText(error_msg);
            }
        });

        symbol.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.e("TEST","TEXT CHANGED"); // For Debugging
                handler.removeCallbacks(fetchStockDataRunnable);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        // Set up the runnable for fetching stock data after every specific time for getting the latest price
        fetchStockDataRunnable = new Runnable() {
            @Override
            public void run() {
                String sym = symbol.getText().toString().trim();
                if (!sym.isEmpty()) {
                    Log.d("TEST", "TEST 1");  // For Debugging
                    stockViewModel.fetchStockData(sym);
                }
                // Repeat this runnable every 30 seconds
                handler.postDelayed(this, FETCH_INTERVAL);
            }
        };

        search.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE); // Show loading on first fetch
            if (isNetworkAvailable()) {
                handler.post(fetchStockDataRunnable);  // Run the fetch for first time after click on button
            }
            else
            {
                // Show a message if there is no internet connection
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Please connect to the internet", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove callbacks to prevent memory leaks
        handler.removeCallbacks(fetchStockDataRunnable);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}