package com.hoangduy.sewingapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.pavlospt.roundedletterview.RoundedLetterView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hoangduy.sewingapp.adapters.HistoryAdapter;
import com.hoangduy.sewingapp.dto.Customer;
import com.hoangduy.sewingapp.dto.History;
import com.hoangduy.sewingapp.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CustomerDetailScreen extends AppCompatActivity {

    private Customer currentCustomer;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_detail_screen);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.title_activity_customer_detail_screen);

        //get metadata
        this.currentCustomer = (Customer) getIntent().getSerializableExtra("CUSTOMER");

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), currentCustomer);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_customer_detail_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static Customer customer;

        /**
         * The component for detail screen
         */
        private ImageView avaBackGround;
        private RoundedLetterView avaForeGround;
        private TextView customerName, customerDetail;
        private Button btn_call, btn_textMessage;
        private FloatingActionButton btn_edit;

        /**
         * The component for history screen
         */
        private FloatingActionButton btn_newSchedule;
        private RecyclerView list_history_view;
        private List<History> historyList;
        private HistoryAdapter historyAdapter;
        private DatabaseReference db;

        public PlaceholderFragment() {

        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber, Customer customer1) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            customer = customer1;
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = null;
            switch (getArguments().getInt(ARG_SECTION_NUMBER)) {
                case 1:
                    //bind view
                    rootView = inflater.inflate(R.layout.fragment_customer_detail_screen, container, false);
                    avaBackGround = rootView.findViewById(R.id.customerAvaBack);
                    avaForeGround = rootView.findViewById(R.id.customerAvaFore);
                    customerName = rootView.findViewById(R.id.customerNameTitle);
                    customerDetail = rootView.findViewById(R.id.tv_details);
                    btn_call = rootView.findViewById(R.id.btn_call);
                    btn_edit = rootView.findViewById(R.id.btn_edit);
                    btn_textMessage = rootView.findViewById(R.id.btn_textMessage);

                    //bind data
                    String Name = customer.getName();
                    customerName.setText(Name);
                    avaForeGround.setTitleText(String.valueOf(Name.charAt(0)).toUpperCase());
                    avaForeGround.setBackgroundColor(Constants.randomColor());
                    avaBackGround.setBackgroundColor(Constants.randomColor());

                    customerDetail.setText(customer.getDescription());

                    btn_edit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            navigateToEditScreen();
                        }
                    });

                    btn_call.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            callCustomer();
                        }
                    });

                    btn_textMessage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            textCustomer();
                        }
                    });

                    break;
                case 2:
                    rootView = inflater.inflate(R.layout.fragment_customer_history_screen, container, false);
                    btn_newSchedule = rootView.findViewById(R.id.btn_add_new_schedule);
                    list_history_view = rootView.findViewById(R.id.list_history);

                    btn_newSchedule.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent i = new Intent(getActivity(), ScheduleAddScreen.class);
                            i.putExtra("MODE", Constants.MODE.CREATE);
                            i.putExtra("CUSTOMER", customer);
                            startActivity(i);
                        }
                    });


                    //load data
                    loadListHistory();

                    historyAdapter = new HistoryAdapter(historyList, new HistoryAdapter.OnItemClickListener() {
                        @Override
                        public void OnItemClick(History history) {
                            Intent i = new Intent(getActivity(), ScheduleAddScreen.class);
                            i.putExtra("MODE", Constants.MODE.EDIT);
                            i.putExtra("CUSTOMER", customer);
                            i.putExtra("HISTORY", history);
                            startActivity(i);
                        }

                        @Override
                        public void OnItemLongClick() {

                        }
                    });
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    list_history_view.setLayoutManager(layoutManager);
                    list_history_view.setAdapter(historyAdapter);

                    break;
            }
            return rootView;
        }

        private void textCustomer() {
            if (!customer.getPhone().isEmpty()
                    && customer.getPhone().length() >= 10) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + customer.getPhone().toString().trim()));
                intent.putExtra("sms_body", "");
                startActivity(intent);
            }
        }

        private void callCustomer() {
            if (!customer.getPhone().isEmpty()
                    && customer.getPhone().length() >= 10) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + customer.getPhone().toString().trim()));
                startActivity(intent);
            }
        }

        private void loadListHistory() {
            historyList = new ArrayList<>();
            db = FirebaseDatabase.getInstance().getReference("history");
            db.child(customer.getName()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    historyList.clear();

                    for (final DataSnapshot data : dataSnapshot.getChildren()) {
                        HashMap<String, String> map = (HashMap<String, String>) data.getValue();

                        History history = new History();
                        history.setName(map.get("name"));
                        history.setDate(map.get("date"));
                        history.setDescription(map.get("description"));

                        historyList.add(history);
                    }

                    historyAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        private void navigateToEditScreen() {
            Intent intent = new Intent(getActivity(), CustomerAddScreen.class);
            intent.putExtra("MODE", Constants.MODE.EDIT);
            intent.putExtra("CUSTOMER", customer);
            startActivityForResult(intent, 1);
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode == 1) {
                if (resultCode == Activity.RESULT_OK) {
                    if (data.getIntExtra("flag", 0) == 1) {
                        customer = (Customer) data.getSerializableExtra("CUSTOMER");
                        customerName.setText(customer.getName());
                        customerDetail.setText(customer.getDescription());
                    }
                }
                if (resultCode == Activity.RESULT_CANCELED) {
                    //Write your code if there's no result
                }
            }
        }
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private Customer data;

        public SectionsPagerAdapter(FragmentManager fm, Customer currentCustomer) {

            super(fm);
            data = currentCustomer;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1, data);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }
    }
}
