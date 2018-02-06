package net.simplifiedcoding.firebasedatabaseexample;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    public static final String FB_Storage_Path = "image/";
    public static final String FB_Database_Path = "Menu";

    public static final int Request_Code = 1234;

    private String name, genre, price, imgurl;



    EditText editTextName;
    EditText editTextName2;
    Spinner spinnerGenre;
    Button buttonAddMenu;
    ListView listViewMenus;
    ImageView imageView;
    ImageView imageView2;
    ImageView imageView3 ;

    private Uri imguri;


    //a list to store all the artist from firebase database
    List<Menu> menus;

    //our database reference object
    private DatabaseReference databaseMenu;
    private StorageReference storageReference;







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        //getting the reference of menu node
        databaseMenu = FirebaseDatabase.getInstance().getReference(FB_Database_Path);
        storageReference = FirebaseStorage.getInstance().getReference();
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextName2 = (EditText) findViewById(R.id.editTextName2);
        spinnerGenre = (Spinner) findViewById(R.id.spinnerGenres);
        listViewMenus = (ListView) findViewById(R.id.listViewMenus);
        imageView2= (ImageView) findViewById(R.id.imageView2);
        buttonAddMenu = (Button) findViewById(R.id.buttonAddMenu);


        //list to store menus
        menus = new ArrayList<>();



        //adding an onclicklistener to button
        buttonAddMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //calling the method addMenu()
                //the method is defined below
                //this method is actually performing the write operation
                addMenu();
            }
        });





        listViewMenus.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Menu menu = menus.get(i);

                System.out.println(menu.getMenuImgUrl());

                showUpdateDeleteDialog(menu.getMenuId(), menu.getMenuName(), menu.getMenuPrice(), menu.getMenuImgUrl());

            }
        });


    }

    public void btnBrowse_Click(View v) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select image"), Request_Code);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Request_Code && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imguri = data.getData();

            try {
                Bitmap bm = MediaStore.Images.Media.getBitmap(getContentResolver(), imguri);
                imageView2.setImageBitmap(bm);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }





    private void showUpdateDeleteDialog(final String menuId, String menuName, String menuPrice, final String menuImgUrl) {

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.update_dialog, null);


        dialogBuilder.setView(dialogView);

        final EditText editTextName = (EditText) dialogView.findViewById(R.id.editTextName);
        final EditText editTextName2 = (EditText) dialogView.findViewById(R.id.editTextName2);
        final Spinner spinnerGenre = (Spinner) dialogView.findViewById(R.id.spinnerGenres);
        final Button buttonUpdate = (Button) dialogView.findViewById(R.id.buttonUpdateMenu);
        final Button buttonDelete = (Button) dialogView.findViewById(R.id.buttonDeleteMenu);
        imageView3= (ImageView) dialogView.findViewById(R.id.imageView3);

        Glide.with(MainActivity.this).load(menuImgUrl).into(imageView3);


        dialogBuilder.setTitle(menuName);



        final AlertDialog b = dialogBuilder.create();
        b.show();


        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                String name = editTextName.getText().toString().trim();
                String price = editTextName2.getText().toString().trim();
                String genre = spinnerGenre.getSelectedItem().toString();






                if (!TextUtils.isEmpty(name)) {
                    updateMenu(menuId, name, price, genre ,menuImgUrl);
                    Log.e(imgurl, "abc: " );
                    b.dismiss();
                }
            }
        });


        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                deleteMenu(menuId);
                b.dismiss();
            }
        });
    }

    private boolean updateMenu(String id, String name, String price, String genre, String imgurl) {
        //getting the specified menu reference
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference(FB_Database_Path).child(id);

        //updating menu
        Menu menu = new Menu(id, name, price, genre, imgurl);
        dR.setValue(menu);
        Toast.makeText(getApplicationContext(), "Item Updated", Toast.LENGTH_LONG).show();
        return true;
    }

    private boolean deleteMenu(String id) {
        //getting the specified menu reference
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference(FB_Database_Path).child(id);
        
        //removing menu
        dR.removeValue();

        Toast.makeText(getApplicationContext(), "Item Deleted", Toast.LENGTH_LONG).show();

        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        //attaching value event listener
        databaseMenu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                menus.clear();

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting menu
                    Menu menu = postSnapshot.getValue(Menu.class);
                    //adding menu to the list
                    menus.add(menu);
                    Log.e("test","hhh:" +menu);




                }

                //creating adapter
                MenuList menuAdapter = new MenuList(MainActivity.this, menus);
                //attaching adapter to the listview
                listViewMenus.setAdapter(menuAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    /*
    * Firebase Realtime Database
    * */
    public void addMenu() {
        //getting the values to save
        name = editTextName.getText().toString().trim();
        price = editTextName2.getText().toString().trim();
        genre = spinnerGenre.getSelectedItem().toString();



        //checking if the value is provided
        if (!TextUtils.isEmpty(name)&& (imguri != null)) {

            //getting a unique id using push().getKey() method
            //it will create a unique id and we will use it as the Primary Key for our Menu


            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setTitle("Uploading image");
            dialog.show();

                //Get the storage reference
            StorageReference ref = storageReference.child(FB_Storage_Path + name);

                //Add file to reference

            ref.putFile(imguri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                        //Dimiss dialog when success
                        dialog.dismiss();
                        //Display success toast msg
                        Toast.makeText(getApplicationContext(), "Image uploaded", Toast.LENGTH_SHORT).show();

                        imgurl = taskSnapshot.getDownloadUrl().toString();

                        //Save image iznfo in to firebase database
                        //uploadId = databaseReference.push().getKey();
                        //databaseReference.child(uploadId).setValue(imgurl);

                        final String id = databaseMenu.push().getKey();


                        //creating an Menu Object
                        Menu menu = new Menu(id, name, price, genre, imgurl);




                        //Saving the Menu
                        databaseMenu.child(id).setValue(menu);

                        imguri = null;
                        imageView2.setImageDrawable(null);
                        editTextName.setText("");
                        editTextName2.setText("");




                    }


                }

                )

                        .addOnFailureListener(new OnFailureListener() {

                            public void onFailure(@NonNull Exception e) {

                                //Dimiss dialog when error
                                dialog.dismiss();
                                //Display err toast msg
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {


                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                                //Show upload progress

                                double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                dialog.setMessage("Uploaded " + (int) progress + "%");
                            }
                        });
            }
         else {
            //if the value is not given displaying a toast
            Toast.makeText(getApplicationContext(), "Please select the price and image", Toast.LENGTH_SHORT).show();


        }

    }

}
