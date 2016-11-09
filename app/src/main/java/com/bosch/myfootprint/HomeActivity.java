package com.bosch.myfootprint;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.flybits.core.api.Flybits;
import com.flybits.core.api.interfaces.IRequestPaginationCallback;
import com.flybits.core.api.models.Pagination;
import com.flybits.core.api.models.Zone;
import com.flybits.core.api.models.ZoneMoment;
import com.flybits.core.api.utils.filters.ZoneMomentOptions;
import com.flybits.core.api.utils.filters.ZoneOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity {

    public static final String TAG_HOME             = "com.flybits.fragments.HOME";
    private final String PACKAGE_NAME_JSON_BUILDER  = "com.flybits.moments.jsonbuilder";
    private final String PACKAGE_NAME_APP           = "com.flybits.moments.nativeapp";
    private final String PACKAGE_WEB_APP           = "com.flybits.moments.website";
    private BeaconManager beaconManager;

    public CompanionCardAdapter adapter;
 private CompanionCard c;
    private ZoneMoment userZoneMoment = null;
    private String id;
    private RecyclerView.LayoutManager mLayoutManager;
   private RecyclerView recyclerView;

    final List<CompanionCard> cardsList = new ArrayList<>();
    ArrayList<ZoneMoment> displayedMoments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);



        setContentView(R.layout.content_main);
        ButterKnife.bind(this);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        initCollapsingToolbar();
         recyclerView = (RecyclerView)findViewById(R.id.my_recycler_view);


       adapter = new CompanionCardAdapter(this, cardsList);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        beaconManager = new BeaconManager(getApplicationContext());
        beaconManager = new BeaconManager(getApplicationContext());
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startMonitoring(new Region(
                        "monitored region",
                        UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"),
                        null, null));
            }
        });
                beaconManager.setMonitoringListener(new BeaconManager.MonitoringListener() {

                    @Override
                    public void onEnteredRegion(com.estimote.sdk.Region region, List<Beacon> list) {
//                         c = new CompanionCard("Cafeteria Menu", "Get Today's Cafeteria Menu", R.mipmap.cafeteria,"Bosch Cafeteria");
//                        cardsList.add(c);
//                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onExitedRegion(com.estimote.sdk.Region region) {
                        cardsList.remove(c);
                        adapter.notifyDataSetChanged();
                    }
                });
        getZones();


    }

    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(getString(R.string.app_name));
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    private void getZones() {
        ZoneOptions options  = new ZoneOptions.Builder()
        //42.06821919999999,-87.9649973,50
                .addLocation(42.06821919999999,-87.9649973,50)
                .build();

        Flybits.include(HomeActivity.this).getZones(options, new IRequestPaginationCallback<ArrayList<Zone>>() {
            @Override
            public void onSuccess(ArrayList<Zone> data, Pagination pagination) {
                if(data.size()>0) {
                    id = data.get(0).id;

                    if(data.get(0).address.contains("1800"))

                    {
                        try {
                            Glide.with(HomeActivity.this).load(R.drawable.home).into((ImageView) findViewById(R.id.backdrop));

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    getZoneMoments();

                }
            }

            @Override
            public void onFailed(String reason) {
                //Some Logic on UI Thread
            }

            @Override
            public void onException(Exception exception) {
                //Some Logic on UI Thread
            }

            @Override
            public void onCompleted() {
                //Clean up function on UI Thread
            }
        });
    }

    private void getZoneMoments() {

        ZoneMomentOptions options = new ZoneMomentOptions.Builder()
                .addZoneId(id)
                .build();

        Flybits.include(HomeActivity.this).getZoneMoments(options, new IRequestPaginationCallback<ArrayList<ZoneMoment>>() {
            @Override
            public void onSuccess(ArrayList<ZoneMoment> zoneMoments, Pagination pagination) {


                for (ZoneMoment moment : zoneMoments){
                    if (!moment.packageName.equals(PACKAGE_NAME_JSON_BUILDER)){
                        displayedMoments.add(moment);
                    }
                }


            }

            @Override
            public void onException(Exception e) {}

            @Override
            public void onFailed(String s) {}

            @Override
            public void onCompleted() {
                for(ZoneMoment moment:displayedMoments)
                {
                    buildCard(moment.packageName,moment.getName());
                }
            }
        });
    }

    private void buildCard(String packageName , String name)
    {
        switch(packageName) {


            case "com.flybits.moments.push":
                if(name.equals("Trash"))
                {
                    CompanionCard c = new CompanionCard("Trash", "Measure", R.drawable.trash,"Trash");
                    cardsList.add(c);
                }
//               else if(name.equals("Bosch Buddy"))
//                {
//                    CompanionCard c = new CompanionCard("Bosch Buddy", "Chat with Bosch Buddy for quick tips", R.mipmap.buddy,"Bosch Buddy");
//                    cardsList.add(c);
//                }
//                else {
//                    CompanionCard d = new CompanionCard("Guest Wifi", "Get the Guest Wifi Info", R.mipmap.wifi,"Bosch Wifi");
//                    cardsList.add(d);
//                }


                break;

        }
        adapter.notifyDataSetChanged();
    }



//    @Override
//    public void onMomentSelected(ZoneMoment moment) {
//        Intent intent = null;
//        switch (moment.packageName){
//            case PACKAGE_NAME_APP:
//                intent = new Intent(getActivity(), NativeAppBit.class);
//                break;
//            case PACKAGE_WEB_APP:
//                intent = new Intent(getActivity(), WebMoment.class);
//                break;
//        }
//        if (intent != null) {
//            intent.putExtra("bit", moment);
//            startActivity(intent);
//        }
//    }



//    public void publishMoment(ZoneMoment zoneMomentModified) {
//
//        if (zoneMomentModified != null && masterObject != null){
//
//            if (masterObject.listOfServices == null){
//                masterObject.listOfServices = new ArrayList<>();
//            }
//
//            if (!zoneMomentModified.packageName.equals(PACKAGE_NAME_JSON_BUILDER) && !masterObject.listOfServices.contains(zoneMomentModified)){
//                masterObject.listOfServices.add(zoneMomentModified);
//            }
//
//            adapter.notifyDataSetChanged();
//
//            if (zoneMomentModified.packageName.equals(PACKAGE_NAME_JSON_BUILDER)){
//                publishUserMoment(zoneMomentModified);
//            }
//        }
//    }

//    public void unpublishMoment(ZoneMoment zoneMomentModified) {
//
//        if (zoneMomentModified != null && masterObject != null && masterObject.listOfServices != null){
//
//            if (masterObject.listOfServices.contains(zoneMomentModified)){
//                masterObject.listOfServices.remove(zoneMomentModified);
//            }
//
//            if (zoneMomentModified.packageName.equals(PACKAGE_NAME_JSON_BUILDER)){
//                masterObject.mortgageRenew  = null;
//            }
//
//            if (zoneMomentModified.packageName.equals(PACKAGE_NAME_JSON_BUILDER)){
//                masterObject.mortgageRenew  = null;
//            }
//
//            adapter.notifyDataSetChanged();
//        }
//    }

//    public void unpublishMoment(String id) {
//
//        if (id != null && masterObject != null && masterObject.listOfServices != null){
//
//            ZoneMoment moment   = new ZoneMoment();
//            moment.id           = id;
//
//            if (masterObject.listOfServices.contains(moment)){
//                masterObject.listOfServices.remove(moment);
//            }
//
//            if (userZoneMoment != null && id.equals(userZoneMoment.id)){
//                masterObject.mortgageRenew  = null;
//            }
//
//            adapter.notifyDataSetChanged();
//        }
//    }
//
//    @Override
//    public void onRefresh() {
//        getZoneMoments();
//    }
//
//    public void publishUserMoment(final ZoneMoment moment) {
//
//        Flybits.include(getActivity()).authenticateZoneMomentUsingJWT(moment, new IRequestGeneralCallback() {
//            @Override
//            public void onSuccess() {
//                userZoneMoment  = moment;
//                getJSONBuilder  = (GetJSONBuilder) new GetJSONBuilder(moment.id).execute(moment.launchURL);
//
//            }
//
//            @Override
//            public void onException(Exception e) {
//                userZoneMoment  = null;
//            }
//
//            @Override
//            public void onFailed(String s) {
//
//            }
//
//            @Override
//            public void onCompleted() {
//
//            }
//        });
//    }
//
//    private class GetJSONBuilder extends AsyncTask<String, Integer, MortgageRenew> {
//
//        private String id;
//
//        public GetJSONBuilder(String id){
//            this.id     = id;
//        }
//
//        protected MortgageRenew doInBackground(String... urls) {
//
//            String url      = urls[0]+"KeyValuePairs/AsMetadata";
//
//            try{
//
//                Result result = new GetRequest(getActivity(), url, null).getResponse();
//                if (result.status >= 200 && result.status < 300) {
//
//                    String resultAsString = result.response;
//                    Root app = new Gson().fromJson(resultAsString, Root.class);
//                    if (app != null && app.localizedKeyValuePairs != null && app.localizedKeyValuePairs.en != null && app.localizedKeyValuePairs.en.root != null) {
//                        return app.localizedKeyValuePairs.en.root;
//                    }
//                }
//
//            }catch(IOException | FlybitsDisabledException | JsonSyntaxException e){}
//
//            return null;
//        }
//
//        protected void onPostExecute(MortgageRenew result) {
//            super.onPostExecute(result);
//
//            if (result != null && !isCancelled()){
//                MainActivity.sendNotification(getActivity(), "Flybits Bank Notification", result.message);
//                masterObject.mortgageRenew  = result;
//                adapter.notifyDataSetChanged();
//            }
//        }
//    }
}
