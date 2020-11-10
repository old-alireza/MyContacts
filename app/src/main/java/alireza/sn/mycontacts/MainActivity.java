package alireza.sn.mycontacts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.pm.PackageManager;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import alireza.sn.mycontacts.adapters.ViewPagerAdapter;
import alireza.sn.mycontacts.models.MyFeatures;
import alireza.sn.mycontacts.models.MyPreferenceManager;

public class MainActivity extends AppCompatActivity {
    ViewPager viewPager;
    ViewPagerAdapter adapter;
    TabLayout tabLayout;

    private static final int REQUEST_READ_CONTACTS = 313;
    private static final int REQUEST_CALL_PHONE = 210;

    private final static int[] TAB_ICONS = {R.drawable.icon_contacts, R.drawable.icon_favorites};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        setViewpager();
    }

    private void setViewpager() {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        setTabLayout();
    }

    private void setTabLayout() {
        tabLayout.setupWithViewPager(viewPager);
        //                 0 --> contacts page
//         1 --> favorites page
        tabLayout.getTabAt(0).setIcon(TAB_ICONS[0]);
        tabLayout.getTabAt(1).setIcon(TAB_ICONS[1]);

    }

    private void findViews() {
        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tab_layout);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_READ_CONTACTS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    new MyFeatures.MyAsyncTask(MyPreferenceManager.getInstance(this)).execute();
                }
                break;

            case (REQUEST_CALL_PHONE):
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivity(MyFeatures.call);
                }
                break;

            default:
                break;
        }
    }

}