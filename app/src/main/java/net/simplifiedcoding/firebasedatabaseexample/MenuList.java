package net.simplifiedcoding.firebasedatabaseexample;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by Belal on 2/26/2017.
 */



public class MenuList extends ArrayAdapter<Menu> {
    private Activity context;
    List<Menu> menus;

    public MenuList(Activity context, List<Menu> menus) {
        super(context, R.layout.layout_menu_list, menus);
        this.context = context;
        this.menus = menus;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.layout_menu_list, null, true);

        TextView textViewName = (TextView) listViewItem.findViewById(R.id.textViewName);
        TextView textViewPrice= (TextView) listViewItem.findViewById(R.id.textViewPrice);
        TextView textViewGenre = (TextView) listViewItem.findViewById(R.id.textViewGenre);
        ImageView imageView = (ImageView) listViewItem.findViewById(R.id.imageView);







        Menu menu = menus.get(position);
        textViewName.setText(menu.getMenuName());
        textViewPrice.setText(menu.getMenuPrice());
        textViewGenre.setText(menu.getMenuGenre());
        Glide.with(context).load(menu.getMenuImgUrl()).into(imageView);




        return listViewItem;
    }




}