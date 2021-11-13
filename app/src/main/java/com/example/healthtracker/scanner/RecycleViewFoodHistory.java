package com.example.healthtracker.scanner;

public class RecycleViewFoodHistory {
    int kcal, protein, fat, carb, sugar, noOfUnit;
    String foodID, foodName, userID;

    public String getFoodHistoryFoodID(){return foodID;}
    public String getFoodHistoryName(){return foodName;}
    public String getFoodHistoryUserID(){return userID;}
    public int getFoodHistoryKcal(){return kcal;}
    public int getFoodHistoryProtein(){return protein;}
    public int getFoodHistoryFat(){return fat;}
    public int getFoodHistoryCarb(){return carb;}
    public int getFoodHistorySugar(){return sugar;}
    public int getFoodHistoryNoOfUnit(){return noOfUnit;}
}
