package com.example.healthtracker.healthymeal.MealRecyclerView;

public class RecycleViewMeal {
    int carb, fat, kcal, noOfDay, protein, sugar;
    String mealID, mealName;

    public String getMealID(){
        return mealID;
    }

    public String getMealName(){
        return mealName;
    }

    public int getCarb(){
        return carb;
    }

    public int getFat(){
        return fat;
    }

    public int getKcal(){
        return kcal;
    }

    public int getNoOfDay(){
        return noOfDay;
    }

    public int getProtein(){
        return protein;
    }

    public int getSugar() {
        return sugar;
    }
}
