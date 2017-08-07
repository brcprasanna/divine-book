package ram.king.com.divinebook.fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import ram.king.com.divinebook.util.AppConstants;
import ram.king.com.divinebook.util.AppUtil;

public class MyPostsFragment extends UserListFragment {

    public MyPostsFragment() {
    }

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // All my posts
        //showProgressDialog();
        Query myPostsQuery;
        String prefLang = AppUtil.getString(activity,AppConstants.PREFERRED_LANGUAGE,AppConstants.DEFAULT_LANGUAGE);
        if (prefLang.equals(AppConstants.DEFAULT_LANGUAGE))
            myPostsQuery = databaseReference.child("users").orderByChild("moderatorFlag").equalTo("1E");
        else
            myPostsQuery = databaseReference.child("users").orderByChild("moderatorFlag").equalTo("1T");
        //hideProgressDialog();
        return myPostsQuery;
    }
}
