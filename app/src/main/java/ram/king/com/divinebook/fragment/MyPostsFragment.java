package ram.king.com.divinebook.fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class MyPostsFragment extends UserListFragment {

    public MyPostsFragment() {
    }

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // All my posts
        //showProgressDialog();
        Query myPostsQuery = databaseReference.child("users");
        //hideProgressDialog();
        return myPostsQuery;
    }
}
