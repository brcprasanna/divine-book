package ram.king.com.divinebook.viewholder;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;

import ram.king.com.divinebook.R;
import ram.king.com.divinebook.models.Post;


public class PostViewHolder extends RecyclerView.ViewHolder {

    public CircularImageView authorPhoto;
    public TextView titleView;
    public TextView authorView;
    //public TextView date;
    public ImageView starView;
    public ImageView deleteView;
    public TextView bodyView;
    public TextView dedicatedTo;
    public TextView courtesy;
    public ImageView share;
    public LinearLayout content;
    public RelativeLayout topUserLayout;


    public PostViewHolder(View itemView) {
        super(itemView);
        authorPhoto = (CircularImageView) itemView.findViewById(R.id.post_author_photo);
        titleView = (TextView) itemView.findViewById(R.id.post_title);
        authorView = (TextView) itemView.findViewById(R.id.post_author);
        //date = (TextView) itemView.findViewById(R.id.post_date);
        starView = (ImageView) itemView.findViewById(R.id.button_star);
        deleteView = (ImageView) itemView.findViewById(R.id.delete);
        bodyView = (TextView) itemView.findViewById(R.id.post_body);
        dedicatedTo = (TextView) itemView.findViewById(R.id.post_dedicated_to);
        courtesy = (TextView) itemView.findViewById(R.id.post_courtesy);
        share = (ImageView) itemView.findViewById(R.id.button_share);
        content = (LinearLayout) itemView.findViewById(R.id.content_layout);
        topUserLayout = (RelativeLayout) itemView.findViewById(R.id.post_author_layout);
    }

    public void bindToPost(Post post, View.OnClickListener starClickListener,
                           View.OnClickListener deleteClickListener, View.OnClickListener contentListener,
                           View.OnClickListener shareListener, View.OnClickListener topUserLayoutListener) {
        if (!TextUtils.isEmpty(post.title)) {
            titleView.setVisibility(View.VISIBLE);
            titleView.setText("Title : " + post.title);
        } else {
            titleView.setVisibility(View.GONE);
        }

        authorView.setText(post.author);
        if (!TextUtils.isEmpty(post.dedicatedTo)) {
            dedicatedTo.setVisibility(View.VISIBLE);
            dedicatedTo.setText("Dedicated To : " + post.dedicatedTo);
        } else {
            dedicatedTo.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(post.courtesy)) {
            courtesy.setVisibility(View.VISIBLE);
            courtesy.setText("Courtesy : " + post.courtesy);
        } else {
            courtesy.setVisibility(View.GONE);
        }

        bodyView.setText(Html.fromHtml(post.body), TextView.BufferType.SPANNABLE);
        starView.setOnClickListener(starClickListener);
        deleteView.setOnClickListener(deleteClickListener);
        content.setOnClickListener(contentListener);
        share.setOnClickListener(shareListener);
        topUserLayout.setOnClickListener(topUserLayoutListener);
    }
}