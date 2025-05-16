package com.example.caratexpense;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.caratexpense.fragments.GoalsFragment;
import com.example.caratexpense.fragments.HomeFragment;
import com.example.caratexpense.fragments.IncomeExpenseFragment;
import com.example.caratexpense.fragments.OnboardingFragment;
import com.example.caratexpense.fragments.PaymentsFragment;
import com.example.caratexpense.fragments.ReportsFragment;
import com.example.caratexpense.utils.NotificationHelper;
import com.example.caratexpense.utils.PreferenceManager;
import com.example.caratexpense.utils.SampleDataGenerator;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private PreferenceManager preferenceManager;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferenceManager = new PreferenceManager(this);

        // Setup drawer layout
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Update username in navigation header
        View headerView = navigationView.getHeaderView(0);
        TextView tvUsername = headerView.findViewById(R.id.nav_header_username);
        if (tvUsername != null) {
            tvUsername.setText(preferenceManager.getUserName());
        }

        // Create notification channel
        NotificationHelper.createNotificationChannel(this);

        // Generate sample data for reports
        SampleDataGenerator.generateSampleData(this);

        // Check if it's the first time launch
        if (preferenceManager.isFirstTimeLaunch()) {
            // Show onboarding screen to get user name
            loadFragment(new OnboardingFragment());
        } else {
            // User already set up, go to home screen
            loadFragment(new HomeFragment());
        }
    }

    public void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();

        // Close drawer if open
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            loadFragment(new HomeFragment());
        } else if (id == R.id.nav_income_expense) {
            loadFragment(new IncomeExpenseFragment());
        } else if (id == R.id.nav_goals) {
            loadFragment(new GoalsFragment());
        } else if (id == R.id.nav_reports) {
            loadFragment(new ReportsFragment());
        } else if (id == R.id.nav_payments) {
            loadFragment(new PaymentsFragment());
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // Thêm phương thức để mở drawer từ các fragment
    public void openDrawer() {
        if (drawerLayout != null) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }
}
