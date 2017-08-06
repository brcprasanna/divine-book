package ram.king.com.divinebook.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import ram.king.com.divinebook.R;
import ram.king.com.divinebook.models.User;


public class UserViewHolder extends RecyclerView.ViewHolder {

    public ImageView authorPhoto;
    // public TextView authorView;
    public RelativeLayout topUserLayout;


    public UserViewHolder(View itemView) {
        super(itemView);
        authorPhoto = (ImageView) itemView.findViewById(R.id.user_author_photo);
        //   authorView = (TextView) itemView.findViewById(R.id.user_author);
        topUserLayout = (RelativeLayout) itemView.findViewById(R.id.top_layout_user);
    }

    public void bindToPost(User user, View.OnClickListener topUserLayoutListener) {
        // authorView.setText(user.displayName);
        topUserLayout.setOnClickListener(topUserLayoutListener);
    }
}
