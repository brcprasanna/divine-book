package ram.king.com.divinebook.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.ocpsoft.prettytime.PrettyTime;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import hotchemi.android.rate.AppRate;
import hotchemi.android.rate.OnClickButtonListener;
import nl.changer.audiowife.AudioWife;
import ram.king.com.divinebook.R;
import ram.king.com.divinebook.models.Post;
import ram.king.com.divinebook.util.AppConstants;
import ram.king.com.divinebook.util.AppUtil;

public class PostDetailActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "PostDetailActivity";
    Post post;
    private DatabaseReference mPostReference;
    private ValueEventListener mPostListener;
    private String mPostKey;
    private String mPlayAudio;
    private CircularImageView mAuthorPhoto;
    private TextView mAuthorView;
    private TextView mTitleView;
    private TextView mBodyView;
    private TextView mDateView;
    private TextView mDedicatedToView;
    private TextView mCourtesyView;

    private SimpleDraweeView mImage;
    private DatabaseReference mDatabase;
    private Menu menu;

    //private AdView mAdView;

    private RelativeLayout mPlayerContainer;
    private ImageButton btnPlay;

    private View mPlayMedia;
    private View mPauseMedia;
    private SeekBar mMediaSeekBar;
    private TextView mRunTime;
    private TextView mTotalTime;
    private TextView mPlaybackTime;

    private PrettyTime prettyTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        getSupportActionBar().setTitle("");

        Intent intent = getIntent();
        String action = intent.getAction();
        Uri data = intent.getData();

        mDatabase = FirebaseDatabase.getInstance().getReference();


        // Get post key from intent
        mPostKey = getIntent().getStringExtra(AppConstants.EXTRA_POST_KEY);
        mPlayAudio = getIntent().getStringExtra(AppConstants.EXTRA_PLAY_AUDIO);

        if (mPostKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_POST_KEY");
        }

        // Initialize Database
        mPostReference = FirebaseDatabase.getInstance().getReference()
                .child("posts").child(mPostKey);
        // Initialize Views
        mAuthorPhoto = (CircularImageView) findViewById(R.id.post_author_photo);
        mAuthorView = (TextView) findViewById(R.id.post_author);
        mTitleView = (TextView) findViewById(R.id.post_title);
        mBodyView = (TextView) findViewById(R.id.post_body);
        mDateView = (TextView) findViewById(R.id.post_date);
        mDedicatedToView = (TextView) findViewById(R.id.post_dedicated_to);
        mCourtesyView = (TextView) findViewById(R.id.post_courtesy);

        // initialize the player controls
        mPlayMedia = findViewById(R.id.play);
        mPauseMedia = findViewById(R.id.pause);
        mMediaSeekBar = (SeekBar) findViewById(R.id.media_seekbar);
        mRunTime = (TextView) findViewById(R.id.run_time);
        mTotalTime = (TextView) findViewById(R.id.total_time);

        mImage = (SimpleDraweeView) findViewById(R.id.post_detail_image);
        /*ImageButton btnNext = (ImageButton) this.findViewById(com.example.jean.jcplayer.R.id.btn_next);
        ImageButton btnPrev = (ImageButton) this.findViewById(com.example.jean.jcplayer.R.id.btn_prev);
        btnPlay = (ImageButton) this.findViewById(com.example.jean.jcplayer.R.id.btn_play);
        */
        /*btnNext.setVisibility(View.GONE);
        btnPrev.setVisibility(View.GONE);*/

        AppRate.with(this)
                .setInstallDays(1) // default 10, 0 means install day.
                .setLaunchTimes(3) // default 10
                .setRemindInterval(2) // default 1
                .setShowLaterButton(true) // default true
                .setDebug(false) // default false
                .setMessage("if you enjoy using this app, please take a moment to rate it. Thanks for your support!")
                .setOnClickButtonListener(new OnClickButtonListener() { // callback listener.
                    @Override
                    public void onClickButton(int which) {
                        Log.d(MainActivity.class.getName(), Integer.toString(which));
                    }
                })
                .monitor();

        // Show a dialog if meets conditions
        AppRate.showRateDialogIfMeetsConditions(this);

        // mAdView = (AdView) findViewById(R.id.adView);
        //AdRequest adRequest = new AdRequest.Builder().build();

        // mAdView.loadAd(adRequest);

        retrievePostInstance();

        mPlayerContainer = (RelativeLayout) findViewById(R.id.playerContainer);

        prettyTime = new PrettyTime();

    }

    private void fetchAudioUrlFromFirebase() {
        final FirebaseStorage storage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReferenceFromUrl(post.audio);
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Download url of file
                final String url = uri.toString();
        /*        ArrayList<JcAudio> jcAudios = new ArrayList<>();
                jcAudios.add(JcAudio.createFromURL("", url));
                jcplayerView.initPlaylist(jcAudios);*/
                AudioWife.getInstance()
                        .init(PostDetailActivity.this, uri)
                        .setPlayView(mPlayMedia)
                        .setPauseView(mPauseMedia)
                        .setSeekBar(mMediaSeekBar)
                        .setRuntimeView(mRunTime)
                        .setTotalTimeView(mTotalTime);
                AudioWife.getInstance().play();
                AudioWife.getInstance().addOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        AudioWife.getInstance().play();
                    }
                });
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("TAG", e.getMessage());
                        Toast.makeText(PostDetailActivity.this, "Server Error",
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    protected void onPause() {
        /*if (mAdView != null) {
            mAdView.pause();
        }*/
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*if (mAdView != null) {
            mAdView.resume();
        }*/
    }


    @Override
    public void onBackPressed() {
        /*if (jcplayerView != null)
            jcplayerView.kill();*/
        AudioWife.getInstance().pause();
        AudioWife.getInstance().release();
        super.onBackPressed();
        finish();
    }

    /**
     * Called before the activity is destroyed
     */
    @Override
    public void onDestroy() {
        /*if (mAdView != null) {
            mAdView.destroy();
        }*/
        super.onDestroy();
    }

    //@VisibleForTesting
    //AdView getAdView() {
    //   return mAdView;
    // }

    @Override
    public void onStart() {
        super.onStart();


    }

    private void retrievePostInstance() {
        // Add value event listener to the post
        // [START post_value_event_listener]
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                post = dataSnapshot.getValue(Post.class);
                if (post != null) {
                    if (!TextUtils.isEmpty(post.audio)) {
                        if (AppUtil.isInternetConnected(PostDetailActivity.this)) {
                            fetchAudioUrlFromFirebase();
                        } else {
                            Toast.makeText(PostDetailActivity.this, getResources().getString(R.string.no_internet_message_audio),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    Glide.with(PostDetailActivity.this).load(post.photoUrl)
                            .into(mAuthorPhoto);

                    mAuthorView.setText(post.author);

                    if (!TextUtils.isEmpty(post.title)) {
                        mTitleView.setVisibility(View.VISIBLE);
                        mTitleView.setText("Title : " + post.title);
                    } else {
                        mTitleView.setVisibility(View.GONE);
                    }

                    if (!TextUtils.isEmpty(post.dedicatedTo)) {
                        mDedicatedToView.setVisibility(View.VISIBLE);
                        mDedicatedToView.setText("Dedicated To : " + post.dedicatedTo);
                    } else {
                        mDedicatedToView.setVisibility(View.GONE);
                    }
                    if (!TextUtils.isEmpty(post.courtesy)) {
                        mCourtesyView.setVisibility(View.VISIBLE);
                        mCourtesyView.setText("Courtesy : " + post.courtesy);
                    } else {
                        mCourtesyView.setVisibility(View.GONE);
                    }

                    if (!TextUtils.isEmpty(post.audio)) {
                        mPlayerContainer.setVisibility(View.VISIBLE);
                    } else {
                        mPlayerContainer.setVisibility(View.GONE);
                    }

                    if (!TextUtils.isEmpty(post.image)) {
                        mImage.setVisibility(View.VISIBLE);
                        mImage.setImageURI(Uri.parse(post.image));
                        /*Glide.with(PostDetailActivity.this)
                                .load(post.image)
                                .into(mImage);
                        mImage.setScaleType(ImageView.ScaleType.FIT_XY);*/
                    } else {
                        mImage.setVisibility(View.GONE);
                    }


                    if (!TextUtils.isEmpty(post.body)) {
                        mBodyView.setVisibility(View.VISIBLE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                            mBodyView.setText(Html.fromHtml(post.body, Html.FROM_HTML_MODE_COMPACT), TextView.BufferType.SPANNABLE);
                        else
                            mBodyView.setText(Html.fromHtml(post.body), TextView.BufferType.SPANNABLE);
                    } else
                        mBodyView.setVisibility(View.GONE);

                    long yourmilliseconds = (long) post.timestamp;
                    if (prettyTime != null)
                        mDateView.setText(prettyTime.format(new Date(yourmilliseconds)));

                    if (menu != null) {
                        MenuItem itemLike = menu.findItem(R.id.menu_like);
                        MenuItem itemDelete = menu.findItem(R.id.menu_delete);
                        if (post != null && post.stars.containsKey(getUid())) {
                            itemLike.setIcon(R.drawable.ic_favorite_white_24dp);
                        } else {
                            itemLike.setIcon(R.drawable.ic_favorite_border_white_24dp);
                        }

                        if (post != null && post.uid.equals(getUid())) {
                            itemDelete.setVisible(true);
                        } else {
                            itemDelete.setVisible(false);
                        }
                    }
                } else {
                    mBodyView.setText("This post is removed by the Author");
                    mDateView.setText("");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                Toast.makeText(PostDetailActivity.this, "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
            }
        };


        /*final Button buttonToggle = (Button) this.findViewById(R.id.button_toggle);
        buttonToggle.setVisibility(View.VISIBLE);
        buttonToggle.setBackgroundResource(R.drawable.ic_expand_more);
// set animation duration via code, but preferable in your layout files by using the animation_duration attribute
        mBodyView.setAnimationDuration(1000L);

        // set interpolators for both expanding and collapsing animations
        mBodyView.setInterpolator(new OvershootInterpolator());

// or set them separately
        mBodyView.setExpandInterpolator(new OvershootInterpolator());
        mBodyView.setCollapseInterpolator(new OvershootInterpolator());

// toggle the mBodyView
        buttonToggle.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                mBodyView.toggle();
                buttonToggle.setText(mBodyView.isExpanded() ? "Collapse" : "Expand");
            }
        });

// but, you can also do the checks yourself
        buttonToggle.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                if (mBodyView.isExpanded())
                {
                    buttonToggle.setBackgroundResource(R.drawable.ic_expand_more);
                    mBodyView.collapse();
                }
                else
                {
                    buttonToggle.setBackgroundResource(R.drawable.ic_expand_less);
                    mBodyView.expand();
                    if (mBodyView.getLineCount() <= 5)
                        buttonToggle.setVisibility(View.GONE);

                }
            }
        });

// listen for expand / collapse events
        mBodyView.setOnExpandListener(new ExpandableTextView.OnExpandListener()
        {
            @Override
            public void onExpand(final ExpandableTextView view)
            {
                Log.d(TAG, "ExpandableTextView expanded");
            }

            @Override
            public void onCollapse(final ExpandableTextView view)
            {
                Log.d(TAG, "ExpandableTextView collapsed");
            }
        });
*/
        mPostReference.addValueEventListener(postListener);
        // Keep copy of post listener so we can remove it when app stops
        mPostListener = postListener;

    }

    @Override
    public void onStop() {
        super.onStop();

        // Remove post value event listener
        if (mPostListener != null) {
            mPostReference.removeEventListener(mPostListener);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        MenuItem itemLike = menu.findItem(R.id.menu_like);
        MenuItem itemDelete = menu.findItem(R.id.menu_delete);
        if (post != null && post.stars.containsKey(getUid())) {
            itemLike.setIcon(R.drawable.ic_favorite_white_24dp);
        } else {
            itemLike.setIcon(R.drawable.ic_favorite_border_white_24dp);
        }

        if (post != null && post.uid.equals(getUid())) {
            itemDelete.setVisible(true);
        } else {
            itemDelete.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.menu_like) {
            onClickStar();
            return true;
        } else if (i == R.id.menu_share) {
            try {
                createShortDynamicLink(Uri.parse(AppConstants.DEEP_LINK_URL + "/" + mPostReference.toString()), 0, post.author, post.title);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return true;
        } else if (i == R.id.menu_delete) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (!isFinishing()) {
                        new AlertDialog.Builder(PostDetailActivity.this)
                                .setTitle(getResources().getString(R.string.delete_header))
                                .setMessage(getResources().getString(R.string.delete_message))
                                .setCancelable(false)
                                .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        DatabaseReference globalPostRef = mDatabase.child("posts").child(mPostReference.getKey());
                                        DatabaseReference userPostRef = mDatabase.child("user-posts").child(post.uid).child(mPostReference.getKey());
                                        DatabaseReference starUserPostRef = mDatabase.child("star-user-posts").child(getUid()).child(mPostReference.getKey());// Run two transactions
                                        globalPostRef.removeValue();
                                        userPostRef.removeValue();
                                        starUserPostRef.removeValue();
                                        finish();
                                    }
                                }).setNegativeButton("CANCEL", null).show();
                    }

                }
            });
            return true;
        } else
            return super.onOptionsItemSelected(item);
    }

    private void onClickStar() {

        // Need to write to both places the post is stored
        DatabaseReference globalPostRef = mDatabase.child("posts").child(mPostReference.getKey());
        DatabaseReference userPostRef = mDatabase.child("user-posts").child(post.uid).child(mPostReference.getKey());

        // Run two transactions

        onStarClicked(globalPostRef);
        onStarClicked(userPostRef);

    }

    private void onStarClicked(final DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Post p = mutableData.getValue(Post.class);
                if (p == null) {
                    return Transaction.success(mutableData);
                }

                if (p.stars.containsKey(getUid())) {
                    // Unstar the post and remove self from stars
                    p.starCount = p.starCount - 1;
                    p.stars.remove(getUid());
                    mDatabase.child("star-user-posts").child(getUid()).child(postRef.getKey()).removeValue();
                    //Toast.makeText(PostDetailActivity.this,R.string.removed_from_fav,Toast.LENGTH_SHORT).show();
                } else {
                    // Star the post and add self to stars
                    p.starCount = p.starCount + 1;
                    p.stars.put(getUid(), true);
                    Map<String, Object> postValues = p.toMap();

                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put("/star-user-posts/" + getUid() + "/" + postRef.getKey(), postValues);

                    mDatabase.updateChildren(childUpdates);
                    //Toast.makeText(PostDetailActivity.this,R.string.added_to_fav,Toast.LENGTH_SHORT).show();
                }

                // Set value and report transaction success
                mutableData.setValue(p);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });
    }

    private void createShortDynamicLink(@NonNull Uri deepLink, int minVersion, final String author, final String title) throws UnsupportedEncodingException {
        String domain = getString(R.string.app_code) + ".app.goo.gl/";

        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLongLink(Uri.parse(buildDeepLink(deepLink, minVersion)))
                .buildShortDynamicLink()
                .addOnCompleteListener(this, new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {
                            // Short link created
                            final Uri shortLink = task.getResult().getShortLink();
                            Uri flowchartLink = task.getResult().getPreviewLink();
                            shareDeepLink(shortLink.toString().replace(" ", "%20"), author, title);
                        } else {
                            // Error
                            // ...
                        }

                    }
                });
    }

    private void shareDeepLink(String deepLink, String author, String title) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Firebase Deep Link");
        intent.putExtra(Intent.EXTRA_TEXT, author + " wrote on " + title + " " + deepLink + " via DivineBook");

        startActivity(intent);
    }

    public String buildDeepLink(@NonNull Uri deepLink, int minVersion) throws UnsupportedEncodingException {
        String domain = getString(R.string.app_code) + ".app.goo.gl/";

        // Set dynamic link parameters:
        //  * Domain (required)
        //  * Android Parameters (required)
        //  * Deep link
        // [START build_dynamic_link]
        DynamicLink.Builder builder = FirebaseDynamicLinks.getInstance()
                .createDynamicLink()
                .setDynamicLinkDomain(domain)
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder()
                        .setMinimumVersion(minVersion)
                        .build())
                .setSocialMetaTagParameters(
                        new DynamicLink.SocialMetaTagParameters.Builder()
                                .setImageUrl(Uri.parse("https://static.wixstatic.com/media/5227b1_d42fdcc2ba9a4957874adafdeb735b53~mv2.png/v1/fill/w_80,h_80,al_c,usm_0.66_1.00_0.01/5227b1_d42fdcc2ba9a4957874adafdeb735b53~mv2.png"))
                                .setTitle(getResources().getString(R.string.app_name))
                                .build())
                .setLink(deepLink);

        // Build the dynamic link
        DynamicLink link = builder.buildDynamicLink();
        // [END build_dynamic_link]

        // Return the dynamic link as a URI
        return java.net.URLDecoder.decode(String.valueOf(link.getUri()), "UTF-8");
    }
}
