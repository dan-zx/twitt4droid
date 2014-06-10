package com.twitt4droid;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.twitt4droid.data.dao.UserDAO;
import com.twitt4droid.data.dao.impl.DAOFactory;

import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;
import twitter4j.UserList;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import java.util.List;

public final class Twitt4droidAsyncTasks {

    private static final String TAG = Twitt4droidAsyncTasks.class.getSimpleName();

    private Twitt4droidAsyncTasks() {
        throw new IllegalAccessError("This class cannot be instantiated nor extended");
    }

    public static abstract class AsyncTwitterFetcher<Params, Result> extends AsyncTask<Params, Void, Result> {

        private final Context context;
        private Twitter twitter;
        private TwitterException twitterException;
        
        public AsyncTwitterFetcher(Context context) {
            this.context = context;
            twitter = Twitt4droid.getTwitter(context);
        }

        protected Context getContext() {
            return context;
        }

        protected Twitter getTwitter() {
            return twitter;
        }

        protected TwitterException getTwitterException() {
            return twitterException;
        }

        protected void setTwitterException(TwitterException twitterException) {
            this.twitterException = twitterException;
        }
    }
    
    public static abstract class AsyncTwitterOp<Params> extends AsyncTwitterFetcher<Params, Void> {
        
        public AsyncTwitterOp(Context context) {
            super(context);
        }
    }
    
    public static class VerifyCredentialsTask extends AsyncTwitterFetcher<Void, User> {

        public VerifyCredentialsTask(Context context) {
            super(context);
        }

        @Override
        protected User doInBackground(Void... params) {
            try {
                return getTwitter().verifyCredentials();
            } catch (TwitterException ex) {
                setTwitterException(ex);
            }

            return null;
        }
    }
    
    public static class GetOAuthRequestTokenTask extends AsyncTwitterFetcher<String, RequestToken> {

        public GetOAuthRequestTokenTask(Context context) {
            super(context);
        }

        @Override
        protected RequestToken doInBackground(String... params) {
            try {
                return getTwitter().getOAuthRequestToken(params[0]);
            } catch (TwitterException ex) {
                setTwitterException(ex);
            }

            return null;
        }
    }

    public static class GetOAuthAccessTokenTask extends AsyncTwitterFetcher<String, AccessToken> {

        public GetOAuthAccessTokenTask(Context context) {
            super(context);
        }

        @Override
        protected AccessToken doInBackground(String... params) {
            try {
                return getTwitter().getOAuthAccessToken(params[0]);
            } catch (TwitterException ex) {
                setTwitterException(ex);
            }

            return null;
        }
    }

    public static class RetweetTask extends AsyncTwitterFetcher<Long, Status> {

        public RetweetTask(Context context) {
            super(context);
        }

        @Override
        protected twitter4j.Status doInBackground(Long... params) {
            try {
                return getTwitter().retweetStatus(params[0]);
            } catch (TwitterException ex) {
                setTwitterException(ex);
            }
            
            return null;
        }
        
        @Override
        protected void onPostExecute(twitter4j.Status result) {
            if (getTwitterException() != null) {
                Log.e(TAG, "Twitter error", getTwitterException());
                Toast.makeText(getContext().getApplicationContext(), 
                        R.string.twitt4droid_error_message, 
                        Toast.LENGTH_LONG)
                        .show();
            } else {
                Toast.makeText(getContext().getApplicationContext(), 
                        R.string.twitt4droid_tweet_retweeted, 
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    public static class CreateFavoriteTask extends AsyncTwitterFetcher<Long, Status> {

        public CreateFavoriteTask(Context context) {
            super(context);
        }

        @Override
        protected twitter4j.Status doInBackground(Long... params) {
            try {
                return getTwitter().createFavorite(params[0]);
            } catch (TwitterException ex) {
                setTwitterException(ex);
            }
            
            return null;
        }
        
        @Override
        protected void onPostExecute(twitter4j.Status result) {
            if (getTwitterException() != null) {
                Log.e(TAG, "Twitter error", getTwitterException());
                Toast.makeText(getContext().getApplicationContext(), 
                        R.string.twitt4droid_error_message, 
                        Toast.LENGTH_LONG)
                        .show();
            } else {
                Toast.makeText(getContext().getApplicationContext(), 
                        R.string.twitt4droid_tweet_favorited, 
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }
    
    public static class TweetTask extends AsyncTwitterFetcher<String, Status> {

        public TweetTask(Context context) {
            super(context);
        }

        @Override
        protected twitter4j.Status doInBackground(String... params) {
            try {
                return getTwitter().updateStatus(params[0]);
            } catch (TwitterException ex) {
                setTwitterException(ex);
            }
            
            return null;
        }
        
        @Override
        protected void onPostExecute(twitter4j.Status result) {
            if (getTwitterException() != null) {
                Log.e(TAG, "Twitter error", getTwitterException());
                Toast.makeText(getContext().getApplicationContext(), 
                        R.string.twitt4droid_error_message, 
                        Toast.LENGTH_LONG)
                        .show();
            } else {
                Toast.makeText(getContext().getApplicationContext(), 
                        R.string.twitt4droid_tweet_sent, 
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }
    
    public abstract static class TweetFetcher<Params> extends AsyncTwitterFetcher<Params, List<Status>> {

        public TweetFetcher(Context context) {
            super(context);
        }

        @Override
        @SuppressWarnings("unchecked")
        protected List<twitter4j.Status> doInBackground(Params... params) {
            try {
                return loadTweetsInBackground(params);
            } catch (TwitterException ex) {
                setTwitterException(ex);
            }
            
            return null;
        }
        
        @SuppressWarnings("unchecked")
        protected abstract List<twitter4j.Status> loadTweetsInBackground (Params... params) throws TwitterException;
    }
    
    public static class UserInfoFetcher extends AsyncTwitterFetcher<String, User> {

        private boolean isConnectedToInternet;
        private UserDAO userDAO;
        
        public UserInfoFetcher(Context context) {
            super(context);
        }
        
        @Override
        protected void onPreExecute() {
            isConnectedToInternet = Resources.isConnectedToInternet(getContext());
            userDAO = new DAOFactory(getContext()).getUserDAO();
        }
        
        @Override
        protected User doInBackground(String... params) {
            User user = null;
            if (isConnectedToInternet) {
                try {
                    user = getTwitter().showUser(params[0]);
                    userDAO.delete(user);
                    userDAO.save(user);
                    return user;
                } catch (TwitterException ex) {
                    setTwitterException(ex);
                }
            } else user = userDAO.fetchByScreenName(params[0]);
            return user;
        }
    }

    public static class UserListsFetcher extends AsyncTwitterFetcher<String, ResponseList<UserList>> {

        public UserListsFetcher(Context context) {
            super(context);
        }

        @Override
        protected ResponseList<UserList> doInBackground(String... args) {
            try {
                return getTwitter().getUserLists(args[0]);
            } catch (TwitterException ex) {
                setTwitterException(ex);
            }
            return null;
        }
    }
}