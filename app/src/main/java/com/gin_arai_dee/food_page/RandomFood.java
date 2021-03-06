package com.gin_arai_dee.food_page;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.gin_arai_dee.R;
import com.gin_arai_dee.general.DatabaseHelper;
import com.gin_arai_dee.general.FoodItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

public class RandomFood extends AppCompatActivity {

    // Data Access Object
    DatabaseHelper db;

    // UI Elements
    ImageView imageDisplay;
    TextView cardTitle;
    TextView cardCalorie;
    TextView cardDescription;
    GridLayout randomizeButton;

    // Category Selector Box
    boolean categoryBoxState = true;
    boolean categoryLoaded = false;
    GridLayout categorySelectorBox;
    TextView chooseCategoryTitle;
    ImageButton categoryDropdownArrow;
    ArrayList<TextView> categoryTextViews;
    ArrayList<CheckBox> categoryCheckBoxes;
    TextView main_dish_text;
    TextView appetizer_text;
    TextView desserts_text;
    TextView snacks_text;
    TextView beverages_text;
    CheckBox main_dish_checkbox;
    CheckBox appetizer_checkbox;
    CheckBox desserts_checkbox;
    CheckBox snacks_checkbox;
    CheckBox beverages_checkbox;

    // Nationality Selector Box
    boolean nationalityBoxState = true;
    boolean nationalityLoaded = false;
    GridLayout nationalitySelectorBox;
    TextView chooseNationalityTitle;
    ImageButton nationalityDropdownArrow;
    ArrayList<TextView> nationalityTextViews;
    ArrayList<CheckBox> nationalityCheckBoxes;
    TextView thai_text;
    TextView japanese_text;
    TextView italian_text;
    TextView chinese_text;
    TextView korean_text;
    CheckBox thai_checkbox;
    CheckBox japanese_checkbox;
    CheckBox italian_checkbox;
    CheckBox chinese_checkbox;
    CheckBox korean_checkbox;

    // Food Items
    List<FoodItem> allFoodItems;
    List<FoodItem> displayFoodItems;
    HashMap<String, List<FoodItem>> categoryFoodGroup;
    HashMap<String, List<FoodItem>> nationalityFoodGroup;
    List<String> categoryFilter;
    List<String> nationalityFilter;

