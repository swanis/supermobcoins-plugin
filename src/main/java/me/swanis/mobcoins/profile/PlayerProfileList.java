package me.swanis.mobcoins.profile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class PlayerProfileList implements Iterable<PlayerProfile> {

    private final int maxsize;
    private ArrayList<PlayerProfile> profileList = new ArrayList<>();

    public PlayerProfileList(int maxsize)  {
        this.maxsize = maxsize;
    }

    /**
     * Adds a new player to the profilelist, makes the approprite checks to make sure the list doesn't get too big.
     * @param profile a new player profile to add to the list
     */
    public void add(PlayerProfile profile) {
        // If list is about to reach maxsize sort it first to make sure the one with the least coins is grabbed
        if (profileList.size() + 1 > maxsize) {
            profileList.sort(Collections.reverseOrder());
        }

        if (profileList.size() > maxsize) {
            // Get the last index
            PlayerProfile oldprofile = profileList.get(profileList.size() - 1);
            if (oldprofile.getTokens() < profile.getTokens()) {
                profileList.add(profile);
                profileList.remove(oldprofile);
            }
        } else {
            profileList.add(profile);
        }
    }

    /**
     * @return the size of the profile list
     */
    public int size() { return profileList.size(); }


    /**
     * @param toRemove Remove all elements in the provided arraylist from the profilelist
     */

    /**
     * Resort the list after it's values has been updated.
     * @param profile profile to be updated
     */
    public void updateprofile(PlayerProfile profile, long updatedValue) {
        profile.setTokens(updatedValue);
    }

    /**
     * Re-sort the list
     */
    public void update() {
        profileList.sort(Collections.reverseOrder());
    }

    /**
     * Works exactly as an arraylist, just for simplification
     * @param index The index
     * @return The playerprofile at the given index
     */
    public PlayerProfile get(int index) {
        return profileList.get(index);
    }

    /**
     * Implements iterator to make foreach loops work.
     * @return Iterator instance of the profielist
     */
    @Override
    public Iterator<PlayerProfile> iterator() { return profileList.iterator(); }

    /**
     * @return getter for the profilelist
     */
}
