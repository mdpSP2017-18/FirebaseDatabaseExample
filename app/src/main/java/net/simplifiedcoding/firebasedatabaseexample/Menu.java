package net.simplifiedcoding.firebasedatabaseexample;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Belal on 2/26/2017.
 */
@IgnoreExtraProperties
public class Menu {
    private String menuId;
    private String menuName;
    private String menuPrice;
    private String menuGenre;
    private String menuImgUrl;

    public Menu(){
        //this constructor is required
    }

    public Menu(String menuId, String menuName, String menuPrice, String menuGenre, String menuImgUrl) {
        this.menuId = menuId;
        this.menuName = menuName;
        this.menuPrice = menuPrice;
        this.menuGenre = menuGenre;
        this.menuImgUrl = menuImgUrl;
    }

    public String getMenuId() {
        return menuId;
    }

    public String getMenuName() {
        return menuName;
    }

    public String getMenuPrice() { return menuPrice; }

   public String getMenuGenre() { return menuGenre; }

   public String getMenuImgUrl() { return menuImgUrl;  }






}
