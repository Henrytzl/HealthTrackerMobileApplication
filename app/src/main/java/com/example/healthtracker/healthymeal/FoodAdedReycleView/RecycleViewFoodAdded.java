package com.example.healthtracker.healthymeal.FoodAdedReycleView;

public class RecycleViewFoodAdded {
    int carb, fat, kcal, noOfUnit, protein, sugar, qty;
    String foodID, foodName, userID;

    public String getFoodID(){
        return foodID;
    }

    public String getFoodName(){
        return foodName;
    }

    public String getUserID(){
        return userID;
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

    public int getNoOfUnit(){
        return noOfUnit;
    }

    public int getProtein(){
        return protein;
    }

    public int getSugar() {
        return sugar;
    }

    public int getQty(){
        return qty;
    }

    public void setCarb(int carb){
        this.carb = carb;
    }
    public void setFat(int fat){
        this.fat = fat;
    }
    public void setKcal(int kcal){
        this.kcal = kcal;
    }
    public void setNoOfUnit(int noOfUnit){
        this.noOfUnit = noOfUnit;
    }
    public void setProtein(int protein){
        this.protein = protein;
    }
    public void setSugar(int sugar){
        this.sugar = sugar;
    }
    public void setQty(int qty){
        this.qty = qty;
    }
    public void setFoodID(String foodID){
        this.foodID = foodID;
    }
    public void setFoodName(String foodName){
        this.foodName = foodName;
    }
    public void setUserID(String userID){
        this.userID = userID;
    }
}
