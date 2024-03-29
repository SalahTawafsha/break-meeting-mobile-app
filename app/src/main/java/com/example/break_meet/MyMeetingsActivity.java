package com.example.break_meet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;

public class MyMeetingsActivity extends AppCompatActivity {
    private RecyclerView list;
    private final FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
    private List<Meeting> all;
    private Meeting meeting;
    private static Button delete;
    private static int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_meetings);


        list = findViewById(R.id.myMeetingList);

        SharedPreferences sharedPref = getSharedPreferences(
                getString(R.string.login)
                , Context.MODE_PRIVATE);

        list.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        delete = findViewById(R.id.delete);

        fireStore.collection("meetings").whereEqualTo("studentId", sharedPref.getString("logInID", ""))
                .get().addOnCompleteListener(task -> {
                    all = task.getResult().toObjects(Meeting.class);

                    MeetingAdapter adapter = new MeetingAdapter(all, MeetingAdapter.MY_MEETING);

                    list.setAdapter(adapter);

                });

    }

    public void delete(View view) {
        meeting = all.get(index);
        delete.setEnabled(true);
        fireStore.collection("meetings").whereEqualTo("studentId", meeting.getStudentId())
                .whereEqualTo("fromTime", meeting.getFromTime())
                .get()
                .addOnCompleteListener(task -> {

                    for (QueryDocumentSnapshot o : task.getResult()) {
                        fireStore.collection("meetings").document(o.getId()).delete();
                        delete.setEnabled(false);
                        all.remove(meeting);
                        MeetingAdapter adapter = new MeetingAdapter(all, MeetingAdapter.MY_MEETING);

                        list.setAdapter(adapter);
                        return;
                    }
                });

        Toast.makeText(this, "Deleted!", Toast.LENGTH_SHORT).show();
    }

    public static void setIndex(int index) {
        MyMeetingsActivity.index = index;
    }

    public static Button getDelete() {
        return delete;
    }
}