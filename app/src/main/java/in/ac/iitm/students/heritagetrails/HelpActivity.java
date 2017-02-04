package in.ac.iitm.students.heritagetrails;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * Created by Nitin on 04-02-2017.
 */

public class HelpActivity extends AppCompatActivity {
    static final int NUM_ITEMS = 3;
    private Button btnSkip;
    private Button btnNext;
    private LinearLayout ll;
    private String who_called_me="blah";

    MyAdapter mAdapter;
    private LinearLayout dotsLayout;
    ViewPager mPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_pager);

        if(getIntent().hasExtra("who_called_me")){
            who_called_me = getIntent().getStringExtra("who_called_me");
        }
        //Toast.makeText(this, who_called_me, Toast.LENGTH_SHORT).show();
        ll = (LinearLayout) findViewById(R.id.linear_layout);
        mAdapter = new MyAdapter(getSupportFragmentManager(),this);

        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
        mPager.addOnPageChangeListener(viewPagerPageChangeListener);

        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        btnSkip = (Button) findViewById(R.id.btn_skip);
        btnNext = (Button) findViewById(R.id.btn_next);

        // Watch for button clicks.
        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(who_called_me.equals("welcome_activity")){
                    Intent intent= new Intent(HelpActivity.this,MapsActivity.class);
                    startActivity(intent);
                    finish();
                }else ReturnToMapsActivity();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // checking for last page
                // if last page home screen will be launched
                int current = getItem(+1);
                if (current < 3) {
                    // move to next screen
                    mPager.setCurrentItem(current);
                } else {
                    if(who_called_me.equals("welcome_activity")){
                        Intent intent= new Intent(HelpActivity.this,MapsActivity.class);
                        startActivity(intent);
                        finish();
                    }else ReturnToMapsActivity();
                }
            }
        });

    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {

            if (position ==0) {
                btnNext.setText(getString(R.string.next));
                btnSkip.setVisibility(View.VISIBLE);
                ll.setBackgroundColor(ContextCompat.getColor(HelpActivity.this,R.color.help_screen_1));
            }
            else if (position ==1) {
                btnNext.setText(getString(R.string.next));
                btnSkip.setVisibility(View.VISIBLE);
                ll.setBackgroundColor(ContextCompat.getColor(HelpActivity.this,R.color.help_screen_2));
            }
            // changing the next button text 'NEXT' / 'GOT IT'
            else if (position ==2) {
                // last page. make button text to GOT IT
                btnNext.setText(getString(R.string.start));
                btnSkip.setVisibility(View.GONE);
                ll.setBackgroundColor(ContextCompat.getColor(HelpActivity.this,R.color.help_screen_3));
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    private int getItem(int i) {
        return mPager.getCurrentItem() + i;
    }

    private void ReturnToMapsActivity() {
        finish();
    }

    public static class MyAdapter extends FragmentPagerAdapter {
        private Context context;
        public MyAdapter(FragmentManager fm, Context _context) {
            super(fm);
            context = _context;
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
            //Toast.makeText(context, position+"", Toast.LENGTH_SHORT).show();
            return HelpFragmentManager.newInstance().getFragment(position);
        }
    }

    public static class HelpFragmentManager {
        private static HelpFragmentManager sHelpFragmentManager;

        static HelpFragmentManager newInstance() {
            if (sHelpFragmentManager == null) {
                sHelpFragmentManager = new HelpFragmentManager();
            }

            return sHelpFragmentManager;
        }

        public HelpFragment getFragment(int num) {
            return new HelpFragment().getFragment(num);
        }

    }
}