package com.example.app.Main;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.app.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager_2;
    private BottomNavigationView bottomNav_2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        anhXa();

        bottomNavi();
    }

    private void anhXa() {
        viewPager_2 = findViewById(R.id.viewPager_2);
        bottomNav_2 = findViewById(R.id.bottomNav_2);
    }

    private void bottomNavi() {
        Adapter_Menu adapter_menu = new Adapter_Menu(this);
        viewPager_2.setAdapter(adapter_menu);

        selected_BottomNav();

        swipe_BottomNav();

        viewPager_2.setUserInputEnabled(false);     // Vo hieu hoa vuot
    }
    // Chon icon
    private void selected_BottomNav() {
        bottomNav_2.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_home:
                    viewPager_2.setCurrentItem(0);
                    break;
                case R.id.action_history:
                    viewPager_2.setCurrentItem(1);
                    break;
                case R.id.action_account:
                    viewPager_2.setCurrentItem(2);
                    break;
            }
            return true;
        });
    }

    // Xu ly vuot
    private void swipe_BottomNav() {
        viewPager_2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        bottomNav_2.getMenu().findItem(R.id.action_home).setChecked(true);
                        break;
                    case 1:
                        bottomNav_2.getMenu().findItem(R.id.action_history).setChecked(true);
                        break;
                    case 2:
                        bottomNav_2.getMenu().findItem(R.id.action_account).setChecked(true);
                        break;
                    default:
                        bottomNav_2.getMenu().findItem(R.id.action_home).setChecked(true);
                        break;
                }
            }
        });
    }
}