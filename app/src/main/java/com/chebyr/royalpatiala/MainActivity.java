package com.chebyr.royalpatiala;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.chebyr.appshell.AppShell;
import com.chebyr.appshell.contact.ContactAccessor;
import com.chebyr.appshell.database.DatabaseManager;
import com.chebyr.appshell.filemamager.FileManager;
import com.chebyr.appshell.PermissionManager;
import com.chebyr.appshell.StrictModeDebugUtils;

import com.chebyr.appshell.ui.scrollgallery.ScrollGalleryFragment;
import com.chebyr.royalpatiala.business.OrderManagement;
import com.chebyr.royalpatiala.ui.buttonbars.CatalogFragment;
import com.chebyr.royalpatiala.ui.buttonbars.DeliveredFragment;
import com.chebyr.royalpatiala.ui.buttonbars.OrdersFragment;
import com.chebyr.royalpatiala.ui.buttonbars.StockFragment;
import com.chebyr.royalpatiala.ui.payments.PaymentsFragment;

import java.io.File;

public class MainActivity extends AppCompatActivity implements
        FileManager.FileManagerCallback,
        OrderManagement,
        CatalogFragment.Callback,
        AppShell.UIEvents
{
    private static String TAG = "MainActivity";
    private static String whatsAppPath = "WhatsApp/Media/WhatsApp Images";

    public final int REQUEST_SELECT_CONTACT = 1;

    private AppShell appShell;
    private FileManager fileManager;
    private DatabaseManager databaseManager;
    private PermissionManager permissionManager;

    private ScrollGalleryFragment scrollGalleryFragment;
    private Fragment catalogFragment;
    private Fragment stockFragment;
    private Fragment ordersFragment;
    private Fragment deliveredFragment;
    private Fragment paymentsFragment;

    private File[] imageList;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG)
            StrictModeDebugUtils.enableStrictMode();

        fileManager = new FileManager(this, this);

        setContentView(R.layout.activity_main);
        appShell = (AppShell) findViewById(R.id.drawer_layout);
        appShell.setMenu(R.menu.activity_main_drawer);

        permissionManager = new PermissionManager();

        //databaseManager = new DatabaseManager(this);
        //databaseManager.write("title", "subtitle");
        //List list = databaseManager.read("title");
        //databaseManager.update("title", "titleNew");
        //databaseManager.delete("titleNew");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        return appShell.onCreateOptionsMenu(R.menu.menu_main, menu);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        if(permissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults))
        {
            readImageList();
        }
    }

    private void readImageList()
    {
        Log.d(TAG, "Read External storage permission granted. Read Image List");
        fileManager.getImageList(whatsAppPath, this);
    }

    @Override
    public void onFileListRead(File[] imageList)
    {
        this.imageList = imageList;
        scrollGalleryFragment.addMedia(imageList);
    }

    @Override
    public void onFileCreated(String path)
    {

    }

    @Override
    public void onFileDeleted(String path)
    {

    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        //databaseManager.close();
    }

    @Override
    public int createOrder(String key, int orderType, int quantity)
    {
        return 0;
    }

    @Override
    public void modifyOrder(int orderID, int orderType, int quantity)
    {

    }

    @Override
    public void cancelOrder(int orderID)
    {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if((requestCode == REQUEST_SELECT_CONTACT) && (resultCode == RESULT_OK))
        {
            Uri contactUri = data.getData();
            ContactAccessor contactAccessor = new ContactAccessor(this);
            String[] contactDetails = contactAccessor.getContactNumberFromUri(contactUri);
        }
    }

    @Override
    public void onCustomerOrder()
    {
        appShell.displaySelectContact();
    }


    @Override
    public void onStockOrder()
    {

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem)
    {
        //Check to see which item was being clicked and perform appropriate action
        switch (menuItem.getItemId())
        {
            case R.id.nav_catalog:
            {
                //mFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                if (scrollGalleryFragment == null)
                    scrollGalleryFragment = ScrollGalleryFragment.newInstance(imageList, appShell.mFragmentManager);

                if(catalogFragment == null)
                    catalogFragment = CatalogFragment.newInstance(this, null, null);

                scrollGalleryFragment.setButtonBar(catalogFragment);

                appShell.showFragment(R.id.container, scrollGalleryFragment, ScrollGalleryFragment.TAG, false);
                appShell.showFragment(R.id.button_bar, catalogFragment, CatalogFragment.TAG, false);

                if(permissionManager.getPermissions(this))
                {
                    readImageList();
                }

                return true;
            }
            case R.id.nav_stock:
            {
                if(stockFragment == null)
                    stockFragment = StockFragment.newInstance(null, null);

                appShell.showFragment(R.id.container, stockFragment, StockFragment.TAG, true);
                return true;
            }
            case R.id.nav_orders:
            {
                if(ordersFragment == null)
                    ordersFragment = OrdersFragment.newInstance(null, null);

                appShell.showFragment(R.id.container, ordersFragment, OrdersFragment.TAG, true);
                return true;
            }
            case R.id.nav_delivered:
            {
                if(deliveredFragment == null)
                    deliveredFragment = DeliveredFragment.newInstance(null, null);

                appShell.showFragment(R.id.container, deliveredFragment, DeliveredFragment.TAG, true);
                return true;
            }
            case R.id.nav_payments:
            {
                if(paymentsFragment == null)
                    paymentsFragment = PaymentsFragment.newInstance(null, null);

                appShell.showFragment(R.id.container, paymentsFragment, PaymentsFragment.TAG, true);
                return true;
            }
        }
        return false;

    }
}
