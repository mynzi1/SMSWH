package com.example.babyak;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import net.daum.mf.map.api.MapView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class PostActivity extends AppCompatActivity {

    DatabaseReference reference;

    Button savebtn;
    ListView dBListView;
    TextInputEditText name, contents, place, numberPerson, title;
    String namE, contentS, placE, numberPersoN, titlE;
    ArrayAdapter<String> adapter;
    static ArrayList<String> arrayIndex = new ArrayList<String>();
    static ArrayList<String> arrayData = new ArrayList<String>();

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_post);

        title = findViewById(R.id.Text_title);
        name = findViewById(R.id.Text_uploader);
        contents = findViewById(R.id.Text_post);
        place = findViewById(R.id.Text_spotName);
        numberPerson = findViewById(R.id.Text_headCount);
        savebtn = findViewById(R.id.savePost);
        dBListView = findViewById(R.id.DBListView);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        dBListView.setAdapter(adapter);
        getFirebaseDataBase();

        // map API
//        MapView mapView = new MapView(this);
//
//        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.mapView);
//        mapViewContainer.addView(mapView);


        savebtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                titlE = title.getText().toString();
                namE = name.getText().toString();
                contentS = contents.getText().toString();
                placE = place.getText().toString();
                numberPersoN = numberPerson.getText().toString();

                if(!isExistName()){
                    postFirebaseDataBase(true);
                    getFirebaseDataBase();
                    title.setText("");
                    name.setText("");
                    contents.setText("");
                    place.setText("");
                    numberPerson.setText("");
                    showToast("???????????? ?????????????????????.");
                }else{
                    showToast("?????? ???????????????.");
                    title.requestFocus();
                }

            }
        });

        dBListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String tempData[] = arrayData.get(position).split("\\s+");
                title.setText(tempData[0]);
                name.setText(tempData[1]);
                contents.setText(tempData[2]);
                place.setText(tempData[3]);
                numberPerson.setText(tempData[4]);
            }
        });

//??????..        dBListView.setOnItemLongClickListener();
    }
    public boolean isExistName(){
        boolean isExist = arrayIndex.contains(namE);
        return isExist;
    }
    public void postFirebaseDataBase(boolean add){
        reference = FirebaseDatabase.getInstance().getReference();
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postValues = null;
        if(add){
            PostData post = new PostData(titlE, namE, contentS, placE, numberPersoN);
            postValues = post.toMap();
        }
        childUpdates.put("/id_list/"+namE, postValues);
        reference.updateChildren(childUpdates);
    }
    public void getFirebaseDataBase(){
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayData.clear();
                arrayIndex.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    String key = dataSnapshot.getKey();
                    PostData get = dataSnapshot.getValue(PostData.class);
                    String info[] = {get.title, get.name, get.content, get.place, get.numberperson};
                    String result = info[0]+" "+info[1]+" "+info[2]+" "+info[3]+" "+info[4];
                    arrayData.add(result);
                    arrayIndex.add(key);
                }
                adapter.clear();
                adapter.addAll(arrayData);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showToast("??????????????????????????????");
            }
        };
        Query data = FirebaseDatabase.getInstance().getReference().child("id_list").orderByChild("id");
        data.addListenerForSingleValueEvent(eventListener);
    }
    void showToast(String msg){
        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
    }
}
