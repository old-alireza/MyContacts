package alireza.sn.mycontacts.models;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import alireza.sn.mycontacts.ContactsFragment;
import alireza.sn.mycontacts.R;

public class MyFeatures extends AppCompatActivity {

    private static final int REQUEST_CALL_PHONE = 210;
    private static final int REQUEST_READ_CONTACTS = 313;
    private static FragmentActivity getActivity;

    public static Intent call = new Intent(Intent.ACTION_CALL);

    static List<MyInfo> contactsList;

    public MyFeatures(FragmentActivity getActivity, List<MyInfo> contactsList) {
        MyFeatures.getActivity = getActivity;
        MyFeatures.contactsList = contactsList;
    }

    static List<MyInfo> favorite;
    public MyFeatures (List<MyInfo> favorite){
        MyFeatures.favorite = favorite;
    }

    public void getAndSaveContacts() {
        requestReadContacts();
    }

    private void requestReadContacts() {
        if (ContextCompat.checkSelfPermission(getActivity, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    REQUEST_READ_CONTACTS);
        } else {
            new MyAsyncTask(MyPreferenceManager.getInstance(getActivity)).execute();
        }
    }

    public static void calling(int position) {
        call.setData(Uri.parse("tel:" + contactsList.get(position).getPhone()));
        requestCall();
    }

    public static void itemClick(int position, ImageView pic, Button callBtn, TextView phone, HashSet<Integer> usedPositions) {

        if (usedPositions.contains(position)) {
            pic.setVisibility(View.GONE);
            callBtn.setVisibility(View.GONE);
            phone.setVisibility(View.GONE);
            usedPositions.remove(position);
        } else {
            pic.setVisibility(View.VISIBLE);
            callBtn.setVisibility(View.VISIBLE);
            phone.setVisibility(View.VISIBLE);
            usedPositions.add(position);
        }
    }

    private static void requestCall() {
        if (ContextCompat.checkSelfPermission(getActivity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity,
                    new String[]{Manifest.permission.CALL_PHONE},
                    REQUEST_CALL_PHONE);
        } else {
            getActivity.startActivity(call);
        }
    }

    public static class MyAsyncTask extends AsyncTask<Void, Integer, Void> {
        MyPreferenceManager instance;
        List<MyInfo> listAll = new ArrayList<>();
        List<MyInfo> list = new ArrayList<>();
        boolean cancel = false;

        public MyAsyncTask(MyPreferenceManager instance) {
            this.instance = instance;
        }

        public void getAllContacts() {

            ContentResolver cr = getActivity.getContentResolver();
            Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                    null, null, null, null);

            if ((cur != null ? cur.getCount() : 0) > 0) {
                while (cur != null && cur.moveToNext()) {
                    String id = cur.getString(
                            cur.getColumnIndex(ContactsContract.Contacts._ID));
                    MyInfo contacts = new MyInfo();
                    contacts.setName(cur.getString(cur.getColumnIndex(
                            ContactsContract.Contacts.DISPLAY_NAME)));

                    if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                        Cursor pCur = cr.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                new String[]{id}, null);
                        while (pCur.moveToNext()) {
                            contacts.setPhone(pCur.getString(pCur.getColumnIndex(
                                    ContactsContract.CommonDataKinds.Phone.NUMBER)));
                            list.add(contacts);
                        }
                        pCur.close();
                    }
                }
            }
            if (cur != null) {
                cur.close();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (contactsList.size() == 0)
                ContactsFragment.textView.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            getAllContacts();

            if (list.size() != contactsList.size()) {
                listAll = list;

                Collections.sort(listAll, new Comparator<MyInfo>() {
                    @Override
                    public int compare(MyInfo o1, MyInfo o2) {
                        return o1.getName().compareTo(o2.getName());
                    }
                });
            } else
                cancel = true;

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (!cancel) {
                contactsList.addAll(listAll);
                instance.putContactsList(contactsList);
                EventBus.getDefault().post("received_contacts");
            }
            ContactsFragment.textView.setVisibility(View.INVISIBLE);
        }
    }

}
