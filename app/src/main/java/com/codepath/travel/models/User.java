package com.codepath.travel.models;

import android.text.TextUtils;

import com.codepath.travel.callbacks.ParseQueryCallback;
import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.List;

import static com.codepath.travel.models.ParseModelConstants.FAVORITES_RELATION_KEY;
import static com.codepath.travel.models.ParseModelConstants.FB_UID_KEY;
import static com.codepath.travel.models.ParseModelConstants.FOLLOWING_RELATION_KEY;
import static com.codepath.travel.models.ParseModelConstants.PHOTO_URL;
import static com.codepath.travel.models.ParseModelConstants.PROFILE_PIC_URL_KEY;
import static com.codepath.travel.models.ParseModelConstants.USER_CLASS_NAME;
import static com.codepath.travel.models.ParseModelConstants.USER_KEY;

/**
 * Parse user model.
 */
@ParseClassName(USER_CLASS_NAME)
public class User extends ParseUser {

    public User() {
        super();
    }

    public int getFbUid() {
        return getInt(FB_UID_KEY);
    }

    public void setFbUid(int fbUid) {
        put(FB_UID_KEY, fbUid);
    }

    public String getProfilePicUrl() {
        return getString(PROFILE_PIC_URL_KEY);
    }

    public void setProfilePicUrl(String profilePicUrl) {
        put(PROFILE_PIC_URL_KEY, profilePicUrl);
    }

    public String getCoverPicUrl() {
        String coverUrl = getString(PHOTO_URL);
        if (coverUrl == null || TextUtils.isEmpty(coverUrl)) {
            return "http://www.english-heritage.org.uk/content/properties/stonehenge/things-to-do/stonehenge-in-day";
        }
        return coverUrl;
    }

    public void setCoverPicUrl(String coverPicUrl) {
        put(PHOTO_URL, coverPicUrl);
    }

    public void queryTrips(FindCallback<Trip> callback) {
        ParseQuery<Trip> query = ParseQuery.getQuery(Trip.class);
        query.whereEqualTo(USER_KEY, this);
        query.findInBackground(callback);
    }

    public ParseRelation<Trip> getFavoriteRelation() {
        return getRelation(FAVORITES_RELATION_KEY);
    }

    public void addFavorite(Trip trip) {
        getFavoriteRelation().add(trip);
        saveInBackground();
    }

    public void removeFavorite(Trip trip) {
        getFavoriteRelation().remove(trip);
        saveInBackground();
    }

    public void queryFavorites(FindCallback<Trip> callback) {
        getFavoriteRelation().getQuery().findInBackground(callback);
    }

    public ParseRelation<User> getFollowingRelation() {
        return getRelation(FOLLOWING_RELATION_KEY);
    }

    public void follow(User user) {
        getFollowingRelation().add(user);
        saveInBackground();
    }

    public void unFollow(User user) {
        getFollowingRelation().remove(user);
        saveInBackground();
    }

    public void queryFollowing(FindCallback<User> callback) {
        getFollowingRelation().getQuery().findInBackground(callback);
    }

    public void queryFollowers(FindCallback<User> callback) {
        ParseQuery<User> query = ParseQuery.getQuery(USER_CLASS_NAME);
        query.whereEqualTo(FOLLOWING_RELATION_KEY, this);
        query.findInBackground(callback);
    }

    // static query methods
    public static void getUserByID(String userId, ParseQueryCallback<User> callback) {
        ParseQuery<User> userQuery = ParseQuery.getQuery(User.class);
        userQuery.whereEqualTo("objectId", userId);
        userQuery.findInBackground((List<User> objects, ParseException e) -> {
            if (e != null || objects.size() == 0) {
                callback.onQueryError(e);
                return;
            }
            callback.onQuerySuccess(objects.get(0));
        });
    }

    public static void saveCoverPicURL(User user, String coverPicURL, ParseQueryCallback<User> callback) {
        user.setCoverPicUrl(coverPicURL);
        user.saveInBackground((ParseException e) -> {
            if (e != null) {
                callback.onQueryError(e);
                return;
            }
            callback.onQuerySuccess(user);
        });
    }

    public static void saveProfilePicURL(User user, String profilePicURL, ParseQueryCallback<User> callback) {
        user.setProfilePicUrl(profilePicURL);
        user.saveInBackground((ParseException e) -> {
            if (e != null) {
                callback.onQueryError(e);
                return;
            }
            callback.onQuerySuccess(user);
        });
    }

    // TODO: figure out how to query for all tags of this user (trip/storyPlace/media)

}
