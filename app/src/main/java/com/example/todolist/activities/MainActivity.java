package com.example.todolist.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.todolist.R;
import com.example.todolist.controllers.Controller_CRUD;
import com.example.todolist.models.TaskList;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

import static com.example.todolist.models.SelectionType.get;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;

    private EditText addListEditText;
    private Controller_CRUD crudController;
    Toolbar toolbar;
    SharedPreferences sharedPreferences;
    View view;

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        this.view = parent;
        return super.onCreateView(parent, name, context, attrs);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        crudController = new Controller_CRUD(getApplicationContext());
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        sharedPreferences = this.getSharedPreferences("com.example.todolist", Context.MODE_PRIVATE);

        Menu menu = navigationView.getMenu();
        List<TaskList> taskLists = crudController.getAllTaskLists();

        SubMenu lists = menu.addSubMenu("Lists");
        for (final TaskList taskList : taskLists) {

            lists.add(taskList.getTitle()).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    SelectedFragment myFragment = new SelectedFragment();
                    sharedPreferences.edit().putString("key", taskList.getKey()).apply();
                    sharedPreferences.edit().putString("listname", taskList.getTitle()).apply();
                    sharedPreferences.edit().putString("last", "list").apply();
                    Bundle bundle = new Bundle();
                    bundle.putString("type", "list");
                    bundle.putString("key", taskList.getKey());
                    myFragment.setArguments(bundle);
                    toolbar.setTitle(taskList.getTitle());
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, myFragment).commit();
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                }
            });

        }

        SelectedFragment myDayFragment = new SelectedFragment();
        Bundle bundle = new Bundle();
        String last = sharedPreferences.getString("last", "");
        if (last.equals("")) {
            toolbar.setTitle("My Day");
            sharedPreferences.edit().putString("last", "day").apply();
            bundle.putString("type", "day");
            myDayFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, myDayFragment).commit();
            navigationView.setCheckedItem(R.id.nav_my_day);
        } else {
            switch (get(last)) {
                case DAY:
                    bundle.putString("type", "day");
                    myDayFragment.setArguments(bundle);
                    navigationView.setCheckedItem(R.id.nav_my_day);
                    toolbar.setTitle("My Day");
                    break;
                case IMPORTANT:
                    toolbar.setTitle("Important");
                    bundle.putString("type", "important");
                    navigationView.setCheckedItem(R.id.nav_important);
                    break;
                case PLANNED:
                    toolbar.setTitle("Planned");
                    bundle.putString("type", "planned");
                    navigationView.setCheckedItem(R.id.nav_planned);
                    break;
                case ALL:
                    toolbar.setTitle("All Tasks");
                    bundle.putString("type", "all");
                    navigationView.setCheckedItem(R.id.nav_all);
                    break;
                case LIST:
                    toolbar.setTitle(sharedPreferences
                            .getString("listname", "List"));
                    bundle.putString("type", "list");
                    bundle.putString("key", sharedPreferences
                            .getString("key", ""));
                    break;
            }
            myDayFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, myDayFragment).commit();
        }
        closeKeyboard();
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                closeKeyboard();
                super.onDrawerSlide(drawerView, slideOffset);
            }
        };

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        switch (keyCode) {
            case KeyEvent.KEYCODE_ENTER:
                if (action == KeyEvent.ACTION_DOWN) {
                    addTaskBroadcast();
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_BACK:
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    closeKeyboard();
                    onBackPressed();
                }
                break;

        }
        return false;
    }

    private void closeKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null)
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    private void openKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    private void addTaskBroadcast() {
        Intent intent = new Intent("activity-says-hi");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Bundle bundle = new Bundle();
        SelectedFragment myDayFragment = new SelectedFragment();
        switch (menuItem.getItemId()) {
            case R.id.nav_my_day:
                sharedPreferences.edit().putString("last", "day").apply();
                bundle.putString("type", "day");
                toolbar.setTitle("My Day");
                break;
            case R.id.nav_important:
                sharedPreferences.edit().putString("last", "important").apply();
                bundle.putString("type", "important");
                toolbar.setTitle("Important");
                break;
            case R.id.nav_planned:
                sharedPreferences.edit().putString("last", "planned").apply();
                toolbar.setTitle("Planned");
                bundle.putString("type", "planned");
                break;
            case R.id.nav_add_list:
                popUpCreateListDialog();
                return true;
            case R.id.nav_all:
                sharedPreferences.edit().putString("last", "all").apply();

                toolbar.setTitle("All Tasks");
                bundle.putString("type", "all");
                break;
        }
        myDayFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, myDayFragment).commit();
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void popUpCreateListDialog() {
        AlertDialog alertDialog = createDialogInterface();
        alertDialog.show();
        closeKeyboard();
    }

    private AlertDialog createDialogInterface() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.add_list_dialog_layout, null);
        addListEditText = view.findViewById(R.id.addListNameEditText);
        openKeyboard();
        builder.setView(view)
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }
                })
                .setPositiveButton("Create List", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newListName = addListEditText.getText().toString();

                        if (!newListName.trim().equals("")) {
                            TaskList newList = new TaskList(newListName);
                            crudController.saveList(newList);
                            closeKeyboard();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.putExtra("type", "list");
                            sharedPreferences.edit().putString("key", newList.getKey()).apply();
                            sharedPreferences.edit().putString("listname", newList.getTitle()).apply();
                            sharedPreferences.edit().putString("last", "list").apply();
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(), "Please enter a title.", Toast.LENGTH_SHORT).show();
                            addListEditText.setError("Please enter a title.");
                            popUpCreateListDialog();
                        }
                    }
                });

        return builder.create();
    }

    @Override
    public void onBackPressed() {
        closeKeyboard();
        super.onBackPressed();
    }


}
