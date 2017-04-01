package com.chebyr.appshell;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.provider.ContactsContract;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.NavigationView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.chebyr.appshell.ui.scrollgallery.ScrollGalleryFragment;

import java.io.File;

public class AppShell extends DrawerLayout implements
        NavigationView.OnNavigationItemSelectedListener
{
    private static final String TAG = "AppUIManager";
    public final int REQUEST_SELECT_CONTACT = 1;
    private int mCurrentSelectedPosition = 0;

    private AppCompatActivity mActivity;
    private UIEvents callback;

    private Toolbar mToolbar;
    private NavigationView mNavigationView;
    private NavigationDrawerToggle mDrawerToggle;
    public FragmentManager mFragmentManager;

    public AppShell(Context context)
    {
        super(context);

        if(isInEditMode())
            return;

        mActivity = (AppCompatActivity)context;
        mFragmentManager = mActivity.getSupportFragmentManager();
        callback = (UIEvents)context;
    }

    public AppShell(Context context, AttributeSet attr)
    {
        super(context, attr);
        Log.d(TAG, "Constructor");

        if(isInEditMode())
            return;

        mActivity = (AppCompatActivity)context;
        mFragmentManager = mActivity.getSupportFragmentManager();
        callback = (UIEvents)context;
    }

    public void setMenu(int menuResource)
    {
        mNavigationView.inflateMenu(menuResource);    }

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();

        if(isInEditMode())
            return;

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setItemIconTintList(null);
        mNavigationView.setNavigationItemSelectedListener(this);
        //mNavigationView.getMenu().getItem(0).setChecked(true);
        //mNavigationView.setCheckedItem(0);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        //mToolbar.setTitle("Navigation Drawer");
        mActivity.setSupportActionBar(mToolbar);

        ActionBar actionBar = mActivity.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions between the navigation drawer and the action bar app icon.
        mDrawerToggle = new NavigationDrawerToggle(mActivity, this, mToolbar, R.string.navigation_drawer_open,  R.string.navigation_drawer_close);

        // Defer code dependent on restoration of previous instance state.
        post(new Runnable()
        {
            @Override
            public void run()
            {
                mDrawerToggle.syncState();
            }
        });

        addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.

        // set a custom shadow that overlays the main content when the drawer opens
        setDrawerShadow(R.mipmap.drawer_shadow, GravityCompat.START);
    }

    public void showFragment(@IdRes int containerViewId, Fragment fragment, String fragmentTAG, boolean addToBackStack)
    {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.replace(containerViewId, fragment, fragmentTAG);

        if(addToBackStack)
            fragmentTransaction.addToBackStack(null);

        fragmentTransaction.commit();
    }

    public boolean restoreLastFragment()
    {
        int backStackEntryCount = mFragmentManager.getBackStackEntryCount();
        Log.d(TAG, "backStackEntryCount:" + backStackEntryCount);

        if(backStackEntryCount > 0)
        {
            mFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            //displayCatalog();
            return true;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem)
    {
        //Checking if the item is in checked state or not, if not make it in checked state
        if(menuItem.isChecked())
            menuItem.setChecked(false);
        else
            menuItem.setChecked(true);

        //Closing drawer on item click
        closeDrawers();

        return callback.onNavigationItemSelected(menuItem);
    }

    public void selectItem(int position)
    {
        mCurrentSelectedPosition = position;
        if(mNavigationView != null)
        {

            //mDrawerListView.setItemChecked(position, true);
        }
        closeDrawer(mNavigationView);
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        return mDrawerToggle.onOptionsItemSelected(item);
    }

    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public boolean onCreateOptionsMenu(int menuResourceId, Menu menu)
    {
        if(isDrawerOpen(mNavigationView))
            return false;

        // Only show items in the action bar relevant to this screen if the drawer is not showing.
        // Otherwise, let the drawer decide what to show in the action bar.
        mActivity.getMenuInflater().inflate(menuResourceId, menu);
        //ActionBar actionBar = mActivity.getSupportActionBar();
        //actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        //actionBar.setDisplayShowTitleEnabled(true);
        //actionBar.setTitle("OptionsTitle");

        return true;
    }

    public void displayToast(String toastStr)
    {
        LayoutInflater inflater = mActivity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast, (ViewGroup) findViewById(R.id.toast_layout_root));
        TextView textView = (TextView) layout.findViewById(R.id.text);
        textView.setText(toastStr);

        Toast toast = new Toast(mActivity);
        //toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    public void displaySelectContact()
    {
        Log.d(TAG, "Select Contact");
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        mActivity.startActivityForResult(intent, REQUEST_SELECT_CONTACT);
    }

    private class NavigationDrawerToggle extends ActionBarDrawerToggle
    {
        public NavigationDrawerToggle(Activity activity, DrawerLayout drawerLayout, Toolbar toolbar, int openDrawerContentDescRes, int closeDrawerContentDescRes)
        {
            super(activity, drawerLayout, toolbar, openDrawerContentDescRes, closeDrawerContentDescRes);
        }
    }

    public interface UIEvents
    {
        boolean onNavigationItemSelected(MenuItem menuItem);
    }
}
