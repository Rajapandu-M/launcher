package com.example.rplauncher;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MyLauncher extends AppCompatActivity {
    private PackageManager packagemanager;
    private List<ResolveInfo> apps;
    private AppAdapater adapater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_launcher);

        packagemanager = getPackageManager();

        Intent intent = new Intent(Intent.ACTION_MAIN,null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        apps = new ArrayList<>(packagemanager.queryIntentActivities(intent,0));

        RecyclerView recyclerview = findViewById(R.id.apps_RV_ID);
        recyclerview.setLayoutManager(new GridLayoutManager(this,4));
        adapater = new AppAdapater();
        recyclerview.setAdapter(adapater);
        ItemTouchHelper itemtouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT,0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int fromposition = viewHolder.getAdapterPosition();
                int toposition = target.getAdapterPosition();
                Collections.swap(apps,fromposition,toposition);
                adapater.notifyItemMoved(fromposition,toposition);
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }
        });
        itemtouchHelper.attachToRecyclerView(recyclerview);
    }

    private class AppAdapater extends RecyclerView.Adapter<AppAdapater.AppsViewHolder> {

        @NonNull
        @Override
        public AppAdapater.AppsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_app_view,parent,false);
            return new AppsViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AppAdapater.AppsViewHolder holder, int position) {
        ResolveInfo info = apps.get(position);
            Drawable icon = info.loadIcon(packagemanager);
            String lable = info.loadLabel(packagemanager).toString();

            holder.iconView.setImageDrawable(icon);
            holder.nameView.setText(lable);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent launchapp = new Intent();
                    launchapp.setClassName(info.activityInfo.packageName,info.activityInfo.name);
                    launchapp.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(launchapp);
                }
            });
        }

        @Override
        public int getItemCount() {
            return apps.size();
        }
        public class AppsViewHolder extends RecyclerView.ViewHolder {
            ImageView iconView;
            TextView nameView;
            public AppsViewHolder(@NonNull View itemView) {
                super(itemView);
                iconView=findViewById(R.id.app_icon_id);
                nameView = findViewById(R.id.app_name_id);
            }
        }
    }
}
