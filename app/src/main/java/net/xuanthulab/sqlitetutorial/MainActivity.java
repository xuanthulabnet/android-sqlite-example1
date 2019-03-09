package net.xuanthulab.sqlitetutorial;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    static String DB_NAME = "myproduct.db";
    final int RESULT_PRODUCT_ACTIVITY = 1;
    ArrayList<Product> listProduct;
    ProductListViewAdapter productListViewAdapter;
    ListView listViewProduct;
    String DB_TAG = "SQLITE_TUTORIAL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        listProduct = new ArrayList<>();
        loadDbProduct();


        productListViewAdapter  = new ProductListViewAdapter(listProduct);
        listViewProduct         = findViewById(R.id.listproduct);
        listViewProduct.setAdapter(productListViewAdapter);


        //Bấm để tạo bảng
        findViewById(R.id.createtable).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTableProduct();
                loadDbProduct();
                productListViewAdapter.notifyDataSetChanged();
            }
        });
        //Bấm để xoá bảng
        findViewById(R.id.deletable).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteTableProduct();
                loadDbProduct();
                productListViewAdapter.notifyDataSetChanged();
            }
        });

        //Thêm dữ liệu
        findViewById(R.id.addbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.putExtra("isupdate", false);
                intent.setClass(MainActivity.this, EditProduct.class);
                startActivityForResult(intent, RESULT_PRODUCT_ACTIVITY);


            }
        });

        //Lắng nghe bắt sự kiện một phần tử danh sách được chọn, mở Activity để soạn thảo phần tử
        listViewProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Product product = (Product) productListViewAdapter.getItem(position);
                Intent intent = new Intent();
                intent.putExtra("isupdate", true);
                intent.putExtra("idproduct", product.productID);
                intent.setClass(MainActivity.this, EditProduct.class);
                startActivityForResult(intent, RESULT_PRODUCT_ACTIVITY);
            }
        });


    }

    private void deleteTableProduct() {
        SQLiteDatabase db = openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
        if (!(isTableExist(db, "product"))) {
            Toast.makeText(this, "Đã không có, không DROP được", Toast.LENGTH_LONG).show();
        } else {
            db.execSQL("DROP TABLE product");
            Toast.makeText(this, "Vùa DROP bảng", Toast.LENGTH_LONG).show();
        }

        db.close();
    }

    void createTableProduct() {
        SQLiteDatabase db = openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
        if (!(isTableExist(db, "product"))) {
            String queryCreateTable = "CREATE TABLE product ( " +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name VARCHAR (255) NOT NULL, " +
                    "price DECIMAL DEFAULT (0)" +
                    ")";

            db.execSQL(queryCreateTable);

            Toast.makeText(this, "Đã tạo bảng thành công", Toast.LENGTH_LONG).show();

        } else
            Toast.makeText(this, "Bảng đang tồn tại, không cần tạo mới", Toast.LENGTH_LONG).show();

        db.close();
    }

    private void loadDbProduct() {

        listProduct.clear();
        SQLiteDatabase db = openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);

        if (!isTableExist(db, "product")) {
            Toast.makeText(this, "Bảng product không tồn tại, cần tạo bảng trước", Toast.LENGTH_LONG).show();
            ((TextView) findViewById(R.id.infomation)).setText("Bảng dữ liệu không có, phải tạo bảng");
            findViewById(R.id.addbutton).setVisibility(View.GONE);
            return;
        }

        ((TextView) findViewById(R.id.infomation)).setText("PRODUCT");
        findViewById(R.id.addbutton).setVisibility(View.VISIBLE);


        Cursor cursor = db.rawQuery("SELECT id, name, price from product", null);

        //Đến dòng đầu của tập dữ liệu
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int productID = cursor.getInt(0);
            String productName = cursor.getString(1);
            int productPrice = cursor.getInt(2);

            listProduct.add(new Product(productID, productName, productPrice));

            // Đến dòng tiếp theo
            cursor.moveToNext();
        }

        cursor.close();

    }

    boolean isTableExist(SQLiteDatabase db, String table) {
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name=?", new String[]{table});
        boolean tableExist = (cursor.getCount() != 0);
        cursor.close();
        return tableExist;
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_PRODUCT_ACTIVITY:
                //Khi đóng Activity EditProduct thì nạp lại dữ liệu
                loadDbProduct();
                productListViewAdapter.notifyDataSetChanged();
                break;
            default:
                break;
        }

    }


    //Model phần tử dữ liệu hiện
    public static class Product {
        String name;
        int price;
        int productID;

        public Product(int productID, String name, int price) {
            this.name = name;
            this.price = price;
            this.productID = productID;
        }

    }

    class ProductListViewAdapter extends BaseAdapter {

        //Dữ liệu liên kết bởi Adapter là một mảng các sản phẩm
        final ArrayList<Product> listProduct;

        ProductListViewAdapter(ArrayList<Product> listProduct) {
            this.listProduct = listProduct;
        }

        @Override
        public int getCount() {
            //Trả về tổng số phần tử, nó được gọi bởi ListView
            return listProduct.size();
        }

        @Override
        public Object getItem(int position) {
            //Trả về dữ liệu ở vị trí position của Adapter, tương ứng là phần tử
            //có chỉ số position trong listProduct
            return listProduct.get(position);
        }

        @Override
        public long getItemId(int position) {
            //Trả về một ID của phần
            return listProduct.get(position).productID;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //convertView là View của phần tử ListView, nếu convertView != null nghĩa là
            //View này được sử dụng lại, chỉ việc cập nhật nội dung mới
            //Nếu null cần tạo mới

            View viewProduct;
            if (convertView == null) {
                viewProduct = View.inflate(parent.getContext(), R.layout.product_view, null);
            } else viewProduct = convertView;

            //Bind sữ liệu phần tử vào View
            Product product = (Product) getItem(position);
            ((TextView) viewProduct.findViewById(R.id.idproduct)).setText(String.format("ID = %d", product.productID));
            ((TextView) viewProduct.findViewById(R.id.nameproduct)).setText(String.format("%s", product.name));
            ((TextView) viewProduct.findViewById(R.id.priceproduct)).setText(String.format("Giá %d", product.price));


            return viewProduct;
        }
    }


}
