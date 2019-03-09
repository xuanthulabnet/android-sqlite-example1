package net.xuanthulab.sqlitetutorial;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EditProduct extends AppCompatActivity {
    boolean isupdate;
    int idproduct;
    EditText editName;
    EditText editPrice;
    MainActivity.Product product;

    //Intent: idproduct, isupdate

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        Intent intent = getIntent();
        isupdate = intent.getBooleanExtra("isupdate", false);
        if (isupdate) {
            //Activity hoạt động biên tập dữ liệu Sản phẩm đã

            //Đọc sản phẩm

            idproduct = intent.getIntExtra("idproduct", 0);

            SQLiteDatabase db = openOrCreateDatabase(MainActivity.DB_NAME, Context.MODE_PRIVATE, null);
            Cursor cursor = db.rawQuery("SELECT id, name, price from product where id = ?",
                    new String[]{idproduct + ""});
            cursor.moveToFirst();
            int productID = cursor.getInt(0);
            String productName = cursor.getString(1);
            int productPrice = cursor.getInt(2);
            product = new MainActivity.Product(productID, productName, productPrice);
            cursor.close();

            findViewById(R.id.deleteBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SQLiteDatabase db = openOrCreateDatabase(MainActivity.DB_NAME, Context.MODE_PRIVATE, null);
                    db.execSQL("DELETE FROM product where id = ?", new String[]{String.valueOf(idproduct)});
                    db.close();
                    finish();
                }
            });


        } else {
            //Activity nhâp dữ liệu thêm Sản phẩm mới

            product = new MainActivity.Product(0, "", 0);
            findViewById(R.id.deleteBtn).setVisibility(View.GONE);
            ((Button) findViewById(R.id.save)).setText("Tạo sản phẩm mới");
        }

        //Update to View
        editName = findViewById(R.id.nameproduct);
        editPrice = findViewById(R.id.priceproduct);


        editName.setText(product.name);
        editPrice.setText(product.price + "");

        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = openOrCreateDatabase(MainActivity.DB_NAME, Context.MODE_PRIVATE, null);
                product.name = editName.getText().toString();
                product.price = Integer.parseInt(editPrice.getText().toString());

                if (isupdate) {
                    //Cập nhật
                    db.execSQL("UPDATE product SET name=?, price = ? where id = ?",
                            new String[]{product.name, product.price + "", product.productID + ""});
                } else {
                    //Tạo
                    //Cập nhật
                    db.execSQL("INSERT INTO product (name, price ) VALUES (?,?)",
                            new String[]{product.name, product.price + ""});
                }
                db.close();
                finish();
            }
        });


    }
}