    // String Constants
    public static final String MAIN_DISH    = "main_dish";
    public static final String APPETIZERS   = "appetizer";
    public static final String SNACKS       = "snack";
    public static final String DESSERTS     = "dessert";
    public static final String BEVERAGES    = "beverage";
    public static final String THAI         = "thai";
    public static final String ITALIAN      = "italian";
    public static final String JAPANESE     = "japanese";
    public static final String CHINESE      = "chinese";
    public static final String KOREAN       = "korean";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_food);
        initializeInstances();
        loadFoodItems();
        setDisplayFood(allFoodItems.get(0));

        // Randomize Button Listener
        randomizeButton.setOnClickListener(v -> setDisplayFood(randomizeFoodItem()));

        // Category Dropdown Settings
        chooseCategoryTitle.setOnClickListener(this::toggleCategoryBox);
        categoryDropdownArrow.setOnClickListener(this::toggleCategoryBox);

        // Nationality Dropdown Settings
        chooseNationalityTitle.setOnClickListener(this::toggleNationalityBox);
        nationalityDropdownArrow.setOnClickListener(this::toggleNationalityBox);

        // Category Checkbox Settings
        main_dish_checkbox.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) categoryFilter.add(MAIN_DISH);
            else categoryFilter.remove(MAIN_DISH);
            filterFoodItems();
        });

        appetizer_checkbox.setOnCheckedChangeListener(((compoundButton, b) -> {
            if (b) categoryFilter.add(APPETIZERS);
            else categoryFilter.remove(APPETIZERS);
            filterFoodItems();
        }));

        snacks_checkbox.setOnCheckedChangeListener(((compoundButton, b) -> {
            if (b) categoryFilter.add(SNACKS);
            else categoryFilter.remove(SNACKS);
            filterFoodItems();
        }));

        desserts_checkbox.setOnCheckedChangeListener(((compoundButton, b) -> {
            if (b) categoryFilter.add(DESSERTS);
            else categoryFilter.remove(DESSERTS);
            filterFoodItems();
        }));

        beverages_checkbox.setOnCheckedChangeListener(((compoundButton, b) -> {
            if (b) categoryFilter.add(BEVERAGES);
            else categoryFilter.remove(BEVERAGES);
            filterFoodItems();
        }));

        // Nationality Checkbox Settings
        thai_checkbox.setOnCheckedChangeListener(((compoundButton, b) -> {
            if (b) nationalityFilter.add(THAI);
            else nationalityFilter.remove(THAI);
            filterFoodItems();
        }));

        italian_checkbox.setOnCheckedChangeListener(((compoundButton, b) -> {
            if (b) nationalityFilter.add(ITALIAN);
            else nationalityFilter.remove(ITALIAN);
            filterFoodItems();
        }));

        japanese_checkbox.setOnCheckedChangeListener(((compoundButton, b) -> {
            if (b) nationalityFilter.add(JAPANESE);
            else nationalityFilter.remove(JAPANESE);
            filterFoodItems();
        }));

        chinese_checkbox.setOnCheckedChangeListener(((compoundButton, b) -> {
            if (b) nationalityFilter.add(CHINESE);
            else nationalityFilter.remove(CHINESE);
            filterFoodItems();
        }));

        korean_checkbox.setOnCheckedChangeListener(((compoundButton, b) -> {
            if (b) nationalityFilter.add(KOREAN);
            else nationalityFilter.remove(KOREAN);
            filterFoodItems();
        }));
    }

    private void initializeInstances() {
        db = new DatabaseHelper(this);

        imageDisplay            = findViewById(R.id.imageDisplay);
        cardTitle               = findViewById(R.id.cardTitle);
        cardCalorie             = findViewById(R.id.cardCalorie);
        cardDescription         = findViewById(R.id.cardDescription);
        randomizeButton         = findViewById(R.id.randomizeButton);
        categorySelectorBox     = findViewById(R.id.category_selector_box);
        nationalitySelectorBox  = findViewById(R.id.nationality_selector_box);

        // Food Data
        displayFoodItems = new ArrayList<>();
        categoryFoodGroup = new HashMap<>();
        categoryFoodGroup.put(MAIN_DISH,    new ArrayList<>());
        categoryFoodGroup.put(APPETIZERS,   new ArrayList<>());
        categoryFoodGroup.put(SNACKS,       new ArrayList<>());
        categoryFoodGroup.put(DESSERTS,     new ArrayList<>());
        categoryFoodGroup.put(BEVERAGES,    new ArrayList<>());

        nationalityFoodGroup = new HashMap<>();
        nationalityFoodGroup.put(THAI,      new ArrayList<>());
        nationalityFoodGroup.put(ITALIAN,   new ArrayList<>());
        nationalityFoodGroup.put(JAPANESE,  new ArrayList<>());
        nationalityFoodGroup.put(CHINESE,   new ArrayList<>());
        nationalityFoodGroup.put(KOREAN,    new ArrayList<>());

        categoryFilter = new ArrayList<>();
        nationalityFilter = new ArrayList<>();

        // Category and Nationality Dropdowns
        chooseCategoryTitle         = findViewById(R.id.choose_category_title);
        categorySelectorBox         = findViewById(R.id.category_selector_box);
        categoryDropdownArrow       = findViewById(R.id.category_arrow);
        chooseNationalityTitle      = findViewById(R.id.choose_nationality_title);
        nationalitySelectorBox      = findViewById(R.id.nationality_selector_box);
        nationalityDropdownArrow    = findViewById(R.id.nationality_arrow);

        // Category Dropdown Box Elements
        categoryTextViews   = new ArrayList<>();
        main_dish_text      = new TextView(this);
        appetizer_text      = new TextView(this);
        desserts_text       = new TextView(this);
        snacks_text         = new TextView(this);
        beverages_text      = new TextView(this);

        categoryCheckBoxes  = new ArrayList<>();
        main_dish_checkbox  = new CheckBox(this);
        appetizer_checkbox  = new CheckBox(this);
        desserts_checkbox   = new CheckBox(this);
        snacks_checkbox     = new CheckBox(this);
        beverages_checkbox  = new CheckBox(this);

        // Nationality Dropdown Box Elements
        nationalityTextViews = new ArrayList<>();
        thai_text       = new TextView(this);
        japanese_text   = new TextView(this);
        italian_text    = new TextView(this);
        chinese_text    = new TextView(this);
        korean_text     = new TextView(this);

        nationalityCheckBoxes = new ArrayList<>();
        thai_checkbox       = new CheckBox(this);
        japanese_checkbox   = new CheckBox(this);
        italian_checkbox    = new CheckBox(this);
        chinese_checkbox    = new CheckBox(this);
        korean_checkbox     = new CheckBox(this);
    }

    /***
     * UI Page Functionality
     */

    // Converts integer value to dp units
    private int getValueInDp(int value) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (value * scale + 0.5f);
    }

    // Expand and minimize category selector box
    public void toggleCategoryBox(View view) {
        if (!categoryLoaded) {
            // Loads TextViews into ArrayList
            categoryTextViews.add(main_dish_text);
            categoryTextViews.add(appetizer_text);
            categoryTextViews.add(desserts_text);
            categoryTextViews.add(snacks_text);
            categoryTextViews.add(beverages_text);

            // Loads Checkboxes to ArrayList
            categoryCheckBoxes.add(main_dish_checkbox);
            categoryCheckBoxes.add(appetizer_checkbox);
            categoryCheckBoxes.add(desserts_checkbox);
            categoryCheckBoxes.add(snacks_checkbox);
            categoryCheckBoxes.add(beverages_checkbox);

            // Category UI Elements Setup
            main_dish_text.setText(R.string.main_dish);
            appetizer_text.setText(R.string.appetizers);
            desserts_text.setText(R.string.desserts);
            snacks_text.setText(R.string.snacks);
            beverages_text.setText(R.string.beverages);

            Typeface rubik_regular = ResourcesCompat.getFont(this, R.font.rubik);

            for (TextView t : categoryTextViews) {
                t.setTextSize(18);
                t.setPadding(getValueInDp(20), 0, 0, 0);
                t.setTypeface(rubik_regular);
            }

            for (CheckBox c : categoryCheckBoxes) {
                c.setGravity(Gravity.END);
                c.setPadding(getValueInDp(20), 0, 0, 0);
            }

            categoryTextViews.get(categoryTextViews.size()-1)
                    .setPadding(getValueInDp(20), 0, 0, getValueInDp(8));
            categoryCheckBoxes.get(categoryCheckBoxes.size()-1)
                    .setPadding(0, 0, getValueInDp(5), getValueInDp(8));

            categoryLoaded = true;
        }

        // Performing tasks based on CategoryBoxState boolean
        if (categoryBoxState) {
            categoryDropdownArrow.animate().rotation(90).setDuration(300);

            for (int i = 0; i < categoryTextViews.size(); i++) {
                categorySelectorBox.addView(categoryTextViews.get(i));
                categorySelectorBox.addView(categoryCheckBoxes.get(i));
            }
        }
        else {
            categoryDropdownArrow.animate().rotation(0).setDuration(300);
            for (int i = 0; i < categoryTextViews.size() * 2; i++)
                categorySelectorBox.removeViewAt(2);
        }
        categoryBoxState = !categoryBoxState;
    }

    public void toggleNationalityBox(View view) {
        if (!nationalityLoaded) {
            // Loads TextViews into ArrayList
            nationalityTextViews.add(thai_text);
            nationalityTextViews.add(japanese_text);
            nationalityTextViews.add(italian_text);
            nationalityTextViews.add(chinese_text);
            nationalityTextViews.add(korean_text);

            // Loads Checkboxes to ArrayList
            nationalityCheckBoxes.add(thai_checkbox);
            nationalityCheckBoxes.add(japanese_checkbox);
            nationalityCheckBoxes.add(italian_checkbox);
            nationalityCheckBoxes.add(chinese_checkbox);
            nationalityCheckBoxes.add(korean_checkbox);

            // Category UI Elements Setup
            thai_text.setText(R.string.thai);
            japanese_text.setText(R.string.japanese);
            italian_text.setText(R.string.italian);
            chinese_text.setText(R.string.chinese);
            korean_text.setText(R.string.korean);

            Typeface rubik_regular = ResourcesCompat.getFont(this, R.font.rubik);

            for (TextView t : nationalityTextViews) {
                t.setTextSize(18);
                t.setPadding(getValueInDp(20), 0, 0, 0);
                t.setTypeface(rubik_regular);
            }

            for (CheckBox c : nationalityCheckBoxes) {
                c.setGravity(Gravity.END);
                c.setPadding(getValueInDp(20), 0, 0, 0);
            }

            nationalityTextViews.get(nationalityTextViews.size()-1)
                    .setPadding(getValueInDp(20), 0, 0, getValueInDp(8));
            nationalityCheckBoxes.get(nationalityCheckBoxes.size()-1)
                    .setPadding(0, 0, getValueInDp(5), getValueInDp(8));

            nationalityLoaded = true;
        }

        // Performing tasks based on CategoryBoxState boolean
        if (nationalityBoxState) {
            nationalityDropdownArrow.animate().rotation(90).setDuration(300);

            for (int i = 0; i < nationalityTextViews.size(); i++) {
                nationalitySelectorBox.addView(nationalityTextViews.get(i));
                nationalitySelectorBox.addView(nationalityCheckBoxes.get(i));
            }
        }
        else {
            nationalityDropdownArrow.animate().rotation(0).setDuration(300);
            for (int i = 0; i < nationalityTextViews.size() * 2; i++)
                nationalitySelectorBox.removeViewAt(2);
        }
        nationalityBoxState = !nationalityBoxState;
    }

    private void loadFoodItems() {
        allFoodItems = db.getAllFoodItems();
        groupFoodItems();
        filterFoodItems();
    }

    private void setDisplayFood(FoodItem foodItem) {
        cardTitle.setText(foodItem.getFood_item());
        String calorie = foodItem.getKcal() + " kcal";
        cardCalorie.setText(calorie);
        cardDescription.setText(foodItem.getDescription());
        Picasso.get().load(foodItem.getImage_url()).into(imageDisplay);
    }

    // Group food items into different nationalities and categories
    private void groupFoodItems() {
        for (FoodItem food : allFoodItems) {
            String dishType = food.getDish_type();
            switch (dishType) {
                case MAIN_DISH:
                    Objects.requireNonNull(categoryFoodGroup.get(MAIN_DISH)).add(food);
                    break;
                case APPETIZERS:
                    Objects.requireNonNull(categoryFoodGroup.get(APPETIZERS)).add(food);
                    break;
                case SNACKS:
                    Objects.requireNonNull(categoryFoodGroup.get(SNACKS)).add(food);
                    break;
                case DESSERTS:
                    Objects.requireNonNull(categoryFoodGroup.get(DESSERTS)).add(food);
                    break;
                case BEVERAGES:
                    Objects.requireNonNull(categoryFoodGroup.get(BEVERAGES)).add(food);
                    break;
                default:
                    System.out.println("No category");
            }

            String nationality = food.getNationality();
            switch (nationality) {
                case THAI:
                    Objects.requireNonNull(nationalityFoodGroup.get(THAI)).add(food);
                    break;
                case ITALIAN:
                    Objects.requireNonNull(nationalityFoodGroup.get(ITALIAN)).add(food);
                    break;
                case JAPANESE:
                    Objects.requireNonNull(nationalityFoodGroup.get(JAPANESE)).add(food);
                    break;
                case CHINESE:
                    Objects.requireNonNull(nationalityFoodGroup.get(CHINESE)).add(food);
                    break;
                case KOREAN:
                    Objects.requireNonNull(nationalityFoodGroup.get(KOREAN)).add(food);
                    break;
                default:
                    System.out.println("No nationality");
            }
        }
    }

    // Filter food by nationality and category
    private void filterFoodItems() {
        Set<FoodItem> tempList = new HashSet<>();
        displayFoodItems.clear();

        if (categoryFilter.isEmpty() && nationalityFilter.isEmpty())
            tempList.addAll(allFoodItems);

        if (!categoryFilter.isEmpty() && nationalityFilter.isEmpty()) {
            for (String type : categoryFilter) {
                List<FoodItem> temp = categoryFoodGroup.get(type);
                if (temp == null || temp.isEmpty()) continue;
                tempList.addAll(temp);
            }
        }

        if (categoryFilter.isEmpty() && !nationalityFilter.isEmpty()) {
            for (String type : nationalityFilter) {
                List<FoodItem> temp = nationalityFoodGroup.get(type);
                if (temp == null || temp.isEmpty()) continue;
                tempList.addAll(temp);
            }
        }

        if (!(categoryFilter.isEmpty() && nationalityFilter.isEmpty())) {
            for (FoodItem f : allFoodItems) {
                if (categoryFilter.contains(f.getDish_type())
                        && nationalityFilter.contains(f.getNationality())) {
                    tempList.add(f);
                }
            }
        }

        displayFoodItems.addAll(tempList);
    }

    private FoodItem randomizeFoodItem() {
        Random random = new Random();
        int size = displayFoodItems.size();
        int index = random.nextInt(size);
        return displayFoodItems.get(index);
    }
}