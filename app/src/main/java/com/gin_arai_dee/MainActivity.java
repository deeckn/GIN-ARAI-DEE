package com.gin_arai_dee;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // openHomePage();
        // openFoodPage();
    }

    // Opens the home page
    private void openHomePage() {
        startActivity(new Intent(this, HomePage.class));
    }

    // Opens the food page
    private void openFoodPage() {
        startActivity(new Intent(this, FoodPage.class));
    }
}