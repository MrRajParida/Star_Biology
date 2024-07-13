package com.example.demo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private RecyclerView rcv;
    private List<itemClass> itemClassList;
    private Adapter adapter;
    private static final String PREFS_NAME = "MyPrefs";
    private static final String ITEMS_KEY = "items";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        EditText sr = findViewById(R.id.SearchView);
        rcv = findViewById(R.id.recyclerView);
        rcv.setLayoutManager(new LinearLayoutManager(this));

        itemClassList = loadItems();

        adapter = new Adapter(itemClassList, this, new OnItemClickListener() {
            @Override
            public void onItemClick(itemClass item) {
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                intent.putExtra("name", item.getName());
                intent.putExtra("email", item.getEmail());
                intent.putExtra("img", item.getImg());
                startActivity(intent);
            }
        });
        rcv.setAdapter(adapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();
                adapter.swapItem(fromPosition, toPosition);
                saveItems();
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // Handle item swipe if needed
            }
        });

        itemTouchHelper.attachToRecyclerView(rcv);

        sr.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.filter(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void saveItems() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> itemSet = new HashSet<>();
        for (itemClass item : itemClassList) {
            itemSet.add(item.getName() + "," + item.getEmail() + "," + item.getImg());
        }
        editor.putStringSet(ITEMS_KEY, itemSet);
        editor.apply();
    }

    private List<itemClass> loadItems() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        Set<String> itemSet = sharedPreferences.getStringSet(ITEMS_KEY, new HashSet<>());
        List<itemClass> items = new ArrayList<>();
        for (String itemString : itemSet) {
            String[] parts = itemString.split(",");
            if (parts.length == 3) {
                String name = parts[0];
                String email = parts[1];
                int img = Integer.parseInt(parts[2]);
                items.add(new itemClass(name, email, img));
            }
        }
        return items.isEmpty() ? getDefaultItems() : items;
    }

    private List<itemClass> getDefaultItems() {
        List<itemClass> itemClassList = new ArrayList<>();
        itemClassList.add(new itemClass("Raj", "code.paridaraj0987@gmail.com", R.drawable.i));
        itemClassList.add(new itemClass("Hari", "hari@gmail.com", R.drawable.im));
        itemClassList.add(new itemClass("Gopal", "gopal@gmail.com", R.drawable.immg));
        itemClassList.add(new itemClass("Kanha", "kanha@gmail.com", R.drawable.imga));
        itemClassList.add(new itemClass("Raj", "code.paridaraj0987@gmail.com", R.drawable.image));
        itemClassList.add(new itemClass("Hari", "hari@gmail.com", R.drawable.im));
        itemClassList.add(new itemClass("Gopal", "gopal@gmail.com", R.drawable.immg));
        itemClassList.add(new itemClass("Kanha", "kanha@gmail.com", R.drawable.image));
        itemClassList.add(new itemClass("Raj", "code.paridaraj0987@gmail.com", R.drawable.imga));
        itemClassList.add(new itemClass("Hari", "hari@gmail.com", R.drawable.i));
        itemClassList.add(new itemClass("Gopal", "gopal@gmail.com", R.drawable.im));
        itemClassList.add(new itemClass("Kanha", "kanha@gmail.com", R.drawable.imga));
        itemClassList.add(new itemClass("Raj", "code.paridaraj0987@gmail.com", R.drawable.immg));
        itemClassList.add(new itemClass("Hari", "hari@gmail.com", R.drawable.i));
        itemClassList.add(new itemClass("Gopal", "gopal@gmail.com", R.drawable.im));
        itemClassList.add(new itemClass("Kanha", "kanha@gmail.com", R.drawable.imga));
        itemClassList.add(new itemClass("Raj", "code.paridaraj0987@gmail.com", R.drawable.immg));
        itemClassList.add(new itemClass("Hari", "hari@gmail.com", R.drawable.i));
        itemClassList.add(new itemClass("Gopal", "gopal@gmail.com", R.drawable.im));
        itemClassList.add(new itemClass("Kanha", "kanha@gmail.com", R.drawable.imga));
        itemClassList.add(new itemClass("Raj", "code.paridaraj0987@gmail.com", R.drawable.immg));
        itemClassList.add(new itemClass("Hari", "hari@gmail.com", R.drawable.i));
        itemClassList.add(new itemClass("Gopal", "gopal@gmail.com", R.drawable.im));
        itemClassList.add(new itemClass("Kanha", "kanha@gmail.com", R.drawable.imga));
        itemClassList.add(new itemClass("Raj", "code.paridaraj0987@gmail.com", R.drawable.immg));
        itemClassList.add(new itemClass("Hari", "hari@gmail.com", R.drawable.i));
        itemClassList.add(new itemClass("Gopal", "gopal@gmail.com", R.drawable.im));
        itemClassList.add(new itemClass("Kanha", "kanha@gmail.com", R.drawable.imga));
        itemClassList.add(new itemClass("Raj", "code.paridaraj0987@gmail.com", R.drawable.immg));
        itemClassList.add(new itemClass("Hari", "hari@gmail.com", R.drawable.i));
        itemClassList.add(new itemClass("Gopal", "gopal@gmail.com", R.drawable.im));
        itemClassList.add(new itemClass("Kanha", "kanha@gmail.com", R.drawable.imga));
        return itemClassList;
    }
}